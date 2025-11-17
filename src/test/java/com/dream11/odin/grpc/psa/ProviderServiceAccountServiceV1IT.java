package com.dream11.odin.grpc.psa;

import static org.assertj.core.api.Assertions.assertThat;

import com.dream11.odin.Setup;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceAccountRequest;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceAccountResponse;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceCategoryRequest;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceCategoryResponse;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceRequest;
import com.dream11.odin.grpc.psa.v1.CreateProviderServiceResponse;
import com.dream11.odin.grpc.psa.v1.GetProviderServiceAccountRequest;
import com.dream11.odin.grpc.psa.v1.GetProviderServiceAccountResponse;
import com.dream11.odin.grpc.psa.v1.RxProviderServiceAccountServiceGrpc;
import com.dream11.odin.util.AssertionUtil;
import com.google.protobuf.Struct;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.grpc.VertxChannelBuilder;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Slf4j
@ExtendWith({VertxExtension.class, Setup.class})
class ProviderServiceAccountServiceV1IT {

  static ManagedChannel channel;
  final JsonObject providerServiceAccountData = new JsonObject().put("repository", 1);
  final JsonObject providerAccountData = new JsonObject().put("accountId", "testId");

  @BeforeAll
  static void setup(Vertx vertx) {
    channel = VertxChannelBuilder.forAddress(vertx, "localhost", 8080).usePlaintext().build();
  }

