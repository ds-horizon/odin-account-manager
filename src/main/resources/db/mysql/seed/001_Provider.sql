--liquibase formatted sql

--changeset odin:001_provider
INSERT INTO `provider`
  VALUES (1, NOW(), NOW(), 1, 'AWS', 1, '{
    "type": "object",
    "required": [
      "accountId",
      "region",
      "runnerServiceAccountAnnotations"
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
      "runnerServiceAccountAnnotations": {
        "type": "object"
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
    (3, NOW(), NOW(), 1, 'Odin', 4, '{
            "type": "object",
            "properties": {
              "description": {
                "type": "object"
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
