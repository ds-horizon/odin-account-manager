# Provider Service Category

[← Back to Overview](../README.md)

## Overview

Provider Service Category is used to classify and organize different types of services that providers offer. For example, within AWS (a provider), you might have services categorized as "COMPUTE" (EC2), "STORAGE" (S3), "NETWORKING" (VPC), etc.


## Predefined Service Categories

The system comes with several predefined service categories:

| ID | Name | Description | Example Services |
|----|------|-------------|------------------|
| 1 | KUBERNETES | Kubernetes orchestration | EKS, GKE, ASK, Kind |
| 2 | VM | Virtual Machine services | EC2, GCE, Azure VMs |
| 3 | NETWORK | Network services | VPC, Cloud DNS, Load Balancers |
| 4 | DISCOVERY | Service discovery | Route53, Cloud DNS, Consul |
| 5 | CERTIFICATE | Certificate management | ACM, Certificate Manager, Let's Encrypt |
| 6 | RELATIONAL_DATABASE | Relational databases | RDS, Cloud SQL, Azure SQL |
| 7 | CACHE | Caching services | ElastiCache, Memorystore, Redis |
| 8 | GRAPH_DATABASE | Graph databases | Neptune, Neo4j, CosmosDB |
| 9 | DOCKER_REGISTRY | Container registries | ECR, GCR, Docker Hub, JFrog |
| 10 | STORAGE | Storage services | S3, Cloud Storage, Blob Storage |
| 11 | ARTIFACTORY | Artifact repositories | JFrog Artifactory, Nexus |
| 12 | HELM_REGISTRY | Helm chart repositories | Helm repos, ChartMuseum |
| 13 | CONFIGURATION | Configuration management | Parameter Store, Config Maps |
| 14 | CONFIG_MANAGER | Configuration managers | Consul, etcd, Zookeeper |
| 15 | SECRET_MANAGER | Secret management | Secrets Manager, Key Vault, Vault |


## Usage Examples

### Create Kubernetes Service Category

```bash
grpcurl -plaintext \
  -H "orgId: 1" \
  -d '{"name": "KUBERNETES"}' \
  localhost:8080 \
  dream11.oam.psa.v1.ProviderServiceAccountService/CreateProviderServiceCategory
```

### Cross-Provider Usage
The same category can be used across different providers:
- AWS EC2 and GCP Compute Engine both can use `COMPUTE`
- AWS S3 and GCP Cloud Storage both can use `STORAGE`
- AWS VPC and GCP VPC both can use `NETWORKING`

## Related Documentation

- [Provider Service](./provider-service.md) - Services that belong to categories
- [Provider Service Account](./provider-service-account.md) - Instances of services
- [Database Schema Overview](../README.md#schema-entities)

## Navigation

- [← Previous: Provider Account](./provider-account.md)
- [Next: Provider Service →](./provider-service.md)
- [Back to Overview](../README.md)
