--liquibase formatted sql

--changeset odin:003_provider_service
INSERT INTO provider_service(id, version, name, data_schema, provider_id, provider_service_category_id)
VALUES (1001, 1, "TEST_SERVICE_1",
        '{"type":"object","properties":{"name":{"type":"string"},"roles":{"type":"array","items":{"type":"string"}}},"required":["name","roles"]}',
        1001, 1001) AS new
ON DUPLICATE KEY
UPDATE id = new.id, version = new.version, name = new.name, provider_service_category_id = new.provider_service_category_id, data_schema=
  new.data_schema, provider_id = new.provider_id;

INSERT INTO provider_service(id, version, name, data_schema, provider_id, provider_service_category_id)
VALUES (1002, 1, "TEST_SERVICE_2",
        '{"type":"object","properties":{"repository":{"type":"number"}},"required":["repository"]}',
        1001, 1002) AS new
ON DUPLICATE KEY
UPDATE id = new.id, version = new.version, name = new.name, provider_service_category_id = new.provider_service_category_id, data_schema=
  new.data_schema, provider_id = new.provider_id;
