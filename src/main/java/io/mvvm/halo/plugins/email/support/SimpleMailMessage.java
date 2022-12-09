package io.mvvm.halo.plugins.email.support;

import io.mvvm.halo.plugins.email.MailMessage;
import lombok.Setter;

/**
 * SimpleMessage.
 *
 * @author: pan
 **/
@Setter
public class SimpleMailMessage implements MailMessage {

    private String to;
    private String subject;
    private String content;

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

}
