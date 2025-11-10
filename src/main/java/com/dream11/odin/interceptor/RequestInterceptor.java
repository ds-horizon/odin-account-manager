package com.dream11.odin.interceptor;

import com.dream11.grpc.annotation.GrpcInterceptor;
import com.dream11.odin.constant.Constants;
import io.grpc.Metadata;
import io.vertx.grpc.ContextServerInterceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GrpcInterceptor
public class RequestInterceptor extends ContextServerInterceptor {
  @Override
  public void bind(Metadata metadata) {
    String orgId =
        metadata.get(
            Metadata.Key.of(Constants.ORGANIZATION_HEADER, Metadata.ASCII_STRING_MARSHALLER));
    if (orgId != null) {
      log.debug("Added orgId {} top context", orgId);
      ContextServerInterceptor.put(Constants.ORGANIZATION_HEADER, Long.parseLong(orgId));
    }
  }
}
