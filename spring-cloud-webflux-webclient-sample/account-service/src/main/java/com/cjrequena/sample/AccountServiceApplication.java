package com.cjrequena.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableAsync
@EnableAutoConfiguration
@EnableDiscoveryClient
public class AccountServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccountServiceApplication.class, args);
  }
}
