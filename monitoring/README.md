# Monitoring stack

Le schema cible du projet est :

- Prometheus pour la collecte des metriques
- Grafana pour les dashboards
- ArgoCD pour le deploiement GitOps
- Jenkins pour CI/CD

Pour une demo rapide, installe :

1. `kube-prometheus-stack`
2. `ingress-nginx`
3. `argocd`

Exemple :

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add argo https://argoproj.github.io/argo-helm
helm repo update

helm install monitoring prometheus-community/kube-prometheus-stack -n monitoring --create-namespace
helm install argocd argo/argo-cd -n argocd --create-namespace
```
