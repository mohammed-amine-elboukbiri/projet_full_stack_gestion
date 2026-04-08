import { useQuery } from "@tanstack/react-query";
import { useMemo, useState } from "react";
import { apiClient, formatCurrency, formatDate, monthRange, toFriendlyError } from "./lib";
import type { DashboardDto, PhaseReportingDto } from "./types";

const reportDefinitions = [
  {
    key: "phases",
    label: "Phases de la periode",
    endpoint: "/api/reporting/phases"
  },
  {
    key: "non-facturees",
    label: "Terminees non facturees",
    endpoint: "/api/reporting/phases-terminees-non-facturees"
  },
  {
    key: "non-payees",
    label: "Facturees non payees",
    endpoint: "/api/reporting/phases-facturees-non-payees"
  },
  {
    key: "payees",
    label: "Payees",
    endpoint: "/api/reporting/phases-payees"
  }
] as const;

export function ReportingPage() {
  const range = monthRange();
  const [dateDebut, setDateDebut] = useState(range.start);
  const [dateFin, setDateFin] = useState(range.end);
  const [activeReport, setActiveReport] = useState<(typeof reportDefinitions)[number]["key"]>(
    "non-facturees"
  );

  const currentReport = useMemo(
    () => reportDefinitions.find((report) => report.key === activeReport) ?? reportDefinitions[0],
    [activeReport]
  );

  const dashboardQuery = useQuery({
    queryKey: ["reporting", "dashboard"],
    queryFn: async () => {
      const { data } = await apiClient.get<DashboardDto>("/api/reporting/dashboard");
      return data;
    }
  });

  const reportQuery = useQuery({
    queryKey: ["reporting", currentReport.key, dateDebut, dateFin],
    queryFn: async () => {
      const { data } = await apiClient.get<PhaseReportingDto[]>(currentReport.endpoint, {
        params: {
          dateDebut,
          dateFin
        }
      });
      return data;
    }
  });

  const totalAmount = (reportQuery.data ?? []).reduce(
    (accumulator, item) => accumulator + (item.montant ?? 0),
    0
  );

  return (
    <section className="page-shell">
      <div className="hero-band hero-band-copper">
        <div>
          <span className="eyebrow">Reporting</span>
          <h1>Lecture financiere et operationnelle des phases.</h1>
          <p>
            Filtrez la periode d analyse puis comparez les etats de realisation, de facturation
            et de paiement sur les phases remontees par l API.
          </p>
        </div>
      </div>

      <div className="toolbar-card">
        <div className="filter-grid">
          <label className="field">
            <span>Date debut</span>
            <input type="date" value={dateDebut} onChange={(event) => setDateDebut(event.target.value)} />
          </label>
          <label className="field">
            <span>Date fin</span>
            <input type="date" value={dateFin} onChange={(event) => setDateFin(event.target.value)} />
          </label>
          <label className="field field-span-2">
            <span>Rapport</span>
            <div className="segment-row">
              {reportDefinitions.map((report) => (
                <button
                  key={report.key}
                  className={activeReport === report.key ? "segment segment-active" : "segment"}
                  onClick={() => setActiveReport(report.key)}
                  type="button"
                >
                  {report.label}
                </button>
              ))}
            </div>
          </label>
        </div>
      </div>

      <div className="stats-grid">
        <article className="stat-card stat-card-teal">
          <span>Lignes trouvees</span>
          <strong>{reportQuery.data?.length ?? 0}</strong>
          <small>{currentReport.label}</small>
        </article>
        <article className="stat-card stat-card-copper">
          <span>Montant cumule</span>
          <strong>{formatCurrency(totalAmount)}</strong>
          <small>sur la periode</small>
        </article>
        <article className="stat-card stat-card-slate">
          <span>Phases payees globales</span>
          <strong>{dashboardQuery.data?.phasesPayees ?? "--"}</strong>
          <small>selon le dashboard</small>
        </article>
      </div>

      <div className="table-card">
        {reportQuery.isLoading ? (
          <div className="table-empty">Chargement du rapport...</div>
        ) : reportQuery.isError ? (
          <div className="table-empty">{toFriendlyError(reportQuery.error)}</div>
        ) : reportQuery.data?.length ? (
          <div className="table-scroll">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Libelle</th>
                  <th>Projet</th>
                  <th>Date fin</th>
                  <th>Montant</th>
                  <th>Realisation</th>
                  <th>Facturation</th>
                  <th>Paiement</th>
                </tr>
              </thead>
              <tbody>
                {reportQuery.data.map((item) => (
                  <tr key={item.id}>
                    <td>{item.code}</td>
                    <td>{item.libelle}</td>
                    <td>{item.projetNom}</td>
                    <td>{formatDate(item.dateFin)}</td>
                    <td>{formatCurrency(item.montant)}</td>
                    <td>{item.etatRealisation ? "Oui" : "Non"}</td>
                    <td>{item.etatFacturation ? "Oui" : "Non"}</td>
                    <td>{item.etatPaiement ? "Oui" : "Non"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="table-empty">Aucune phase sur cette fenetre.</div>
        )}
      </div>
    </section>
  );
}
