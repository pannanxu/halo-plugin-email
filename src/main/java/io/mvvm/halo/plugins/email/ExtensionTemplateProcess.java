package io.mvvm.halo.plugins.email;

import reactor.core.publisher.Flux;

/**
 * @description:
 * @author: pan
 **/
public interface ExtensionTemplateProcess {

    String getEndpoint();

    Flux<EmailMessage> process(Object extension);

}