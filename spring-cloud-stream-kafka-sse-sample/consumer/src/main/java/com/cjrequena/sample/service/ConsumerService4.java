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
@Component("consumer4")
public class ConsumerService4 implements Function<Flux<Message<FooEvent>>, Mono<Void>> {

  private final Sinks.Many<FooEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

  @Override
  public Mono<Void> apply(Flux<Message<FooEvent>> messageFlux) {
    return messageFlux
      .doOnNext(message -> {
        log.info("New event notification: {}", message);
        final FooEvent payload = message.getPayload();
        Sinks.EmitResult result = sink.tryEmitNext(payload);
        if (result.isFailure()) {
          log.error("Failed to emit FooEvent: {}", result);
        }
      })
      .then(); // Ensure completion is returned as Mono<Void>
  }

  public Flux<FooEvent> subscribe() {
    return sink.asFlux();
  }
}
