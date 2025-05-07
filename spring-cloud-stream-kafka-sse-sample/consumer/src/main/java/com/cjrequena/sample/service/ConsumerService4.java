package com.cjrequena.sample.service;

import com.cjrequena.sample.event.FooEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@Component("consumer4")
public class ConsumerService4 extends EventConsumer<FooEvent> {

  private final Sinks.Many<FooEvent> sink = Sinks.many().multicast().onBackpressureBuffer();
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
    sink.tryEmitNext(message.getPayload());
    return Mono.empty();
  }

  @Override
  protected Message<FooEvent> deserializeMessage(Message<String> message) throws JsonProcessingException {
    FooEvent fooEvent = objectMapper.readValue(message.getPayload(), FooEvent.class);
    return MessageBuilder.withPayload(fooEvent).copyHeaders(message.getHeaders()).build();
  }

  public Flux<FooEvent> getMessageStream() {
    return sink.asFlux();
  }
}
