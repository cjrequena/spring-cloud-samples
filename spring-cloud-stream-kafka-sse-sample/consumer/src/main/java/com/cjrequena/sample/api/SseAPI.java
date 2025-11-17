package com.cjrequena.sample.api;

import com.cjrequena.sample.event.FooEvent;
import com.cjrequena.sample.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.cjrequena.sample.common.Constant.*;

@Slf4j
@RestController
@RequestMapping(value = SseAPI.ENDPOINT)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SseAPI {

  public static final String ENDPOINT = "/foo-service/api";
  public static final String ACCEPT_VERSION_V1 = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;
  public static final String ACCEPT_VERSION_V2 = "Accept-Version=" + VND_SAMPLE_SERVICE_V2;
  public static final String ACCEPT_VERSION_V3 = "Accept-Version=" + VND_SAMPLE_SERVICE_V3;
  public static final String ACCEPT_VERSION_V4 = "Accept-Version=" + VND_SAMPLE_SERVICE_V4;
  public static final String ACCEPT_VERSION_V5 = "Accept-Version=" + VND_SAMPLE_SERVICE_V5;
  public static final String ACCEPT_VERSION_V6 = "Accept-Version=" + VND_SAMPLE_SERVICE_V6;

  private final ConsumerService1 consumerService1;
  private final ConsumerService2 consumerService2;
  private final ConsumerService3 consumerService3;
  private final ConsumerService4 consumerService4;
  private final ConsumerService5 consumerService5;
  private final ConsumerService6 consumerService6;

  @GetMapping(
    path = "/subscribe",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE,
    headers = {ACCEPT_VERSION_V1}
  )
  public Flux<FooEvent> subscribeV1() {
    return consumerService1.subscribe().map(data -> ServerSentEvent.builder(data).build().data());
  }

  @GetMapping(
    path = "/subscribe",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE,
    headers = {ACCEPT_VERSION_V2}
  )
  public Flux<FooEvent> subscribeV2() {
    return consumerService2.subscribe().map(data -> ServerSentEvent.builder(data).build().data());
  }

  @GetMapping(
    path = "/subscribe",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE,
    headers = {ACCEPT_VERSION_V3}
  )
  public Flux<FooEvent> subscribeV3() {
    return consumerService3.subscribe().map(data -> ServerSentEvent.builder(data).build().data());
  }

  @GetMapping(
    path = "/subscribe",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE,
    headers = {ACCEPT_VERSION_V4}
  )
  public Flux<FooEvent> subscribeV4() {
    return consumerService4.subscribe().map(data -> ServerSentEvent.builder(data).build().data());
  }

  @GetMapping(
    path = "/subscribe",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE,
    headers = {ACCEPT_VERSION_V5}
  )
  public Flux<ServerSentEvent<String>> subscribeV5(@RequestHeader("Subscription-Key") String subscriptionKey) {
    return consumerService5.subscribeV2(subscriptionKey).map(data -> ServerSentEvent.builder(data).build().data());
  }

  @GetMapping(
    path = "/subscribe",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE,
    headers = {ACCEPT_VERSION_V6}
  )
  public Flux<ServerSentEvent<String>> subscribeV6(@RequestHeader("Subscription-Key") String subscriptionKey) {
    return consumerService6.subscribeV2(subscriptionKey).map(data -> ServerSentEvent.builder(data).build().data());
  }
}
