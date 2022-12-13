package io.mvvm.halo.plugins.email.support;

import io.mvvm.halo.plugins.email.MailMessage;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * SimpleMessage.
 *
 * @author: pan
 **/
@Setter
@SuperBuilder
public class SimpleMailMessage implements MailMessage {

    private String to;
    private String subject;
    private String content;
    
    private String fromName;

    public SimpleMailMessage(String to) {
        this.to = to;
    }

    @Override
    public String to() {
        return this.to;
    }

    @Override
    public String subject() {
        return this.subject;
    }

    @Override
    public String content() {
        return this.content;
    }

    @Override
    public String fromName() {
        return this.fromName;
    }
}
