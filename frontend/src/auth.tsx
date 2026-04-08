import { createContext, useContext, useMemo, useState, type ReactNode } from "react";
import { Navigate, Outlet, useLocation, useNavigate } from "react-router-dom";
import { apiClient, queryClient, sessionStore, toFriendlyError } from "./lib";
import type { LoginPayload, Role, Session } from "./types";

interface AuthContextValue {
  session: Session | null;
  login: (payload: LoginPayload) => Promise<Session>;
  applySession: (session: Session) => Promise<void>;
  logout: () => void;
  hasAnyRole: (roles?: Role[]) => boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(() => sessionStore.read());

  async function applySession(nextSession: Session) {
    setSession(nextSession);
    sessionStore.write(nextSession);
    await queryClient.invalidateQueries();
  }

  const value = useMemo<AuthContextValue>(
    () => ({
      session,
      async login(payload) {
        const { data } = await apiClient.post<Session>("/api/auth/login", payload);
        await applySession(data);
        return data;
      },
      applySession,
      logout() {
        setSession(null);
        sessionStore.clear();
        void queryClient.clear();
      },
      hasAnyRole(roles) {
        if (!roles || roles.length === 0) {
          return true;
        }

        return Boolean(session?.profil && roles.includes(session.profil));
      }
    }),
    [session]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }

  return context;
}

export function RequireAuth() {
  const { session } = useAuth();
  const location = useLocation();

  if (!session) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  return <Outlet />;
}

export function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { session, login } = useAuth();
  const [form, setForm] = useState<LoginPayload>({ login: "", password: "" });
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  if (session) {
    return <Navigate to={getDefaultRoute(session.profil)} replace />;
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const nextSession = await login(form);
      const fallbackRoute = getDefaultRoute(nextSession.profil);
      navigate(location.state?.from ?? fallbackRoute, { replace: true });
    } catch (submitError) {
      setError(toFriendlyError(submitError));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-panel auth-panel-copy">
        <div className="auth-badge">ZenTask</div>
        <div className="auth-copy-body">
          <span className="eyebrow auth-eyebrow">Pilotage centralise</span>
          <h1>Pilotage projet, execution et suivi financier dans un espace unique.</h1>
          <p>
            ZenTask centralise les projets, phases, livrables, documents et reportings dans
            une interface structuree selon les droits de chaque profil.
          </p>
        </div>
      </div>

      <div className="auth-panel auth-panel-form">
        <div className="auth-card">
          <div className="auth-form-header">
            <span className="eyebrow">Connexion securisee</span>
            <h2>Acceder a votre espace</h2>
          </div>

          <form className="stack-lg" onSubmit={handleSubmit}>
            <label className="field">
              <span>Identifiant</span>
              <input
                type="text"
                autoComplete="username"
                value={form.login}
                onChange={(event) => setForm((current) => ({ ...current, login: event.target.value }))}
                placeholder="Votre identifiant"
                required
              />
            </label>

            <label className="field">
              <span>Mot de passe</span>
              <input
                type="password"
                autoComplete="current-password"
                value={form.password}
                onChange={(event) =>
                  setForm((current) => ({ ...current, password: event.target.value }))
                }
                placeholder="Votre mot de passe"
                required
              />
            </label>

            {error ? <div className="inline-error">{error}</div> : null}

            <button className="button button-primary button-block" disabled={loading} type="submit">
              {loading ? "Connexion..." : "Se connecter"}
            </button>
          </form>

          <small className="auth-card-footnote">Acces reserve aux utilisateurs autorises.</small>
        </div>
      </div>
    </div>
  );
}

function getDefaultRoute(role: Role | undefined) {
  if (role === "COMPTABLE" || role === "DIRECTEUR" || role === "ADMINISTRATEUR") {
    return "/dashboard";
  }

  return "/projets";
}
