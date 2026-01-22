package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.mapper.FooMapper;
import com.cjrequena.sample.domain.model.event.FooEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Service("consumer3")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConsumerService3 implements Function<Flux<Message<String>>, Mono<Void>> {

  private final FooMapper fooMapper;

  private Mono<Void> handleMessage(Message<String> message) {
    log.info("New event notification: {}", message);

    String payload = message.getPayload();
    // Extract headers
    MessageHeaders headers = message.getHeaders();
    Object kafkaKey = headers.get("kafka_receivedMessageKey");
    Object topic = headers.get("kafka_receivedTopic");
    Object partition = headers.get("kafka_receivedPartitionId");
    Object offset = headers.get("kafka_offset");
    Object headerTest = headers.get("x-test-header");

    log.info("Received from topic={}, partition={}, offset={}, key={}, payload={}", topic, partition, offset, kafkaKey, payload);

    FooEvent event = this.fooMapper.mapToFooEventFromJsonString(payload);
    return Mono.empty();
  }

  @Override
  public Mono<Void> apply(Flux<Message<String>> messageFlux) {
    return messageFlux
      .concatMap(this::handleMessage) // sequential processing; use flatMap for parallel
      .then();
  }
}
