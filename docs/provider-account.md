# Provider Account

[← Back to Overview](../README.md)

## Overview

Provider Account represents a specific instance or configuration of a Provider. For example, while "AWS" is a Provider, "AWS Production Account" and "AWS Development Account" would be separate Provider Accounts with different configurations, credentials, and settings.


## Usage Examples

### Get a Specific Provider Account

```bash
grpcurl -plaintext \
  -H "orgId: 1" \
  -d '{"name": "aws-prod-account", "fetch_linked_account_details": true}' \
  localhost:8080 \
  dream11.oam.provideraccount.v1.ProviderAccountService/GetProviderAccount
```

**Expected Response**:
```json
{
  "account": {
    "name": "aws-prod-account",
    "provider": "AWS",
    "category": "CLOUD",
    "data": {
      "accountId": "123456789012",
      "region": "us-east-1",
      "assumeRoleArn": "arn:aws:iam::123456789012:role/OdinRole",
      "description": "Production AWS Account"
    },
    "default": true,
    "id": "1",
    "services": [...]
  }
}
```

### Get Multiple Provider Accounts

```bash
grpcurl -plaintext \
  -H "orgId: 1" \
  -d '{"name": ["aws-prod-account", "gcp-dev-account"], "fetch_linked_account_details": false}' \
  localhost:8080 \
  dream11.oam.provideraccount.v1.ProviderAccountService/GetProviderAccounts
```

### Get All Provider Accounts

```bash
grpcurl -plaintext \
  -H "orgId: 1" \
  -d '{"fetch_linked_account_details": false}' \
  localhost:8080 \
  dream11.oam.provideraccount.v1.ProviderAccountService/GetAllProviderAccounts
```

### Create AWS Development Account

```bash
grpcurl -plaintext \
  -H "orgId: 1" \
  -d '{
    "name": "aws-dev-account",
    "provider_name": "AWS",
    "provider_data": {
      "accountId": "987654321098",
      "region": "us-west-2",
      "assumeRoleArn": "arn:aws:iam::987654321098:role/OdinDevRole",
      "description": "Development AWS Account",
      "resourceLabels": {
        "environment": "development",
        "team": "engineering"
      }
    },
    "is_default": false,
    "linked_accounts": ["aws-prod-account"]
  }' \
  localhost:8080 \
  dream11.oam.provideraccount.v1.ProviderAccountService/CreateProviderAccount
```

## Account Linking

Provider Accounts can be linked to other Provider Accounts to establish relationships. Example use cases:

- Linking a cloud provider with some artifactory provider
- Associating accounts across different providers

### Example: Link Development to Production
```bash
grpcurl -plaintext \
  -H "orgId: 1" \
  -d '{
    "name": "aws-staging-account",
    "provider_name": "AWS",
    "provider_data": {...},
    "is_default": false,
    "linked_accounts": ["jfrog-account"]
  }' \
  localhost:8080 \
  dream11.oam.provideraccount.v1.ProviderAccountService/CreateProviderAccount
```

## Related Documentation

- [Provider](./provider.md) - Providers that accounts are instances of
- [Provider Service Account](./provider-service-account.md) - Services within accounts
- [Database Schema Overview](../README.md#schema-entities)

## Navigation

- [← Previous: Provider](./provider.md)
- [Next: Provider Service Category →](./provider-service-category.md)
- [Back to Overview](../README.md)
