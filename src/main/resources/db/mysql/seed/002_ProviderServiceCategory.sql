--liquibase formatted sql

--changeset odin:002_provider_service_category
INSERT INTO `provider_service_category`
  VALUES (1, NOW(), NOW(), 1, 'KUBERNETES'),
         (2, NOW(), NOW(), 1, 'VM'),
         (3, NOW(), NOW(), 1, 'NETWORK'),
         (4, NOW(), NOW(), 1, 'DISCOVERY'),
         (5, NOW(), NOW(), 1, 'CERTIFICATE'),
         (6, NOW(), NOW(), 1, 'RELATIONAL_DATABASE'),
         (7, NOW(), NOW(), 1, 'CACHE'),
         (8, NOW(), NOW(), 1, 'GRAPH_DATABASE'),
         (9, NOW(), NOW(), 1, 'DOCKER_REGISTRY'),
         (10, NOW(), NOW(), 1, 'STORAGE'),
         (11, NOW(), NOW(), 1, 'ARTIFACTORY'),
         (12, NOW(), NOW(), 1, 'HELM_REGISTRY'),
         (13, NOW(), NOW(), 1, 'CONFIGURATION'),
         (14, NOW(), NOW(), 1, 'CONFIG_MANAGER'),
         (15, NOW(), NOW(), 1, 'SECRET_MANAGER')
    AS NEW
ON DUPLICATE KEY
  UPDATE id      = NEW.id,
         version = NEW.version,
         name    = NEW.name;
