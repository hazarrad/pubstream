package com.micro.pubstream.api;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class PubSubstreamApi {

    private final Publisher publisher;
    private final Subscriber subscriber;

    public PubSubstreamApi(Publisher publisher, Subscriber subscriber) {
        this.publisher = publisher;
        this.subscriber = subscriber;
    }

    @PostMapping("pub")
    public String publishMessage(@RequestParam("message") String message)
            throws ExecutionException, InterruptedException {

        ByteString data = ByteString.copyFromUtf8(message);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
        String messageId = publisher.publish(pubsubMessage).get();
        log.info("Published message ID: {}", messageId);

        return "Published Successfully";

    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        log.info("Starting Pub/Sub subscriber... {}", subscriber.getSubscriptionNameString());
        subscriber.startAsync().awaitRunning();
        log.info("Subscriber started for: {}", subscriber.getSubscriptionNameString());
    }


}

