package io.mvvm.halo.plugins.email.support;

import io.mvvm.halo.plugins.email.MailBeanContext;
import io.mvvm.halo.plugins.email.MailMessage;
import io.mvvm.halo.plugins.email.MailPublisher;
import org.springframework.stereotype.Component;

/**
 * SimpleMailPublisher.
 *
 * @author: pan
 **/
@Component
public class SimpleMailPublisher implements MailPublisher {
    @Override
    public boolean publish(MailMessage message) {
        MailBeanContext.MAIL_QUEUE_POOL.put(message);
        return true;
    }
}
