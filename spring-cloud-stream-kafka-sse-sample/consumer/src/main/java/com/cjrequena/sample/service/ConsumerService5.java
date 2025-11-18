package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.model.event.FooEvent;
import com.cjrequena.sample.shared.common.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Component("consumer5")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConsumerService5 implements Function<Flux<Message<String>>, Mono<Void>> {
  private static final String LOG_PROCESSING_ERROR = "Error processing event: {}. Error: ";
  private static final String X_SUBSCRIPTION_KEY = "Subscription-Key";
  private final Sinks.Many<FooEvent> defaultSink = Sinks.many().multicast().onBackpressureBuffer();
  private final Map<String, Sinks.Many<FooEvent>> sinks = new ConcurrentHashMap<>();

  private final ObjectMapper objectMapper;

  @Override
  public Mono<Void> apply(Flux<Message<String>> messageFlux) {
    return messageFlux
      .doOnNext(message -> {
        log.info("Processing new message: {}", message);
        handleMessage(message);
      })
      .then();
  }

  protected void handleMessage(Message<String> message) {
    try {
      final String payload = message.getPayload();
      final MessageHeaders headers = message.getHeaders();
      String subscriptionKey = headers.get(X_SUBSCRIPTION_KEY, String.class);
      FooEvent event = JsonUtil.jsonStringToObject(payload, FooEvent.class);
      var sink = this.getSink(subscriptionKey);
      Sinks.EmitResult result = sink.tryEmitNext(event);
      if (result.isFailure()) {
        log.error("Failed to emit FooEvent: {}", result);
      }
    } catch (JsonProcessingException ex) {
      log.error(LOG_PROCESSING_ERROR, message, ex);
    }
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
