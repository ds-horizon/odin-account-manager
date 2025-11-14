--liquibase formatted sql

--changeset odin:000_provider_category
INSERT INTO `provider_category`
  VALUES (1, NOW(), NOW(), 1, 'CLOUD'),
         (2, NOW(), NOW(), 1, 'ARTIFACTORY'),
         (3, NOW(), NOW(), 1, 'MONITORING'),
         (4, NOW(), NOW(), 1, 'INFRASTRUCTURE_MANAGEMENT')
    AS NEW
ON DUPLICATE KEY
  UPDATE id      = NEW.id,
         version = NEW.version,
         NAME    = NEW.name;
