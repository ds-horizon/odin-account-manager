# Provider Service

[← Back to Overview](../README.md)

## Overview

Provider Service represents a specific service offered by a Provider within a Provider Service Category. For example, within AWS (provider), EC2 would be a Provider Service in the COMPUTE category, S3 would be a Provider Service in the STORAGE category, etc.

## Predefined Provider Services

The system comes with several predefined provider services:

### AWS Services
| ID | Name | Category | Provider | Description |
|----|------|----------|----------|-------------|
| 1 | EKS | KUBERNETES | AWS | Elastic Kubernetes Service |
| 2 | VPC | NETWORK | AWS | Virtual Private Cloud |
| 3 | EC2 | VM | AWS | Elastic Compute Cloud |
| 4 | ACM | CERTIFICATE | AWS | AWS Certificate Manager |
| 5 | R53 | DISCOVERY | AWS | Route 53 DNS Service |
| 6 | RDS | RELATIONAL_DATABASE | AWS | Relational Database Service |
| 7 | Elasticache | CACHE | AWS | ElastiCache Redis/Memcached |

### JFrog Services
| ID | Name | Category | Provider | Description |
|----|------|----------|----------|-------------|
| 8 | DockerRegistry | DOCKER_REGISTRY | Jfrog | Docker Registry |
| 9 | Storage | STORAGE | Jfrog | Artifact Storage |
| 10 | HelmRegistry | HELM_REGISTRY | Jfrog | Helm Chart Repository |

### Odin Services
| ID | Name | Category | Provider | Description |
|----|------|----------|----------|-------------|
| 11 | DiscoveryConfig | DISCOVERY | Odin | Service Discovery Configuration |

### Local Services
| ID | Name | Category | Provider | Description |
|----|------|----------|----------|-------------|
| 12 | Kind | KUBERNETES | local | Kubernetes in Docker |
| 13 | DockerRegistry | DOCKER_REGISTRY | local | Local Docker Registry |


## Usage Examples

### Create AWS EKS Service

```bash
grpcurl -plaintext \
  -H "orgId: 1" \
  -d '{
    "provider_name": "AWS",
    "provider_service_category_name": "KUBERNETES",
    "name": "EKS",
    "data_schema": {
      "type": "object",
      "required": ["clusters"],
      "properties": {
        "clusters": {
          "type": "array",
          "items": {
            "type": "object",
            "required": ["name", "kubeconfig"],
            "properties": {
              "name": {"type": "string"},
              "kubeconfig": {"type": "string"},
              "namespaceConfig": {"type": "object"},
              "labels": {"type": "object"}
            }
          }
        },
        "pullSecrets": {"type": "array"},
        "serviceAnnotations": {"type": "object"},
        "environmentVariables": {"type": "object"},
        "serviceAccountAnnotations": {"type": "object"}
      }
    }
  }' \
  localhost:8080 \
  dream11.oam.psa.v1.ProviderServiceAccountService/CreateProviderService
```

## Related Documentation

- [Provider](./provider.md) - Providers that offer services
- [Provider Service Category](./provider-service-category.md) - Categories that services belong to
- [Provider Service Account](./provider-service-account.md) - Instances of services
- [Database Schema Overview](../README.md#schema-entities)

## Navigation

- [← Previous: Provider Service Category](./provider-service-category.md)
- [Next: Provider Service Account →](./provider-service-account.md)
- [Back to Overview](../README.md)
