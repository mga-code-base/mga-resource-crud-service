package com.mygym.app.outbox;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxRelay {

    public static final String EVENT_ID_HEADER = "mga-event-id";
    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxRelay.class);
    private static final Duration LEASE = Duration.ofSeconds(30);

    private final MongoTemplate mongoTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final int batchSize;
    private final Clock clock;

    @Autowired
    public OutboxRelay(MongoTemplate mongoTemplate, KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${mga.outbox.batch-size:100}") int batchSize) {
        this(mongoTemplate, kafkaTemplate, batchSize, Clock.systemUTC());
    }

    OutboxRelay(MongoTemplate mongoTemplate, KafkaTemplate<String, Object> kafkaTemplate, int batchSize, Clock clock) {
        this.mongoTemplate = mongoTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.batchSize = batchSize;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${mga.outbox.poll-interval:2s}")
    public void publishPending() {
        for (int i = 0; i < batchSize; i++) {
            OutboxEvent event = claimNext();
            if (event == null) {
                return;
            }
            if (!publish(event)) {
                return;
            }
        }
    }

    OutboxEvent claimNext() {
        Instant now = clock.instant();
        Query pending = Query.query(Criteria.where("publishedAt").is(null)
                .orOperator(Criteria.where("lockedUntil").is(null), Criteria.where("lockedUntil").lt(now)))
                .with(Sort.by(Sort.Direction.ASC, "createdAt"));
        Update lease = new Update().set("lockedUntil", now.plus(LEASE)).inc("attempts", 1);
        return mongoTemplate.findAndModify(pending, lease, FindAndModifyOptions.options().returnNew(true), OutboxEvent.class);
    }

    boolean publish(OutboxEvent event) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(event.topic(), event.messageKey(), event.payload());
        record.headers().add(EVENT_ID_HEADER, event.id().getBytes(StandardCharsets.UTF_8));
        try {
            kafkaTemplate.send(record).get(10, TimeUnit.SECONDS);
            mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(event.id())),
                    new Update().set("publishedAt", clock.instant()).unset("lockedUntil"), OutboxEvent.class);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            LOGGER.warn("Outbox event {} not published (attempt {}), will retry", event.id(), event.attempts(), e);
            return false;
        }
    }
}
