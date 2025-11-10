package com.dream11.odin.grpc.provideraccount;

import static com.dream11.odin.constant.Constants.MAX_NO_OF_PROVIDER_ACCOUNT_NAMES;

import com.dream11.grpc.annotation.GrpcService;
import com.dream11.grpc.error.GrpcException;
import com.dream11.grpc.util.ExceptionUtil;
import com.dream11.odin.constant.Constants;
import com.dream11.odin.error.OdinError;
import com.dream11.odin.grpc.provideraccount.v1.CreateProviderAccountRequest;
import com.dream11.odin.grpc.provideraccount.v1.CreateProviderAccountResponse;
import com.dream11.odin.grpc.provideraccount.v1.CreateProviderCategoryRequest;
import com.dream11.odin.grpc.provideraccount.v1.CreateProviderCategoryResponse;
import com.dream11.odin.grpc.provideraccount.v1.CreateProviderRequest;
import com.dream11.odin.grpc.provideraccount.v1.CreateProviderResponse;
import com.dream11.odin.grpc.provideraccount.v1.GetAllProviderAccountsRequest;
import com.dream11.odin.grpc.provideraccount.v1.GetAllProviderAccountsResponse;
import com.dream11.odin.grpc.provideraccount.v1.GetProviderAccountRequest;
import com.dream11.odin.grpc.provideraccount.v1.GetProviderAccountResponse;
import com.dream11.odin.grpc.provideraccount.v1.GetProviderAccountsRequest;
import com.dream11.odin.grpc.provideraccount.v1.GetProviderAccountsResponse;
import com.dream11.odin.grpc.provideraccount.v1.RxProviderAccountServiceGrpc;
import com.dream11.odin.service.ProviderAccountBusiness;
import com.google.inject.Inject;
import io.reactivex.Single;
import io.vertx.grpc.ContextServerInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GrpcService
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ProviderAccountServiceV1
    extends RxProviderAccountServiceGrpc.ProviderAccountServiceImplBase {

  final ProviderAccountBusiness providerAccount;
  private static final String ERROR_STRING = "Error {}";

  @Override
  public Single<GetProviderAccountResponse> getProviderAccount(
      Single<GetProviderAccountRequest> request) {
    return request
        .map(this::validateGetProviderAccountRequest)
        .flatMap(
            getProviderAccountRequest ->
                providerAccount.getProviderAccount(
                    ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER),
                    getProviderAccountRequest.getName(),
                    getProviderAccountRequest.getFetchLinkedAccountDetails()))
        .doOnError(err -> log.error(ERROR_STRING, err.getMessage(), err))
        .onErrorResumeNext(err -> Single.error(ExceptionUtil.parseThrowable(err)));
  }

  private GetProviderAccountRequest validateGetProviderAccountRequest(
      GetProviderAccountRequest getProviderAccountRequest) {
    if (ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER) == null) {
      throw new GrpcException(OdinError.ORGANIZATION_HEADER_NOT_FOUND);
    } else if (getProviderAccountRequest.getName().isEmpty()) {
      throw new GrpcException(OdinError.PROVIDER_ACCOUNT_NAME_NOT_FOUND);
    } else {
      return getProviderAccountRequest;
    }
  }

  @Override
  public Single<GetProviderAccountsResponse> getProviderAccounts(
      Single<GetProviderAccountsRequest> request) {
    return request
        .map(this::validateGetProviderAccountsRequest)
        .flatMap(
            getProviderAccountRequest ->
                providerAccount.getProviderAccounts(
                    ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER),
                    getProviderAccountRequest.getNameList().stream().toList(),
                    getProviderAccountRequest.getFetchLinkedAccountDetails()))
        .doOnError(err -> log.error(ERROR_STRING, err.getMessage(), err))
        .onErrorResumeNext(err -> Single.error(ExceptionUtil.parseThrowable(err)));
  }

  @Override
  public Single<GetAllProviderAccountsResponse> getAllProviderAccounts(
      Single<GetAllProviderAccountsRequest> request) {
    if (ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER) == null) {
      throw new GrpcException(OdinError.ORGANIZATION_HEADER_NOT_FOUND);
    }
    return request
        .flatMap(
            getProviderAccountRequest ->
                providerAccount.getAllProviderAccounts(
                    ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER),
                    getProviderAccountRequest.getFetchLinkedAccountDetails()))
        .doOnError(err -> log.error(ERROR_STRING, err.getMessage(), err))
        .onErrorResumeNext(err -> Single.error(ExceptionUtil.parseThrowable(err)));
  }

  private GetProviderAccountsRequest validateGetProviderAccountsRequest(
      GetProviderAccountsRequest getProviderAccountRequest) {
    if (ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER) == null) {
      throw new GrpcException(OdinError.ORGANIZATION_HEADER_NOT_FOUND);
    } else if (getProviderAccountRequest.getNameList().size() > MAX_NO_OF_PROVIDER_ACCOUNT_NAMES) {
      throw ExceptionUtil.getException(
          OdinError.MAX_PROVIDER_ACCOUNT_NAME_COUNT, MAX_NO_OF_PROVIDER_ACCOUNT_NAMES);
    } else {
      return getProviderAccountRequest;
    }
  }

  @Override
  public Single<CreateProviderCategoryResponse> createProviderCategory(
      Single<CreateProviderCategoryRequest> request) {
    return request
        .map(this::validateCreateProviderCategoryRequest)
        .flatMap(
            createProviderCategoryRequest ->
                providerAccount.createProviderCategory(createProviderCategoryRequest.getName()))
        .doOnError(err -> log.error(ERROR_STRING, err.getMessage(), err))
        .onErrorResumeNext(err -> Single.error(ExceptionUtil.parseThrowable(err)));
  }

  private CreateProviderCategoryRequest validateCreateProviderCategoryRequest(
      CreateProviderCategoryRequest createProviderCategoryRequest) {
    if (ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER) == null) {
      throw new GrpcException(OdinError.ORGANIZATION_HEADER_NOT_FOUND);
    } else if (createProviderCategoryRequest.getName().isEmpty()) {
      throw new GrpcException(OdinError.PROVIDER_CATEGORY_NAME_NOT_FOUND);
    } else {
      return createProviderCategoryRequest;
    }
  }

  @Override
  public Single<CreateProviderResponse> createProvider(Single<CreateProviderRequest> request) {
    return request
        .map(this::validateCreateProviderRequest)
        .flatMap(
            createProviderRequest ->
                providerAccount.createProvider(
                    createProviderRequest.getName(),
                    createProviderRequest.getProviderCategoryName(),
                    createProviderRequest.getDataSchema()))
        .doOnError(err -> log.error(ERROR_STRING, err.getMessage(), err))
        .onErrorResumeNext(err -> Single.error(ExceptionUtil.parseThrowable(err)));
  }

  private CreateProviderRequest validateCreateProviderRequest(
      CreateProviderRequest createProviderRequest) {
    if (ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER) == null) {
      throw new GrpcException(OdinError.ORGANIZATION_HEADER_NOT_FOUND);
    } else if (createProviderRequest.getName().isEmpty()) {
      throw new GrpcException(OdinError.PROVIDER_NAME_NOT_FOUND);
    } else if (createProviderRequest.getProviderCategoryName().isEmpty()) {
      throw new GrpcException(OdinError.PROVIDER_CATEGORY_NAME_NOT_FOUND);
    } else {
      return createProviderRequest;
    }
  }

  @Override
  public Single<CreateProviderAccountResponse> createProviderAccount(
      Single<CreateProviderAccountRequest> request) {
    return request
        .map(this::validateCreateProviderAccountRequest)
        .flatMap(
            createProviderAccountRequest -> {
              Long orgId = ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER);
              return providerAccount.createProviderAccount(
                  createProviderAccountRequest.getName(),
                  createProviderAccountRequest.getProviderName(),
                  createProviderAccountRequest.getProviderData(),
                  orgId,
                  createProviderAccountRequest.getIsDefault(),
                  createProviderAccountRequest.getLinkedAccountsList());
            })
        .doOnError(err -> log.error("Error while creating provider account", err))
        .onErrorResumeNext(err -> Single.error(ExceptionUtil.parseThrowable(err)));
  }

  private CreateProviderAccountRequest validateCreateProviderAccountRequest(
      CreateProviderAccountRequest createProviderAccountRequest) {
    if (ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER) == null) {
      throw new GrpcException(OdinError.ORGANIZATION_HEADER_NOT_FOUND);
    } else if (createProviderAccountRequest.getName().isEmpty()) {
      throw new GrpcException(OdinError.ACCOUNT_NAME_NOT_FOUND);
    } else if (createProviderAccountRequest.getProviderName().isEmpty()) {
      throw new GrpcException(OdinError.PROVIDER_NAME_NOT_FOUND);
    } else {
      return createProviderAccountRequest;
    }
  }
}
