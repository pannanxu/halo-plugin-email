package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.support.SimpleMailMessage;

/**
 * Message.
 *
 * @author: pan
 **/
public interface MailMessage {

    static SimpleMailMessage of(String to) {
        return new SimpleMailMessage(to);
    }

    String to();

    String subject();

    String content();

    String fromName();

    void setTo(String to);

    void setSubject(String subject);

    void setContent(String content);
}
