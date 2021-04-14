package org.arlian.site.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartupTask {


    @EventListener(ApplicationReadyEvent.class)
    public void doSomething() {

    }
}
