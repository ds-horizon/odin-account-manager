# Provider

[← Back to Overview](../README.md)

## Overview

Provider represents a specific cloud service provider or tool (like AWS, GCP, Jfrog) within a Provider Category. Each provider defines its own data schema that specifies the required and optional fields for creating provider accounts.

## Predefined Providers

The system comes with several predefined providers:

| ID | Name | Category | Description |
|----|------|----------|-------------|
| 1 | AWS | CLOUD | Amazon Web Services |
| 2 | Jfrog | ARTIFACTORY | JFrog Artifactory |
| 3 | Odin | INFRASTRUCTURE_MANAGEMENT | Internal infrastructure tool |
| 4 | local | CLOUD | Local deployment |

## Data Schemas

Each provider defines a JSON schema that specifies the structure and validation rules for provider account data. For eg.

### AWS Schema
```json
{
  "type": "object",
  "required": ["accountId", "region", "assumeRoleArn"],
  "properties": {
    "region": {"type": "string"},
    "accountId": {"type": "string"},
    "description": {"type": "string"},
    "assumeRoleArn": {"type": "string"},
    "resourceLabels": {"type": "object"}
  }
}
```

## Usage Examples

### Create Local Development Provider

```bash
grpcurl -plaintext \
  -d '{
    "name": "local",
    "provider_category_name": "INFRASTRUCTURE_MANAGEMENT",
    "data_schema": {
      "type": "object",
      "properties": {
        "description": {"type": "string"},
        "environment": {"type": "string", "default": "development"}
      }
    }
  }' \
  localhost:8080 \
  dream11.oam.provideraccount.v1.ProviderAccountService/CreateProvider
```


## Related Documentation

- [Provider Category](./provider-category.md) - Categories that contain providers
- [Provider Account](./provider-account.md) - Instances of providers
- [Provider Service](./provider-service.md) - Services within providers

## Navigation

- [← Previous: Provider Category](./provider-category.md)
- [Next: Provider Account →](./provider-account.md)
- [Back to Overview](../README.md)
