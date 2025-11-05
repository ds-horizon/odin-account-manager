package com.dream11.odin.grpc.provideraccount;

import static org.assertj.core.api.Assertions.assertThat;

import com.dream11.odin.Setup;
import com.dream11.odin.dto.v1.ProviderAccount;
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
import com.dream11.odin.util.AssertionUtil;
import com.google.protobuf.Struct;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.grpc.VertxChannelBuilder;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Slf4j
@ExtendWith({VertxExtension.class, Setup.class})
class ProviderAccountServiceV1IT {

  static ManagedChannel channel;
  final JsonObject providerAccountData = new JsonObject().put("accountId", "testId");
  final JsonObject linkedProviderAccountData =
      new JsonObject()
          .put("url", "https://some-url.com")
          .put(
              "credentials",
              new JsonObject().put("password", "password").put("username", "username"));

  final JsonObject providerServiceAccount1Data =
      new JsonObject()
          .put("name", "testService")
          .put("roles", new JsonArray().add("testRole1").add("testRole2"));
  final JsonObject providerServiceAccount2Data = new JsonObject().put("repository", 1);

  static Stream<Arguments> testAccounts() {
    return Stream.of(
        Arguments.of("TEST_PROVIDER_ACCOUNT_1"), Arguments.of("TEST_PROVIDER_ACCOUNT_1"));
  }

  @BeforeAll
  static void setup(Vertx vertx) {
    channel = VertxChannelBuilder.forAddress(vertx, "localhost", 8080).usePlaintext().build();
  }

