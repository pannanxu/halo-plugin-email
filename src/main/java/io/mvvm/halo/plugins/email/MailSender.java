package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.support.MailSenderFactory;
import io.mvvm.halo.plugins.email.support.MailServerConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * MailSender.
 *
 * @author: pan
 **/
@Slf4j
public class MailSender {
    private final JavaMailSender sender;

    private MailSender(JavaMailSender sender) {
        this.sender = sender;
    }

    public static MailSender createSender(MailServerConfig config) {
        MailSenderFactory mailSenderFactory = new MailSenderFactory();
        JavaMailSender sender = mailSenderFactory.getMailSender(getMailProperties(config));
        return new MailSender(sender);
    }

    @NonNull
    private static synchronized MailProperties getMailProperties(MailServerConfig config) {
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

    public boolean testConnection() {
        try {
            if (sender instanceof JavaMailSenderImpl mailSender) {
                mailSender.testConnection();
                log.debug("连接初始化成功: {}", this.sender);
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            log.error("连接初始化失败: {}", ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public Boolean send(MailMessage message) {
        if (!message.checkParams()) {
            return Boolean.FALSE;
        }
        
        log.debug("准备发送邮件: {}", message);

        try {
            List<Attach> attaches = message.attaches();
            MimeMessageHelper messageHelper;
            if (null == attaches || attaches.isEmpty()) {
                messageHelper = new MimeMessageHelper(sender.createMimeMessage());
            } else {
                messageHelper = new MimeMessageHelper(sender.createMimeMessage(), true);

                attaches.forEach(attach -> {
                    if (null != attach.getSource()) {
                        try {
                            if (StringUtils.hasLength(attach.getContentType())) {
                                messageHelper.addAttachment(attach.getName(), attach.getSource(), attach.getContentType());
                            } else {
                                messageHelper.addAttachment(attach.getName(), attach.getSource());
                            }
                        } catch (MessagingException e) {
                            log.error("邮件添加附件失败, {}", e.getMessage(), e);
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
            // set from-name
            messageHelper.setFrom(getFromAddress(sender, message.fromName()));
            // handle message set separately
            messageHelper.setTo(message.to());
            messageHelper.setSubject(message.subject());
            messageHelper.setText(message.content(), true);

            // get mime message
            MimeMessage mimeMessage = messageHelper.getMimeMessage();
            // send email
            sender.send(mimeMessage);

            log.info("Sent an email to [{}] successfully, subject: [{}], sent date: [{}]",
                    Arrays.toString(mimeMessage.getAllRecipients()),
                    mimeMessage.getSubject(),
                    mimeMessage.getSentDate());
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("邮件发送失败，请检查 SMTP 服务配置是否正确, {}", e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * Get from-address.
     *
     * @param javaMailSender java mail sender.
     * @return from-name internet address
     * @throws UnsupportedEncodingException throws when you give a wrong character encoding
     */
    private synchronized InternetAddress getFromAddress(@NonNull JavaMailSender javaMailSender, String fromName)
            throws UnsupportedEncodingException {
        Assert.notNull(javaMailSender, "Java mail sender must not be null");

        if (javaMailSender instanceof JavaMailSenderImpl mailSender) {
            // get user name(email)
            String username = mailSender.getUsername();

            // build internet address
            return new InternetAddress(username, fromName, mailSender.getDefaultEncoding());
        }

        throw new UnsupportedOperationException(
                "Unsupported java mail sender: " + javaMailSender.getClass().getName());
    }

}
