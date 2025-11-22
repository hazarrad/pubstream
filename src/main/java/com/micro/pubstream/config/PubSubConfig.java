package com.micro.pubstream.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.TopicName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class PubSubConfig {

    @Autowired
    private PropertiesConfig propConfig;

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {

        Path resourcesPath = Paths.get(propConfig.getResourcesFolder());
        log.info("Resources Path: {}", resourcesPath);
        String serviceAccountKeyFile = resourcesPath + propConfig.getKey();
        log.info("----------> service account key file : {}", serviceAccountKeyFile);
        InputStream serviceAccountStream = new ClassPathResource(propConfig.getKey()).getInputStream();
        log.info("Service account key loaded");
        return GoogleCredentials.fromStream(serviceAccountStream);

    }

    @Bean
    public Publisher publisher(GoogleCredentials googleCredentials) throws IOException {

        TopicName topicName = TopicName.of(propConfig.getProjectId(), propConfig.getTopicId());
        log.info("Creating Publisher for topic: {}", topicName);
        return Publisher.newBuilder(topicName)
                .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
                .build();
    }


    @Bean
    public Subscriber subscriber(GoogleCredentials googleCredentials) throws IOException {

        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(propConfig.getProjectId(), propConfig.getSubscription());
        log.info("Creating Subscriber for subscription: {}", subscriptionName);
        MessageReceiver receiver = (message, consumer) -> {
            String body = message.getData().toStringUtf8();
            log.info("Received: {}", body);
            consumer.ack();
        };

        return Subscriber.newBuilder(subscriptionName, receiver)
                .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
                .build();
    }

}
