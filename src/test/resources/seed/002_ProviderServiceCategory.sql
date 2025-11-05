--liquibase formatted sql

--changeset odin:002_provider_service_category
INSERT INTO provider_service_category(id, version, name)
VALUES (1001, 1, 'TEST_SERVICE_CATEGORY_1') AS new
ON DUPLICATE KEY
UPDATE id = new.id, version = new.version, name = new.name;

INSERT INTO provider_service_category(id, version, name)
VALUES (1002, 1, 'TEST_SERVICE_CATEGORY_2') AS new
ON DUPLICATE KEY
UPDATE id = new.id, version = new.version, name = new.name;
