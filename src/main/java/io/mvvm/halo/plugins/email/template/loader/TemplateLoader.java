package io.mvvm.halo.plugins.email.template.loader;

import reactor.core.publisher.Mono;

/**
 * TemplateLoader.
 *
 * @author: pan
 **/
public interface TemplateLoader {
    
    Mono<String> load(String path);
    
}
