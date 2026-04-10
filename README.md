<img width="600" height="900" alt="logo" src="https://github.com/user-attachments/assets/eb1ba566-8ef0-4251-b78d-f1a81a09188b" />



# 📋 ZENTASK — Système de Suivi de Projets

> Application web full-stack de gestion et suivi de projets pour sociétés de services, développée avec **Spring Boot** et **React**.

---

## 📌 Description

**Zentask** est une application de suivi de projets destinée aux sociétés de services (développement logiciel, études techniques, intégration, assistance). Elle centralise l'ensemble des informations liées aux projets afin d'assurer une visibilité complète sur leur évolution, d'améliorer la coordination entre les intervenants et de sécuriser les opérations de mise à jour selon les responsabilités de chaque utilisateur.

---
## ❓ Problématique : 

Les sociétés de services peinent à gérer efficacement leurs projets faute d'un outil commun, ce qui entraîne un manque de coordination entre les équipes. Les informations sont dispersées et le suivi financier reste souvent manuel et peu fiable. Face à ces difficultés, il est essentiel de mettre en place une solution centralisée, sécurisée et adaptée aux rôles de chaque utilisateur. C'est dans ce contexte que s'inscrit le développement de cette application de suivi de projets.

---

## 🎯 Objectifs

- Centraliser la gestion des projets, phases et livrables au sein d'une interface unifiée
- Contrôler les accès et les actions selon les rôles fonctionnels (directeur, chef de projet, comptable, secrétaire, etc.)
- Offrir un suivi financier complet : facturation, paiement et reporting par période
- Conserver un historique fiable et exploitable des projets en cours et clôturés

---

## 🏗️ Architecture du projet

```
┌─────────────────────────────────────────────────────────┐
│                      CLIENT (Browser)                   │
│                  React + Axios + Redux                  │
└─────────────────────────┬───────────────────────────────┘
                          │ HTTP/REST (JSON)
                          ▼
┌─────────────────────────────────────────────────────────┐
│               Backend — Spring Boot API                  │
│   Controllers → Services → Repositories (JPA)           │
│              Spring Security (JWT)                       │
└─────────────────────────┬───────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│              Base de données — MySQL                     │
│          (Projets, Phases, Livrables, Employés)          │
└─────────────────────────────────────────────────────────┘

          Toute l'infrastructure est conteneurisée via Docker
```

### Backend (Spring Boot)
- API REST exposant les ressources projets, phases, livrables, utilisateurs
- Sécurisation des endpoints via JWT et contrôle des rôles
- Gestion de la persistance avec Spring Data JPA / Hibernate

### Frontend (React)
- Interface SPA avec routage protégé selon le rôle
- Tableaux de bord interactifs, formulaires de saisie et recherche avancée
- Consommation de l'API REST via Axios

### Base de données
- MySQL — modèle relationnel complet (Projet, Phase, Livrable, Employé, Organisme client)

### Infrastructure (Docker)
- Conteneurisation du backend, du frontend et de la base de données
- Orchestration via `docker-compose`

---

## 🛠️ Technologies utilisées

### Backend
- Java 21
- Spring Boot 4.0.3
- Spring Security + JWT
- Spring Data JPA / Hibernate
- Maven

### Frontend
- React 18
- React Router DOM
- Axios
- Redux Toolkit
- Tailwind CSS / Bootstrap

### Base de données
- MySQL 8

### DevOps
- Docker & Docker Compose
- Git / GitHub
- PostMan

---

## 📁 Structure du projet

```
projet-suivi/
├── backend/
│   ├── src/
│   │   ├── main/java/com/projettrack/
│   │   │   ├── controllers/
│   │   │   ├── services/
│   │   │   ├── repositories/
│   │   │   ├── models/
│   │   │   └── security/
│   │   └── resources/
│   │       └── application.properties
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   └── store/
│   └── package.json
│
└── docker-compose.yml
```

---

## ⚙️ Installation et exécution

### Prérequis
- Java 21+
- Node.js 18+
- Maven 3.8+
- Docker & Docker Compose (optionnel)
- MySQL 8 (si sans Docker)

### Cloner le projet

```bash
git clone https://github.com/votre-username/projet-suivi.git
cd projet-suivi
```

### 🐳 Lancer avec Docker (recommandé)

```bash
docker-compose up --build
```

