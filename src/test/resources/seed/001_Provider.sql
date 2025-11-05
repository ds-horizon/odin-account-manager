--liquibase formatted sql

--changeset odin:001_provider
INSERT INTO provider(id, version, name, provider_category_id, data_schema)
VALUES (1001, 1, 'TEST_PROVIDER_1', 1001,
        '{"type":"object","properties":{"accountId":{"type":"string"}},"required":["accountId"]}') AS new
ON DUPLICATE KEY
UPDATE id = new.id, version = new.version, name = new.name, provider_category_id = new.provider_category_id, data_schema= new.data_schema;

INSERT INTO provider(id, version, name, provider_category_id, data_schema)
VALUES (1002, 1, 'TEST_PROVIDER_2', 1002,
        '{"type":"object","properties":{"url":{"type":"string"},"credentials":{"type":"object","properties":{"username":{"type":"string"},"password":{"type":"string"}},"required":["username","password"]}},"required":["url", "credentials"]}') AS new
ON DUPLICATE KEY
UPDATE id = new.id, version = new.version, name = new.name, provider_category_id = new.provider_category_id, data_schema= new.data_schema;
