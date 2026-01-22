package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.model.event.FooEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Slf4j
@Service("consumer2")
public class ConsumerService2 implements Consumer<Flux<Message<FooEvent>>> {

  public Mono<Void> execute(Message<FooEvent> message) {
    log.info("New event notification: {}", message.getPayload());
    FooEvent fooEvent = message.getPayload();
    // Extract headers
    MessageHeaders headers = message.getHeaders();
    Object kafkaKey = headers.get("kafka_receivedMessageKey");
    Object topic = headers.get("kafka_receivedTopic");
    Object partition = headers.get("kafka_receivedPartitionId");
    Object offset = headers.get("kafka_offset");
    Object xTestHeader = headers.get("x-test-header");
    log.info("Received from topic={}, partition={}, offset={}, key={}, payload={}", topic, partition, offset, kafkaKey, fooEvent);

    return Mono.empty();
  }

  @Override
  public void accept(Flux<Message<FooEvent>> flux) {
    flux.concatMap(this::execute).subscribe();
  }
}
