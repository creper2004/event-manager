package ru.gorohov.eventmanager.scheduling;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import ru.gorohov.eventmanager.event.db.EventRepository;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class EventStatusScheduledUpdater {

    private final EventRepository eventRepository;
    private final Logger log = LoggerFactory.getLogger(EventStatusScheduledUpdater.class);

    @Scheduled(cron = "${event.status.cron}")
    @Transactional
    public void updateEventStatus() {
        log.info("Updating events status");
        eventRepository.updateUpcomingEvents();
        eventRepository.updateFinishingEvents();
    }

}
