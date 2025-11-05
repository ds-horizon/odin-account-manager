--liquibase formatted sql

--changeset odin:001_provider
INSERT INTO `provider`
  VALUES (1, NOW(), NOW(), 1, 'AWS', 1, '{
    "type": "object",
    "required": [
      "accountId",
      "region",
      "assumeRoleArn"
    ],
    "properties": {
      "region": {
        "type": "string"
      },
      "accountId": {
        "type": "string"
      },
      "description": {
        "type": "string"
      },
      "assumeRoleArn": {
        "type": "string"
      },
      "resourceLabels": {
        "type": "object"
      }
    }
  }'),
         (2, NOW(), NOW(), 1, 'Jfrog', 2, '{
           "type": "object",
           "required": [
             "url",
             "token"
           ],
           "properties": {
             "url": {
               "type": "string"
             },
             "token": {
               "type": "string"
             },
             "description": {
               "type": "string"
             }
           }
         }'),
         (3, NOW(), NOW(), 1, 'Datadog', 3, '{
           "type": "object",
           "required": [
             "apiKey",
             "appKey",
             "url"
           ],
           "properties": {
             "url": {
               "type": "string"
             },
             "apiKey": {
               "type": "string"
             },
             "appKey": {
               "type": "string"
             }
           }
         }'),
         (4, NOW(), NOW(), 1, 'Hashicorp', 4, '{
           "type": "object",
           "properties": {
             "description": {
               "type": "string"
             }
           }
         }'),
         (5, NOW(), NOW(), 1, 'LogCentral', 3, '{
           "type": "object",
           "required": [
             "apiKey",
             "appKey",
             "url"
           ],
           "properties": {
             "url": {
               "type": "string"
             },
             "apiKey": {
               "type": "string"
             },
             "appKey": {
               "type": "string"
             }
           }
         }'),
         (6, NOW(), NOW(), 1, 'Odin', 4, '{
           "type": "object",
           "properties": {
             "description": {
               "type": "object"
             }
           }
         }'),
         (7, NOW(), NOW(), 1, 'GCP', 1, '{
           "type": "object",
           "required": [
             "projectId",
             "region"
           ],
           "properties": {
             "region": {
               "type": "string"
             },
             "projectId": {
               "type": "string"
             },
             "description": {
               "type": "string"
             },
             "tags": {
               "type": "object"
             }
           }
         }'),
         (8, NOW(), NOW(), 1, 'optimus', 4, '{
           "type": "object",
           "properties": {
             "optimusApiKey": {
               "type": "string"
             }
           },
           "additionalProperties": false
         }'),
         (10, NOW(), NOW(), 1, 'LogCentralCriq', 3, '{
           "type": "object",
           "required": [
             "apiKey",
             "appKey",
             "url"
           ],
           "properties": {
             "url": {
               "type": "string"
             },
             "apiKey": {
               "type": "string"
             },
             "appKey": {
               "type": "string"
             }
           }
         }')
    AS NEW
ON DUPLICATE KEY
  UPDATE id                   = NEW.id,
         version              = NEW.version,
         name                 = NEW.name,
         provider_category_id = NEW.provider_category_id,
         data_schema          = NEW.data_schema;
