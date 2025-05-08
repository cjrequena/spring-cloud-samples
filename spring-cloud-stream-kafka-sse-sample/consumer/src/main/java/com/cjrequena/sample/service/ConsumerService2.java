package com.cjrequena.sample.service;

import com.cjrequena.sample.event.FooEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

@Slf4j
@Service("consumer2")
public class ConsumerService2 implements Consumer<Flux<Message<FooEvent>>> {

  private final Sinks.Many<FooEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

  public Mono<Void> execute(Message<FooEvent> message) {
    log.info("New event notification: {}", (FooEvent) message.getPayload());
    sink.tryEmitNext(message.getPayload());
    return Mono.empty();
  }

  @Override
  public void accept(Flux<Message<FooEvent>> flux) {
    flux.concatMap(this::execute).subscribe();
  }

  public Flux<FooEvent> subscribe() {
    return sink.asFlux();
  }
}
