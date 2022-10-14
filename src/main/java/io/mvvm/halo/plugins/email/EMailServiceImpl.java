package io.mvvm.halo.plugins.email;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.halo.app.extension.ReactiveExtensionClient;

/**
 * @description:
 * @author: pan
 **/
@Slf4j
@Component
public class EMailServiceImpl extends AbstractMailService {

    private final EmailProcessManager processManager;
    private final ReactiveExtensionClient client;

    public EMailServiceImpl(EmailProcessManager processManager,
                            ReactiveExtensionClient client) {
        this.processManager = processManager;
        this.client = client;
    }

    @Override
    public void send(EMailRequestPayload payload) {
        processManager.getProcessFlux(payload.getEndpoint())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(process -> process.process(payload.getData()))
                .subscribe(email -> sendMailTemplate(mimeMessageHelper -> {
                    try {
                        mimeMessageHelper.setTo(email.getTo());
                        mimeMessageHelper.setSubject(email.getSubject());
                        mimeMessageHelper.setText(email.getContent(), true);
                    } catch (MessagingException e) {
                        log.error("构建邮件通知内容异常: {}", e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                }));
    }

    @Override
    protected Mono<EmailConfigExtension> getConfigExtension() {
        return client.get(EmailConfigExtension.class, "config");
    }

    @EventListener
    public void event(EmailConfigExtension extension) {
        clearCache();
    }
}