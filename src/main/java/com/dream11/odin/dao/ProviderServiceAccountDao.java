package com.dream11.odin.dao;

import com.dream11.grpc.util.ExceptionUtil;
import com.dream11.odin.client.MysqlClient;
import com.dream11.odin.dao.query.MysqlQuery;
import com.dream11.odin.dto.v1.ProviderAccount;
import com.dream11.odin.dto.v1.ProviderServiceAccount;
import com.dream11.odin.dto.v1.ProviderServiceAccountEnriched;
import com.dream11.odin.dto.v1.ProviderServiceCategory;
import com.dream11.odin.error.OdinError;
import com.dream11.odin.util.JsonUtil;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.reactivex.sqlclient.Row;
import io.vertx.reactivex.sqlclient.RowSet;
import io.vertx.reactivex.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ProviderServiceAccountDao {
  final MysqlClient mysqlClient;

  public Maybe<ProviderServiceAccountEnriched> getProviderServiceAccount(Long id) {
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(MysqlQuery.GET_PROVIDER_SERVICE_ACCOUNT_QUERY)
        .rxExecute(Tuple.of(id))
        .filter(rowSet -> rowSet.size() != 0)
        .map(this::buildServiceProviderAccountEnriched);
  }

  private ProviderServiceAccountEnriched buildServiceProviderAccountEnriched(RowSet<Row> rowSet)
      throws InvalidProtocolBufferException {
    Row row = rowSet.iterator().next();
    ProviderAccount providerAccount =
        ProviderAccount.newBuilder()
            .setName(row.getString("provider_name"))
            .setProvider(row.getString("provider"))
            .setCategory(row.getString("provider_category"))
            .setData(
                JsonUtil.jsonToProtoBuilder(
                    row.getJsonObject("provider_data"), Struct.newBuilder()))
            .build();

    ProviderServiceAccount providerServiceAccount =
        ProviderServiceAccount.newBuilder()
            .setId(row.getLong("id"))
            .setName(row.getString("name"))
            .setCategory(row.getString("category"))
            .setData(JsonUtil.jsonToProtoBuilder(row.getJsonObject("data"), Struct.newBuilder()))
            .build();
    return ProviderServiceAccountEnriched.newBuilder()
        .setAccount(providerAccount)
        .setService(providerServiceAccount)
        .build();
  }

  public Single<Boolean> createProviderServiceCategory(String name) {
    return this.mysqlClient
        .getMasterClient()
        .preparedQuery(MysqlQuery.CREATE_PROVIDER_SERVICE_CATEGORY_QUERY)
        .rxExecute(Tuple.of(name))
        .map(rowSet -> true)
        .onErrorReturn(
            throwable -> {
              log.error("Failed to create provider service category: {}", name, throwable);
              return false;
            });
  }

  public Maybe<ProviderServiceCategory> getProviderServiceCategoryByName(String name) {
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(MysqlQuery.GET_PROVIDER_SERVICE_CATEGORY_BY_NAME_QUERY)
        .rxExecute(Tuple.of(name))
        .filter(rowSet -> rowSet.size() != 0)
        .map(this::buildProviderServiceCategory);
  }

  private ProviderServiceCategory buildProviderServiceCategory(RowSet<Row> rowSet) {
    Row row = rowSet.iterator().next();
    return ProviderServiceCategory.newBuilder()
        .setId(row.getLong("id"))
        .setName(row.getString("name"))
        .build();
  }

  public Single<Long> getProviderServiceCategoryIdByName(String categoryName) {
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(MysqlQuery.GET_PROVIDER_SERVICE_CATEGORY_ID_BY_NAME_QUERY)
        .rxExecute(Tuple.of(categoryName))
        .map(
            rowSet -> {
              if (rowSet.size() == 0) {
                throw ExceptionUtil.getException(
                    OdinError.PROVIDER_SERVICE_CATEGORY_NOT_FOUND, categoryName);
              }
              Row row = rowSet.iterator().next();
              return row.getLong("id");
            });
  }

  public Single<Boolean> checkProviderServiceExists(Long providerId, Long categoryId) {
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(MysqlQuery.CHECK_PROVIDER_SERVICE_EXISTS_QUERY)
        .rxExecute(Tuple.of(providerId, categoryId))
        .map(
            rowSet -> {
              Row row = rowSet.iterator().next();
              return row.getInteger("COUNT(*)") > 0;
            });
  }

  @SneakyThrows
  public Single<Boolean> createProviderService(
      String name, Struct dataSchema, Long providerId, Long categoryId) {
    return this.mysqlClient
        .getMasterClient()
        .preparedQuery(MysqlQuery.CREATE_PROVIDER_SERVICE_QUERY)
        .rxExecute(Tuple.of(name, JsonFormat.printer().print(dataSchema), providerId, categoryId))
        .map(
            rowSet -> {
              if (rowSet.rowCount() == 0) {
                throw ExceptionUtil.getException(OdinError.PROVIDER_SERVICE_CREATION_FAILED, name);
              }
              return true;
            });
  }

  public Single<Long> getProviderServiceIdByName(String serviceName) {
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(MysqlQuery.GET_PROVIDER_SERVICE_BY_NAME_QUERY)
        .rxExecute(Tuple.of(serviceName))
        .map(
            rowSet -> {
              if (rowSet.size() == 0) {
                throw ExceptionUtil.getException(OdinError.PROVIDER_SERVICE_NOT_FOUND, serviceName);
              }
              Row row = rowSet.iterator().next();
              return row.getLong("id");
            });
  }

  public Single<Long> getProviderAccountIdByNameAndOrg(String accountName, Long orgId) {
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(MysqlQuery.GET_PROVIDER_ACCOUNT_BY_NAME_AND_ORG_QUERY)
        .rxExecute(Tuple.of(accountName, orgId))
        .map(
            rowSet -> {
              if (rowSet.size() == 0) {
                throw ExceptionUtil.getException(
                    OdinError.PROVIDER_ACCOUNT_NOT_FOUND_BY_NAME, accountName);
              }
              Row row = rowSet.iterator().next();
              return row.getLong("id");
            });
  }

  public Single<Boolean> checkProviderServiceAccountExists(Long serviceId, Long accountId) {
    return this.mysqlClient
        .getSlaveClient()
        .preparedQuery(MysqlQuery.CHECK_PROVIDER_SERVICE_ACCOUNT_EXISTS_QUERY)
        .rxExecute(Tuple.of(serviceId, accountId))
        .map(
            rowSet -> {
              Row row = rowSet.iterator().next();
              return row.getInteger("COUNT(*)") > 0;
            });
  }

  @SneakyThrows
  public Single<Boolean> createProviderServiceAccount(
      Long serviceId, Long accountId, Struct serviceData, Long orgId, Boolean isActive) {
    return this.mysqlClient
        .getMasterClient()
        .preparedQuery(MysqlQuery.CREATE_PROVIDER_SERVICE_ACCOUNT_QUERY)
        .rxExecute(
            Tuple.of(
                serviceId, accountId, JsonFormat.printer().print(serviceData), orgId, isActive))
        .map(
            rowSet -> {
              if (rowSet.rowCount() == 0) {
                throw ExceptionUtil.getException(
                    OdinError.PROVIDER_SERVICE_ACCOUNT_CREATION_FAILED);
              }
              return true;
            });
  }
}
