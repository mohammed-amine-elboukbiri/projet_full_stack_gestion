import { useMutation, useQueries, useQuery, useQueryClient } from "@tanstack/react-query";
import { LoaderCircle, Plus, Search, SlidersHorizontal, Trash2 } from "lucide-react";
import { useMemo, useState, type FormEvent } from "react";
import { useAuth } from "./auth";
import { apiClient, classNames, formatCurrency, formatDate, formatRole, toFriendlyError } from "./lib";
import type {
  FieldDefinition,
  QueryResultBundle,
  ResourceConfig,
  SearchDefinition,
  SelectOption
} from "./types";

function dedupeSources(config: ResourceConfig) {
  const allSources = [
    ...config.formFields.map((field) => field.source).filter(Boolean),
    ...(config.searchOptions ?? []).map((field) => field.source).filter(Boolean)
  ] as NonNullable<FieldDefinition["source"]>[];

  return allSources.filter(
    (source, index, array) =>
      array.findIndex((candidate) => candidate?.queryKey === source?.queryKey) === index
  );
}

function resolveOptions(
  field: FieldDefinition | SearchDefinition | undefined,
  optionLookup: Record<string, SelectOption[]>
) {
  if (!field) {
    return [];
  }

  if ("options" in field && field.options) {
    return field.options;
  }

  if ("source" in field && field.source) {
    return optionLookup[field.source.queryKey] ?? [];
  }

  return [];
}

function buildPayload(formFields: FieldDefinition[], formState: Record<string, string | boolean>) {
  return Object.fromEntries(
    formFields.map((field) => {
      const rawValue = formState[field.name];

      if (field.type === "boolean") {
        return [field.name, Boolean(rawValue)];
      }

      if (typeof rawValue === "boolean") {
        return [field.name, rawValue];
      }

      if (field.type === "number" || field.valueType === "number") {
        return [field.name, rawValue === "" ? null : Number(rawValue)];
      }

      return [field.name, rawValue];
    })
  );
}

function buildFormState(
  fields: FieldDefinition[],
  initialValues: Record<string, string | boolean>,
  item?: Record<string, unknown> | null
) {
  if (!item) {
    return { ...initialValues };
  }

  return Object.fromEntries(
    fields.map((field) => {
      const value = item[field.name];

      if (field.type === "boolean") {
        return [field.name, Boolean(value)];
      }

      if (value === null || value === undefined) {
        return [field.name, ""];
      }

      return [field.name, String(value)];
    })
  );
}

function renderCell(config: ResourceConfig, key: string, item: Record<string, unknown>) {
  const column = config.columns.find((candidate) => candidate.key === key);
  const value = column?.getValue ? column.getValue(item) : item[key];

  switch (column?.kind) {
    case "currency":
      return formatCurrency(value);
    case "date":
      return formatDate(value);
    case "boolean":
      return (
        <span className={classNames("status-pill", value ? "status-good" : "status-muted")}>
          {value ? column.trueLabel ?? "Oui" : column.falseLabel ?? "Non"}
        </span>
      );
    case "role":
      return <span className="status-pill status-role">{formatRole(String(value ?? ""))}</span>;
    default:
      return String(value ?? "--");
  }
}

