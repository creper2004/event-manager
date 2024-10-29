package ru.gorohov.eventmanager.scheduling;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import ru.gorohov.HistoryOfFields;
import ru.gorohov.InfoOfEditedEvent;
import ru.gorohov.eventmanager.event.db.EventEntity;
import ru.gorohov.eventmanager.event.db.EventRegistrationEntity;
import ru.gorohov.eventmanager.event.db.EventRepository;
import ru.gorohov.eventmanager.kafka.KafkaSender;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class EventStatusScheduledUpdater {

    private final EventRepository eventRepository;
    private final Logger log = LoggerFactory.getLogger(EventStatusScheduledUpdater.class);
    private final KafkaSender kafkaSender;

    @Scheduled(cron = "${event.status.cron}")
    @Transactional
    public void updateEventStatus() {
        log.info("Updating events status");

        var upcomingEvents = eventRepository.updateAndGetUpcomingEvents();
        var upcomingEventsInfo = toInfo(upcomingEvents);
        log.info("Upcoming events info: {}", upcomingEventsInfo);
        if (!upcomingEventsInfo.isEmpty()) {
            upcomingEventsInfo.forEach(infoOfEditedEvent -> kafkaSender.sendMessage(infoOfEditedEvent, "changed-event-topic"));
        }

        var finishingEvents = eventRepository.updateAndGetFinishingEvents();
        var finishingEventsInfo = toInfo(finishingEvents);
        log.info("Finishing events info: {}", finishingEventsInfo);
        if (!finishingEventsInfo.isEmpty()) {
            finishingEventsInfo.forEach(it -> kafkaSender.sendMessage(it, "changed-event-topic"));
        }
    }

    private List<InfoOfEditedEvent> toInfo(List<EventEntity> events) {
        List<InfoOfEditedEvent> infoOfEditedEvents = new ArrayList<>();
        for (EventEntity event : events) {
            var currentInfo = InfoOfEditedEvent.builder();
            if (event.getStatus().equals("STARTED")) {
                currentInfo.status(new HistoryOfFields<String>("WAIT_START", "STARTED"));
            }
            else if  (event.getStatus().equals("FINISHED")) {
                currentInfo.status(new HistoryOfFields<String>("STARTED", "FINISHED"));
            }
            currentInfo.eventId(event.getId());
            currentInfo.ownerId(event.getOwnerId());
            currentInfo.registeredUsersId(event.getRegistration().stream()
                    .map(EventRegistrationEntity::getUserId).toList());
            infoOfEditedEvents.add(currentInfo.build());
        }
        return infoOfEditedEvents;
    }

}
