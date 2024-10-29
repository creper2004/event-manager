package ru.gorohov.eventmanager.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.gorohov.InfoOfEditedEvent;

@Component
@Slf4j
public class KafkaSender {

    @Autowired
    private KafkaTemplate<Long, InfoOfEditedEvent> kafkaTemplate;

    public void sendMessage(InfoOfEditedEvent message, String topic) {
        log.info("sending message to topic {}", topic);
        log.info("message {}", message);
        kafkaTemplate.send(topic, message);
    }
}
