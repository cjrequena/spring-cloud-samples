package com.cjrequena.sample.service;

import com.cjrequena.sample.event.FooEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConsumerService1 {

  private final Sinks.Many<FooEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

  @Bean("consumer1")
  public Consumer<FooEvent> consumer() {
    return event -> {
      log.info("New event notification: {}", event);
      sink.tryEmitNext(event);
    };
  }

  public Flux<FooEvent> subscribe() {
    return sink.asFlux();
  }
}
