# Backend Contribution Guide

## Scope

This file applies to `spotify-backend/`, its five Maven modules, Docker
configuration, seed data, and its Jenkins pipeline.

## Boundaries

- `catalog-service` owns catalog data and media metadata.
- `auth-service` owns authentication, users, and refresh tokens.
- `library-service` owns Cassandra-backed library data.
- `notification-service` owns activity/notification persistence.
- `api-gateway` is the external HTTP entry point on port `8084`.
- Infrastructure is defined in the Compose files; application secrets are
  supplied at runtime.

## Development Gates

Run from this directory:

```bash
bash ./mvnw -B clean verify
```

The Jenkinsfile runs the same Maven verification in a pinned Java 17
container. `target/` output is disposable. If context tests need external
services, make that dependency explicit and isolated rather than hiding it
with `-DskipTests`.

Build/deploy configuration checks:

```bash
docker compose config --quiet
docker compose -f docker-compose.yml -f docker-compose.deploy.yml --profile manual config --quiet
```

## Safety Rules

- Keep `.env` local or in Jenkins Credentials; use `.env.example` as the
  contract and rotate any credential that was exposed.
- Keep service-to-service URLs configurable through environment variables.
- Do not change database volumes or seed data destructively without an
  explicit migration/reset step.
- Verify the gateway and dependent services after deployment before calling
  the release healthy.

Read the workspace `llm-wiki/architecture.md`, `delivery.md`, and
`guardrails.md` for cross-repository contracts.
