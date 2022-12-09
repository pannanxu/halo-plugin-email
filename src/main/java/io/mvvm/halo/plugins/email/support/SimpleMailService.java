package io.mvvm.halo.plugins.email.support;

import io.mvvm.halo.plugins.email.MailBeanContext;
import io.mvvm.halo.plugins.email.MailMessage;
import io.mvvm.halo.plugins.email.MailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * SimpleMailService.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class SimpleMailService implements MailService {

    private JavaMailSender cachedMailSender;
    private MailServerConfig cachedConfig;

    public SimpleMailService() {
        MailBeanContext.MAIL_QUEUE_POOL.start(this);
    }

    @Override
    public MailServerConfig getCachedConfig() {
        return this.cachedConfig;
    }

    @Override
    public Boolean connection(MailServerConfig config) {
        log.info("连接初始化配置: {}", config);
        if (null == config) {
            return false;
        }
        this.cachedMailSender = null;
        try {
            MailSenderFactory mailSenderFactory = new MailSenderFactory();
            JavaMailSender sender = mailSenderFactory.getMailSender(getMailProperties(config));
            if (sender instanceof JavaMailSenderImpl mailSender) {
                mailSender.testConnection();
                this.cachedMailSender = sender;
                this.cachedConfig = config;
                log.info("连接初始化成功: {}", this.cachedMailSender);
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            log.error("连接初始化失败: {}", ex.getMessage(), ex);
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean send(MailMessage message) {
        if (null == this.cachedMailSender) {
            log.info("邮件发送失败, 请优先测试连接成功后发送.");
            return false;
        }
        if (!StringUtils.hasLength(message.to())
                || !StringUtils.hasLength(message.content())
                || !StringUtils.hasLength(message.subject())) {
            log.info("邮件发送取消, 邮件参数错误.");
            return false;
        }
        // create mime message helper
        MimeMessageHelper messageHelper = new MimeMessageHelper(cachedMailSender.createMimeMessage());

        try {
            // set from-name
            messageHelper.setFrom(getFromAddress(cachedMailSender, cachedConfig));
            // handle message set separately
            messageHelper.setTo(message.to());
            messageHelper.setSubject(message.subject());
            messageHelper.setText(message.content(), true);

            // get mime message
            MimeMessage mimeMessage = messageHelper.getMimeMessage();
            // send email
            cachedMailSender.send(mimeMessage);

            log.info("Sent an email to [{}] successfully, subject: [{}], sent date: [{}]",
                    Arrays.toString(mimeMessage.getAllRecipients()),
                    mimeMessage.getSubject(),
                    mimeMessage.getSentDate());
            return true;
        } catch (Exception e) {
            log.error("邮件发送失败，请检查 SMTP 服务配置是否正确, {}", e.getMessage(), e);
        }
        return false;
    }

    @NonNull
    private synchronized MailProperties getMailProperties(MailServerConfig config) {
        MailProperties mailProperties = new MailProperties();
        mailProperties.setHost(config.getHost());
        mailProperties.setPort(config.getPort());
        mailProperties.setUsername(config.getUsername());
        mailProperties.setPassword(config.getPassword());
        mailProperties.setProtocol(config.getProtocol());
        if (config.isEnableTls()) {
            mailProperties.getProperties().put("mail.smtp.starttls.enable", "true");
            mailProperties.getProperties().put("mail.smtp.auth", "true");
        }
        return mailProperties;
    }

    /**
     * Get from-address.
     *
     * @param javaMailSender java mail sender.
     * @return from-name internet address
     * @throws UnsupportedEncodingException throws when you give a wrong character encoding
     */
    private synchronized InternetAddress getFromAddress(@NonNull JavaMailSender javaMailSender, MailServerConfig config)
            throws UnsupportedEncodingException {
        Assert.notNull(javaMailSender, "Java mail sender must not be null");

        if (javaMailSender instanceof JavaMailSenderImpl mailSender) {
            // get user name(email)
            String username = mailSender.getUsername();

            // build internet address
            return new InternetAddress(username, config.getForm(), mailSender.getDefaultEncoding());
        }

        throw new UnsupportedOperationException(
                "Unsupported java mail sender: " + javaMailSender.getClass().getName());
    }

}
