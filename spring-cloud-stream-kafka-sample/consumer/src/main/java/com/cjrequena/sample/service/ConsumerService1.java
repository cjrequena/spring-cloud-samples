package com.cjrequena.sample.service;

import com.cjrequena.sample.event.FooEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service
public class ConsumerService1 {

  @Bean("consumer1")
  public Consumer<FooEvent> consumer() {
    return event -> {
      log.info("New event notification: {}", event);
    };
  }
}
