import type { LucideIcon } from "lucide-react";
import type { ReactNode } from "react";

export type Role =
  | "ADMINISTRATEUR"
  | "DIRECTEUR"
  | "CHEF_PROJET"
  | "COMPTABLE"
  | "SECRETAIRE"
  | string;

export interface Session {
  token: string;
  type: string;
  employeId: number;
  login: string;
  profil: Role;
}

export interface LoginPayload {
  login: string;
  password: string;
}

export interface DashboardDto {
  totalProjets: number;
  totalPhases: number;
  totalEmployes: number;
  totalOrganismes: number;
  phasesTerminees: number;
  phasesNonFacturees: number;
  phasesFactureesNonPayees: number;
  phasesPayees: number;
}

export interface PhaseReportingDto {
  id: number;
  code: string;
  libelle: string;
  dateDebut: string;
  dateFin: string;
  montant: number;
  etatRealisation: boolean;
  etatFacturation: boolean;
  etatPaiement: boolean;
  projetId: number;
  projetNom: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface OptionSource {
  endpoint: string;
  queryKey: string;
  valueKey: string;
  label: (item: Record<string, unknown>) => string;
}

export interface SelectOption {
  label: string;
  value: string;
}

export type FieldType =
  | "text"
  | "textarea"
  | "email"
  | "password"
  | "number"
  | "date"
  | "select"
  | "boolean";

export interface FieldDefinition {
  name: string;
  label: string;
  type: FieldType;
  required?: boolean;
  placeholder?: string;
  valueType?: "string" | "number";
  source?: OptionSource;
  options?: SelectOption[];
  helperText?: string;
}

export interface SearchDefinition {
  key: string;
  label: string;
  payloadKey: string;
  type: "text" | "number" | "select";
  source?: OptionSource;
  valueType?: "string" | "number";
}

export interface ColumnDefinition {
  key: string;
  label: string;
  kind?: "text" | "currency" | "date" | "boolean" | "role";
  trueLabel?: string;
  falseLabel?: string;
  getValue?: (item: Record<string, unknown>) => unknown;
}

export interface ResourceConfig {
  key: string;
  route: string;
  title: string;
  subtitle: string;
  description: string;
  endpoint: string;
  paginated?: boolean;
  searchEndpoint?: string;
  columns: ColumnDefinition[];
  formFields: FieldDefinition[];
  initialValues: Record<string, string | boolean>;
  searchOptions?: SearchDefinition[];
  icon: LucideIcon;
  accent: string;
  emptyMessage: string;
  visibleRoles?: Role[];
  createRoles?: Role[];
  editRoles?: Role[];
  deleteRoles?: Role[];
  pageSize?: number;
  primaryKey: (item: Record<string, unknown>) => string | number;
  updatePath?: (item: Record<string, unknown>) => string;
  deletePath?: (item: Record<string, unknown>) => string;
}

export interface NavItem {
  label: string;
  to: string;
  icon: LucideIcon;
  roles?: Role[];
  caption?: string;
}

export interface QueryResultBundle {
  items: Record<string, unknown>[];
  totalElements: number;
  totalPages: number;
}

export interface SectionMetric {
  label: string;
  value: ReactNode;
  tone?: "teal" | "copper" | "slate";
}
