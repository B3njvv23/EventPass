package com.preventa.ordenes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@EnableScheduling
public class QueueConfig {
  @Bean(destroyMethod = "close")
  @ConditionalOnProperty(name = "eventpass.queue.mode", havingValue = "sqs")
  SqsClient sqs(@Value("${eventpass.queue.region}") String region) {
    return SqsClient.builder()
        .region(Region.of(region))
        .overrideConfiguration(c -> c.apiCallTimeout(java.time.Duration.ofSeconds(15)))
        .build();
  }
}