  private RxProviderServiceAccountServiceGrpc.RxProviderServiceAccountServiceStub
      createStubWithOrgId(String orgId) {
    Metadata metadata = new Metadata();
    metadata.put(Metadata.Key.of("orgId", Metadata.ASCII_STRING_MARSHALLER), orgId);
    return RxProviderServiceAccountServiceGrpc.newRxStub(channel)
        .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));
  }

  @Test
  void testGetProviderServiceAccountFailIfNotExist(VertxTestContext testContext) {
    // Arrange
    RxProviderServiceAccountServiceGrpc.RxProviderServiceAccountServiceStub
        rxProviderServiceAccountServiceStub =
            RxProviderServiceAccountServiceGrpc.newRxStub(channel);
    GetProviderServiceAccountRequest request =
        GetProviderServiceAccountRequest.newBuilder().setId(10000).build();

    // Act
    Single<GetProviderServiceAccountResponse> responseSingle =
        rxProviderServiceAccountServiceStub.getProviderServiceAccount(request);

    // Assert
    responseSingle
        .doOnError(err -> assertThat(err.getClass()).isEqualTo(StatusRuntimeException.class))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getCode())
                    .isEqualTo(Status.Code.NOT_FOUND))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getDescription())
                    .isEqualTo("Provider service account '10000' does not exist"))
        .subscribe(
            record -> testContext.failNow("Should throw provider service account not exists"),
            err -> testContext.completeNow());
  }

  @Test
  void testGetProviderServiceAccount(VertxTestContext testContext) {
    // Arrange
    RxProviderServiceAccountServiceGrpc.RxProviderServiceAccountServiceStub
        rxProviderServiceAccountServiceStub =
            RxProviderServiceAccountServiceGrpc.newRxStub(channel);
    long id = 1002L;
    GetProviderServiceAccountRequest request =
        GetProviderServiceAccountRequest.newBuilder().setId(id).build();

    // Act
    Single<GetProviderServiceAccountResponse> responseSingle =
        rxProviderServiceAccountServiceStub.getProviderServiceAccount(request);

    // Assert
    responseSingle
        .doOnSuccess(
            getProviderServiceAccountResponse -> {
              AssertionUtil.assertProviderServiceAccount(
                  getProviderServiceAccountResponse.getServiceAccount().getService(),
                  "TEST_SERVICE_2",
                  "TEST_SERVICE_CATEGORY_2",
                  id,
                  providerServiceAccountData);
              AssertionUtil.assertProviderAccount(
                  getProviderServiceAccountResponse.getServiceAccount().getAccount(),
                  "TEST_PROVIDER_ACCOUNT_1",
                  "TEST_PROVIDER_1",
                  "TEST_PROVIDER_CATEGORY_1",
                  providerAccountData);
            })
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  void testCreateProviderServiceCategorySuccess(VertxTestContext testContext) {
    // Arrange
    RxProviderServiceAccountServiceGrpc.RxProviderServiceAccountServiceStub
        rxProviderServiceAccountServiceStub = createStubWithOrgId("1");

    CreateProviderServiceCategoryRequest request =
        CreateProviderServiceCategoryRequest.newBuilder().setName("TEST_SERVICE_CATEGORY").build();

    // Act
    Single<CreateProviderServiceCategoryResponse> responseSingle =
        rxProviderServiceAccountServiceStub.createProviderServiceCategory(request);

    // Assert
    responseSingle
        .doOnSuccess(
            createProviderServiceCategoryResponse -> {
              assertThat(createProviderServiceCategoryResponse.getSuccess()).isTrue();
            })
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  void testCreateProviderServiceCategoryAlreadyExistsFailure(VertxTestContext testContext) {
    // Arrange
    RxProviderServiceAccountServiceGrpc.RxProviderServiceAccountServiceStub
        rxProviderServiceAccountServiceStub = createStubWithOrgId("1");

    CreateProviderServiceCategoryRequest request =
        CreateProviderServiceCategoryRequest.newBuilder()
            .setName("TEST_SERVICE_CATEGORY_1")
            .build();

    // Act
    Single<CreateProviderServiceCategoryResponse> responseSingle =
        rxProviderServiceAccountServiceStub.createProviderServiceCategory(request);

    // Assert
    responseSingle
        .doOnError(err -> assertThat(err.getClass()).isEqualTo(StatusRuntimeException.class))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getCode())
                    .isEqualTo(Status.Code.ALREADY_EXISTS))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getDescription())
                    .isEqualTo(
                        "Provider service category 'TEST_SERVICE_CATEGORY_1' already exists"))
        .subscribe(
            record ->
                testContext.failNow(
                    "Provider service category 'TEST_SERVICE_CATEGORY_1' already exists"),
            err -> testContext.completeNow());
  }

  @Test
  void testCreateProviderServiceCategoryOrgIdValidationFailure(VertxTestContext testContext) {
    // Arrange
    RxProviderServiceAccountServiceGrpc.RxProviderServiceAccountServiceStub
        rxProviderServiceAccountServiceStub =
            RxProviderServiceAccountServiceGrpc.newRxStub(channel);

    CreateProviderServiceCategoryRequest request =
        CreateProviderServiceCategoryRequest.newBuilder().setName("TEST_SERVICE_CATEGORY").build();

    // Act
    Single<CreateProviderServiceCategoryResponse> responseSingle =
        rxProviderServiceAccountServiceStub.createProviderServiceCategory(request);

    // Assert
    responseSingle
        .doOnError(err -> assertThat(err.getClass()).isEqualTo(StatusRuntimeException.class))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getCode())
                    .isEqualTo(Status.Code.INVALID_ARGUMENT))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getDescription())
                    .isEqualTo("orgId header cannot be null"))
        .subscribe(
            record -> testContext.failNow("orgId header cannot be null"),
            err -> testContext.completeNow());
  }

  @Test
  void testCreateProviderServiceSuccess(VertxTestContext testContext) {
    // Arrange
    RxProviderServiceAccountServiceGrpc.RxProviderServiceAccountServiceStub
        rxProviderServiceAccountServiceStub = createStubWithOrgId("1");

    CreateProviderServiceRequest request =
        CreateProviderServiceRequest.newBuilder()
            .setName("TEST_PROVIDER_SERVICE")
            .setProviderName("TEST_PROVIDER_2")
            .setProviderServiceCategoryName("TEST_SERVICE_CATEGORY_2")
            .setDataSchema(Struct.newBuilder().build())
            .build();

    // Act
    Single<CreateProviderServiceResponse> responseSingle =
        rxProviderServiceAccountServiceStub.createProviderService(request);

    // Assert
    responseSingle
        .doOnSuccess(
            createProviderServiceCategoryResponse -> {
              assertThat(createProviderServiceCategoryResponse.getSuccess()).isTrue();
            })
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  void testCreateProviderServiceAlreadyExistsFailure(VertxTestContext testContext) {
    // Arrange
    RxProviderServiceAccountServiceGrpc.RxProviderServiceAccountServiceStub
        rxProviderServiceAccountServiceStub = createStubWithOrgId("1");

    CreateProviderServiceRequest request =
        CreateProviderServiceRequest.newBuilder()
            .setName("TEST_SERVICE_1")
            .setProviderName("TEST_PROVIDER_1")
            .setProviderServiceCategoryName("TEST_SERVICE_CATEGORY_1")
            .setDataSchema(Struct.newBuilder().build())
            .build();

    // Act
    Single<CreateProviderServiceResponse> responseSingle =
        rxProviderServiceAccountServiceStub.createProviderService(request);

    // Assert
    responseSingle
        .doOnError(err -> assertThat(err.getClass()).isEqualTo(StatusRuntimeException.class))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getCode())
                    .isEqualTo(Status.Code.ALREADY_EXISTS))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getDescription())
                    .isEqualTo("Provider service 'TEST_SERVICE_1' already exists"))
        .subscribe(
            record -> testContext.failNow("Provider service 'TEST_SERVICE_1' already exists"),
            err -> testContext.completeNow());
  }

  @Test
  void testCreateProviderServiceOrgIdValidationFailure(VertxTestContext testContext) {
    // Arrange
    RxProviderServiceAccountServiceGrpc.RxProviderServiceAccountServiceStub
        rxProviderServiceAccountServiceStub = createStubWithOrgId("1");

    CreateProviderServiceRequest request =
        CreateProviderServiceRequest.newBuilder()
            .setName("TEST_SERVICE_1")
            .setProviderName("TEST_PROVIDER_2")
            .setProviderServiceCategoryName("TEST_SERVICE_CATEGORY_2")
            .setDataSchema(Struct.newBuilder().build())
            .build();

    // Act
    Single<CreateProviderServiceResponse> responseSingle =
        rxProviderServiceAccountServiceStub.createProviderService(request);

    // Assert
    responseSingle
        .doOnError(err -> assertThat(err.getClass()).isEqualTo(StatusRuntimeException.class))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getCode())
                    .isEqualTo(Status.Code.INVALID_ARGUMENT))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getDescription())
                    .isEqualTo("orgId header cannot be null"))
        .subscribe(
            record -> testContext.failNow("orgId header cannot be null"),
            err -> testContext.completeNow());
  }

  @Test
  void testCreateProviderServiceAccountSuccess(VertxTestContext testContext) {
    // Arrange
    RxProviderServiceAccountServiceGrpc.RxProviderServiceAccountServiceStub
        rxProviderServiceAccountServiceStub = createStubWithOrgId("1002");

    CreateProviderServiceAccountRequest request =
        CreateProviderServiceAccountRequest.newBuilder()
            .setProviderServiceName("TEST_SERVICE_1")
            .setProviderAccountName("TEST_PROVIDER_ACCOUNT_3")
            .setProviderServiceData(Struct.newBuilder().build())
            .setIsActive(true)
            .build();

    // Act
    Single<CreateProviderServiceAccountResponse> responseSingle =
        rxProviderServiceAccountServiceStub.createProviderServiceAccount(request);

    // Assert
    responseSingle
        .doOnSuccess(
            createProviderServiceCategoryResponse -> {
              assertThat(createProviderServiceCategoryResponse.getSuccess()).isTrue();
            })
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  void testCreateProviderServiceAccountAlreadyExistsFailure(VertxTestContext testContext) {
    // Arrange
    RxProviderServiceAccountServiceGrpc.RxProviderServiceAccountServiceStub
        rxProviderServiceAccountServiceStub = createStubWithOrgId("1002");

    CreateProviderServiceAccountRequest request =
        CreateProviderServiceAccountRequest.newBuilder()
            .setProviderServiceName("TEST_SERVICE_2")
            .setProviderAccountName("TEST_PROVIDER_ACCOUNT_3")
            .setProviderServiceData(Struct.newBuilder().build())
            .setIsActive(true)
            .build();

    // Act
    Single<CreateProviderServiceAccountResponse> responseSingle =
        rxProviderServiceAccountServiceStub.createProviderServiceAccount(request);

    // Assert
    responseSingle
        .doOnError(err -> assertThat(err.getClass()).isEqualTo(StatusRuntimeException.class))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getCode())
                    .isEqualTo(Status.Code.ALREADY_EXISTS))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getDescription())
                    .isEqualTo(
                        "Provider service account already exists for service 'TEST_SERVICE_2' and account 'TEST_PROVIDER_ACCOUNT_3'"))
        .subscribe(
            record ->
                testContext.failNow(
                    "Provider service account already exists for service 'TEST_SERVICE_2' and account 'TEST_PROVIDER_ACCOUNT_3'"),
            err -> testContext.completeNow());
  }

  @Test
  void testCreateProviderServiceAccountOrgIdValidationFailure(VertxTestContext testContext) {
    // Arrange
    RxProviderServiceAccountServiceGrpc.RxProviderServiceAccountServiceStub
        rxProviderServiceAccountServiceStub =
            RxProviderServiceAccountServiceGrpc.newRxStub(channel);

    CreateProviderServiceAccountRequest request =
        CreateProviderServiceAccountRequest.newBuilder()
            .setProviderServiceName("TEST_SERVICE_1")
            .setProviderAccountName("TEST_PROVIDER_ACCOUNT_3")
            .setProviderServiceData(Struct.newBuilder().build())
            .setIsActive(true)
            .build();

    // Act
    Single<CreateProviderServiceAccountResponse> responseSingle =
        rxProviderServiceAccountServiceStub.createProviderServiceAccount(request);

    // Assert
    responseSingle
        .doOnError(err -> assertThat(err.getClass()).isEqualTo(StatusRuntimeException.class))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getCode())
                    .isEqualTo(Status.Code.INVALID_ARGUMENT))
        .doOnError(
            err ->
                assertThat(((StatusRuntimeException) err).getStatus().getDescription())
                    .isEqualTo("orgId header cannot be null"))
        .subscribe(
            record -> testContext.failNow("orgId header cannot be null"),
            err -> testContext.completeNow());
  }
}
