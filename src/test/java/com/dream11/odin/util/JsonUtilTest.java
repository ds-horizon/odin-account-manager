package com.dream11.odin.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

class JsonUtilTest {
  @Test
  void testGetJsonObjectFromNestedJson() {
    // arrange
    JsonObject nestedObject = new JsonObject().put("key", "Hello");
    JsonObject jsonObject =
        new JsonObject().put("key", new JsonObject().put("nestedKey", nestedObject));

    // act
    JsonObject nestedJsonObject = JsonUtil.getJsonObjectFromNestedJson(jsonObject, "key.nestedKey");

    // assert
    assertThat(nestedJsonObject).isEqualTo(nestedObject);
  }

  @Test
  void testGetJsonObjectFromNestedJsonNonExistingKey() {
    // arrange
    JsonObject nestedObject = new JsonObject().put("key", "Hello");
    JsonObject jsonObject =
        new JsonObject().put("key", new JsonObject().put("nestedKey", nestedObject));

    // act
    JsonObject nestedJsonObject =
        JsonUtil.getJsonObjectFromNestedJson(jsonObject, "key.nonExisting");

    // assert
    assertThat(nestedJsonObject).isEqualTo(new JsonObject());
  }

  @Test
  void testJsonToProtoBuilder() throws InvalidProtocolBufferException {
    // arrange
    JsonObject jsonObject = new JsonObject().put("k1", "v1").put("k2", 1);

    // act
    Struct.Builder builder = JsonUtil.jsonToProtoBuilder(jsonObject, Struct.newBuilder());

    // assert
    assertThat(builder.getFieldsCount()).isEqualTo(2);
    assertThat(builder.getFieldsMap())
        .containsKey("k1")
        .containsEntry("k1", Value.newBuilder().setStringValue("v1").build())
        .containsKey("k2")
        .containsEntry("k2", Value.newBuilder().setNumberValue(1).build());
  }
}
