#!/usr/bin/env sh
set -eu

ROOT_DIR="$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)"

echo "Building backend image..."
docker build -t project-management-backend:local "${ROOT_DIR}"

echo "Building frontend image..."
docker build -t project-management-frontend:local "${ROOT_DIR}/frontend"

echo "Applying Kubernetes manifests..."
kubectl apply -k "${ROOT_DIR}/k8s/local-docker-desktop"

echo "Waiting for MySQL..."
kubectl rollout status deployment/mysql-deployment -n project-management --timeout=240s

echo "Waiting for backend..."
kubectl rollout status deployment/backend-deployment -n project-management --timeout=240s

echo "Waiting for frontend..."
kubectl rollout status deployment/frontend-deployment -n project-management --timeout=240s

echo "Application deployed."
echo "Frontend: http://localhost:30080"
echo "Backend Swagger: http://localhost:30081/swagger-ui.html"
