package io.mvvm.halo.plugins.email.support;

import io.mvvm.halo.plugins.email.MailMessage;
import io.mvvm.halo.plugins.email.MailSender;
import io.mvvm.halo.plugins.email.MailService;
import io.mvvm.halo.plugins.email.event.SendMailEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * SimpleMailService.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class SimpleMailService implements MailService {

    private final AtomicReference<MailSender> sender = new AtomicReference<>();

    @Override
    public Boolean connection(MailServerConfig config) {
        log.debug("连接初始化配置: {}", config);
        if (null == config) {
            throw new RuntimeException("暂未初始化配置，请前往插件设置中配置后重试");
        }
        try {
            MailSender mailSender = sender.updateAndGet(old -> MailSender.createSender(config));
            return mailSender.testConnection();
        } catch (Exception ex) {
            log.error("连接初始化失败: {}", ex.getMessage(), ex);
            throw new RuntimeException("连接初始化失败: " + ex.getMessage());
        }
    }

    @Override
    public boolean send(MailMessage message) {
        if (null == sender.get()) {
            log.info("邮件发送失败, 请优先测试连接成功后发送.");
            return false;
        }
        if (!StringUtils.hasLength(message.to())
            || !StringUtils.hasLength(message.content())
            || !StringUtils.hasLength(message.subject())) {
            log.debug("邮件发送取消, 邮件参数错误.");
            return false;
        }
        return sender.get().send(message);
    }

    @EventListener(SendMailEvent.class)
    public void sendMailEvent(SendMailEvent event) {
        send(event.getMessage());
    }

}
