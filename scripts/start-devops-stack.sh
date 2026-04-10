#!/usr/bin/env sh
set -eu

ROOT_DIR="$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)"
docker compose -f "${ROOT_DIR}/docker-compose.devops.yml" up -d

echo "Jenkins: http://localhost:8080"
echo "Jenkins: http://localhost:8082"
echo "SonarQube: http://localhost:9000"
