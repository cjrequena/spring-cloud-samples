package com.cjrequena.sample.service;

import com.cjrequena.sample.event.FooEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.Message;
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
@Component("consumer4")
public class ConsumerService4 extends EventConsumer<FooEvent> {

  private final Sinks.Many<FooEvent> defaultSink = Sinks.many().multicast().onBackpressureBuffer();
  private final Map<String, Sinks.Many<FooEvent>> sinks = new ConcurrentHashMap<>();

  private final ObjectMapper objectMapper;

  public ConsumerService4(
    BindingServiceProperties bindingServiceProperties,
    ApplicationContext applicationContext,
    ObjectMapper objectMapper
  ) {
    super(bindingServiceProperties, applicationContext);
    this.objectMapper = objectMapper;
  }

  @Override
  protected Mono<Void> processDeserializedMessage(Message<FooEvent> message) throws JsonProcessingException {
    log.info("New event notification: {}", message.getPayload());
    var sink = this.getSink(null);
    sink.tryEmitNext(message.getPayload());
    return Mono.empty();
  }

  @Override
  protected Message<FooEvent> deserializeMessage(Message<String> message) throws JsonProcessingException {
    FooEvent fooEvent = objectMapper.readValue(message.getPayload(), FooEvent.class);
    return MessageBuilder.withPayload(fooEvent).copyHeaders(message.getHeaders()).build();
  }

  public Flux<FooEvent> subscribe() {
    var sink = this.getSink(null);
    return sink.asFlux();
  }

  public Flux<ServerSentEvent<String>> subscribeV2() {
    var sink = this.getSink(null);
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
        .id(UUID.randomUUID().toString())
        .event(event.getType())
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

  private Sinks.Many<FooEvent> getSink(String name) {
    if (Objects.nonNull(name)) {
      return sinks.computeIfAbsent(name, key -> Sinks.many().replay().limit(Duration.ofSeconds(1)));
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
