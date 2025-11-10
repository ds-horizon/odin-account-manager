--liquibase formatted sql

--changeset odin:006_provider_account_mapping
REPLACE INTO provider_account_mapping(id, version, provider_account_id, mapped_provider_account_id) VALUES(1001, 1, 1001, 1002);
