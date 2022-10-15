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
                .doOnNext(this::doSendEmail)
                .doOnError(err -> log.error("发送邮件异常: {}", err.getMessage(), err))
                .subscribe();
    }

    void doSendEmail(EmailMessage email) {
        sendMailTemplate(mimeMessageHelper -> {
            try {
                mimeMessageHelper.setTo(email.getTo());
                mimeMessageHelper.setSubject(email.getSubject());
                mimeMessageHelper.setText(email.getContent(), true);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected Mono<EmailServerConfig> getConfigExtension() {
        return client.get(ConfigMap.class, EmailPluginConst.emailServerSettingName)
                .map(ConfigMap::getData)
                .flatMap(config -> {
                    String basic = config.get("basic");
                    EmailServerConfig extension = JsonUtils.jsonToObject(basic, EmailServerConfig.class);
                    return Mono.just(extension);
                });
    }

    @Override
    public void refreshCache(ConfigMap configMap) {
        if (EmailPluginConst.emailServerSettingName.equals(configMap.getMetadata().getName())) {
            log.info("刷新 email server configMap");
            clearCache();
        }
    }
}