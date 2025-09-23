package com.cjrequena.sample.configuration;

import com.cjrequena.sample.exception.ErrorDTO;
import com.cjrequena.sample.exception.service.InsufficientBalanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Configuration
@Slf4j
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
      .filter(errorHandler())
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
      .filter(errorHandler())
      .build();
  }

  private static ExchangeFilterFunction errorHandler() {
    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      if (clientResponse.statusCode().isError()) {
        return clientResponse
          .bodyToMono(ErrorDTO.class)
          .flatMap(errorDTO -> {
            if (errorDTO.getErrorCode().equals(InsufficientBalanceException.class.getSimpleName())) {
              return Mono.error(new InsufficientBalanceException(errorDTO.getMessage()));
            }else{
              return Mono.just(clientResponse);
            }
          });
      } else {
        return Mono.just(clientResponse);
      }
    });
  }

}
