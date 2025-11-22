package com.micro.pubstream;

import com.micro.pubstream.config.PropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PropertiesConfig.class)
public class PubSubstreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(PubSubstreamApplication.class, args);
	}

}
