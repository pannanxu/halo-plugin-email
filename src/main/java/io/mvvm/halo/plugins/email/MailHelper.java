package io.mvvm.halo.plugins.email;

import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import reactor.core.publisher.Mono;

/**
 * MailHelper.
 *
 * @author: pan
 **/
public class MailHelper implements ApplicationContextAware {

    private static MailPublisher publisher;

    public static void publish(MailMessage message) {
        MailHelper.publisher.publish(message);
    }

    public static Mono<Void> publishReactive(MailMessage message) {
        return MailHelper.publisher.publishReactive(message);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext ctx) throws BeansException {
        MailHelper.publisher = ctx.getBean(MailPublisher.class);
    }
}
