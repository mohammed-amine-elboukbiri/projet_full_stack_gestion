import { QueryClient } from "@tanstack/react-query";
import axios from "axios";
import type { Session } from "./types";

const SESSION_KEY = "zentask.session.v2";
export const SESSION_EXPIRED_EVENT = "zentask:session-expired";

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 60_000,
      refetchOnWindowFocus: false,
      retry: 1
    }
  }
});

export const sessionStore = {
  read(): Session | null {
    const raw = window.localStorage.getItem(SESSION_KEY);
    if (!raw) {
      return null;
    }

    try {
      const parsed = JSON.parse(raw) as unknown;
      if (!isSession(parsed)) {
        window.localStorage.removeItem(SESSION_KEY);
        return null;
      }

      return parsed;
    } catch {
      window.localStorage.removeItem(SESSION_KEY);
      return null;
    }
  },
  write(session: Session) {
    window.localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  },
  clear() {
    window.localStorage.removeItem(SESSION_KEY);
  }
};

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8081",
  headers: {
    "Content-Type": "application/json"
  }
});

apiClient.interceptors.request.use((config) => {
  const session = sessionStore.read();

  if (session?.token) {
    config.headers = config.headers ?? {};
    (config.headers as Record<string, string>).Authorization = `Bearer ${session.token}`;
  }

  return config;
});

apiClient.interceptors.response.use((response) => response, (error) => Promise.reject(error));

export function toFriendlyError(error: unknown): string {
  if (axios.isAxiosError(error)) {
    if (!error.response) {
      return "Aucune reponse du backend. Ouvrez l application via http://localhost:5173 et verifiez que l API sur le port 8081 a bien ete redemarree.";
    }

    const responseMessage =
      typeof error.response?.data?.message === "string"
        ? error.response.data.message
        : typeof error.response?.data?.error === "string"
          ? error.response.data.error
          : null;

    if (responseMessage) {
      return responseMessage;
    }

    if (error.response?.status === 401) {
      return "Session invalide, identifiants incorrects ou acces non autorise.";
    }

    if (error.response?.status === 403) {
      return "Acces refuse sur cette action.";
    }
  }

  return "Une erreur inattendue est survenue.";
}

export function isForbiddenError(error: unknown): boolean {
  return axios.isAxiosError(error) && error.response?.status === 403;
}

export function formatCurrency(value: unknown): string {
  if (typeof value !== "number") {
    return "--";
  }

  return new Intl.NumberFormat("fr-MA", {
    style: "currency",
    currency: "MAD",
    maximumFractionDigits: 0
  }).format(value);
}

export function formatDate(value: unknown): string {
  if (typeof value !== "string" || !value) {
    return "--";
  }

  return new Intl.DateTimeFormat("fr-FR", {
    day: "2-digit",
    month: "short",
    year: "numeric"
  }).format(new Date(value));
}

export function formatRole(role: string | undefined): string {
  if (!role) {
    return "Profil inconnu";
  }

  return role
    .toLowerCase()
    .replace(/_/g, " ")
    .replace(/\b\w/g, (letter: string) => letter.toUpperCase());
}

export function monthRange(reference = new Date()) {
  const start = new Date(reference.getFullYear(), reference.getMonth(), 1);
  const end = new Date(reference.getFullYear(), reference.getMonth() + 1, 0);

  return {
    start: start.toISOString().slice(0, 10),
    end: end.toISOString().slice(0, 10)
  };
}

export function classNames(...values: Array<string | false | null | undefined>) {
  return values.filter(Boolean).join(" ");
}

function isSession(value: unknown): value is Session {
  if (!value || typeof value !== "object") {
    return false;
  }

  const candidate = value as Record<string, unknown>;

  return (
    typeof candidate.token === "string" &&
    typeof candidate.type === "string" &&
    typeof candidate.employeId === "number" &&
    typeof candidate.login === "string" &&
    typeof candidate.profil === "string"
  );
}
