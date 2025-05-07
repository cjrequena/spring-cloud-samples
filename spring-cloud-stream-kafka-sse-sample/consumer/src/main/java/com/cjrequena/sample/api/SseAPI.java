package com.cjrequena.sample.api;

import com.cjrequena.sample.event.FooEvent;
import com.cjrequena.sample.service.ConsumerService3;
import com.cjrequena.sample.service.ConsumerService4;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.cjrequena.sample.api.SseAPI.ACCEPT_VERSION;
import static com.cjrequena.sample.common.Constants.VND_SAMPLE_SERVICE_V1;

@Slf4j
@RestController
@RequestMapping(value = SseAPI.ENDPOINT, headers = {ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SseAPI {

    public static final String ENDPOINT = "/foo-service/api";
    public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;
    private final ConsumerService3 consumerService3;
    private final ConsumerService4 consumerService4;

    @GetMapping(
      path = "/subscribe",
      produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<FooEvent>> stream() {
        //return consumerService3.getMessageStream().map(data -> ServerSentEvent.builder(data).build());
        return consumerService4.getMessageStream().map(data -> ServerSentEvent.builder(data).build());
    }
}
