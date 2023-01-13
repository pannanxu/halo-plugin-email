package io.mvvm.halo.plugins.email;

import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import reactor.core.publisher.Mono;

/**
 * MailContextHolder.
 *
 * @author: pan
 **/
public class MailContextHolder implements ApplicationContextAware {

    private static MailPublisher publisher;

    public static void publish(MailMessage message) {
        MailContextHolder.publisher.publish(message);
    }

    public static Mono<Void> publishReactive(MailMessage message) {
        return MailContextHolder.publisher.publishReactive(message);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext ctx) throws BeansException {
        MailContextHolder.publisher = ctx.getBean(MailPublisher.class);
    }
}
