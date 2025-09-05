package com.cjrequena.sample.configuration;

import com.cjrequena.sample.proto.AccountServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Log4j2
@Configuration
public class GrpcClientConfiguration {

  // Account Service Configuration
  @Value("${grpc.client.account-service.address}")
  private String accountServiceHost;

  @Value("${grpc.client.account-service.plaintext:true}")
  private boolean accountServicePlaintext;

  @Bean("accountServiceChannel")
  public ManagedChannel accountServiceChannel() {
    log.info("Creating AccountService gRPC channel: {}", accountServiceHost);

    ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forTarget(accountServiceHost);

    if (accountServicePlaintext) {
      channelBuilder.usePlaintext();
    }

    return channelBuilder
      .keepAliveTime(30, TimeUnit.SECONDS)
      .keepAliveTimeout(5, TimeUnit.SECONDS)
      .keepAliveWithoutCalls(true)
      .maxInboundMessageSize(1024 * 1024) // 1MB
      .build();
  }

  @Bean("accountServiceBlockingStub")
  public AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub(ManagedChannel channel) {
    return AccountServiceGrpc.newBlockingStub(channel);
  }

  @Bean("accountServiceAsyncStub")
  public AccountServiceGrpc.AccountServiceStub accountServiceAsyncStub() {
    return AccountServiceGrpc.newStub(accountServiceChannel());
  }

  @Bean("accountServiceFutureStub")
  public AccountServiceGrpc.AccountServiceFutureStub accountServiceFutureStub() {
    return AccountServiceGrpc.newFutureStub(accountServiceChannel());
  }
}
