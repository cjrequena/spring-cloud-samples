package com.cjrequena.sample.service;

import com.cjrequena.sample.event.FooEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.function.Function;

@Slf4j
@Component("consumer3")
public class ConsumerService3 implements Function<Flux<Message<FooEvent>>, Mono<Void>> {

  private final Sinks.Many<FooEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

  @Override
  public Mono<Void> apply(Flux<Message<FooEvent>> flux) {
    return flux
      .doOnNext(message -> {
        log.info("New event notification: {}", message.getPayload());
        sink.tryEmitNext(message.getPayload());
      })
      .then(); // Ensure completion is returned as Mono<Void>
  }

  public Flux<FooEvent> getMessageStream() {
    return sink.asFlux();
  }
}
