# Complete DevOps workflow

Ce projet suit maintenant ce modele :

1. Developer push sur GitHub
2. Jenkins CI pull le code
3. Jenkins CI lance :
   - tests backend
   - build frontend
   - OWASP Dependency Check
   - SonarQube analysis
   - Trivy filesystem scan
4. Jenkins CI build les images Docker
5. Jenkins CI push les images sur GHCR
6. Jenkins CI declenche Jenkins CD
7. Jenkins CD met a jour le tag des images dans `k8s/gitops/kustomization.yaml`
8. Jenkins CD push le changement sur GitHub
9. ArgoCD detecte le changement et deploye sur Kubernetes
10. Prometheus/Grafana sont prevus pour le monitoring

## URLs locales recommandees

- Jenkins: `http://localhost:8082`
- SonarQube: `http://localhost:9000`
- Frontend local deployee: `http://localhost:30080`
- Backend local deployee: `http://localhost:30081/swagger-ui.html`

## Deploiement local rapide

Lancer l infrastructure DevOps locale :

```bash
./scripts/start-devops-stack.sh
```

Deployer l application sur Docker Desktop Kubernetes :

```bash
./scripts/deploy-local-docker-desktop.sh
```
