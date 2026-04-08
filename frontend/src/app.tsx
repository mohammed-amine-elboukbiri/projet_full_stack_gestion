import { QueryClientProvider } from "@tanstack/react-query";
import { Navigate, Route, BrowserRouter as Router, Routes } from "react-router-dom";
import { AuthProvider, LoginPage, RequireAuth } from "./auth";
import { DashboardPage } from "./dashboard";
import { queryClient } from "./lib";
import { AppShell } from "./layout";
import { ReportingPage } from "./reporting";
import { resourceConfigMap, resourceConfigs } from "./resource-config";
import { ResourcePage } from "./resource-page";

function ResourceRoute({ route }: { route: string }) {
  const config = resourceConfigMap[route];

  if (!config) {
    return <Navigate to="/dashboard" replace />;
  }

  return <ResourcePage config={config} />;
}

export function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route element={<RequireAuth />}>
              <Route element={<AppShell />}>
                <Route index element={<Navigate replace to="/dashboard" />} />
                <Route path="/dashboard" element={<DashboardPage />} />
                {resourceConfigs.map((resource) => (
                  <Route
                    key={resource.route}
                    path={resource.route}
                    element={<ResourceRoute route={resource.route} />}
                  />
                ))}
                <Route path="/reporting" element={<ReportingPage />} />
              </Route>
            </Route>
            <Route path="*" element={<Navigate replace to="/dashboard" />} />
          </Routes>
        </AuthProvider>
      </Router>
    </QueryClientProvider>
  );
}
