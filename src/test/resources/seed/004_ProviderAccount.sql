--liquibase formatted sql

--changeset odin:004_provider_account
INSERT INTO provider_account(id, version, provider_id, org_id, name, provider_data, is_default)
VALUES (1001, 1, 1001, 1001, 'TEST_PROVIDER_ACCOUNT_1', '{"accountId":"testId"}', 1) AS new
ON DUPLICATE KEY
UPDATE id = new.id, version = new.version, name = new.name, provider_id = new.provider_id, org_id = new.org_id,
  provider_data = new.provider_data, is_default = new.is_default;

INSERT INTO provider_account(id, version, provider_id, org_id, name, provider_data)
VALUES (1002, 1, 1002, 1001, 'TEST_PROVIDER_ACCOUNT_2',
        '{"url":"https://some-url.com","credentials":{"username":"username","password":"password"}}') AS new
ON DUPLICATE KEY
UPDATE id = new.id, version = new.version, name = new.name, provider_id = new.provider_id, org_id = new.org_id, provider_data= new.provider_data;
