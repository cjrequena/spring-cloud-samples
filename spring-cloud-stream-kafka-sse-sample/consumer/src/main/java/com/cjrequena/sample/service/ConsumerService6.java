package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.model.event.FooEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component("consumer6")
public class ConsumerService6 extends EventConsumer<FooEvent> {
  private static final String X_SUBSCRIPTION_KEY = "Subscription-Key";
  private final Sinks.Many<FooEvent> defaultSink = Sinks.many().multicast().onBackpressureBuffer();
  private final Map<String, Sinks.Many<FooEvent>> sinks = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper;

  public ConsumerService6(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  protected Mono<Void> processDeserializedMessage(Message<FooEvent> message) {
    final FooEvent payload = message.getPayload();
    final MessageHeaders headers = message.getHeaders();
    String subscriptionKey = headers.get(X_SUBSCRIPTION_KEY, String.class);
    var sink = this.getSink(subscriptionKey);
    Sinks.EmitResult result = sink.tryEmitNext(payload);
    if (result.isFailure()) {
      log.error("Failed to emit FooEvent: {}", result);
    }
    return Mono.empty();
  }

  @Override
  protected Message<FooEvent> deserializeMessage(Message<String> message) throws JsonProcessingException {
    FooEvent fooEvent = objectMapper.readValue(message.getPayload(), FooEvent.class);
    return MessageBuilder.withPayload(fooEvent).copyHeaders(message.getHeaders()).build();
  }

  public Flux<FooEvent> subscribe(String subscriptionKey) {
    var sink = this.getSink(subscriptionKey);
    return sink.asFlux();
  }

  public Flux<ServerSentEvent<String>> subscribeV2(String subscriptionKey) {
    var sink = this.getSink(subscriptionKey);
    return Flux.merge(
      getWelcomeEvent(),
      sink
        .asFlux()
        .map(this::buildServerSentEvent)
        .doOnNext(event -> log.debug("SSE emitted:{}", event))
    );
  }

  private ServerSentEvent<String> buildServerSentEvent(FooEvent event) {
    try {
      return ServerSentEvent.<String>builder()
        .id(event.getId())
        .event(event.getType())
        .comment("source: " + event.getSource())
        .data(objectMapper.writeValueAsString(event.getData()))
        .build();
    } catch (Exception e) {
      log.error("Error serializing data", e);
      return ServerSentEvent.<String>builder()
        .id(UUID.randomUUID().toString())
        .event("error")
        .data("Error serializing data")
        .retry(Duration.ofMillis(1000))
        .build();
    }
  }

  private Sinks.Many<FooEvent> getSink(String key) {
    if (Objects.nonNull(key)) {
      return sinks.computeIfAbsent(key, k -> Sinks.many().replay().limit(Duration.ofSeconds(1)));
    } else {
      return this.defaultSink;
    }
  }

  private Flux<ServerSentEvent<String>> getWelcomeEvent() {
    var welcomeEvent = ServerSentEvent.<String>builder()
      .id(UUID.randomUUID().toString())
      .event("sse-welcome-connection.v1")
      .data("Welcome to SSE")
      .retry(Duration.ofSeconds(1))
      .build();
    return Flux.just(welcomeEvent);
  }

}
