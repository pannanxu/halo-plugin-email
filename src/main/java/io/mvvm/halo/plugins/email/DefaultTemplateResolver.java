package io.mvvm.halo.plugins.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;

/**
 * @description: 从数据库加载通知模板
 * @author: pan
 **/
@Slf4j
@Component
public class DefaultTemplateResolver implements TemplateResolver {

    private final ReactiveExtensionClient client;
    private final TemplateLoaderAdapter templateLoaderAdapter;

    public DefaultTemplateResolver(ReactiveExtensionClient client) {
        this.client = client;
        this.templateLoaderAdapter = new TemplateLoaderAdapter();
    }

    @Override
    public Mono<String> getTemplate(String template) {
        return client.get(EmailTemplateExtension.class, template)
                .filter(notify -> null != notify.getSpec() && notify.getSpec().isEnable())
                .flatMap(templateLoaderAdapter::load);
    }

}