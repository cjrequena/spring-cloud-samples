package com.cjrequena.sample.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Configuration
public class WebClientConfiguration {

  @Value("${account-service.url}")
  private String accountServiceUrl;

  @Autowired
  private ReactorLoadBalancerExchangeFilterFunction lbFunction;

  @Bean("webClientBuilder")
  WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  @Bean("accountServiceWebClient")
  public WebClient accountServiceWebClient() {
    return webClientBuilder()
      .baseUrl(accountServiceUrl)
      .defaultHeaders(httpHeaders -> {
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
      })
      .build();
  }

  @LoadBalanced
  @Bean("lbAccountServiceWebClient")
  public WebClient lbAccountServiceWebClient() {
    return webClientBuilder()
      .filter(lbFunction)
      .baseUrl(accountServiceUrl)
      .defaultHeaders(httpHeaders -> {
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
      })
      .build();
  }

}
