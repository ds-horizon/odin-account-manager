package com.dream11.odin.util;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.vertx.core.json.JsonObject;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {
  public JsonObject getJsonObjectFromNestedJson(JsonObject json, String flattenedKey) {
    JsonObject cur = json;
    String[] keys = flattenedKey.split("\\.");
    for (String key : keys) {
      if (!cur.containsKey(key)) {
        return new JsonObject();
      }
      cur = cur.getJsonObject(key);
    }
    return cur;
  }

  @SneakyThrows
  public <T extends Message.Builder> T jsonToProtoBuilder(JsonObject json, T builder) {
    JsonFormat.parser().ignoringUnknownFields().merge(json.encode(), builder);
    return builder;
  }
}
