package com.dream11.odin.service;

import com.dream11.grpc.util.ExceptionUtil;
import com.dream11.odin.client.MysqlClient;
import com.dream11.odin.dao.ProviderAccountDao;
import com.dream11.odin.dao.ProviderAccountMappingDao;
import com.dream11.odin.dto.v1.ProviderAccount;
import com.dream11.odin.error.OdinError;
import com.dream11.odin.grpc.provideraccount.v1.CreateProviderAccountResponse;
import com.dream11.odin.grpc.provideraccount.v1.CreateProviderCategoryResponse;
import com.dream11.odin.grpc.provideraccount.v1.CreateProviderResponse;
import com.dream11.odin.grpc.provideraccount.v1.GetAllProviderAccountsResponse;
import com.dream11.odin.grpc.provideraccount.v1.GetProviderAccountResponse;
import com.dream11.odin.grpc.provideraccount.v1.GetProviderAccountsResponse;
import com.dream11.odin.util.CollectionUtil;
import com.google.inject.Inject;
import com.google.protobuf.Struct;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.vertx.reactivex.sqlclient.SqlConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ProviderAccountBusiness {

  final ProviderAccountDao providerAccountDao;
  final ProviderAccountMappingDao providerAccountMappingDao;
  final MysqlClient mysqlClient;

  public Single<GetProviderAccountResponse> getProviderAccount(
      Long orgId, String name, Boolean fetchLinkedAccounts) {
    return providerAccountDao
        .getProviderAccount(orgId, name, fetchLinkedAccounts)
        .doOnSuccess(
            providerAccounts -> {
              if (providerAccounts.isEmpty()) {
                throw ExceptionUtil.getException(OdinError.ACCOUNT_DOES_NOT_EXIST, name);
              }
            })
        .map(
            providerAccounts ->
                this.buildGetProviderAccountResponse(
                    providerAccounts,
                    fetchLinkedAccounts,
                    providerAccount -> providerAccount.getName().equals(name)));
  }

  private GetProviderAccountResponse buildGetProviderAccountResponse(
      List<ProviderAccount> providerAccounts,
      Boolean fetchLinkedAccounts,
      Predicate<ProviderAccount> isRequestedAccount) {
    GetProviderAccountResponse.Builder getProviderAccountResponseBuilder =
        GetProviderAccountResponse.newBuilder();

    ProviderAccount requestedAccount =
        CollectionUtil.filterList(isRequestedAccount, providerAccounts).get(0);

    List<ProviderAccount> linkedAccounts =
        CollectionUtil.filterList(
            providerAccount -> !providerAccount.getName().equals(requestedAccount.getName()),
            providerAccounts);

    getProviderAccountResponseBuilder.setAccount(requestedAccount);
    if (fetchLinkedAccounts) {
      getProviderAccountResponseBuilder.addAllLinkedAccounts(linkedAccounts);
    }
    return getProviderAccountResponseBuilder.build();
  }

  public Single<GetAllProviderAccountsResponse> getAllProviderAccounts(
      Long orgId, Boolean fetchLinkedAccounts) {
    return providerAccountDao
        .getAllProviderAccounts(orgId, fetchLinkedAccounts)
        .doOnSuccess(providerAccounts -> {})
        .map(
            providerAccounts ->
                buildGetAllProviderAccountsResponse(fetchLinkedAccounts, providerAccounts));
  }

  public Single<GetProviderAccountsResponse> getProviderAccounts(
      Long orgId, List<String> name, Boolean fetchLinkedAccounts) {
    return providerAccountDao
        .getProviderAccounts(orgId, name, fetchLinkedAccounts)
        .doOnSuccess(
            providerAccounts -> {
              if (providerAccounts.isEmpty()) {
                throw ExceptionUtil.getException(OdinError.ACCOUNT_DOES_NOT_EXIST, name);
              }
              if (Boolean.TRUE.equals(!fetchLinkedAccounts)
                  && !name.isEmpty()
                  && name.size() != providerAccounts.size()) {

                Set<String> difference =
                    SetUtils.difference(
                        new HashSet<>(name),
                        providerAccounts.stream()
                            .map(ProviderAccount::getName)
                            .collect(Collectors.toSet()));

                throw ExceptionUtil.getException(
                    OdinError.ACCOUNT_DOES_NOT_EXIST, String.join(", ", difference));
              }
            })
        .map(
            providerAccounts ->
                buildGetProviderAccountsResponse(name, fetchLinkedAccounts, providerAccounts));
  }

  private GetAllProviderAccountsResponse buildGetAllProviderAccountsResponse(
      Boolean fetchLinkedAccounts, List<ProviderAccount> providerAccounts) {
    List<GetProviderAccountResponse> getProviderAccountResponses;
    getProviderAccountResponses =
        providerAccounts.stream()
            .map(
                curProvider ->
                    this.buildGetAllProviderAccountResponse(
                        curProvider, providerAccounts, fetchLinkedAccounts))
            .toList();
    return GetAllProviderAccountsResponse.newBuilder()
        .addAllAccounts(getProviderAccountResponses)
        .build();
  }

  private GetProviderAccountResponse buildGetAllProviderAccountResponse(
      ProviderAccount curProviderAccount,
      List<ProviderAccount> providerAccounts,
      Boolean fetchLinkedAccounts) {
    GetProviderAccountResponse.Builder getProviderAccountResponseBuilder =
        GetProviderAccountResponse.newBuilder();

    List<ProviderAccount> linkedAccounts =
        CollectionUtil.filterList(
            providerAccount -> !providerAccount.getName().equals(curProviderAccount.getName()),
            providerAccounts);

    getProviderAccountResponseBuilder.setAccount(curProviderAccount);
    if (Boolean.TRUE.equals(fetchLinkedAccounts)) {
      getProviderAccountResponseBuilder.addAllLinkedAccounts(linkedAccounts);
    }
    return getProviderAccountResponseBuilder.build();
  }

  private GetProviderAccountsResponse buildGetProviderAccountsResponse(
      List<String> name, Boolean fetchLinkedAccounts, List<ProviderAccount> providerAccounts) {
    List<GetProviderAccountResponse> getProviderAccountResponses;
    if (name.isEmpty()) {
      getProviderAccountResponses =
          providerAccounts.stream()
              .filter(ProviderAccount::getDefault)
              .map(
                  curProvider ->
                      this.buildGetProviderAccountResponse(
                          providerAccounts, fetchLinkedAccounts, ProviderAccount::getDefault))
              .toList();
    } else {
      getProviderAccountResponses =
          name.stream()
              .map(
                  curName ->
                      this.buildGetProviderAccountResponse(
                          providerAccounts,
                          fetchLinkedAccounts,
                          providerAccount -> providerAccount.getName().equals(curName)))
              .toList();
    }

    return GetProviderAccountsResponse.newBuilder()
        .addAllAccounts(getProviderAccountResponses)
        .build();
  }

  public Single<CreateProviderCategoryResponse> createProviderCategory(String name) {
    return providerAccountDao
        .checkProviderCategoryExists(name)
        .flatMap(
            exists -> {
              if (exists.equals(Boolean.TRUE)) {
                throw ExceptionUtil.getException(OdinError.PROVIDER_CATEGORY_ALREADY_EXISTS, name);
              }
              return providerAccountDao.createProviderCategory(name);
            })
        .map(success -> CreateProviderCategoryResponse.newBuilder().setSuccess(success).build());
  }

  public Single<CreateProviderResponse> createProvider(
      String name, String providerCategoryName, Struct dataSchema) {
    return providerAccountDao
        .checkProviderExists(name)
        .flatMap(
            providerExists -> {
              if (providerExists.equals(Boolean.TRUE)) {
                throw ExceptionUtil.getException(OdinError.PROVIDER_ALREADY_EXISTS, name);
              }
              return providerAccountDao.getProviderCategoryIdByName(providerCategoryName);
            })
        .flatMap(
            providerCategoryId ->
                providerAccountDao.createProvider(name, providerCategoryId, dataSchema))
        .map(success -> CreateProviderResponse.newBuilder().setSuccess(success).build());
  }

  public Single<CreateProviderAccountResponse> createProviderAccount(
      String name,
      String providerName,
      Struct providerData,
      Long orgId,
      Boolean isDefault,
      List<String> linkedAccounts) {
    return mysqlClient
        .getMasterClient()
        .rxWithTransaction(
            (Function<SqlConnection, Maybe<Boolean>>)
                connection ->
                    providerAccountDao
                        .createProviderAccount(
                            connection, name, providerName, providerData, orgId, isDefault)
                        .flatMap(
                            providerAccountId ->
                                providerAccountMappingDao.createProviderAccountMappings(
                                    connection, providerAccountId, linkedAccounts, orgId))
                        .toMaybe())
        .toSingle()
        .map(success -> CreateProviderAccountResponse.newBuilder().setSuccess(success).build());
  }
}
