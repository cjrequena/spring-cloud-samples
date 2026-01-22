package com.cjrequena.sample.service;

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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerService1 {

  private final StreamBridge streamBridge;

  @Counted
  @Timed
  public void produce(FooEvent event) {

    Map<String, String> headers = new HashMap<>();
    headers.put(KafkaHeaders.KEY, String.valueOf(event.getId()));
    headers.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON.toString());

    Message<FooEvent> message = MessageBuilder.withPayload(event).copyHeaders(headers).build();
    streamBridge.send("producer1-out-0", message);
    log.info("Event emitted {}", event);
  }
}
