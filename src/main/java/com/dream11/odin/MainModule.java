package com.dream11.odin;

import com.dream11.odin.client.MysqlClient;
import com.dream11.odin.constant.Constants;
import com.dream11.odin.util.VertxUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.inject.AbstractModule;
import io.vertx.core.Vertx;

public class MainModule extends AbstractModule {
  private final Vertx vertx;
  private final ObjectMapper objectMapper =
      JsonMapper.builder()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
          .serializationInclusion(JsonInclude.Include.NON_NULL)
          .build();

  public MainModule(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  protected void configure() {
    bind(Vertx.class).toInstance(this.vertx);
    bind(ObjectMapper.class).toInstance(this.objectMapper);
    bind(MysqlClient.class)
        .toProvider(() -> VertxUtil.getInstanceFromContext(Constants.MYSQL_CLIENT));
  }
}
