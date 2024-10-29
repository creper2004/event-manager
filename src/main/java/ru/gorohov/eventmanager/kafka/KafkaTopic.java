package ru.gorohov.eventmanager.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopic {

    @Bean
    public NewTopic changedEventTopic() {
        return new NewTopic("changed-event-topic", 1, (short) 1);
    }

}
