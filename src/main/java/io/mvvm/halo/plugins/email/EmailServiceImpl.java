package io.mvvm.halo.plugins.email;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.halo.app.extension.ConfigMap;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.utils.JsonUtils;

/**
 * @description:
 * @author: pan
 **/
@Slf4j
@Component
public class EmailServiceImpl extends AbstractMailService {

    private final ReactiveExtensionClient client;

    public EmailServiceImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public void send(EmailRequestPayload payload) {
        EmailPluginManager.getProcess(payload.getEndpoint())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(process -> process.process(payload.getData()))
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(this::doSendEmail)
                .doOnError(err -> log.error("发送邮件异常: {}", err.getMessage(), err))
                .subscribe();
    }

    void doSendEmail(EmailMessage email) {
        sendMailTemplate(mimeMessageHelper -> {
            try {
                Thread.sleep(10000);
                mimeMessageHelper.setTo(email.getTo());
                mimeMessageHelper.setSubject(email.getSubject());
                mimeMessageHelper.setText(email.getContent(), true);
            } catch (MessagingException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected Mono<EmailServerConfig> getConfigExtension() {
        return client.get(ConfigMap.class, EmailServerConfig.NAME)
                .map(ConfigMap::getData)
                .flatMap(config -> {
                    String basic = config.get(EmailServerConfig.BASIC_GROUP);
                    EmailServerConfig extension = JsonUtils.jsonToObject(basic, EmailServerConfig.class);
                    return Mono.just(extension);
                });
    }

}