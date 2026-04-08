import {
  BadgeCheck,
  Banknote,
  BriefcaseBusiness,
  Building2,
  CircleUserRound,
  ClipboardList,
  FileAxis3d,
  FileText,
  Layers3,
  ShieldCheck
} from "lucide-react";
import type { NavItem, ResourceConfig } from "./types";

const profileSource = {
  endpoint: "/api/profils",
  queryKey: "profiles",
  valueKey: "id",
  label: (item: Record<string, unknown>) => `${item.code ?? ""} - ${item.libelle ?? ""}`
};

const organismeSource = {
  endpoint: "/api/organismes",
  queryKey: "organismes",
  valueKey: "id",
  label: (item: Record<string, unknown>) => `${item.code ?? ""} - ${item.nom ?? ""}`
};

const employeSource = {
  endpoint: "/api/employes",
  queryKey: "employes",
  valueKey: "id",
  label: (item: Record<string, unknown>) =>
    `${item.matricule ?? ""} - ${item.nom ?? ""} ${item.prenom ?? ""}`
};

const projetSource = {
  endpoint: "/api/projets",
  queryKey: "projets",
  valueKey: "id",
  label: (item: Record<string, unknown>) => `${item.code ?? ""} - ${item.nom ?? ""}`
};

const phaseSource = {
  endpoint: "/api/phases",
  queryKey: "phases",
  valueKey: "id",
  label: (item: Record<string, unknown>) => `${item.code ?? ""} - ${item.libelle ?? ""}`
};

