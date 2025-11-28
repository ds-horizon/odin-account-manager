# Provider Category

[← Back to Overview](../README.md)

## Overview

Provider Category is the top-level classification system for organizing different types of providers in the Odin Account Manager.

## Predefined Categories

The system comes with the following predefined categories:

| ID | Name | Description |
|----|------|-------------|
| 1 | CLOUD | Cloud service providers (AWS, GCP, Azure) |
| 2 | ARTIFACTORY | Artifact repositories (Jfrog, Nexus) |
| 3 | MONITORING | Monitoring and observability tools (Datadog, Prometheus) |
| 4 | INFRASTRUCTURE_MANAGEMENT | Infrastructure management tools (Hashicorp, Odin) |


## Usage Examples

### Create a New Provider Category

```bash
grpcurl -plaintext \
  -d '{"name": "DATABASE"}' \
  localhost:8080 \
  dream11.oam.provideraccount.v1.ProviderAccountService/CreateProviderCategory
```

## Related Documentation

- [Provider](./provider.md) - Providers that belong to provider categories
- [Provider Account](./provider-account.md) - Instances of providers
