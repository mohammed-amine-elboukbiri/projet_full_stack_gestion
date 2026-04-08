import { useQuery } from "@tanstack/react-query";
import { AlertTriangle, ArrowRight, Clock3, Wallet } from "lucide-react";
import { Link } from "react-router-dom";
import { useAuth } from "./auth";
import {
  apiClient,
  classNames,
  formatCurrency,
  formatDate,
  formatRole,
  isForbiddenError,
  monthRange,
  toFriendlyError
} from "./lib";
import { navigationItems } from "./resource-config";
import type { DashboardDto, PageResponse, PhaseReportingDto } from "./types";

function StatCard({
  label,
  value,
  tone,
  caption
}: {
  label: string;
  value: string | number;
  tone: "teal" | "copper" | "slate";
  caption: string;
}) {
  return (
    <article className={classNames("stat-card", `stat-card-${tone}`)}>
      <span>{label}</span>
      <strong>{value}</strong>
      <small>{caption}</small>
    </article>
  );
}

export function DashboardPage() {
  const { session, hasAnyRole } = useAuth();
  const range = monthRange();
  const canViewReporting = hasAnyRole(["COMPTABLE", "DIRECTEUR", "ADMINISTRATEUR"]);

  const dashboardQuery = useQuery({
    queryKey: ["dashboard"],
    enabled: canViewReporting,
    queryFn: async () => {
      const { data } = await apiClient.get<DashboardDto>("/api/reporting/dashboard");
      return data;
    }
  });

  const nonFactureesQuery = useQuery({
    queryKey: ["reporting", "nonFacturees", range.start, range.end],
    enabled: canViewReporting,
    queryFn: async () => {
      const { data } = await apiClient.get<PhaseReportingDto[]>(
        "/api/reporting/phases-terminees-non-facturees",
        {
          params: {
            dateDebut: range.start,
            dateFin: range.end
          }
        }
      );
      return data;
    }
  });

  const projetPulseQuery = useQuery({
    queryKey: ["dashboard", "projets-page"],
    queryFn: async () => {
      const { data } = await apiClient.get<PageResponse<Record<string, unknown>>>("/api/projets/page", {
        params: {
          page: 0,
          size: 4,
          sortBy: "id",
          sortDir: "desc"
        }
      });
      return data.content;
    }
  });

  const visibleShortcuts = navigationItems
    .filter((item) => item.to !== "/dashboard" && hasAnyRole(item.roles))
    .slice(0, 6);

  const reportingBlocked = canViewReporting && dashboardQuery.isError && isForbiddenError(dashboardQuery.error);
  const watchItems = canViewReporting ? nonFactureesQuery.data ?? [] : [];
  const budgetSignal = watchItems.reduce((accumulator, item) => accumulator + (item.montant ?? 0), 0);

  return (
    <section className="page-shell">
      <div className="hero-band hero-band-teal">
        <div>
          <span className="eyebrow">Tableau de bord</span>
          <h1>Vue de pilotage des projets et des points de vigilance.</h1>
          <p>
            Suivez les indicateurs globaux, les phases a surveiller et les acces prioritaires
            associes a votre profil de travail.
          </p>
        </div>

        <div className="hero-side-panel">
          <span>Session active</span>
          <strong>{session?.login ?? "Utilisateur"}</strong>
          <small>
            {formatRole(session?.profil)} - {visibleShortcuts.length} modules accessibles
          </small>
        </div>
      </div>

      <div className="stats-grid">
        {canViewReporting ? (
          <>
            <StatCard
              label="Phases a facturer"
              value={watchItems.length}
              tone="copper"
              caption="Finies dans la fenetre active"
            />
            <StatCard
              label="Budget expose"
              value={formatCurrency(budgetSignal)}
              tone="teal"
              caption="Montant cumule de ces phases"
            />
            <StatCard
              label="Acces directs"
              value={visibleShortcuts.length}
              tone="slate"
              caption="Modules visibles pour votre role"
            />
          </>
        ) : (
          <>
            <StatCard
              label="Profil actif"
              value={formatRole(session?.profil)}
              tone="teal"
              caption="navigation adaptee a votre role"
            />
            <StatCard
              label="Acces directs"
              value={visibleShortcuts.length}
              tone="slate"
              caption="modules visibles pour votre role"
            />
            <StatCard
              label="Projets recents"
              value={projetPulseQuery.data?.length ?? 0}
              tone="copper"
              caption="elements affiches sur cette vue"
            />
          </>
        )}
      </div>

      <div className="dashboard-grid">
        <article className="content-card content-card-large">
          <div className="card-header">
            <div>
              <span className="eyebrow">Vue de pilotage</span>
              <h2>KPI globaux</h2>
            </div>
          </div>

          {!canViewReporting ? (
            <div className="table-empty">
              Cette vue financiere est reservee a la direction, a la comptabilite et a l administration.
            </div>
          ) : dashboardQuery.isLoading ? (
            <div className="table-empty">Chargement des indicateurs...</div>
          ) : dashboardQuery.isError ? (
            <div className="table-empty">
              {reportingBlocked
                ? "Le backend refuse le reporting pour ce role. La navigation reste disponible."
                : toFriendlyError(dashboardQuery.error)}
            </div>
          ) : (
            <div className="kpi-grid">
              <StatCard
                label="Projets"
                value={dashboardQuery.data?.totalProjets ?? 0}
                tone="teal"
                caption="portefeuille global"
              />
              <StatCard
                label="Phases"
                value={dashboardQuery.data?.totalPhases ?? 0}
                tone="slate"
                caption="lots d execution"
              />
              <StatCard
                label="Employes"
                value={dashboardQuery.data?.totalEmployes ?? 0}
                tone="copper"
                caption="ressources referencees"
              />
              <StatCard
                label="Organismes"
                value={dashboardQuery.data?.totalOrganismes ?? 0}
                tone="slate"
                caption="clients et partenaires"
              />
            </div>
          )}
        </article>

        <article className="content-card">
          <div className="card-header">
            <div>
              <span className="eyebrow">Actions rapides</span>
              <h2>Modules utiles maintenant</h2>
            </div>
          </div>

          <div className="shortcut-list">
            {visibleShortcuts.map((item) => {
              const Icon = item.icon;
              return (
                <Link key={item.to} className="shortcut-link" to={item.to}>
                  <div className="icon-badge">
                    <Icon size={18} />
                  </div>
                  <div>
                    <strong>{item.label}</strong>
                    <span>{item.caption}</span>
                  </div>
                  <ArrowRight size={16} />
                </Link>
              );
            })}
          </div>
        </article>

        <article className="content-card content-card-large">
          <div className="card-header">
            <div>
              <span className="eyebrow">Vigilance</span>
              <h2>Phases terminees non facturees</h2>
            </div>
            <div className="status-pill status-role">
              <Clock3 size={14} />
              Fenetre du mois
            </div>
          </div>

          {!canViewReporting ? (
            <div className="table-empty">
              Le profil chef de projet se concentre sur les projets, phases, livrables et documents.
            </div>
          ) : nonFactureesQuery.isLoading ? (
            <div className="table-empty">Calcul du radar financier...</div>
          ) : nonFactureesQuery.isError ? (
            <div className="table-empty">{toFriendlyError(nonFactureesQuery.error)}</div>
          ) : watchItems.length ? (
            <div className="watch-list">
              {watchItems.slice(0, 6).map((item) => (
                <article key={item.id} className="watch-item">
                  <div>
                    <strong>{item.libelle}</strong>
                    <span>
                      {item.projetNom} - cloture {formatDate(item.dateFin)}
                    </span>
                  </div>
                  <div className="watch-value">
                    <Wallet size={15} />
                    {formatCurrency(item.montant)}
                  </div>
                </article>
              ))}
            </div>
          ) : (
            <div className="table-empty">
              <AlertTriangle size={18} />
              Aucune phase terminee non facturee sur la fenetre active.
            </div>
          )}
        </article>

        <article className="content-card">
          <div className="card-header">
            <div>
              <span className="eyebrow">Flux recents</span>
              <h2>Derniers projets charges</h2>
            </div>
          </div>

          {projetPulseQuery.isLoading ? (
            <div className="table-empty">Chargement des projets...</div>
          ) : projetPulseQuery.isError ? (
            <div className="table-empty">{toFriendlyError(projetPulseQuery.error)}</div>
          ) : (
            <div className="project-pulse-list">
              {projetPulseQuery.data?.map((item) => (
                <article key={String(item.id)} className="pulse-card">
                  <strong>{String(item.nom ?? "Projet sans nom")}</strong>
                  <span>{String(item.organismeNom ?? "Organisme non renseigne")}</span>
                  <small>{formatCurrency(item.montant)}</small>
                </article>
              ))}
            </div>
          )}
        </article>
      </div>
    </section>
  );
}

