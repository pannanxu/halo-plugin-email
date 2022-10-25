package io.mvvm.halo.plugins.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @description: 从数据库加载通知模板
 * @author: pan
 **/
@Slf4j
@Component
public class DefaultTemplateResolver implements TemplateResolver {

    private final ReactiveExtensionClient client;
    private final Set<TemplateLoader> templateLoaders = new CopyOnWriteArraySet<>();

    public DefaultTemplateResolver(ReactiveExtensionClient client) {
        this.client = client;
        addTemplateLoader(new ClassPathTemplateResolver());
    }

    @Override
    public Mono<String> getTemplate(String template) {
        return client.get(EmailTemplateExtension.class, template)
                .filter(notify -> null != notify.getSpec() && notify.getSpec().isEnable())
                .flatMap(notify ->
                        templateLoaders.stream().filter(loader -> loader.check(notify))
                                .findFirst()
                                .map(loader -> loader.load(notify.getSpec().getTemplate()))
                                .orElse(Mono.just(notify.getSpec().getTemplate())))
                .doOnError(err -> {
                    log.error("加载邮件模板 " + template + " 失败: {}", err.getMessage(), err);
                });
    }

    public void addTemplateLoader(TemplateLoader loader) {
        templateLoaders.add(loader);
    }
}