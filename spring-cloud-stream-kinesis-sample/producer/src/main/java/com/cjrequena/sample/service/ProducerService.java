package com.cjrequena.sample.service;

import com.cjrequena.sample.dto.FooDTO;
import com.cjrequena.sample.event.FooEvent;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

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
  public void produce(FooDTO dto) {
    FooEvent event = new FooEvent();
    event.setId(String.valueOf(UUID.randomUUID()));
    event.setData(dto);
    Map<String, String> headers = new HashMap<>();
    //headers.put(KafkaHeaders.KEY, event.getId());
    headers.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON.toString());
    headers.put("x-test-header", "header-test-001");
    Message<FooEvent> message = MessageBuilder.withPayload(event).copyHeaders(headers).build();
    streamBridge.send("producer-out-0", message);
    log.info("Event emitted {}", event);
  }
}
