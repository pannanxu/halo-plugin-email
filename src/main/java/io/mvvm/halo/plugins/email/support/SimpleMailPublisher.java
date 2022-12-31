package io.mvvm.halo.plugins.email.support;

import io.mvvm.halo.plugins.email.MailMessage;
import io.mvvm.halo.plugins.email.MailPublisher;
import io.mvvm.halo.plugins.email.event.SendMailEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * SimpleMailPublisher.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class SimpleMailPublisher implements MailPublisher {
    private final ApplicationEventPublisher publisher;

    public SimpleMailPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(MailMessage message) {
        publisher.publishEvent(new SendMailEvent(this, message));
    }

}
