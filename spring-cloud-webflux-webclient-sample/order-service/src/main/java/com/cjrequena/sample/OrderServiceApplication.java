package com.cjrequena.sample;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableDiscoveryClient
@EnableWebFlux
@EnableAutoConfiguration
@EnableScheduling
public class OrderServiceApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(OrderServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
  }
}
