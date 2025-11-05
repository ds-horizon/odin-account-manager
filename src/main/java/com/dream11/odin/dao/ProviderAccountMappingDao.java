package com.dream11.odin.dao;

import com.dream11.odin.dao.query.MysqlQuery;
import com.google.inject.Inject;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.reactivex.sqlclient.SqlConnection;
import io.vertx.reactivex.sqlclient.Tuple;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ProviderAccountMappingDao {

  final ProviderAccountDao providerAccountDao;

  /**
   * Creates mappings between a provider account and multiple linked accounts. This method executes
   * within an existing transaction using the provided connection.
   *
   * @param connection The database connection within a transaction
   * @param providerAccountId The ID of the main provider account
   * @param linkedAccountNames List of linked account names to map
   * @param orgId Organization ID for account lookup
   * @return Single<Boolean> indicating success
   */
  public Single<Boolean> createProviderAccountMappings(
      SqlConnection connection,
      Long providerAccountId,
      List<String> linkedAccountNames,
      Long orgId) {
    if (linkedAccountNames == null || linkedAccountNames.isEmpty()) {
      return Single.just(true);
    }
    // Create mapping tasks for all linked accounts
    List<Single<Boolean>> mappingTasks =
        linkedAccountNames.stream()
            .map(
                linkedAccountName ->
                    providerAccountDao
                        .getAccountIdByName(connection, linkedAccountName, orgId)
                        .flatMap(
                            linkedAccountId ->
                                createProviderAccountMapping(
                                        connection, providerAccountId, linkedAccountId)
                                    .andThen(Single.just(true))))
            .toList();
    // Execute all mapping operations in parallel
    return Single.zip(mappingTasks, objects -> true);
  }

  /**
   * Creates a mapping record between provider account and linked account.
   *
   * @param connection The database connection within a transaction
   * @param providerAccountId The ID of the main provider account
   * @param linkedAccountId The ID of the linked account
   * @return Single<Boolean> indicating success
   */
  private Completable createProviderAccountMapping(
      SqlConnection connection, Long providerAccountId, Long linkedAccountId) {
    return connection
        .preparedQuery(MysqlQuery.CREATE_PROVIDER_ACCOUNT_MAPPING_QUERY)
        .rxExecute(Tuple.of(providerAccountId, linkedAccountId))
        .ignoreElement();
  }
}
