package com.dream11.odin.grpc.health;

import static org.assertj.core.api.Assertions.assertThat;

import com.dream11.odin.Setup;
import com.dream11.odin.grpc.health.v1.HealthCheckRequest;
import com.dream11.odin.grpc.health.v1.HealthCheckResponse;
import com.dream11.odin.grpc.health.v1.RxHealthGrpc;
import io.grpc.ManagedChannel;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.grpc.VertxChannelBuilder;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Slf4j
@ExtendWith({VertxExtension.class, Setup.class})
class HealthcheckServiceV1IT {
  @Test
  void testHealthcheck(VertxTestContext testContext, Vertx vertx) {
    // Arrange
    ManagedChannel channel =
        VertxChannelBuilder.forAddress(vertx, "localhost", 8080).usePlaintext().build();
    RxHealthGrpc.RxHealthStub rxHealthcheckServiceStub = RxHealthGrpc.newRxStub(channel);

    // Act
    Single<HealthCheckResponse> responseSingle =
        rxHealthcheckServiceStub.check(HealthCheckRequest.getDefaultInstance());

    // Assert
    responseSingle
        .doOnSuccess(
            healthcheckResponse -> assertThat(healthcheckResponse.getStatusValue()).isEqualTo(1))
        .subscribe(healthcheckResponse -> testContext.completeNow(), testContext::failNow);
  }
}
