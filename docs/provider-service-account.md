# Provider Service Account

[← Back to Overview](../README.md)

## Overview

Provider Service Account represents a specific instance or configuration of a Provider Service within a Provider Account. For example, if you have an "AWS Production Account" (Provider Account) and want to use EC2 (Provider Service), you would create an "EC2 Production Service Account" (Provider Service Account) with specific EC2 configurations like instance types, regions, security groups, etc.

## Usage Examples

### Get Provider Service Account

```bash
grpcurl -plaintext \
  -H "orgId: 1" \
  -d '{"id": 1}' \
  localhost:8080 \
  dream11.oam.psa.v1.ProviderServiceAccountService/GetProviderServiceAccount
```

**Expected Response**:
```json
{
  "service_account": {
    "service": {
      "name": "EC2",
      "category": "COMPUTE",
      "data": {
        "instanceType": "t3.medium",
        "region": "us-east-1",
        "keyPairName": "prod-keypair",
        "securityGroups": ["sg-12345678"]
      },
      "id": "1"
    },
    "account": {
      "name": "aws-prod-account",
      "provider": "AWS",
      "category": "CLOUD",
      "data": {...},
      "id": "1"
    }
  }
}
```

### Create AWS EC2 Service Account

```bash
grpcurl -plaintext \
  -H "orgId: 1" \
  -d '{
    "provider_service_name": "EC2",
    "provider_account_name": "aws-prod-account",
    "provider_service_data": "{\"instanceType\": \"t3.medium\", \"region\": \"us-east-1\", \"availabilityZone\": \"us-east-1a\", \"keyPairName\": \"prod-keypair\", \"securityGroups\": [\"sg-12345678\"], \"subnetId\": \"subnet-12345678\", \"tags\": {\"Environment\": \"production\", \"Team\": \"platform\"}}",
    "is_active": true
  }' \
  localhost:8080 \
  dream11.oam.psa.v1.ProviderServiceAccountService/CreateProviderServiceAccount
```
### Organization Scoping
All Provider Service Accounts are scoped to a specific organization via `org_id`.

## Related Documentation

- [Provider Service](./provider-service.md) - Services that accounts are instances of
- [Provider Account](./provider-account.md) - Accounts that contain service accounts
- [Provider](./provider.md) - Root providers
- [Database Schema Overview](../README.md#schema-entities)

## Navigation

- [← Previous: Provider Service](./provider-service.md)
- [Back to Overview](../README.md)
