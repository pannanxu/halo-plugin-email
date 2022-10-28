package io.mvvm.halo.plugins.email;

import reactor.core.publisher.Mono;

/**
 * @description:
 * @author: pan
 **/
public interface TemplateLoader {

    boolean check(EmailTemplateExtension extension);

    Mono<String> load(EmailTemplateExtension extension);
}