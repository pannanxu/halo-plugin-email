package io.mvvm.halo.plugins.email.support;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.mail.MailProperties;

/**
 * MailConfig.
 *
 * @author: pan
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class MailServerConfig extends MailProperties {

    public static final String NAME = "mail-settings";
    public static final String GROUP = "basic";
    
    private boolean enable;

    private String adminMail;
    
    private String fromName;

    private boolean enableTls;
}
