package com.dream11.odin.service;

import com.dream11.grpc.util.ExceptionUtil;
import com.dream11.odin.constant.Constants;
import com.dream11.odin.dao.ProviderAccountDao;
import com.dream11.odin.dao.ProviderServiceAccountDao;
import com.dream11.odin.error.OdinError;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceAccountRequest;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceAccountResponse;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceCategoryRequest;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceCategoryResponse;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceRequest;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceResponse;
import com.dream11.odin.grpc.psa.v1.GetProviderServiceAccountResponse;
import com.google.inject.Inject;
import io.reactivex.Single;
import io.vertx.grpc.ContextServerInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ProviderServiceAccountBusiness {

  final ProviderServiceAccountDao providerServiceAccountDao;
  final ProviderAccountDao providerAccountDao;

  public Single<GetProviderServiceAccountResponse> getProviderAccountService(Long id) {
    return providerServiceAccountDao
        .getProviderServiceAccount(id)
        .map(
            providerServiceAccountEnriched ->
                GetProviderServiceAccountResponse.newBuilder()
                    .setServiceAccount(providerServiceAccountEnriched)
                    .build())
        .switchIfEmpty(
            Single.error(
                ExceptionUtil.getException(OdinError.PROVIDER_SERVICE_ACCOUNT_DOES_NOT_EXIST, id)));
  }

  public Single<CreateProviderServiceCategoryResponse> createProviderServiceCategory(
      CreateProviderServiceCategoryRequest request) {
    return validateCreateProviderServiceCategoryRequest(request)
        .flatMap(
            validatedRequest ->
                providerServiceAccountDao
                    .getProviderServiceCategoryByName(validatedRequest.getName())
                    .flatMap(
                        existingCategory -> {
                          throw ExceptionUtil.getException(
                              OdinError.PROVIDER_SERVICE_CATEGORY_ALREADY_EXISTS,
                              existingCategory.getName());
                        })
                    .cast(CreateProviderServiceCategoryResponse.class)
                    .switchIfEmpty(
                        providerServiceAccountDao
                            .createProviderServiceCategory(validatedRequest.getName())
                            .flatMap(
                                success -> {
                                  if (success) {
                                    return Single.just(
                                        CreateProviderServiceCategoryResponse.newBuilder()
                                            .setSuccess(true)
                                            .build());
                                  } else {
                                    throw ExceptionUtil.getException(
                                        OdinError.PROVIDER_SERVICE_CATEGORY_CREATION_FAILED,
                                        validatedRequest.getName());
                                  }
                                })));
  }

  public Single<CreateProviderServiceResponse> createProviderService(
      CreateProviderServiceRequest request) {
    return validateCreateProviderServiceRequest(request)
        .flatMap(
            validatedRequest ->
                providerAccountDao
                    .getProviderIdByName(validatedRequest.getProviderName())
                    .flatMap(
                        providerId ->
                            providerServiceAccountDao
                                .getProviderServiceCategoryIdByName(
                                    validatedRequest.getProviderServiceCategoryName())
                                .flatMap(
                                    categoryId ->
                                        providerServiceAccountDao
                                            .checkProviderServiceExists(providerId, categoryId)
                                            .flatMap(
                                                exists -> {
                                                  if (exists) {
                                                    return Single.error(
                                                        ExceptionUtil.getException(
                                                            OdinError
                                                                .PROVIDER_SERVICE_ALREADY_EXISTS,
                                                            validatedRequest.getProviderName(),
                                                            validatedRequest
                                                                .getProviderServiceCategoryName()));
                                                  }
                                                  return providerServiceAccountDao
                                                      .createProviderService(
                                                          validatedRequest.getName(),
                                                          validatedRequest.getDataSchema(),
                                                          providerId,
                                                          categoryId)
                                                      .map(
                                                          success ->
                                                              CreateProviderServiceResponse
                                                                  .newBuilder()
                                                                  .setSuccess(success)
                                                                  .build());
                                                }))));
  }

  private Single<CreateProviderServiceCategoryRequest> validateCreateProviderServiceCategoryRequest(
      CreateProviderServiceCategoryRequest request) {
    if (ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER) == null) {
      return Single.error(ExceptionUtil.getException(OdinError.ORGANIZATION_HEADER_NOT_FOUND));
    }
    if (request.getName().trim().isEmpty()) {
      return Single.error(
          ExceptionUtil.getException(OdinError.PROVIDER_SERVICE_CATEGORY_NAME_NOT_FOUND));
    }
    return Single.just(request);
  }

  public Single<CreateProviderServiceAccountResponse> createProviderServiceAccount(
      CreateProviderServiceAccountRequest request) {
    return validateCreateProviderServiceAccountRequest(request)
        .flatMap(
            validatedRequest ->
                providerServiceAccountDao
                    .getProviderServiceIdByName(validatedRequest.getProviderServiceName())
                    .flatMap(
                        serviceId ->
                            providerServiceAccountDao
                                .getProviderAccountIdByNameAndOrg(
                                    validatedRequest.getProviderAccountName(),
                                    ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER))
                                .flatMap(
                                    accountId ->
                                        providerServiceAccountDao
                                            .checkProviderServiceAccountExists(serviceId, accountId)
                                            .flatMap(
                                                exists -> {
                                                  if (exists) {
                                                    return Single.error(
                                                        ExceptionUtil.getException(
                                                            OdinError
                                                                .PROVIDER_SERVICE_ACCOUNT_ALREADY_EXISTS,
                                                            validatedRequest
                                                                .getProviderServiceName(),
                                                            validatedRequest
                                                                .getProviderAccountName()));
                                                  }
                                                  return providerServiceAccountDao
                                                      .createProviderServiceAccount(
                                                          serviceId,
                                                          accountId,
                                                          validatedRequest.getProviderServiceData(),
                                                          ContextServerInterceptor.get(
                                                              Constants.ORGANIZATION_HEADER),
                                                          validatedRequest.getIsActive())
                                                      .map(
                                                          success ->
                                                              CreateProviderServiceAccountResponse
                                                                  .newBuilder()
                                                                  .setSuccess(success)
                                                                  .build());
                                                }))));
  }

  private Single<CreateProviderServiceRequest> validateCreateProviderServiceRequest(
      CreateProviderServiceRequest request) {
    if (ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER) == null) {
      return Single.error(ExceptionUtil.getException(OdinError.ORGANIZATION_HEADER_NOT_FOUND));
    }
    if (request.getProviderName().trim().isEmpty()) {
      return Single.error(ExceptionUtil.getException(OdinError.PROVIDER_NAME_NOT_FOUND));
    }
    if (request.getProviderServiceCategoryName().trim().isEmpty()) {
      return Single.error(
          ExceptionUtil.getException(OdinError.PROVIDER_SERVICE_CATEGORY_NAME_NOT_FOUND));
    }
    if (request.getName().trim().isEmpty()) {
      return Single.error(ExceptionUtil.getException(OdinError.PROVIDER_SERVICE_NAME_NOT_FOUND));
    }
    return Single.just(request);
  }

  private Single<CreateProviderServiceAccountRequest> validateCreateProviderServiceAccountRequest(
      CreateProviderServiceAccountRequest request) {
    if (ContextServerInterceptor.get(Constants.ORGANIZATION_HEADER) == null) {
      return Single.error(ExceptionUtil.getException(OdinError.ORGANIZATION_HEADER_NOT_FOUND));
    }
    if (request.getProviderServiceName().trim().isEmpty()) {
      return Single.error(ExceptionUtil.getException(OdinError.PROVIDER_SERVICE_NAME_NOT_FOUND));
    }
    if (request.getProviderAccountName().trim().isEmpty()) {
      return Single.error(ExceptionUtil.getException(OdinError.PROVIDER_ACCOUNT_NAME_NOT_FOUND));
    }
    return Single.just(request);
  }
}
