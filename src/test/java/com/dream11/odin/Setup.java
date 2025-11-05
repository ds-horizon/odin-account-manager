package com.dream11.odin;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import com.dream11.odin.injector.GuiceInjector;
import com.dream11.odin.util.VertxUtil;
import com.google.inject.Guice;
import io.vertx.reactivex.core.Vertx;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MySQLContainer;

@Slf4j
public class Setup
    implements BeforeAllCallback, AfterAllCallback, ExtensionContext.Store.CloseableResource {

  static boolean started = false;
  final Vertx vertx = Vertx.vertx();
  MySQLContainer<?> mySQLContainer;

  Connection connection;

  @Override
  public void afterAll(ExtensionContext extensionContext) {}

  @Override
  public void beforeAll(ExtensionContext extensionContext) throws SQLException, LiquibaseException {
    if (!started) {
      this.mySQLContainer =
          new MySQLContainer<>(
                  System.getProperty(Constants.MYSQL_IMAGE_KEY, Constants.DEFAULT_MYSQL_IMAGE))
              .withDatabaseName(Constants.MYSQL_DATABASE)
              .withUsername(Constants.MYSQL_USER)
              .withPassword(Constants.MYSQL_PASSWORD);
      mySQLContainer.start();
      log.info("Started mysql container on port:{}", mySQLContainer.getFirstMappedPort());
      this.setMysqlSystemProperties(mySQLContainer);
      this.runDatabaseMigrations();
      this.startApplication();

      started = true;
      extensionContext.getRoot().getStore(GLOBAL).put("test", this);
    }
  }

  //  @Override
  public void close() {
    log.info("Closing all resources");
    this.mySQLContainer.close();
    this.vertx.close();
  }

  private void startApplication() {
    log.info("Starting application for running integration tests");
    GuiceInjector injector =
        new GuiceInjector(Guice.createInjector(List.of(new MainModule(vertx.getDelegate()))));
    VertxUtil.setInstanceInSharedData(vertx.getDelegate(), injector);

    String deploymentId =
        this.vertx
            .rxDeployVerticle(Constants.VERTICLE_NAME)
            .doOnError(
                error ->
                    log.error("Error in deploying verticle : {}", Constants.VERTICLE_NAME, error))
            .doOnSuccess(v -> log.info("Deployed verticle : {}", Constants.VERTICLE_NAME))
            .blockingGet();
  }

  private void setMysqlSystemProperties(MySQLContainer<?> mySQLContainer) {
    for (String PREFIX : Constants.MYSQL_PREFIXES) {
      System.setProperty(PREFIX + Constants.MYSQL_HOST_KEY, mySQLContainer.getHost());
      System.setProperty(
          PREFIX + Constants.MYSQL_PORT_KEY, String.valueOf(mySQLContainer.getFirstMappedPort()));
      System.setProperty(PREFIX + Constants.MYSQL_DATABASE_KEY, mySQLContainer.getDatabaseName());
      System.setProperty(PREFIX + Constants.MYSQL_USER_KEY, mySQLContainer.getUsername());
      System.setProperty(PREFIX + Constants.MYSQL_PASSWORD_KEY, mySQLContainer.getPassword());
    }
    System.setProperty(Constants.APP_ENVIRONMENT, "test");
  }

  private void runDatabaseMigrations() throws SQLException, LiquibaseException {
    log.info("Starting migrations on data sources");
    String dbUrl =
        String.format(
            "jdbc:mysql://%s:%d/%s",
            mySQLContainer.getHost(),
            mySQLContainer.getFirstMappedPort(),
            Constants.MYSQL_DATABASE);
    try (Connection conn =
        DriverManager.getConnection(
            dbUrl, mySQLContainer.getUsername(), mySQLContainer.getPassword())) {
      Database database =
          DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));

      try (Liquibase liquibase =
          new Liquibase(
              "testdb-migrate-changelog.xml", new ClassLoaderResourceAccessor(), database)) {
        liquibase.update((String) null);
      }
    }
  }
}
