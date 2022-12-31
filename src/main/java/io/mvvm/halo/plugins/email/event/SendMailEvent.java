package io.mvvm.halo.plugins.email.event;

import io.mvvm.halo.plugins.email.MailMessage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * SendMailEvent.
 *
 * @author: pan
 **/
public class SendMailEvent extends ApplicationEvent {
    
    @Getter
    private final MailMessage message;
    
    public SendMailEvent(Object source, MailMessage message) {
        super(source);
        this.message = message;
    }
}
