package com.dream11.odin.verticle;

import com.dream11.grpc.AbstractGrpcVerticle;
import com.dream11.grpc.ClassInjector;
import com.dream11.odin.client.MysqlClient;
import com.dream11.odin.client.MysqlClientFactory;
import com.dream11.odin.config.AppConfig;
import com.dream11.odin.constant.Constants;
import com.dream11.odin.injector.GuiceInjector;
import com.dream11.odin.interceptor.CustomValidatingServerInterceptor;
import com.dream11.odin.util.VertxUtil;
import io.reactivex.Completable;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcVerticle extends AbstractGrpcVerticle {

  MysqlClient mysqlClient;

  public GrpcVerticle() {
    super("com.dream11.odin", new HttpServerOptions().setPort(8080));
  }

  @Override
  public ClassInjector getInjector() {
    return VertxUtil.getInstanceFromSharedData(this.vertx.getDelegate(), GuiceInjector.class);
  }

  @Override
  public Completable rxStart() {
    this.mysqlClient = MysqlClientFactory.getDefaultInstance(this.vertx);
    AppConfig appConfig =
        VertxUtil.getInstanceFromSharedData(this.vertx.getDelegate(), AppConfig.class);
    VertxUtil.setInstanceInContext(this.mysqlClient, Constants.MYSQL_CLIENT);
    return this.mysqlClient
        .rxConnect(JsonObject.mapFrom(appConfig.getMysql()))
        .andThen(super.rxStart());
  }

  @Override
  public Completable rxStop() {
    return this.mysqlClient
        .rxClose()
        .doOnComplete(() -> log.info("Database connections closed successfully"))
        .doOnError(err -> log.info("Failed to close database connections", err))
        .andThen(super.rxStop());
  }

  // Add validating interceptor to the list of interceptors at first position
  @Override
  protected List<Class<?>> getGrpcInterceptors() {
    List<Class<?>> interceptors = super.getGrpcInterceptors();
    interceptors.add(0, CustomValidatingServerInterceptor.class);
    return interceptors;
  }
}