export const resourceConfigs: ResourceConfig[] = [
  {
    key: "profils",
    route: "/profils",
    title: "Profils",
    subtitle: "Referentiel des roles applicatifs",
    description: "Codes de roles consommes par l authentification et la securite backend.",
    endpoint: "/api/profils",
    icon: ShieldCheck,
    accent: "teal",
    emptyMessage: "Aucun profil charge.",
    visibleRoles: ["ADMINISTRATEUR"],
    createRoles: ["ADMINISTRATEUR"],
    editRoles: ["ADMINISTRATEUR"],
    deleteRoles: ["ADMINISTRATEUR"],
    columns: [
      { key: "code", label: "Code" },
      { key: "libelle", label: "Libelle" }
    ],
    formFields: [
      { name: "code", label: "Code", type: "text", required: true },
      { name: "libelle", label: "Libelle", type: "text", required: true }
    ],
    initialValues: {
      code: "",
      libelle: ""
    },
    primaryKey: (item) => String(item.id ?? "")
  },
  {
    key: "organismes",
    route: "/organismes",
    title: "Organismes",
    subtitle: "Clients, partenaires et maitres d ouvrage",
    description: "Referentiel des organismes avec coordonnees, contact principal et site web.",
    endpoint: "/api/organismes",
    searchEndpoint: "/api/organismes/search",
    paginated: true,
    icon: Building2,
    accent: "copper",
    emptyMessage: "Aucun organisme disponible.",
    visibleRoles: ["SECRETAIRE", "DIRECTEUR", "ADMINISTRATEUR"],
    createRoles: ["SECRETAIRE", "ADMINISTRATEUR"],
    editRoles: ["SECRETAIRE", "DIRECTEUR", "ADMINISTRATEUR"],
    deleteRoles: ["SECRETAIRE", "DIRECTEUR", "ADMINISTRATEUR"],
    columns: [
      { key: "code", label: "Code" },
      { key: "nom", label: "Nom" },
      { key: "nomContact", label: "Contact" },
      { key: "telephone", label: "Telephone" },
      { key: "emailContact", label: "Email contact" }
    ],
    searchOptions: [
      { key: "code", label: "Code", payloadKey: "code", type: "text" },
      { key: "nom", label: "Nom", payloadKey: "nom", type: "text" },
      { key: "nomContact", label: "Nom contact", payloadKey: "nomContact", type: "text" },
      { key: "emailContact", label: "Email contact", payloadKey: "emailContact", type: "text" }
    ],
    formFields: [
      { name: "code", label: "Code", type: "text", required: true },
      { name: "nom", label: "Nom", type: "text", required: true },
      { name: "adresse", label: "Adresse", type: "textarea" },
      { name: "telephone", label: "Telephone", type: "text" },
      { name: "nomContact", label: "Nom du contact", type: "text" },
      { name: "emailContact", label: "Email du contact", type: "email" },
      { name: "siteWeb", label: "Site web", type: "text" }
    ],
    initialValues: {
      code: "",
      nom: "",
      adresse: "",
      telephone: "",
      nomContact: "",
      emailContact: "",
      siteWeb: ""
    },
    primaryKey: (item) => String(item.id ?? "")
  },
  {
    key: "employes",
    route: "/employes",
    title: "Employes",
    subtitle: "Annuaire operationnel",
    description: "Comptes applicatifs, matricules, contacts et rattachement aux profils de securite.",
    endpoint: "/api/employes",
    searchEndpoint: "/api/employes/search",
    paginated: true,
    icon: CircleUserRound,
    accent: "slate",
    emptyMessage: "Aucun employe charge.",
    visibleRoles: ["ADMINISTRATEUR"],
    createRoles: ["ADMINISTRATEUR"],
    editRoles: ["ADMINISTRATEUR"],
    deleteRoles: ["ADMINISTRATEUR"],
    columns: [
      { key: "matricule", label: "Matricule" },
      { key: "nom", label: "Nom" },
      { key: "prenom", label: "Prenom" },
      { key: "login", label: "Login" },
      { key: "profilCode", label: "Profil", kind: "role" }
    ],
    searchOptions: [
      { key: "matricule", label: "Matricule", payloadKey: "matricule", type: "text" },
      { key: "nom", label: "Nom", payloadKey: "nom", type: "text" },
      { key: "prenom", label: "Prenom", payloadKey: "prenom", type: "text" },
      { key: "email", label: "Email", payloadKey: "email", type: "text" },
      { key: "login", label: "Login", payloadKey: "login", type: "text" },
      {
        key: "profilId",
        label: "Profil",
        payloadKey: "profilId",
        type: "select",
        source: profileSource,
        valueType: "number"
      }
    ],
    formFields: [
      { name: "matricule", label: "Matricule", type: "text", required: true },
      { name: "nom", label: "Nom", type: "text", required: true },
      { name: "prenom", label: "Prenom", type: "text", required: true },
      { name: "telephone", label: "Telephone", type: "text" },
      { name: "email", label: "Email", type: "email", required: true },
      { name: "login", label: "Login", type: "text", required: true },
      {
        name: "password",
        label: "Mot de passe",
        type: "password",
        required: true,
        helperText: "Le backend re-encode le mot de passe a chaque creation ou mise a jour."
      },
      {
        name: "profilId",
        label: "Profil",
        type: "select",
        required: true,
        valueType: "number",
        source: profileSource
      }
    ],
    initialValues: {
      matricule: "",
      nom: "",
      prenom: "",
      telephone: "",
      email: "",
      login: "",
      password: "",
      profilId: ""
    },
    primaryKey: (item) => String(item.id ?? "")
  },
  {
    key: "projets",
    route: "/projets",
    title: "Projets",
    subtitle: "Portefeuille, budget et pilotage",
    description: "Vue principale des projets avec organisme client, chef de projet et budget engage.",
    endpoint: "/api/projets",
    searchEndpoint: "/api/projets/search",
    paginated: true,
    icon: BriefcaseBusiness,
    accent: "teal",
    emptyMessage: "Aucun projet trouve.",
    visibleRoles: ["SECRETAIRE", "DIRECTEUR", "CHEF_PROJET", "COMPTABLE", "ADMINISTRATEUR"],
    createRoles: ["SECRETAIRE", "DIRECTEUR", "ADMINISTRATEUR"],
    editRoles: ["SECRETAIRE", "DIRECTEUR", "CHEF_PROJET", "ADMINISTRATEUR"],
    deleteRoles: ["DIRECTEUR", "ADMINISTRATEUR"],
    columns: [
      { key: "code", label: "Code" },
      { key: "nom", label: "Projet" },
      { key: "organismeNom", label: "Organisme" },
      { key: "chefProjetNomComplet", label: "Chef de projet" },
      { key: "dateFin", label: "Date fin", kind: "date" },
      { key: "montant", label: "Budget", kind: "currency" }
    ],
    searchOptions: [
      { key: "code", label: "Code", payloadKey: "code", type: "text" },
      { key: "nom", label: "Nom", payloadKey: "nom", type: "text" },
      {
        key: "organismeId",
        label: "Organisme",
        payloadKey: "organismeId",
        type: "select",
        source: organismeSource,
        valueType: "number"
      },
      {
        key: "chefProjetId",
        label: "Chef de projet",
        payloadKey: "chefProjetId",
        type: "select",
        source: employeSource,
        valueType: "number"
      }
    ],
    formFields: [
      { name: "code", label: "Code projet", type: "text", required: true },
      { name: "nom", label: "Nom", type: "text", required: true },
      { name: "description", label: "Description", type: "textarea" },
      { name: "dateDebut", label: "Date de debut", type: "date", required: true },
      { name: "dateFin", label: "Date de fin", type: "date", required: true },
      { name: "montant", label: "Montant", type: "number", required: true, valueType: "number" },
      {
        name: "organismeId",
        label: "Organisme",
        type: "select",
        required: true,
        valueType: "number",
        source: organismeSource
      },
      {
        name: "chefProjetId",
        label: "Chef de projet",
        type: "select",
        required: true,
        valueType: "number",
        source: employeSource
      }
    ],
    initialValues: {
      code: "",
      nom: "",
      description: "",
      dateDebut: "",
      dateFin: "",
      montant: "",
      organismeId: "",
      chefProjetId: ""
    },
    primaryKey: (item) => String(item.id ?? "")
  },
  {
    key: "phases",
    route: "/phases",
    title: "Phases",
    subtitle: "Decoupage d execution et etats metier",
    description: "Chaque phase garde ses dates, son montant et les indicateurs de realisation, facturation et paiement.",
    endpoint: "/api/phases",
    paginated: true,
    icon: Layers3,
    accent: "copper",
    emptyMessage: "Aucune phase disponible.",
    visibleRoles: ["CHEF_PROJET", "COMPTABLE", "DIRECTEUR", "ADMINISTRATEUR"],
    createRoles: ["CHEF_PROJET", "ADMINISTRATEUR"],
    editRoles: ["CHEF_PROJET", "DIRECTEUR", "ADMINISTRATEUR"],
    deleteRoles: ["CHEF_PROJET", "DIRECTEUR", "ADMINISTRATEUR"],
    columns: [
      { key: "code", label: "Code" },
      { key: "libelle", label: "Libelle" },
      { key: "projetNom", label: "Projet" },
      { key: "montant", label: "Montant", kind: "currency" },
      { key: "etatRealisation", label: "Realisee", kind: "boolean", trueLabel: "Oui", falseLabel: "Non" },
      { key: "etatFacturation", label: "Facturee", kind: "boolean", trueLabel: "Oui", falseLabel: "Non" }
    ],
    formFields: [
      { name: "code", label: "Code phase", type: "text", required: true },
      { name: "libelle", label: "Libelle", type: "text", required: true },
      { name: "description", label: "Description", type: "textarea" },
      { name: "dateDebut", label: "Date de debut", type: "date", required: true },
      { name: "dateFin", label: "Date de fin", type: "date", required: true },
      { name: "montant", label: "Montant", type: "number", required: true, valueType: "number" },
      { name: "etatRealisation", label: "Realisee", type: "boolean" },
      { name: "etatFacturation", label: "Facturee", type: "boolean" },
      { name: "etatPaiement", label: "Payee", type: "boolean" },
      {
        name: "projetId",
        label: "Projet",
        type: "select",
        required: true,
        valueType: "number",
        source: projetSource
      }
    ],
    initialValues: {
      code: "",
      libelle: "",
      description: "",
      dateDebut: "",
      dateFin: "",
      montant: "",
      etatRealisation: false,
      etatFacturation: false,
      etatPaiement: false,
      projetId: ""
    },
    primaryKey: (item) => String(item.id ?? "")
  },
  {
    key: "livrables",
    route: "/livrables",
    title: "Livrables",
    subtitle: "Sorties attendues par phase",
    description: "Documents livrables lies aux phases. Le backend attend pour l instant un simple chemin de fichier.",
    endpoint: "/api/livrables",
    icon: FileAxis3d,
    accent: "slate",
    emptyMessage: "Aucun livrable enregistre.",
    visibleRoles: ["CHEF_PROJET", "DIRECTEUR", "ADMINISTRATEUR"],
    createRoles: ["CHEF_PROJET", "DIRECTEUR", "ADMINISTRATEUR"],
    editRoles: ["CHEF_PROJET", "DIRECTEUR", "ADMINISTRATEUR"],
    deleteRoles: ["CHEF_PROJET", "DIRECTEUR", "ADMINISTRATEUR"],
    columns: [
      { key: "code", label: "Code" },
      { key: "libelle", label: "Libelle" },
      { key: "phaseLibelle", label: "Phase" },
      { key: "chemin", label: "Chemin" }
    ],
    formFields: [
      { name: "code", label: "Code livrable", type: "text", required: true },
      { name: "libelle", label: "Libelle", type: "text", required: true },
      { name: "description", label: "Description", type: "textarea" },
      { name: "chemin", label: "Chemin fichier", type: "text", required: true },
      {
        name: "phaseId",
        label: "Phase",
        type: "select",
        required: true,
        valueType: "number",
        source: phaseSource
      }
    ],
    initialValues: {
      code: "",
      libelle: "",
      description: "",
      chemin: "",
      phaseId: ""
    },
    primaryKey: (item) => String(item.id ?? "")
  },
  {
    key: "documents",
    route: "/documents",
    title: "Documents",
    subtitle: "Pieces attachees aux projets",
    description: "Registre documentaire rattache directement aux projets.",
    endpoint: "/api/documents",
    icon: FileText,
    accent: "teal",
    emptyMessage: "Aucun document trouve.",
    visibleRoles: ["SECRETAIRE", "CHEF_PROJET", "DIRECTEUR", "COMPTABLE", "ADMINISTRATEUR"],
    createRoles: ["SECRETAIRE", "CHEF_PROJET", "DIRECTEUR", "ADMINISTRATEUR"],
    editRoles: ["SECRETAIRE", "CHEF_PROJET", "DIRECTEUR", "ADMINISTRATEUR"],
    deleteRoles: ["SECRETAIRE", "CHEF_PROJET", "DIRECTEUR", "ADMINISTRATEUR"],
    columns: [
      { key: "code", label: "Code" },
      { key: "libelle", label: "Libelle" },
      { key: "projetNom", label: "Projet" },
      { key: "chemin", label: "Chemin" }
    ],
    formFields: [
      { name: "code", label: "Code document", type: "text", required: true },
      { name: "libelle", label: "Libelle", type: "text", required: true },
      { name: "description", label: "Description", type: "textarea" },
      { name: "chemin", label: "Chemin fichier", type: "text", required: true },
      {
        name: "projetId",
        label: "Projet",
        type: "select",
        required: true,
        valueType: "number",
        source: projetSource
      }
    ],
    initialValues: {
      code: "",
      libelle: "",
      description: "",
      chemin: "",
      projetId: ""
    },
    primaryKey: (item) => String(item.id ?? "")
  },
  {
    key: "affectations",
    route: "/affectations",
    title: "Affectations",
    subtitle: "Ressources positionnees sur les phases",
    description: "Association employe-phase avec dates de mobilisation.",
    endpoint: "/api/affectations",
    icon: ClipboardList,
    accent: "copper",
    emptyMessage: "Aucune affectation trouvee.",
    visibleRoles: ["CHEF_PROJET", "ADMINISTRATEUR"],
    createRoles: ["CHEF_PROJET", "ADMINISTRATEUR"],
    editRoles: ["CHEF_PROJET", "ADMINISTRATEUR"],
    deleteRoles: ["CHEF_PROJET", "ADMINISTRATEUR"],
    columns: [
      { key: "employeNomComplet", label: "Employe" },
      { key: "phaseLibelle", label: "Phase" },
      { key: "dateDebut", label: "Debut", kind: "date" },
      { key: "dateFin", label: "Fin", kind: "date" }
    ],
    formFields: [
      {
        name: "employeId",
        label: "Employe",
        type: "select",
        required: true,
        valueType: "number",
        source: employeSource
      },
      {
        name: "phaseId",
        label: "Phase",
        type: "select",
        required: true,
        valueType: "number",
        source: phaseSource
      },
      { name: "dateDebut", label: "Date de debut", type: "date", required: true },
      { name: "dateFin", label: "Date de fin", type: "date", required: true }
    ],
    initialValues: {
      employeId: "",
      phaseId: "",
      dateDebut: "",
      dateFin: ""
    },
    primaryKey: (item) => `${item.employeId ?? ""}-${item.phaseId ?? ""}`,
    updatePath: (item) => `/api/affectations/${item.employeId}/${item.phaseId}`,
    deletePath: (item) => `/api/affectations/${item.employeId}/${item.phaseId}`
  },
  {
    key: "factures",
    route: "/factures",
    title: "Factures",
    subtitle: "Suivi de facturation des phases",
    description: "Creation et suivi des factures liees aux phases terminees.",
    endpoint: "/api/factures",
    paginated: true,
    icon: Banknote,
    accent: "teal",
    emptyMessage: "Aucune facture a afficher.",
    visibleRoles: ["COMPTABLE", "DIRECTEUR", "ADMINISTRATEUR"],
    createRoles: ["COMPTABLE", "ADMINISTRATEUR"],
    editRoles: ["COMPTABLE", "ADMINISTRATEUR"],
    deleteRoles: ["COMPTABLE", "ADMINISTRATEUR"],
    columns: [
      { key: "code", label: "Code" },
      { key: "dateFacture", label: "Date facture", kind: "date" },
      { key: "phaseLibelle", label: "Phase" },
      { key: "etatFacturation", label: "Facturee", kind: "boolean", trueLabel: "Oui", falseLabel: "Non" },
      { key: "etatPaiement", label: "Payee", kind: "boolean", trueLabel: "Oui", falseLabel: "Non" }
    ],
    formFields: [
      { name: "code", label: "Code facture", type: "text", required: true },
      { name: "dateFacture", label: "Date facture", type: "date", required: true },
      {
        name: "phaseId",
        label: "Phase",
        type: "select",
        required: true,
        valueType: "number",
        source: phaseSource
      }
    ],
    initialValues: {
      code: "",
      dateFacture: "",
      phaseId: ""
    },
    primaryKey: (item) => String(item.id ?? "")
  }
];

export const resourceConfigMap = Object.fromEntries(
  resourceConfigs.map((resource) => [resource.route, resource])
) as Record<string, ResourceConfig>;

export const navigationItems: NavItem[] = [
  {
    label: "Dashboard",
    to: "/dashboard",
    icon: BadgeCheck,
    caption: "Pilotage"
  },
  ...resourceConfigs.map((resource) => ({
    label: resource.title,
    to: resource.route,
    icon: resource.icon,
    roles: resource.visibleRoles,
    caption: resource.subtitle
  })),
  {
    label: "Reporting",
    to: "/reporting",
    icon: ClipboardList,
    roles: ["COMPTABLE", "DIRECTEUR", "ADMINISTRATEUR"],
    caption: "Analyse"
  }
];

