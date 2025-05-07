package com.cjrequena.sample.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class EventConsumer<T> implements Function<Flux<Message<String>>, Mono<Void>> {
  private static final String LOG_PROCESSING_ERROR = "Error processing event: {}. Error: ";

  private final BindingServiceProperties bindingServiceProperties;
  private final ApplicationContext context;

  public Mono<Void> apply(Flux<Message<String>> eventFlux) {
    return eventFlux
      .concatMap(message -> processSerializedMessage(message)
      .onErrorResume(throwable -> {
        log.error(LOG_PROCESSING_ERROR, message, throwable);
        return Mono.empty();
      })).then();
  }

  private Mono<Void> processSerializedMessage(Message<String> message) {
    try {
      var deserializedMessage = deserializeMessage(message);
      return processDeserializedMessage(deserializedMessage);
    } catch (Exception e) {
      return Mono.error(e);
    }
  }

  protected abstract Mono<Void> processDeserializedMessage(Message<T> message) throws JsonProcessingException;

  protected abstract Message<T> deserializeMessage(Message<String> message) throws JsonProcessingException;

}
