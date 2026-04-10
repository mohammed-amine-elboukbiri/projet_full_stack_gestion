## TEST Profil et Login

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










https://github.com/user-attachments/assets/6c8f2faf-64b1-4dc5-b533-d9a2b8f30e03

## DevOps workflow

Ce depot contient maintenant deux workflows :

- un workflow simple `GitHub -> Jenkins -> Docker -> Kubernetes`
- un workflow complet de type `GitHub -> Jenkins CI -> OWASP -> SonarQube -> Trivy -> Docker -> Jenkins CD -> GitHub GitOps -> ArgoCD -> Kubernetes`

### Fichiers ajoutes

- `Jenkinsfile` : pipeline Jenkins CI
- `.jenkins/Jenkinsfile.cd` : pipeline Jenkins CD
- `.jenkins/update_gitops_images.sh` : mise a jour de la version des images dans le dossier GitOps
- `Dockerfile` : image Docker du backend Spring Boot
- `frontend/Dockerfile` : image Docker du frontend React
- `frontend/nginx.conf` : serveur Nginx avec proxy `/api` vers le backend
- `k8s/` : manifests Kubernetes
- `k8s/gitops/` : manifests suivis par ArgoCD
- `argocd/application.yaml` : application ArgoCD
- `monitoring/README.md` : base pour Prometheus/Grafana

### Fonctionnement

1. Le code est pousse sur GitHub.
2. Le job Jenkins CI recupere le depot.
3. Jenkins CI execute les tests backend et le build frontend.
4. Jenkins CI lance :
   - `OWASP Dependency Check`
   - `SonarQube analysis + quality gate`
   - `Trivy filesystem scan`
5. Jenkins CI construit et pousse les images Docker backend/frontend.
6. Jenkins CI lance un `Trivy image scan`.
7. Jenkins CI declenche le job Jenkins CD.
8. Jenkins CD met a jour `k8s/gitops/kustomization.yaml` avec le nouveau tag.
9. Jenkins CD pousse cette modification sur GitHub.
10. ArgoCD detecte le changement et deploie automatiquement sur Kubernetes.
11. Prometheus et Grafana servent pour le monitoring.

### Prerequis Jenkins

- Installer `Docker`, `kubectl`, `npm` et un JDK 21 sur l agent Jenkins
- Ajouter un credential Jenkins `dockerhub-creds`
- Ajouter un credential Jenkins `github-push-creds`
- Configurer les plugins Jenkins :
  - SonarQube Scanner
  - OWASP Dependency-Check
  - Email Extension
- Installer sur l agent Jenkins :
  - `trivy`
  - `dependency-check.sh`
- Adapter dans [`Jenkinsfile`](/Users/amine/Desktop/Application-Full-Stack-de-Gestion-de-Projets/Jenkinsfile) :
  - `IMAGE_NAMESPACE`
  - `SONARQUBE_ENV`
  - `CD_JOB_NAME`
- Adapter dans [`argocd/application.yaml`](/Users/amine/Desktop/Application-Full-Stack-de-Gestion-de-Projets/argocd/application.yaml) :
  - `repoURL`
  - `targetRevision`

### Secrets Kubernetes

Le fichier `k8s/secret.example.yaml` est un exemple. Remplace les valeurs avant utilisation :

- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `MYSQL_ROOT_PASSWORD`
- `MYSQL_PASSWORD`

### Images Docker

Backend :

```bash
docker build -t your-dockerhub-user/project-management-backend:latest .
```

Frontend :

```bash
docker build -t your-dockerhub-user/project-management-frontend:latest ./frontend
```

### Deploiement Kubernetes manuel

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secret.example.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/mysql.yaml
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml
kubectl apply -f k8s/ingress.yaml
```

### Deploiement GitOps avec ArgoCD

1. Installer ArgoCD dans le cluster
2. Appliquer [`argocd/application.yaml`](/Users/amine/Desktop/Application-Full-Stack-de-Gestion-de-Projets/argocd/application.yaml)
3. Donner a ArgoCD acces au repo GitHub
4. Laisser Jenkins CD mettre a jour `k8s/gitops/kustomization.yaml`

ArgoCD synchronisera ensuite automatiquement le dossier [`k8s/gitops`](/Users/amine/Desktop/Application-Full-Stack-de-Gestion-de-Projets/k8s/gitops).
