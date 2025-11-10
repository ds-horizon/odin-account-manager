package com.dream11.odin.util;

import static org.assertj.core.api.Assertions.*;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({VertxExtension.class})
class VertxUtilTest {

  @Test
  void testGetInstanceFromSharedData(Vertx vertx) {
    // Arrange
    JsonObject jsonObject = new JsonObject().put("key", "value");
    VertxUtil.setInstanceInSharedData(vertx, jsonObject);

    // Act
    JsonObject getFromSharedData = VertxUtil.getInstanceFromSharedData(vertx, JsonObject.class);

    // Assert
    assertThat(getFromSharedData).isEqualTo(jsonObject);
  }

  @Test
  void testGetInstanceFromSharedDataFailIfNotFound(Vertx vertx) {
    // Act & Assert
    assertThatThrownBy(() -> VertxUtil.getInstanceFromSharedData(vertx, JsonObject.class))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage("Cannot find default instance of io.vertx.core.json.JsonObject");
  }
}
