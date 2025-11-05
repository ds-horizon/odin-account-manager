--liquibase formatted sql

--changeset odin:005_provider_service_account
REPLACE
INTO provider_service_account(id, version, provider_account_id, provider_service_id, org_id, provider_service_data) VALUES(1001, 1,
  1001, 1001, 1001, '{"name":"testService","roles":["testRole1", "testRole2"]}');

REPLACE
INTO provider_service_account(id, version, provider_account_id, provider_service_id, org_id, provider_service_data) VALUES(1002, 1,
  1001, 1002, 1001, '{"repository":1}');

REPLACE
INTO provider_service_account(id, version, provider_account_id, provider_service_id, org_id, provider_service_data, is_active) VALUES (1003, 1,
  1001, 1001, 1001, '{"name":"testServiceInactive","roles":["testRole1", "testRole2"]}', false);
