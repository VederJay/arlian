package org.arlian.site.tasks;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupTask {


    @EventListener(ApplicationReadyEvent.class)
    public void doSomething() {

    }
}
