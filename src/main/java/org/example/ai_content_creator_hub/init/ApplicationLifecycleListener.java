package org.example.ai_content_creator_hub.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLifecycleListener {
    private final DatabasePopulator databasePopulator;

    @Autowired
    public ApplicationLifecycleListener(DatabasePopulator databasePopulator) {
        this.databasePopulator = databasePopulator;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Populate database on startup
        databasePopulator.populateApiUsers();
    }

    @EventListener
    public void onApplicationEvent(ContextClosedEvent event) {
        // Clear database on shutdown
        databasePopulator.clearDatabase();
    }
}
