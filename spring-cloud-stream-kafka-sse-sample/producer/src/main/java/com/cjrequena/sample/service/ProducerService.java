package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.model.FooVO;
import com.cjrequena.sample.domain.model.event.FooEvent;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerService {

  private final StreamBridge streamBridge;

  @Counted
  @Timed
  public void produce(FooVO fooVO, String subscriptionKey) {
    FooEvent event = FooEvent
      .builder()
      .id(UUID.randomUUID())
      .time(OffsetDateTime.now())
      .type("com.cjrequena.sample.sse.v1")
      .source("sse-producer")
      .data(fooVO)
      .build();

    Map<String, String> headers = new HashMap<>();
    headers.put(KafkaHeaders.KEY, String.valueOf(event.getId()));
    headers.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON.toString());
    headers.put("Subscription-Key", subscriptionKey);

    Message<FooEvent> message = MessageBuilder.withPayload(event).copyHeaders(headers).build();
    streamBridge.send("producer-out-0", message);
    log.info("Event emitted {}", event);
  }
}
