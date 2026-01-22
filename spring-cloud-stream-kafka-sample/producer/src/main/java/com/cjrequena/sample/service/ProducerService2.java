package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.model.event.FooEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProducerService2 {

  private final Sinks.Many<Message<FooEvent>> sink = Sinks.many().multicast().onBackpressureBuffer();

  /**
   * Functional PRODUCER (Kafka output)
   */
  @Bean("producer2")
  public Supplier<Flux<Message<FooEvent>>> producer2() {
    return sink::asFlux;
  }

  /**
   * Imperative API used by your application
   */
  public void produce(FooEvent event) {

    Map<String, String> headers = new HashMap<>();
    headers.put(KafkaHeaders.KEY, String.valueOf(event.getId()));
    headers.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON.toString());

    Message<FooEvent> message = MessageBuilder.withPayload(event).copyHeaders(headers).build();

    Sinks.EmitResult result = sink.tryEmitNext(message);

    if (result.isFailure()) {
      log.error("Failed to emit FooEvent: {}", result);
    } else {
      log.info("Event emitted: {}", event);
    }
  }
}
