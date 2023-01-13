package io.mvvm.halo.plugins.email.support;

import io.mvvm.halo.plugins.email.MailMessage;
import io.mvvm.halo.plugins.email.MailPublisher;
import io.mvvm.halo.plugins.email.event.SendMailEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * SimpleMailPublisher.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class SimpleMailPublisher implements MailPublisher {

    private final Queue<MailMessage> queue = new ConcurrentLinkedQueue<>();

    public SimpleMailPublisher(ApplicationEventPublisher publisher) {
        Timer timer = new Timer("mail-publish-queue", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (null != queue.peek()) {
                        MailMessage message = queue.poll();
                        publisher.publishEvent(new SendMailEvent(this, message));
                    }
                } catch (Exception ignored) {

                }
            }
        }, 3000, 1000);
    }

    @Override
    public void publish(MailMessage message) {
        queue.add(message);
    }

}
