package io.mvvm.halo.plugins.email;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @description:
 * @author: pan
 **/
@Slf4j
public class TemplateLoaderAdapter {

    public Mono<String> load(EmailTemplateExtension extension) {
        return EmailPluginManager.getTemplateLoaders()
                .stream()
                .filter(loader -> loader.check(extension))
                .findFirst()
                .map(loader -> loader.load(extension))
                .orElse(Mono.just(extension.getSpec().getTemplate()))
                .doOnError(err -> {
                    log.error("加载邮件模板 " + extension.getSpec().getTemplate() + " 失败: {}", err.getMessage(), err);
                });
    }

}