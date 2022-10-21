package io.mvvm.halo.plugins.email;

import lombok.Setter;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ConfigMap;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.SystemConfigurableEnvironmentFetcher;
import run.halo.app.infra.utils.JsonUtils;

/**
 * @description:
 * @author: pan
 **/
public abstract class AbstractTemplateProcess implements ExtensionTemplateProcess {

    @Setter
    protected EMailTemplateEngineManager engineManager;
    @Setter
    protected SystemConfigurableEnvironmentFetcher environmentFetcher;
    @Setter
    protected ReactiveExtensionClient extensionClient;

    protected String process(String template, Context context) {
        return engineManager.getTemplateEngine().process(template, context);
    }

    protected <T> Mono<T> fetchSystemSetting(String key, Class<T> clazz) {
        return environmentFetcher.fetch(key, clazz);
    }

    protected Mono<EmailServerConfig> getEmailServerConfig() {
        return extensionClient.get(ConfigMap.class, EmailServerConfig.NAME)
                .map(ConfigMap::getData)
                .map(config -> JsonUtils.jsonToObject(config.get(EmailServerConfig.BASIC_GROUP), EmailServerConfig.class));
    }
}