> L'application sera disponible sur `http://localhost:5173`  
> L'API backend sur `http://localhost:8081`

---

### 🔧 Lancer sans Docker

#### Backend

```bash
cd backend
mvn spring-boot:run
```

#### Frontend

```bash
cd frontend
npm install
npm start
```

---

## 🔐 Sécurité

| Aspect | Détail |
|---|---|
| **Authentification** | JWT (JSON Web Token) — login avec matricule et mot de passe |
| **Gestion des rôles** | Directeur, Chef de Projet, Comptable, Secrétaire, Administrateur, Employé |
| **Protection des routes** | Routes frontend et endpoints API protégés selon le profil utilisateur |
| **Mots de passe** | Hashage BCrypt |

---

## ✨ Fonctionnalités principales

### 👤 Gestion des utilisateurs (Administrateur)
- Création, modification et suppression des comptes
- Affectation des rôles et profils fonctionnels

### 📂 Gestion des projets
- Création et mise à jour des projets (secrétaire : infos administratives / directeur : toutes les infos)
- Association à un organisme client et affectation d'un chef de projet
- Ajout de documents techniques liés au projet

### 📊 Gestion des phases (Chef de Projet)
- Décomposition du projet en phases planifiées
- Affectation des employés par phase
- Gestion des états : réalisation, facturation, paiement
- Calcul automatique du montant de la phase (% du montant total)

### 📎 Gestion des livrables
- Ajout de livrables par phase avec fichier associé
- Traçabilité documentaire complète

### 💰 Suivi financier (Comptable)
- Facturation des phases terminées
- Mise à jour des états de paiement
- Recherche par période : phases non facturées, facturées non payées, payées

---

## 📈 Dashboard / Reporting

- Vue d'ensemble des projets en cours et clôturés
- Indicateurs financiers : montants engagés, facturés, encaissés
- Liste des phases par état (à facturer, en attente de paiement, soldées)
- Historique des collaborations par organisme client

---

## 🎬 Vidéo de démonstration

> 📹


https://github.com/user-attachments/assets/49651bd7-a6e6-4d83-ab3d-a0e0612af6ef



La vidéo couvre :
- Connexion et gestion des rôles
- Création et suivi d'un projet complet
- Gestion des phases et livrables
- Suivi financier (comptable)
- Dashboard et reporting

---

## 📖 TEST Profil et Login : 

https://github.com/user-attachments/assets/6c8f2faf-64b1-4dc5-b533-d9a2b8f30e03

## TEST Organisme

https://github.com/user-attachments/assets/c9b67723-49f1-434b-9554-29f93bb2a9b0

## TEST Employe

https://github.com/user-attachments/assets/d64dffeb-67d0-4d24-b64e-406b47fc3cb9

## TEST Projet

https://github.com/user-attachments/assets/7e381bfe-72b0-4058-9b69-caae120f0574

## TEST Phase 

https://github.com/user-attachments/assets/bbb186d0-dc7b-4dc4-8ff3-f82c53478ac0

## TEST Livrable

https://github.com/user-attachments/assets/8ebe1bf3-ef6a-445a-8e03-b2e6a588c131

## TEST Facture

https://github.com/user-attachments/assets/00208998-a6c5-4dce-8e89-2c7d5ead2675

## TEST Document

https://github.com/user-attachments/assets/6c7e3e5d-2e3f-49bd-88ca-4885b9f41963

## TEST Affectation

https://github.com/user-attachments/assets/7a6374ca-cd39-4f56-84bd-6f0b5b4cb915


## 🧪 Tests

- Tests unitaires : JUnit 5 + Mockito (services & repositories)
- Tests d'intégration : Spring Boot Test
- Tests API : Postman Collection disponible dans `/docs/postman/`

---

## 🐋 Conteneurisation

| Fichier | Rôle |
|---|---|
| `backend/Dockerfile` | Image du serveur Spring Boot |
| `frontend/Dockerfile` | Image de l'application React (Nginx) |
| `docker-compose.yml` | Orchestration des 3 services (backend, frontend, mysql) |

---

## 👨‍💻 Auteurs

| Nom | Profile |
|---|---|
| **Othmane EL MATLINI** | https://github.com/OTHMANE-ELM |
| **Amine BAANI** | https://github.com/baaniamine |
| **Mohammed Amine EL BOUKBIRI** | https://github.com/mohammed-amine-elboukbiri |

---

















