package io.mvvm.halo.plugins.email.support;

import lombok.Data;

/**
 * MailConfig.
 *
 * @author: pan
 **/
@Data
public class MailServerConfig {

    public static final String NAME = "mail-settings";
    public static final String GROUP = "basic";

    /**
     * SMTP server host. For instance, 'smtp.example.com'.
     */
    private String host;

    /**
     * SMTP server port.
     */
    private Integer port;

    /**
     * Login user of the SMTP server.
     */
    private String username;

    /**
     * Login password of the SMTP server.
     */
    private String password;

    /**
     * Protocol used by the SMTP server.
     */
    private String protocol = "smtp";

    private boolean enable = true;

    private String adminMail;

    private String fromName;

    private boolean enableTls = false;
}
