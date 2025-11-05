--liquibase formatted sql

--changeset odin:20231211134221_create_tables
CREATE TABLE provider_category
(
  id         BIGINT                              NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
  version    INT                                 NOT NULL,

  name       VARCHAR(256)                        NOT NULL,

  PRIMARY KEY (id),
  UNIQUE (name)
);

CREATE TABLE provider
(
    id                   BIGINT                              NOT NULL AUTO_INCREMENT,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    version              INT                                 NOT NULL,

    name                 VARCHAR(256)                        NOT NULL,
    provider_category_id BIGINT                              NOT NULL,
    data_schema          JSON                                NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (name),
    FOREIGN KEY (provider_category_id) REFERENCES provider_category (id)
);


CREATE TABLE provider_service_category
(
    id         BIGINT                              NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    version    INT                                 NOT NULL,

    name       VARCHAR(256)                        NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE provider_service
(
    id                           BIGINT                              NOT NULL AUTO_INCREMENT,
    created_at                   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at                   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    version                      INT                                 NOT NULL,

    name                         VARCHAR(256)                        NOT NULL,
    data_schema                  JSON                                NOT NULL,
    provider_id                  BIGINT                              NOT NULL,
    provider_service_category_id BIGINT                              NOT NULL,


    PRIMARY KEY (id),
    UNIQUE (provider_id, provider_service_category_id),
    FOREIGN KEY (provider_id) REFERENCES provider (id),
    FOREIGN KEY (provider_service_category_id) REFERENCES provider_service_category (id)
);

CREATE TABLE provider_account
(
    id            BIGINT                              NOT NULL AUTO_INCREMENT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    version       INT                                 NOT NULL,

    provider_data JSON                                NOT NULL,
    provider_id   BIGINT                              NOT NULL,
    org_id        BIGINT                              NOT NULL,
    name          VARCHAR(256)                        NOT NULL,
    is_default    BOOLEAN                             NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id),
    UNIQUE (org_id, name),
    FOREIGN KEY (provider_id) REFERENCES provider (id)
);

CREATE TABLE provider_service_account
(
    id                    BIGINT                              NOT NULL AUTO_INCREMENT,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    version               INT                                 NOT NULL,

    provider_service_data JSON                                NOT NULL,
    provider_service_id   BIGINT                              NOT NULL,
    provider_account_id   BIGINT                              NOT NULL,
    org_id                BIGINT                              NOT NULL,
    is_active             BOOLEAN                             NOT NULL DEFAULT TRUE,

    PRIMARY KEY (id),
    FOREIGN KEY (provider_service_id) REFERENCES provider_service (id),
    FOREIGN KEY (provider_account_id) REFERENCES provider_account (id),
    INDEX(org_id)
);

CREATE TABLE provider_account_mapping
(
    id                         BIGINT                              NOT NULL AUTO_INCREMENT,
    created_at                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    version                    INT                                 NOT NULL,

    provider_account_id        BIGINT                              NOT NULL,
    mapped_provider_account_id BIGINT                              NOT NULL,


    PRIMARY KEY (id),
    FOREIGN KEY (provider_account_id) REFERENCES provider_account (id),
    FOREIGN KEY (mapped_provider_account_id) REFERENCES provider_account (id)
);
