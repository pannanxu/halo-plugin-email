package io.mvvm.halo.plugins.email;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Abstract mail service.
 *
 * @author johnniang, Pan
 */
@Slf4j
public abstract class AbstractMailService implements IEMailService {

    private volatile JavaMailSender cachedMailSender;

    private final AtomicReference<EmailServerConfig> configRef = new AtomicReference<>();

    /**
     * Test connection with email server.
     */
    @Override
    public Mono<Boolean> testConnection() {
        return getConfigExtension().filter(EmailServerConfig::isEnable)
                .flatMap(config -> {
                    JavaMailSender javaMailSender = getMailSender(config);
                    if (javaMailSender instanceof JavaMailSenderImpl mailSender) {
                        try {
                            mailSender.testConnection();
                            return Mono.just(true);
                        } catch (Throwable e) {
                            return Mono.error(new RuntimeException(e.getMessage(), e));
                        }
                    }
                    return Mono.just(false);
                })
                .onErrorReturn(false)
                .switchIfEmpty(Mono.defer(() -> Mono.just(false)));
    }

    /**
     * Send mail template.
     *
     * @param callback mime message callback.
     */
    protected void sendMailTemplate(@Nullable Consumer<MimeMessageHelper> callback) {
        if (callback == null) {
            log.info("Callback is null, skip to send email");
            return;
        }
        getConfigExtension().filter(EmailServerConfig::isEnable)
                .doOnNext(config -> {
                    // get mail sender
                    JavaMailSender mailSender = getMailSender(config);

                    // create mime message helper
                    MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage());

                    try {
                        // set from-name
                        messageHelper.setFrom(getFromAddress(mailSender, config));
                        // handle message set separately
                        callback.accept(messageHelper);

                        // get mime message
                        MimeMessage mimeMessage = messageHelper.getMimeMessage();
                        // send email
                        mailSender.send(mimeMessage);

                        log.info("Sent an email to [{}] successfully, subject: [{}], sent date: [{}]",
                                Arrays.toString(mimeMessage.getAllRecipients()),
                                mimeMessage.getSubject(),
                                mimeMessage.getSentDate());
                    } catch (Exception e) {
                        throw new RuntimeException("邮件发送失败，请检查 SMTP 服务配置是否正确", e);
                    }
                })
                .subscribe();
    }

    /**
     * Get java mail sender.
     *
     * @return java mail sender
     */
    @NonNull
    private JavaMailSender getMailSender(EmailServerConfig newConfig) {
        // 如果配置已修改则更新缓存中的 cachedMailSender
        EmailServerConfig oldConfig = configRef.getAndUpdate(emailServerConfig -> newConfig);
        if (null != oldConfig && !oldConfig.equals(newConfig)) {
            clearCache();
        }

        if (this.cachedMailSender == null) {
            synchronized (IEMailService.class) {
                if (this.cachedMailSender == null) {
                    // create mail sender factory
                    MailSenderFactory mailSenderFactory = new MailSenderFactory();
                    // get mail sender
                    this.cachedMailSender = mailSenderFactory.getMailSender(getMailProperties(newConfig));
                }
            }
        }
        return this.cachedMailSender;
    }

    /**
     * Get from-address.
     *
     * @param javaMailSender java mail sender.
     * @return from-name internet address
     * @throws UnsupportedEncodingException throws when you give a wrong character encoding
     */
    private synchronized InternetAddress getFromAddress(@NonNull JavaMailSender javaMailSender, EmailServerConfig config)
            throws UnsupportedEncodingException {
        Assert.notNull(javaMailSender, "Java mail sender must not be null");

        if (javaMailSender instanceof JavaMailSenderImpl mailSender) {
            // get user name(email)
            String username = mailSender.getUsername();

            // build internet address
            return new InternetAddress(username, config.getFormName(),
                    mailSender.getDefaultEncoding());
        }

        throw new UnsupportedOperationException(
                "Unsupported java mail sender: " + javaMailSender.getClass().getName());
    }

    /**
     * Get mail properties.
     *
     * @return mail properties
     */
    @NonNull
    private synchronized MailProperties getMailProperties(EmailServerConfig config) {
        MailProperties mailProperties = new MailProperties();
        mailProperties.setHost(config.getHost());
        mailProperties.setPort(config.getPort());
        mailProperties.setUsername(config.getUsername());
        mailProperties.setPassword(config.getPassword());
        mailProperties.setProtocol(config.getProtocol());
        if (config.isStartTls()) {
            mailProperties.getProperties().put("mail.smtp.starttls.enable", "true");
            mailProperties.getProperties().put("mail.smtp.auth", "true");
        }
        return mailProperties;
    }

    /**
     * Clear cached instance.
     */
    protected void clearCache() {
        this.cachedMailSender = null;
    }

    protected abstract Mono<EmailServerConfig> getConfigExtension();
}