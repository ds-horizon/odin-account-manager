--liquibase formatted sql

--changeset odin:000_provider_category
INSERT INTO provider_category(id, version, name)
VALUES (1001, 1, 'TEST_PROVIDER_CATEGORY_1') AS new
ON DUPLICATE KEY
UPDATE id = new.id, version = new.version, name = new.name;

INSERT INTO provider_category(id, version, name)
VALUES (1002, 1, 'TEST_PROVIDER_CATEGORY_2') AS new
ON DUPLICATE KEY
UPDATE id = new.id, version = new.version, name = new.name;
