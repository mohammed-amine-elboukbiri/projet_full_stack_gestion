#!/usr/bin/env sh
set -eu

IMAGE_NAMESPACE="${1:?missing image namespace}"
IMAGE_TAG="${2:?missing image tag}"
KUSTOMIZATION_FILE="k8s/gitops/kustomization.yaml"

sed -i.bak "s|newName: .*project-management-backend|newName: ghcr.io/${IMAGE_NAMESPACE}/project-management-backend|g" "${KUSTOMIZATION_FILE}"
sed -i.bak "s|newName: .*project-management-frontend|newName: ghcr.io/${IMAGE_NAMESPACE}/project-management-frontend|g" "${KUSTOMIZATION_FILE}"
sed -i.bak "s|newTag: .*|newTag: ${IMAGE_TAG}|g" "${KUSTOMIZATION_FILE}"
rm -f "${KUSTOMIZATION_FILE}.bak"
