package com.dream11.odin.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.dream11.odin.dto.v1.ProviderAccount;
import com.dream11.odin.dto.v1.ProviderServiceAccount;
import com.dream11.odin.grpc.provideraccount.v1.GetProviderAccountResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.vertx.core.json.JsonObject;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AssertionUtil {

  public static JsonObject protoMessageToJsonObject(Message message)
      throws InvalidProtocolBufferException {
    return new JsonObject(JsonFormat.printer().print(message));
  }

  public static void assertProviderAccount(
      ProviderAccount providerAccount,
      String name,
      String provider,
      String category,
      JsonObject providerAccountData)
      throws InvalidProtocolBufferException {
    assertThat(providerAccount.getName()).isEqualTo(name);
    assertThat(providerAccount.getProvider()).isEqualTo(provider);
    assertThat(providerAccount.getCategory()).isEqualTo(category);
    assertThat(protoMessageToJsonObject(providerAccount.getData())).isEqualTo(providerAccountData);
  }

  public static void assertProviderServiceAccount(
      ProviderServiceAccount providerAServiceAccount,
      String name,
      String category,
      Long id,
      JsonObject providerServiceAccountData)
      throws InvalidProtocolBufferException {
    assertThat(providerAServiceAccount.getName()).isEqualTo(name);
    assertThat(providerAServiceAccount.getCategory()).isEqualTo(category);
    assertThat(providerAServiceAccount.getId()).isEqualTo(id);
    assertThat(protoMessageToJsonObject(providerAServiceAccount.getData()))
        .isEqualTo(providerServiceAccountData);
  }

  public static void assertProviderAccounts(
      List<GetProviderAccountResponse> providerAccountResponses,
      String name,
      String provider,
      String category,
      JsonObject providerAccountData)
      throws InvalidProtocolBufferException {
    for (GetProviderAccountResponse providerAccountResponse : providerAccountResponses) {
      ProviderAccount providerAccount = providerAccountResponse.getAccount();
      assertThat(providerAccount.getName()).isEqualTo(name);
      assertThat(providerAccount.getProvider()).isEqualTo(provider);
      assertThat(providerAccount.getCategory()).isEqualTo(category);
      assertThat(protoMessageToJsonObject(providerAccount.getData()))
          .isEqualTo(providerAccountData);
    }
  }

  public static void assertAllProviderAccounts(
      List<GetProviderAccountResponse> providerAccountResponses,
      List<String> names,
      List<String> providers,
      List<String> categories,
      List<JsonObject> providerAccountData)
      throws InvalidProtocolBufferException {
    for (int i = 0; i < providerAccountResponses.size(); i++) {
      ProviderAccount providerAccount = providerAccountResponses.get(i).getAccount();
      assertThat(providerAccount.getName()).isEqualTo(names.get(i));
      assertThat(providerAccount.getProvider()).isEqualTo(providers.get(i));
      assertThat(providerAccount.getCategory()).isEqualTo(categories.get(i));
      assertThat(protoMessageToJsonObject(providerAccount.getData()))
          .isEqualTo(providerAccountData.get(i));
    }
  }
}
