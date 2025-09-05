package com.cjrequena.sample.configuration;

import com.cjrequena.sample.proto.AccountServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfiguration {
  @Bean
  public ManagedChannel managedChannel(@Value("${grpc.client.account-service.address}") String target) {
    return ManagedChannelBuilder.forTarget(target)
      .usePlaintext()
      .build();
  }

  @Bean("accountServiceBlockingStub")
  public AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub(ManagedChannel channel) {
    return AccountServiceGrpc.newBlockingStub(channel);
  }
}
