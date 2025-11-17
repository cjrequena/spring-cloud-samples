package com.cjrequena.sample.service;

import com.cjrequena.sample.common.JsonUtil;
import com.cjrequena.sample.event.FooEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

@Slf4j
@Service("consumer3")
public class ConsumerService3 implements Consumer<Flux<Message<String>>> {
  private static final String LOG_PROCESSING_ERROR = "Error processing event: {}. Error: ";
  private final Sinks.Many<FooEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

  @Override
  public void accept(Flux<Message<String>> messageFlux) {
    messageFlux.concatMap(this::execute).subscribe();
  }

  public Mono<Void> execute(Message<String> message)  {
    try {
      log.info("New event notification: {}", message);
      final String payload = message.getPayload();
      FooEvent event = JsonUtil.jsonStringToObject(payload, FooEvent.class);
      Sinks.EmitResult result = sink.tryEmitNext(event);
      if (result.isFailure()) {
        log.error("Failed to emit FooEvent: {}", result);
      }
      return Mono.empty();
    } catch (JsonProcessingException ex) {
      log.error(LOG_PROCESSING_ERROR, message, ex);
      throw new RuntimeException(ex);
    }
  }

  public Flux<FooEvent> subscribe() {
    return sink.asFlux();
  }
}
