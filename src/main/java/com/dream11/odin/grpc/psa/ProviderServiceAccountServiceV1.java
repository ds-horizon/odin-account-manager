package com.dream11.odin.grpc.psa;

import com.dream11.grpc.annotation.GrpcService;
import com.dream11.grpc.util.ExceptionUtil;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceAccountRequest;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceAccountResponse;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceCategoryRequest;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceCategoryResponse;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceRequest;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceResponse;
import com.dream11.odin.grpc.psa.v1.GetProviderServiceAccountRequest;
import com.dream11.odin.grpc.psa.v1.GetProviderServiceAccountResponse;
import com.dream11.odin.grpc.psa.v1.RxProviderServiceAccountServiceGrpc;
import com.dream11.odin.service.ProviderServiceAccountBusiness;
import com.google.inject.Inject;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GrpcService
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ProviderServiceAccountServiceV1
    extends RxProviderServiceAccountServiceGrpc.ProviderServiceAccountServiceImplBase {

  final ProviderServiceAccountBusiness providerServiceAccountBusiness;

  @Override
  public Single<GetProviderServiceAccountResponse> getProviderServiceAccount(
      Single<GetProviderServiceAccountRequest> request) {
    return request
        .flatMap(
            getProviderServiceAccountRequest ->
                providerServiceAccountBusiness.getProviderAccountService(
                    getProviderServiceAccountRequest.getId()))
        .doOnError(err -> log.error("Error {}", err.getMessage(), err))
        .onErrorResumeNext(err -> Single.error(ExceptionUtil.parseThrowable(err)));
  }

  @Override
  public Single<CreateProviderServiceCategoryResponse> createProviderServiceCategory(
      Single<CreateProviderServiceCategoryRequest> request) {
    return request
        .flatMap(providerServiceAccountBusiness::createProviderServiceCategory)
        .doOnError(err -> log.error("Error {}", err.getMessage(), err))
        .onErrorResumeNext(err -> Single.error(ExceptionUtil.parseThrowable(err)));
  }

  @Override
  public Single<CreateProviderServiceResponse> createProviderService(
      Single<CreateProviderServiceRequest> request) {
    return request
        .flatMap(providerServiceAccountBusiness::createProviderService)
        .doOnError(err -> log.error("Error {}", err.getMessage(), err))
        .onErrorResumeNext(err -> Single.error(ExceptionUtil.parseThrowable(err)));
  }

  @Override
  public Single<CreateProviderServiceAccountResponse> createProviderServiceAccount(
      Single<CreateProviderServiceAccountRequest> request) {
    return request
        .flatMap(providerServiceAccountBusiness::createProviderServiceAccount)
        .doOnError(err -> log.error("Error {}", err.getMessage(), err))
        .onErrorResumeNext(err -> Single.error(ExceptionUtil.parseThrowable(err)));
  }
}
