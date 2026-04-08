import { LogOut, Menu, PanelLeftClose } from "lucide-react";
import { useMemo, useState } from "react";
import { NavLink, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "./auth";
import { classNames, formatRole } from "./lib";
import { navigationItems } from "./resource-config";

export function AppShell() {
  const { session, logout, hasAnyRole } = useAuth();
  const location = useLocation();
  const [navOpen, setNavOpen] = useState(false);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const isMobileViewport =
    typeof window !== "undefined" && window.matchMedia("(max-width: 960px)").matches;

  function handleSidebarToggle() {
    if (window.matchMedia("(max-width: 960px)").matches) {
      setNavOpen((open) => !open);
      return;
    }

    setSidebarCollapsed((collapsed) => !collapsed);
  }

  const currentSection = useMemo(() => {
    if (location.pathname === "/" || location.pathname === "/dashboard") {
      return {
        eyebrow: "Vue generale",
        title: "Tableau de bord",
        description: "Indicateurs globaux, points de vigilance et acces rapides."
      };
    }

    if (location.pathname.startsWith("/reporting")) {
      return {
        eyebrow: "Analyse",
        title: "Reporting",
        description: "Lecture financiere et operationnelle des phases."
      };
    }

    const activeNavigationItem = navigationItems.find((item) => location.pathname.startsWith(item.to));

    return {
      eyebrow: "Module",
      title: activeNavigationItem?.label ?? "ZenTask",
      description: activeNavigationItem?.caption ?? "Gestion et consultation des donnees."
    };
  }, [location.pathname]);

  return (
    <div className={classNames("shell", sidebarCollapsed && "shell-collapsed")}>
      <button
        aria-label="Fermer la navigation"
        className={navOpen ? "shell-overlay shell-overlay-open" : "shell-overlay"}
        onClick={() => setNavOpen(false)}
        type="button"
      />

      <aside
        className={classNames(
          "shell-sidebar",
          navOpen && "shell-sidebar-open",
          sidebarCollapsed && "shell-sidebar-collapsed"
        )}
      >
        <div className="brand-block">
          <div className="brand-orb" />
          <div>
            <strong>ZenTask</strong>
            <span>plateforme de gestion projet</span>
          </div>
        </div>

        <nav className="nav-list">
          {navigationItems
            .filter((item) => hasAnyRole(item.roles))
            .map((item) => {
              const Icon = item.icon;
              return (
                <NavLink
                  key={item.to}
                  className={({ isActive }) => (isActive ? "nav-link nav-link-active" : "nav-link")}
                  onClick={() => setNavOpen(false)}
                  to={item.to}
                >
                  <div className="icon-badge">
                    <Icon size={17} />
                  </div>
                  <div>
                    <strong>{item.label}</strong>
                    <span>{item.caption}</span>
                  </div>
                </NavLink>
              );
            })}
        </nav>

        <div className="sidebar-footer">
          <span>Session</span>
          <strong>{session?.login}</strong>
          <small>{formatRole(session?.profil)}</small>
        </div>
      </aside>

      <div className="shell-main">
        <header className="topbar">
          <div className="topbar-left">
            <button className="button button-ghost button-icon shell-toggle" onClick={handleSidebarToggle} type="button">
              {isMobileViewport ? (
                navOpen ? <PanelLeftClose size={18} /> : <Menu size={18} />
              ) : sidebarCollapsed ? (
                <Menu size={18} />
              ) : (
                <PanelLeftClose size={18} />
              )}
            </button>

            <div className="topbar-context">
              <span className="eyebrow">{currentSection.eyebrow}</span>
              <strong>{currentSection.title}</strong>
              <small>{currentSection.description}</small>
            </div>
          </div>

          <div className="topbar-right">
            <div className="profile-chip">
              <span>{session?.login?.slice(0, 1).toUpperCase()}</span>
              <div>
                <strong>{session?.login}</strong>
                <small>{formatRole(session?.profil)}</small>
              </div>
            </div>
            <button className="button button-ghost" onClick={logout} type="button">
              <LogOut size={16} />
              Se deconnecter
            </button>
          </div>
        </header>

        <main className="shell-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
