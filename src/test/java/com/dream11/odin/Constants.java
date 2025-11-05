package com.dream11.odin;

import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public final List<String> MYSQL_PREFIXES = Arrays.asList("mysql.master.", "mysql.slave.");
  public final String MYSQL_HOST_KEY = "connectOptions.host";
  public final String MYSQL_PORT_KEY = "connectOptions.port";
  public final String MYSQL_DATABASE_KEY = "connectOptions.database";
  public final String MYSQL_USER_KEY = "connectOptions.user";
  public final String MYSQL_PASSWORD_KEY = "connectOptions.password";
  public final String MYSQL_DATABASE = "odin_account_manager_test";
  public final String MYSQL_USER = "test_user";
  public final String MYSQL_PASSWORD = "test_password";
  public final String MYSQL_IMAGE_KEY = "mysql.image";
  public final String DEFAULT_MYSQL_IMAGE = "mysql:8.0.23";
  public final String APP_ENVIRONMENT = "app.environment";
  public final String VERTICLE_NAME = "com.dream11.odin.verticle.MainVerticle";
  public final String TEST_DATA_DIRECTORY_PATH = "src/test/resources/seed";
}
