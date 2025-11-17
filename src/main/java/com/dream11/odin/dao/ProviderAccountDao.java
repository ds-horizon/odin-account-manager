package com.dream11.odin.dao;

import static com.dream11.odin.constant.Constants.MAX_NO_OF_PROVIDER_ACCOUNT_NAMES;

import com.dream11.grpc.util.ExceptionUtil;
import com.dream11.odin.client.MysqlClient;
import com.dream11.odin.dao.query.MysqlQuery;
import com.dream11.odin.dto.v1.ProviderAccount;
import com.dream11.odin.dto.v1.ProviderServiceAccount;
import com.dream11.odin.error.OdinError;
import com.dream11.odin.util.JsonUtil;
import com.google.inject.Inject;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import io.reactivex.Single;
import io.vertx.mysqlclient.MySQLException;
import io.vertx.reactivex.mysqlclient.MySQLClient;
import io.vertx.reactivex.sqlclient.Row;
import io.vertx.reactivex.sqlclient.RowSet;
import io.vertx.reactivex.sqlclient.SqlConnection;
import io.vertx.reactivex.sqlclient.Tuple;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ProviderAccountDao {
  final MysqlClient mysqlClient;

  public Single<List<ProviderAccount>> getProviderAccount(
      Long orgId, String name, boolean fetchLinkedAccounts) {
    String query = MysqlQuery.GET_PROVIDER_ACCOUNT_AND_SERVICES_QUERY.apply("pa.name = ?");
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(query)
        .rxExecute(Tuple.of(orgId, name, orgId, name, fetchLinkedAccounts))
        .map(this::buildProviderAccount);
  }

  private List<ProviderAccount> buildProviderAccount(RowSet<Row> rowSet) {
    // key: provider_account_name, value: provider_account builder
    Map<String, ProviderAccount.Builder> providerAccountBuilderMap = new HashMap<>();
    for (Row row : rowSet) {
      String providerAccountName = row.getString("name");
      providerAccountBuilderMap.computeIfAbsent(
          providerAccountName,
          k ->
              ProviderAccount.newBuilder()
                  .setName(providerAccountName)
                  .setProvider(row.getString("provider"))
                  .setCategory(row.getString("provider_category"))
                  .setData(
                      JsonUtil.jsonToProtoBuilder(
                          row.getJsonObject("provider_data"), Struct.newBuilder()))
                  .setDefault(row.getBoolean("is_default"))
                  .setId(row.getInteger("id")));

      if (row.getString("provider_service_name") != null) {
        ProviderServiceAccount.Builder serviceBuilder =
            ProviderServiceAccount.newBuilder()
                .setName(row.getString("provider_service_name"))
                .setCategory(row.getString("provider_service_category"))
                .setData(
                    JsonUtil.jsonToProtoBuilder(
                        row.getJsonObject("provider_service_data"), Struct.newBuilder()))
                .setId(row.getLong("provider_service_id"));
        providerAccountBuilderMap.get(providerAccountName).addServices(serviceBuilder);
      }
    }
    return providerAccountBuilderMap.values().stream().map(ProviderAccount.Builder::build).toList();
  }

  public Single<List<ProviderAccount>> getAllProviderAccounts(
      Long orgId, boolean fetchLinkedAccounts) {
    String query = MysqlQuery.GET_ALL_PROVIDER_ACCOUNT_AND_SERVICES_QUERY.apply("");
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(query)
        .rxExecute(Tuple.of(orgId, orgId, fetchLinkedAccounts))
        .map(this::buildProviderAccount);
  }

  public Single<List<ProviderAccount>> getProviderAccounts(
      Long orgId, List<String> name, boolean fetchLinkedAccounts) {

    if (name.size() > MAX_NO_OF_PROVIDER_ACCOUNT_NAMES) {
      throw ExceptionUtil.getException(
          OdinError.MAX_PROVIDER_ACCOUNT_NAME_COUNT, MAX_NO_OF_PROVIDER_ACCOUNT_NAMES);
    }

    Tuple queryParams = Tuple.tuple();

    queryParams.addValue(orgId);
    if (!name.isEmpty()) {
      name.forEach(queryParams::addValue);
    } else {
      queryParams.addValue(1);
    }

    queryParams.addValue(orgId);
    if (!name.isEmpty()) {
      name.forEach(queryParams::addValue);
    } else {
      queryParams.addValue(1);
    }

    queryParams.addValue(fetchLinkedAccounts);

    String multiNameParam = name.stream().map(r -> "?").collect(Collectors.joining(","));

    String query =
        name.isEmpty()
            ? MysqlQuery.GET_PROVIDER_ACCOUNT_AND_SERVICES_QUERY.apply("pa.is_default = ?")
            : MysqlQuery.GET_PROVIDER_ACCOUNT_AND_SERVICES_QUERY.apply(
                "pa.name in (" + multiNameParam + ")");

    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(query)
        .rxExecute(queryParams)
        .map(this::buildProviderAccount);
  }

  public Single<Boolean> createProviderCategory(String name) {
    return this.mysqlClient
        .getMasterClient()
        .preparedQuery(MysqlQuery.CREATE_PROVIDER_CATEGORY_QUERY)
        .rxExecute(Tuple.of(name))
        .map(
            rowSet -> {
              if (rowSet.rowCount() == 0) {
                throw ExceptionUtil.getException(OdinError.SERVICE_UNKNOWN_EXCEPTION);
              }
              return true;
            });
  }

  public Single<Boolean> checkProviderCategoryExists(String name) {
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(MysqlQuery.GET_PROVIDER_CATEGORY_BY_NAME_QUERY)
        .rxExecute(Tuple.of(name))
        .map(rowSet -> rowSet.size() > 0);
  }

  @SneakyThrows
  public Single<Boolean> createProvider(String name, Long providerCategoryId, Struct dataSchema) {
    return this.mysqlClient
        .getMasterClient()
        .preparedQuery(MysqlQuery.CREATE_PROVIDER_QUERY)
        .rxExecute(Tuple.of(name, providerCategoryId, JsonFormat.printer().print(dataSchema)))
        .map(
            rowSet -> {
              if (rowSet.rowCount() == 0) {
                throw ExceptionUtil.getException(OdinError.SERVICE_UNKNOWN_EXCEPTION);
              }
              return true;
            });
  }

  public Single<Boolean> checkProviderExists(String name) {
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(MysqlQuery.GET_PROVIDER_BY_NAME_QUERY)
        .rxExecute(Tuple.of(name))
        .map(rowSet -> rowSet.size() > 0);
  }

  public Single<Long> getProviderCategoryIdByName(String categoryName) {
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(MysqlQuery.GET_PROVIDER_CATEGORY_BY_NAME_QUERY)
        .rxExecute(Tuple.of(categoryName))
        .map(
            rowSet -> {
              if (rowSet.size() == 0) {
                throw ExceptionUtil.getException(
                    OdinError.PROVIDER_CATEGORY_NOT_FOUND, categoryName);
              }
              Row row = rowSet.iterator().next();
              return row.getLong("id");
            });
  }

  public Single<Long> getProviderIdByName(String providerName) {
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(MysqlQuery.GET_PROVIDER_BY_NAME_QUERY)
        .rxExecute(Tuple.of(providerName))
        .map(
            rowSet -> {
              if (rowSet.size() == 0) {
                throw ExceptionUtil.getException(OdinError.PROVIDER_NOT_FOUND, providerName);
              }
              Row row = rowSet.iterator().next();
              return row.getLong("id");
            });
  }

  /**
   * Creates a provider account using an existing database connection and returns the created ID.
   * This method is designed to be used within transactions.
   *
   * @param connection The database connection within a transaction
   * @param name Provider account name
   * @param providerName Provider name
   * @param providerData Provider configuration data
   * @param orgId Organization ID
   * @param isDefault Whether this is the default account
   * @return Single<Long> the ID of the created provider account
   */
  public Single<Long> createProviderAccount(
      SqlConnection connection,
      String name,
      String providerName,
      Struct providerData,
      Long orgId,
      Boolean isDefault) {
    return getProviderIdByName(providerName)
        .flatMap(
            providerId ->
                connection
                    .preparedQuery(MysqlQuery.CREATE_PROVIDER_ACCOUNT_QUERY)
                    .rxExecute(
                        Tuple.of(
                            JsonFormat.printer().print(providerData),
                            providerId,
                            orgId,
                            name,
                            isDefault))
                    .map(createResult -> createResult.property(MySQLClient.LAST_INSERTED_ID)))
        .onErrorResumeNext(
            err -> {
              log.error("Error while creating provider account", err);
              if (err instanceof MySQLException mySQLException
                  && mySQLException.getErrorCode() == 1062) {
                return Single.error(
                    ExceptionUtil.getException(OdinError.PROVIDER_ACCOUNT_ALREADY_EXISTS, name));
              }
              return Single.error(ExceptionUtil.parseThrowable(err));
            });
  }

  /**
   * Gets the ID of a provider account by name within the organization using an existing connection.
   * This method is designed to be used within transactions.
   *
   * @param connection The database connection within a transaction
   * @param accountName The name of the account
   * @param orgId Organization ID
   * @return Single<Long> the account ID
   */
  public Single<Long> getAccountIdByName(SqlConnection connection, String accountName, Long orgId) {
    return connection
        .preparedQuery(MysqlQuery.GET_PROVIDER_ACCOUNT_BY_NAME_QUERY)
        .rxExecute(Tuple.of(accountName, orgId))
        .map(
            accountResult -> {
              if (accountResult.size() == 0) {
                throw ExceptionUtil.getException(OdinError.ACCOUNT_DOES_NOT_EXIST, accountName);
              }
              return accountResult.iterator().next().getLong("id");
            });
  }
}
