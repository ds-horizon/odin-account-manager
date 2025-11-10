package com.dream11.odin.dao.query;

import java.util.function.UnaryOperator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MysqlQuery {
  public static final UnaryOperator<String> GET_PROVIDER_ACCOUNT_AND_SERVICES_QUERY =
      cond ->
          String.format(
              "SELECT pc.name AS "
                  + "provider_category, "
                  + "pa.id AS id, "
                  + "p.name AS provider, "
                  + "pa.name AS name, "
                  + "pa.is_default AS is_default, "
                  + "pa.provider_data AS provider_data, "
                  + "ps.name AS provider_service_name, "
                  + "psa.provider_service_data AS provider_service_data, "
                  + "psc.name AS provider_service_category, "
                  + "psa.id AS provider_service_id "
                  + "FROM provider_category pc "
                  + "JOIN provider p ON p.provider_category_id = pc.id "
                  + "JOIN provider_account pa ON pa.provider_id = p.id "
                  + "LEFT JOIN provider_service_account psa ON psa.provider_account_id = pa.id "
                  + "LEFT JOIN provider_service ps ON psa.provider_service_id = ps.id "
                  + "LEFT JOIN provider_service_category psc ON ps.provider_service_category_id = psc.id "
                  + "WHERE ((pa.org_id = ? AND %s) "
                  + "OR pa.id IN (SELECT pam.mapped_provider_account_id FROM provider_account pa JOIN provider_account_mapping pam ON pam"
                  + ".provider_account_id = pa.id WHERE pa.org_id = ? AND %s AND true=?)) "
                  + "AND (psa.is_active IS NULL OR psa.is_active = 1);",
              cond, cond);

  public static final UnaryOperator<String> GET_ALL_PROVIDER_ACCOUNT_AND_SERVICES_QUERY =
      cond ->
          String.format(
              "SELECT pc.name AS "
                  + "provider_category, "
                  + "pa.id AS id, "
                  + "p.name AS provider, "
                  + "pa.name AS name, "
                  + "pa.is_default AS is_default, "
                  + "pa.provider_data AS provider_data, "
                  + "ps.name AS provider_service_name, "
                  + "psa.provider_service_data AS provider_service_data, "
                  + "psc.name AS provider_service_category, "
                  + "psa.id AS provider_service_id "
                  + "FROM provider_category pc "
                  + "JOIN provider p ON p.provider_category_id = pc.id "
                  + "JOIN provider_account pa ON pa.provider_id = p.id "
                  + "LEFT JOIN provider_service_account psa ON psa.provider_account_id = pa.id "
                  + "LEFT JOIN provider_service ps ON psa.provider_service_id = ps.id "
                  + "LEFT JOIN provider_service_category psc ON ps.provider_service_category_id = psc.id "
                  + "WHERE ((pa.org_id = ?) "
                  + "OR pa.id IN (SELECT pam.mapped_provider_account_id FROM provider_account pa JOIN provider_account_mapping pam ON pam"
                  + ".provider_account_id = pa.id WHERE pa.org_id = ? AND true=? )) AND (psa.is_active IS NULL OR psa.is_active = 1);",
              cond,
              cond);

  public static final String GET_PROVIDER_SERVICE_ACCOUNT_QUERY =
      "SELECT psa.id AS id, psa.provider_service_data AS data, "
          + "ps.name AS name, psc.name as category, pa.provider_data AS provider_data, pa.name AS provider_name, "
          + "p.name AS provider, pc.name AS provider_category "
          + "FROM provider_service_account psa, provider_service_category psc, provider_service ps, provider_account pa, "
          + "provider_category pc, provider p "
          + "WHERE psa.provider_account_id = pa.id AND pa.provider_id = p.id AND p.provider_category_id = pc.id "
          + "AND psa.provider_service_id = ps.id AND ps.provider_service_category_id = psc.id AND psa.id=? AND psa.is_active = 1;";

  public static final String CREATE_PROVIDER_CATEGORY_QUERY =
      "INSERT INTO provider_category (name, version) VALUES (?, 1)";

  public static final String GET_PROVIDER_CATEGORY_BY_NAME_QUERY =
      "SELECT id, name FROM provider_category WHERE name = ?";

  public static final String CREATE_PROVIDER_QUERY =
      "INSERT INTO provider (name, provider_category_id, data_schema, version) VALUES (?, ?, ?, 1)";

  public static final String GET_PROVIDER_BY_NAME_QUERY =
      "SELECT id, name, provider_category_id, data_schema FROM provider WHERE name = ?";

  public static final String CREATE_PROVIDER_ACCOUNT_QUERY =
      "INSERT INTO provider_account (provider_data, provider_id, org_id, name, is_default, version) VALUES (?, ?, ?, ?, ?, 1)";

  public static final String GET_PROVIDER_ACCOUNT_BY_NAME_QUERY =
      "SELECT id, name FROM provider_account WHERE name = ? AND org_id = ?";

  public static final String CREATE_PROVIDER_ACCOUNT_MAPPING_QUERY =
      "INSERT INTO provider_account_mapping (provider_account_id, mapped_provider_account_id, version) VALUES (?, ?, 1)";

  public static final String CREATE_PROVIDER_SERVICE_CATEGORY_QUERY =
      "INSERT INTO provider_service_category (name, version) VALUES (?, 1)";

  public static final String GET_PROVIDER_SERVICE_CATEGORY_BY_NAME_QUERY =
      "SELECT id, name FROM provider_service_category WHERE name = ?";

  public static final String CREATE_PROVIDER_SERVICE_QUERY =
      "INSERT INTO provider_service (name, data_schema, provider_id, provider_service_category_id, version) VALUES (?, ?, ?, ?, 1)";

  public static final String GET_PROVIDER_SERVICE_CATEGORY_ID_BY_NAME_QUERY =
      "SELECT id FROM provider_service_category WHERE name = ?";

  public static final String CHECK_PROVIDER_SERVICE_EXISTS_QUERY =
      "SELECT COUNT(*) FROM provider_service WHERE provider_id = ? AND provider_service_category_id = ?";

  public static final String GET_PROVIDER_SERVICE_BY_NAME_QUERY =
      "SELECT id, name, provider_id, provider_service_category_id FROM provider_service WHERE name = ?";

  public static final String GET_PROVIDER_ACCOUNT_BY_NAME_AND_ORG_QUERY =
      "SELECT id, name, provider_id FROM provider_account WHERE name = ? AND org_id = ?";

  public static final String CREATE_PROVIDER_SERVICE_ACCOUNT_QUERY =
      "INSERT INTO provider_service_account (provider_service_id, provider_account_id, provider_service_data, org_id, is_active, version) "
          + "VALUES (?, ?, ?, ?, ?, 1)";

  public static final String CHECK_PROVIDER_SERVICE_ACCOUNT_EXISTS_QUERY =
      "SELECT COUNT(*) FROM provider_service_account WHERE provider_service_id = ? AND provider_account_id = ?";
}