  private RxProviderAccountServiceGrpc.RxProviderAccountServiceStub createStubWithOrgId(
      String orgId) {
    Metadata metadata = new Metadata();
    metadata.put(Metadata.Key.of("orgId", Metadata.ASCII_STRING_MARSHALLER), orgId);
    return RxProviderAccountServiceGrpc.newRxStub(channel)
        .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));
  }

  private RxProviderAccountServiceGrpc.RxProviderAccountServiceStub createStubWithoutHeaders() {
    return RxProviderAccountServiceGrpc.newRxStub(channel);
  }

  @Test
  void testGetProviderAccountFailIfOrgIdMissing(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithoutHeaders();
    GetProviderAccountRequest request =
        GetProviderAccountRequest.newBuilder().setName("testAccount").build();

    // Act
    Single<GetProviderAccountResponse> responseSingle =
        rxProviderAccountServiceStub.getProviderAccount(request);

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
            record -> testContext.failNow("Should throw orgId cannot be null"),
            err -> testContext.completeNow());
  }

  @Test
  void testGetProviderAccountFailIfNameMissing(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1");
    GetProviderAccountRequest request = GetProviderAccountRequest.newBuilder().build();

    // Act
    Single<GetProviderAccountResponse> responseSingle =
        rxProviderAccountServiceStub.getProviderAccount(request);

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
                    .contains("length must be at least 1 but got: 0"))
        .subscribe(
            record -> testContext.failNow("Should throw name length cannot be 0"),
            err -> testContext.completeNow());
  }

  @Test
  void testGetProviderAccountFailIfNoMatchingAccount(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1");
    GetProviderAccountRequest request =
        GetProviderAccountRequest.newBuilder().setName("non_existing").build();

    // Act
    Single<GetProviderAccountResponse> responseSingle =
        rxProviderAccountServiceStub.getProviderAccount(request);

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
                    .isEqualTo("Provider account 'non_existing' does not exist"))
        .subscribe(
            record -> testContext.failNow("Should throw provider account does not exist error"),
            err -> testContext.completeNow());
  }

  @ParameterizedTest
  @MethodSource("testAccounts")
  void testGetProviderAccountWithoutLinkedAccounts(
      String accountName, VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1001");

    GetProviderAccountRequest request =
        GetProviderAccountRequest.newBuilder()
            .setName(accountName)
            .setFetchLinkedAccountDetails(false)
            .build();

    // Act
    Single<GetProviderAccountResponse> responseSingle =
        rxProviderAccountServiceStub.getProviderAccount(request);

    // Assert
    responseSingle
        .doOnSuccess(
            response -> {
              ProviderAccount providerAccount = response.getAccount();
              AssertionUtil.assertProviderAccount(
                  providerAccount,
                  "TEST_PROVIDER_ACCOUNT_1",
                  "TEST_PROVIDER_1",
                  "TEST_PROVIDER_CATEGORY_1",
                  providerAccountData);
              assertThat(providerAccount.getServicesCount()).isEqualTo(2);
              AssertionUtil.assertProviderServiceAccount(
                  providerAccount.getServices(0),
                  "TEST_SERVICE_1",
                  "TEST_SERVICE_CATEGORY_1",
                  1001L,
                  providerServiceAccount1Data);
              AssertionUtil.assertProviderServiceAccount(
                  providerAccount.getServices(1),
                  "TEST_SERVICE_2",
                  "TEST_SERVICE_CATEGORY_2",
                  1002L,
                  providerServiceAccount2Data);
              assertThat(response.getLinkedAccountsCount()).isZero();
            })
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @ParameterizedTest
  @MethodSource("testAccounts")
  void testGetProviderAccountWithLinkedAccounts(String accountName, VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1001");
    GetProviderAccountRequest request =
        GetProviderAccountRequest.newBuilder()
            .setName(accountName)
            .setFetchLinkedAccountDetails(true)
            .build();

    // Act
    Single<GetProviderAccountResponse> responseSingle =
        rxProviderAccountServiceStub.getProviderAccount(request);

    // Assert
    responseSingle
        .doOnSuccess(
            response -> {
              ProviderAccount providerAccount = response.getAccount();
              AssertionUtil.assertProviderAccount(
                  providerAccount,
                  "TEST_PROVIDER_ACCOUNT_1",
                  "TEST_PROVIDER_1",
                  "TEST_PROVIDER_CATEGORY_1",
                  providerAccountData);
              assertThat(providerAccount.getServicesCount()).isEqualTo(2);
              AssertionUtil.assertProviderServiceAccount(
                  providerAccount.getServices(0),
                  "TEST_SERVICE_1",
                  "TEST_SERVICE_CATEGORY_1",
                  1001L,
                  providerServiceAccount1Data);
              AssertionUtil.assertProviderServiceAccount(
                  providerAccount.getServices(1),
                  "TEST_SERVICE_2",
                  "TEST_SERVICE_CATEGORY_2",
                  1002L,
                  providerServiceAccount2Data);

              assertThat(response.getLinkedAccountsCount()).isEqualTo(1);
              ProviderAccount linkedProviderAccount = response.getLinkedAccounts(0);
              AssertionUtil.assertProviderAccount(
                  linkedProviderAccount,
                  "TEST_PROVIDER_ACCOUNT_2",
                  "TEST_PROVIDER_2",
                  "TEST_PROVIDER_CATEGORY_2",
                  linkedProviderAccountData);
              assertThat(linkedProviderAccount.getServicesCount()).isOne();
            })
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  void testGetProviderAccountsFailIfOrgIdMissing(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithoutHeaders();
    GetProviderAccountsRequest request =
        GetProviderAccountsRequest.newBuilder().addName("testAccount").build();

    // Act
    Single<GetProviderAccountsResponse> responseSingle =
        rxProviderAccountServiceStub.getProviderAccounts(request);

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
            record -> testContext.failNow("Should throw orgId cannot be null"),
            err -> testContext.completeNow());
  }

  @Test
  void testGetProviderAccountsFailIfNameMissing(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1");
    GetProviderAccountsRequest request = GetProviderAccountsRequest.newBuilder().build();

    // Act
    Single<GetProviderAccountsResponse> responseSingle =
        rxProviderAccountServiceStub.getProviderAccounts(request);

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
                    .contains("length must be at least 1 but got: 0"))
        .subscribe(
            record -> testContext.failNow("Should throw name length cannot be 0"),
            err -> testContext.completeNow());
  }

  @Test
  void testGetProviderAccountsFailIfNoMatchingAccount(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1");
    GetProviderAccountsRequest request =
        GetProviderAccountsRequest.newBuilder().addName("non_existing").build();

    // Act
    Single<GetProviderAccountsResponse> responseSingle =
        rxProviderAccountServiceStub.getProviderAccounts(request);

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
                    .isEqualTo("Provider account 'non_existing' does not exist"))
        .subscribe(
            record -> testContext.failNow("Should throw provider account does not exist error"),
            err -> testContext.completeNow());
  }

  @ParameterizedTest
  @MethodSource("testAccounts")
  void testGetProviderAccountsWithoutLinkedAccounts(
      String accountName, VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1001");

    log.info("accountName: " + accountName);
    GetProviderAccountsRequest request =
        GetProviderAccountsRequest.newBuilder()
            .addName(accountName)
            .setFetchLinkedAccountDetails(false)
            .build();

    // Act
    Single<GetProviderAccountsResponse> responseSingle =
        rxProviderAccountServiceStub.getProviderAccounts(request);

    // Assert
    responseSingle
        .doOnSuccess(
            response -> {
              List<GetProviderAccountResponse> providerAccountResponses =
                  response.getAccountsList();
              AssertionUtil.assertProviderAccounts(
                  providerAccountResponses,
                  "TEST_PROVIDER_ACCOUNT_1",
                  "TEST_PROVIDER_1",
                  "TEST_PROVIDER_CATEGORY_1",
                  providerAccountData);
              ProviderAccount providerAccount = providerAccountResponses.get(0).getAccount();
              assertThat(providerAccount.getServicesCount()).isEqualTo(2);
              AssertionUtil.assertProviderServiceAccount(
                  providerAccount.getServices(0),
                  "TEST_SERVICE_1",
                  "TEST_SERVICE_CATEGORY_1",
                  1001L,
                  providerServiceAccount1Data);
              AssertionUtil.assertProviderServiceAccount(
                  providerAccount.getServices(1),
                  "TEST_SERVICE_2",
                  "TEST_SERVICE_CATEGORY_2",
                  1002L,
                  providerServiceAccount2Data);
              assertThat(response.getAccountsList().get(0).getLinkedAccountsCount()).isZero();
            })
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @ParameterizedTest
  @MethodSource("testAccounts")
  void testGetProviderAccountsWithLinkedAccounts(String accountName, VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1001");
    GetProviderAccountsRequest request =
        GetProviderAccountsRequest.newBuilder()
            .addName(accountName)
            .setFetchLinkedAccountDetails(true)
            .build();

    // Act
    Single<GetProviderAccountsResponse> responseSingle =
        rxProviderAccountServiceStub.getProviderAccounts(request);

    // Assert
    responseSingle
        .doOnSuccess(
            response -> {
              List<GetProviderAccountResponse> providerAccountResponses =
                  response.getAccountsList();
              AssertionUtil.assertProviderAccounts(
                  providerAccountResponses,
                  "TEST_PROVIDER_ACCOUNT_1",
                  "TEST_PROVIDER_1",
                  "TEST_PROVIDER_CATEGORY_1",
                  providerAccountData);
              ProviderAccount providerAccount = providerAccountResponses.get(0).getAccount();
              assertThat(providerAccount.getServicesCount()).isEqualTo(2);
              AssertionUtil.assertProviderServiceAccount(
                  providerAccount.getServices(0),
                  "TEST_SERVICE_1",
                  "TEST_SERVICE_CATEGORY_1",
                  1001L,
                  providerServiceAccount1Data);
              AssertionUtil.assertProviderServiceAccount(
                  providerAccount.getServices(1),
                  "TEST_SERVICE_2",
                  "TEST_SERVICE_CATEGORY_2",
                  1002L,
                  providerServiceAccount2Data);

              assertThat(response.getAccountsList().get(0).getLinkedAccountsCount()).isEqualTo(1);
              ProviderAccount linkedProviderAccount =
                  response.getAccountsList().get(0).getLinkedAccounts(0);
              AssertionUtil.assertProviderAccount(
                  linkedProviderAccount,
                  "TEST_PROVIDER_ACCOUNT_2",
                  "TEST_PROVIDER_2",
                  "TEST_PROVIDER_CATEGORY_2",
                  linkedProviderAccountData);
              assertThat(linkedProviderAccount.getServicesCount()).isOne();
            })
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  void testGetAllProviderAccountsWithoutLinkedAccounts(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1001");

    GetAllProviderAccountsRequest request =
        GetAllProviderAccountsRequest.newBuilder().setFetchLinkedAccountDetails(false).build();

    // Act
    Single<GetAllProviderAccountsResponse> responseSingle =
        rxProviderAccountServiceStub.getAllProviderAccounts(request);

    // Assert
    responseSingle
        .doOnSuccess(
            response -> {
              List<GetProviderAccountResponse> providerAccountResponses =
                  response.getAccountsList();
              AssertionUtil.assertAllProviderAccounts(
                  providerAccountResponses,
                  List.of("TEST_PROVIDER_ACCOUNT_1", "TEST_PROVIDER_ACCOUNT_2"),
                  List.of("TEST_PROVIDER_1", "TEST_PROVIDER_2"),
                  List.of("TEST_PROVIDER_CATEGORY_1", "TEST_PROVIDER_CATEGORY_2"),
                  List.of(providerAccountData, linkedProviderAccountData));
              ProviderAccount providerAccount = providerAccountResponses.get(0).getAccount();
              assertThat(providerAccount.getServicesCount()).isEqualTo(2);
              AssertionUtil.assertProviderServiceAccount(
                  providerAccount.getServices(0),
                  "TEST_SERVICE_1",
                  "TEST_SERVICE_CATEGORY_1",
                  1001L,
                  providerServiceAccount1Data);
              AssertionUtil.assertProviderServiceAccount(
                  providerAccount.getServices(1),
                  "TEST_SERVICE_2",
                  "TEST_SERVICE_CATEGORY_2",
                  1002L,
                  providerServiceAccount2Data);
              assertThat(response.getAccountsList().get(0).getLinkedAccountsCount()).isZero();
            })
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  void testCreateProviderCategorySuccess(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1001");

    CreateProviderCategoryRequest request =
        CreateProviderCategoryRequest.newBuilder().setName("TEST_PROVIDER_CATEGORY_TEMP").build();

    // Act
    Single<CreateProviderCategoryResponse> responseSingle =
        rxProviderAccountServiceStub.createProviderCategory(request);

    // Assert
    responseSingle
        .doOnSuccess(
            response -> {
              assertThat(response.getSuccess()).isEqualTo(true);
            })
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  void testCreateProviderCategoryAlreadyExists(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1001");

    CreateProviderCategoryRequest request =
        CreateProviderCategoryRequest.newBuilder().setName("TEST_PROVIDER_CATEGORY_1").build();

    // Act
    Single<CreateProviderCategoryResponse> responseSingle =
        rxProviderAccountServiceStub.createProviderCategory(request);

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
                    .isEqualTo("Provider category 'TEST_PROVIDER_CATEGORY_1' already exists"))
        .subscribe(
            record ->
                testContext.failNow("Provider category 'TEST_PROVIDER_CATEGORY_1' already exists"),
            err -> testContext.completeNow());
  }

  @Test
  void testCreateProviderSuccess(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1001");

    CreateProviderRequest request =
        CreateProviderRequest.newBuilder()
            .setName("TEST_PROVIDER")
            .setProviderCategoryName("TEST_PROVIDER_CATEGORY_1")
            .setDataSchema(Struct.newBuilder().build())
            .build();

    // Act
    Single<CreateProviderResponse> responseSingle =
        rxProviderAccountServiceStub.createProvider(request);

    // Assert
    responseSingle
        .doOnSuccess(
            response -> {
              assertThat(response.getSuccess()).isEqualTo(true);
            })
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  void testCreateProviderAlreadyExistsFailure(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1001");

    CreateProviderRequest request =
        CreateProviderRequest.newBuilder()
            .setName("TEST_PROVIDER_1")
            .setProviderCategoryName("TEST_PROVIDER_CATEGORY_1")
            .setDataSchema(Struct.newBuilder().build())
            .build();

    // Act
    Single<CreateProviderResponse> responseSingle =
        rxProviderAccountServiceStub.createProvider(request);

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
                    .isEqualTo("Provider 'TEST_PROVIDER_1' already exists"))
        .subscribe(
            record -> testContext.failNow("Provider 'TEST_PROVIDER_1' already exists"),
            err -> testContext.completeNow());
  }

  @Test
  void testCreateProviderOrgIdValidationFailure(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        RxProviderAccountServiceGrpc.newRxStub(channel);
    CreateProviderRequest request =
        CreateProviderRequest.newBuilder()
            .setName("TEST_PROVIDER")
            .setProviderCategoryName("TEST_PROVIDER_CATEGORY_1")
            .setDataSchema(Struct.newBuilder().build())
            .build();

    // Act
    Single<CreateProviderResponse> responseSingle =
        rxProviderAccountServiceStub.createProvider(request);

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
  void testCreateProviderAccountSuccess(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1001");

    CreateProviderAccountRequest request =
        CreateProviderAccountRequest.newBuilder()
            .setName("TEST_PROVIDER_ACCOUNT")
            .setProviderName("TEST_PROVIDER_1")
            .setProviderData(Struct.newBuilder().build())
            .setIsDefault(true)
            .addLinkedAccounts("TEST_PROVIDER_ACCOUNT_1")
            .build();

    // Act
    Single<CreateProviderAccountResponse> responseSingle =
        rxProviderAccountServiceStub.createProviderAccount(request);

    // Assert
    responseSingle
        .doOnSuccess(
            response -> {
              assertThat(response.getSuccess()).isEqualTo(true);
            })
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  void testCreateProviderAccountAlreadyExistsFailure(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        createStubWithOrgId("1001");

    CreateProviderAccountRequest request =
        CreateProviderAccountRequest.newBuilder()
            .setName("TEST_PROVIDER_ACCOUNT_1")
            .setProviderName("TEST_PROVIDER_1")
            .setProviderData(Struct.newBuilder().build())
            .setIsDefault(true)
            .addLinkedAccounts("TEST_PROVIDER_ACCOUNT_2")
            .build();

    // Act
    Single<CreateProviderAccountResponse> responseSingle =
        rxProviderAccountServiceStub.createProviderAccount(request);

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
                        "Duplicate entry '1001-TEST_PROVIDER_ACCOUNT_1' for key 'provider_account.org_id'"))
        .subscribe(
            record ->
                testContext.failNow(
                    "Duplicate entry '1001-TEST_PROVIDER_ACCOUNT_1' for key 'provider_account.org_id'"),
            err -> testContext.completeNow());
  }

  @Test
  void testCreateProviderAccountOrgIdValidationFailure(VertxTestContext testContext) {
    // Arrange
    RxProviderAccountServiceGrpc.RxProviderAccountServiceStub rxProviderAccountServiceStub =
        RxProviderAccountServiceGrpc.newRxStub(channel);
    CreateProviderAccountRequest request =
        CreateProviderAccountRequest.newBuilder()
            .setName("TEST_PROVIDER_ACCOUNT_1")
            .setProviderName("TEST_PROVIDER_1")
            .setProviderData(Struct.newBuilder().build())
            .setIsDefault(true)
            .addLinkedAccounts("TEST_PROVIDER_ACCOUNT_2")
            .build();

    // Act
    Single<CreateProviderAccountResponse> responseSingle =
        rxProviderAccountServiceStub.createProviderAccount(request);

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
