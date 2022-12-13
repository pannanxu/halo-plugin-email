package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.support.MailServerConfig;

/**
 * MailSender.
 *
 * @author: pan
 **/
public interface MailService {

    Boolean connection(MailServerConfig config);

    boolean send(MailMessage message);

}
