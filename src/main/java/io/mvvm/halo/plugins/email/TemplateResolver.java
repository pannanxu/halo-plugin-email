package io.mvvm.halo.plugins.email;

import reactor.core.publisher.Mono;

/**
 * @description:
 * @author: pan
 **/
public interface TemplateResolver {

    Mono<String> getTemplate(String template);

}