function ResourceField({
  field,
  value,
  options,
  onChange
}: {
  field: FieldDefinition;
  value: string | boolean;
  options: SelectOption[];
  onChange: (name: string, value: string | boolean) => void;
}) {
  if (field.type === "textarea") {
    return (
      <label className="field field-span-2">
        <span>{field.label}</span>
        <textarea
          placeholder={field.placeholder}
          required={field.required}
          value={String(value)}
          onChange={(event) => onChange(field.name, event.target.value)}
        />
        {field.helperText ? <small>{field.helperText}</small> : null}
      </label>
    );
  }

  if (field.type === "select") {
    return (
      <label className="field">
        <span>{field.label}</span>
        <select
          required={field.required}
          value={String(value)}
          onChange={(event) => onChange(field.name, event.target.value)}
        >
          <option value="">Selectionner...</option>
          {options.map((option) => (
            <option key={`${field.name}-${option.value}`} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
        {field.helperText ? <small>{field.helperText}</small> : null}
      </label>
    );
  }

  if (field.type === "boolean") {
    return (
      <label className="toggle-field">
        <input
          type="checkbox"
          checked={Boolean(value)}
          onChange={(event) => onChange(field.name, event.target.checked)}
        />
        <span>{field.label}</span>
      </label>
    );
  }

  return (
    <label className="field">
      <span>{field.label}</span>
      <input
        type={field.type}
        placeholder={field.placeholder}
        required={field.required}
        value={String(value)}
        onChange={(event) => onChange(field.name, event.target.value)}
      />
      {field.helperText ? <small>{field.helperText}</small> : null}
    </label>
  );
}

export function ResourcePage({ config }: { config: ResourceConfig }) {
  const queryClient = useQueryClient();
  const { hasAnyRole } = useAuth();
  const [page, setPage] = useState(0);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<Record<string, unknown> | null>(null);
  const [formState, setFormState] = useState<Record<string, string | boolean>>(config.initialValues);
  const [actionError, setActionError] = useState<string | null>(null);
  const [searchKey, setSearchKey] = useState(config.searchOptions?.[0]?.key ?? "");
  const [searchValue, setSearchValue] = useState("");

  const sources = useMemo(() => dedupeSources(config), [config]);
  const optionQueries = useQueries({
    queries: sources.map((source) => ({
      queryKey: [source.queryKey, "options"],
      queryFn: async () => {
        const { data } = await apiClient.get<Record<string, unknown>[]>(source.endpoint);
        return data.map((item) => ({
          label: source.label(item),
          value: String(item[source.valueKey] ?? "")
        }));
      }
    }))
  });

  const optionLookup = Object.fromEntries(
    sources.map((source, index) => [source.queryKey, optionQueries[index]?.data ?? []])
  ) as Record<string, SelectOption[]>;

  const activeSearch = config.searchOptions?.find((option) => option.key === searchKey);
  const canCreate = hasAnyRole(config.createRoles);
  const canEdit = hasAnyRole(config.editRoles);
  const canDelete = hasAnyRole(config.deleteRoles);

  const dataQuery = useQuery<QueryResultBundle>({
    queryKey: [config.key, page, searchKey, searchValue],
    queryFn: async () => {
      if (searchValue.trim() && config.searchEndpoint && activeSearch) {
        const payload =
          activeSearch.valueType === "number"
            ? { [activeSearch.payloadKey]: Number(searchValue) }
            : { [activeSearch.payloadKey]: searchValue.trim() };
        const { data } = await apiClient.post<Record<string, unknown>[]>(config.searchEndpoint, payload);

        return {
          items: data,
          totalElements: data.length,
          totalPages: 1
        };
      }

      if (config.paginated) {
        const { data } = await apiClient.get(`/api${config.route}/page`, {
          params: {
            page,
            size: config.pageSize ?? 6,
            sortBy: "id",
            sortDir: "desc"
          }
        });

        return {
          items: data.content,
          totalElements: data.totalElements,
          totalPages: data.totalPages
        };
      }

      const { data } = await apiClient.get<Record<string, unknown>[]>(config.endpoint);
      return {
        items: data,
        totalElements: data.length,
        totalPages: 1
      };
    }
  });

  const saveMutation = useMutation({
    mutationFn: async (payload: Record<string, unknown>) => {
      if (editingItem) {
        const endpoint = config.updatePath?.(editingItem) ?? `${config.endpoint}/${editingItem.id}`;
        return apiClient.put(endpoint, payload);
      }

      return apiClient.post(config.endpoint, payload);
    },
    onSuccess: async () => {
      setDrawerOpen(false);
      setEditingItem(null);
      setFormState(config.initialValues);
      setActionError(null);
      await queryClient.invalidateQueries({
        predicate: (query) => typeof query.queryKey[0] === "string"
      });
    },
    onError: (error) => setActionError(toFriendlyError(error))
  });

  const deleteMutation = useMutation({
    mutationFn: async (item: Record<string, unknown>) => {
      const endpoint = config.deletePath?.(item) ?? `${config.endpoint}/${item.id}`;
      return apiClient.delete(endpoint);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        predicate: (query) => typeof query.queryKey[0] === "string"
      });
    }
  });

  if (!hasAnyRole(config.visibleRoles)) {
    return (
      <section className="page-shell">
        <div className="hero-band hero-band-slate">
          <div>
            <span className="eyebrow">Module verrouille</span>
            <h1>{config.title}</h1>
            <p>Votre role actuel ne peut pas ouvrir cette section.</p>
          </div>
        </div>
      </section>
    );
  }

  function openCreate() {
    setEditingItem(null);
    setFormState(config.initialValues);
    setActionError(null);
    setDrawerOpen(true);
  }

  function openEdit(item: Record<string, unknown>) {
    setEditingItem(item);
    setFormState(buildFormState(config.formFields, config.initialValues, item));
    setActionError(null);
    setDrawerOpen(true);
  }

  function handleFieldChange(name: string, value: string | boolean) {
    setFormState((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const payload = buildPayload(config.formFields, formState);
    await saveMutation.mutateAsync(payload);
  }

  async function handleDelete(item: Record<string, unknown>) {
    if (!window.confirm("Confirmer la suppression de cet element ?")) {
      return;
    }

    await deleteMutation.mutateAsync(item);
  }

  return (
    <section className="page-shell">
      <div className={`hero-band hero-band-${config.accent}`}>
        <div>
          <span className="eyebrow">{config.subtitle}</span>
          <h1>{config.title}</h1>
          <p>{config.description}</p>
        </div>
        <div className="hero-stat-grid">
          <article>
            <strong>{dataQuery.data?.totalElements ?? "--"}</strong>
            <span>enregistrements visibles</span>
          </article>
          <article>
            <strong>{config.formFields.length}</strong>
            <span>champs pilotes</span>
          </article>
          <article>
            <strong>{config.searchOptions?.length ?? 0}</strong>
            <span>criteres de recherche</span>
          </article>
        </div>
      </div>

      <div className="toolbar-card">
        <div className="toolbar-main">
          <span className="eyebrow">Recherche et actions</span>

          {config.searchOptions?.length ? (
            <div className="search-cluster">
              <div className="icon-badge">
                <Search size={16} />
              </div>
              <select value={searchKey} onChange={(event) => setSearchKey(event.target.value)}>
                {config.searchOptions.map((option) => (
                  <option key={option.key} value={option.key}>
                    {option.label}
                  </option>
                ))}
              </select>
              {activeSearch?.type === "select" ? (
                <select value={searchValue} onChange={(event) => setSearchValue(event.target.value)}>
                  <option value="">Tous</option>
                  {resolveOptions(activeSearch, optionLookup).map((option) => (
                    <option key={`search-${option.value}`} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              ) : (
                <input
                  type={activeSearch?.type === "number" ? "number" : "text"}
                  value={searchValue}
                  onChange={(event) => setSearchValue(event.target.value)}
                  placeholder="Filtrer le module"
                />
              )}
              <button className="button button-ghost" onClick={() => setSearchValue("")} type="button">
                Reinitialiser
              </button>
            </div>
          ) : (
            <div className="search-cluster static-cluster">
              <div className="icon-badge">
                <SlidersHorizontal size={16} />
              </div>
              <span>Ce module charge directement les donnees disponibles sans filtre dedie.</span>
            </div>
          )}
        </div>

        {canCreate ? (
          <button className="button button-primary" onClick={openCreate} type="button">
            <Plus size={16} />
            Nouveau
          </button>
        ) : null}
      </div>

      <div className="table-card">
        <div className="table-head">
          <div>
            <span className="eyebrow">Resultats</span>
            <strong>{config.title}</strong>
          </div>
          <small className="table-head-meta">
            {dataQuery.data?.totalElements ?? 0} element(s)
          </small>
        </div>

        {dataQuery.isLoading ? (
          <div className="table-empty">
            <LoaderCircle className="spinner" />
            <span>Chargement des donnees...</span>
          </div>
        ) : dataQuery.isError ? (
          <div className="table-empty">
            <span className="inline-error">{toFriendlyError(dataQuery.error)}</span>
          </div>
        ) : dataQuery.data?.items.length ? (
          <>
            <div className="table-scroll">
              <table className="data-table">
                <thead>
                  <tr>
                    {config.columns.map((column) => (
                      <th key={`${config.key}-${column.key}`}>{column.label}</th>
                    ))}
                    {canEdit || canDelete ? <th>Actions</th> : null}
                  </tr>
                </thead>
                <tbody>
                  {dataQuery.data.items.map((item) => (
                    <tr key={config.primaryKey(item)}>
                      {config.columns.map((column) => (
                        <td key={`${config.primaryKey(item)}-${column.key}`}>
                          {renderCell(config, column.key, item)}
                        </td>
                      ))}
                      {canEdit || canDelete ? (
                        <td>
                          <div className="row-actions">
                            {canEdit ? (
                              <button
                                className="button button-ghost"
                                onClick={() => openEdit(item)}
                                type="button"
                              >
                                Modifier
                              </button>
                            ) : null}
                            {canDelete ? (
                              <button
                                className="button button-danger"
                                disabled={deleteMutation.isPending}
                                onClick={() => handleDelete(item)}
                                type="button"
                              >
                                <Trash2 size={14} />
                              </button>
                            ) : null}
                          </div>
                        </td>
                      ) : null}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {config.paginated && (dataQuery.data.totalPages ?? 0) > 1 ? (
              <div className="pagination-row">
                <button
                  className="button button-ghost"
                  disabled={page === 0}
                  onClick={() => setPage((current) => Math.max(current - 1, 0))}
                  type="button"
                >
                  Precedent
                </button>
                <span>
                  Page {page + 1} / {dataQuery.data.totalPages}
                </span>
                <button
                  className="button button-ghost"
                  disabled={page >= dataQuery.data.totalPages - 1}
                  onClick={() =>
                    setPage((current) =>
                      Math.min(current + 1, Math.max((dataQuery.data.totalPages ?? 1) - 1, 0))
                    )
                  }
                  type="button"
                >
                  Suivant
                </button>
              </div>
            ) : null}
          </>
        ) : (
          <div className="table-empty">{config.emptyMessage}</div>
        )}
      </div>

      <div className={classNames("drawer-backdrop", drawerOpen && "drawer-backdrop-open")}>
        <aside className={classNames("drawer", drawerOpen && "drawer-open")}>
          <div className="drawer-header">
            <div>
              <span className="eyebrow">{editingItem ? "Modification" : "Creation"}</span>
              <h2>{editingItem ? `Modifier ${config.title}` : `Nouveau ${config.title}`}</h2>
            </div>
            <button className="button button-ghost" onClick={() => setDrawerOpen(false)} type="button">
              Fermer
            </button>
          </div>

          <form className="drawer-form" onSubmit={handleSubmit}>
            <div className="form-grid">
              {config.formFields.map((field) => (
                <ResourceField
                  key={`${config.key}-${field.name}`}
                  field={field}
                  options={resolveOptions(field, optionLookup)}
                  value={formState[field.name]}
                  onChange={handleFieldChange}
                />
              ))}
            </div>

            {actionError ? <div className="inline-error">{actionError}</div> : null}

            <div className="drawer-actions">
              <button
                className="button button-ghost"
                onClick={() => setDrawerOpen(false)}
                type="button"
              >
                Annuler
              </button>
              <button className="button button-primary" disabled={saveMutation.isPending} type="submit">
                {saveMutation.isPending ? "Enregistrement..." : "Enregistrer"}
              </button>
            </div>
          </form>
        </aside>
      </div>
    </section>
  );
}
