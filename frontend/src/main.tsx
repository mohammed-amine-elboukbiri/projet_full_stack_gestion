import React from "react";
import ReactDOM from "react-dom/client";
import { App } from "./app";
import "./styles.css";

const shouldRedirectToCanonicalHost =
  window.location.hostname === "127.0.0.1" && window.location.port === "5173";

if (shouldRedirectToCanonicalHost) {
  const canonicalUrl = new URL(window.location.href);
  canonicalUrl.hostname = "localhost";
  window.location.replace(canonicalUrl.toString());
}

if (!shouldRedirectToCanonicalHost) {
  ReactDOM.createRoot(document.getElementById("root")!).render(
    <React.StrictMode>
      <App />
    </React.StrictMode>
  );
}
