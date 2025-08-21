package jroullet.msnotification.config;

import jroullet.msnotification.document.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MongoInitializer {

    @Autowired
    private final MongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeIndexes() {
        log.info("Creating MongoDB index for optimal query performance");

        try {
            IndexOperations indexOps = mongoTemplate.indexOps(Notification.class);

            Index sessionIdIndex = new Index()
                    .on("sessionId", Sort.Direction.ASC)
                    .named("idx_sessionId");

            indexOps.createIndex(sessionIdIndex);
            log.info("Index created : idx_sessionId");

            var indexes = indexOps.getIndexInfo();
            log.info("Total indexes: {}", indexes.size());

        } catch (Exception e) {
            log.error("Error creating MongoDB index", e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void verifyConnection() {
        try {
            long count = mongoTemplate.getCollection("notifications").estimatedDocumentCount();
            log.info("MongoDB connection OK - {} existing notifications", count);
        } catch (Exception e) {
            log.error("MongoDB connection FAILED", e);
        }
    }
}
