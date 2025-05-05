package com.cjrequena.sample.service;

import com.cjrequena.sample.event.FooEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Slf4j
@Service("consumer2")
public class ConsumerService2 implements Consumer<Flux<Message<FooEvent>>> {

  public Mono<Void> execute(Message<FooEvent> message) {
    log.info("New event notification: {}", message.getPayload());
    return Mono.empty();
  }

  @Override
  public void accept(Flux<Message<FooEvent>> flux) {
    flux.concatMap(this::execute).subscribe();
  }
}
