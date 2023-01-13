package io.mvvm.halo.plugins.email;

import reactor.core.publisher.Mono;

/**
 * MailPublisher.
 *
 * @author: pan
 **/
public interface MailPublisher {

    void publish(MailMessage message);

    default Mono<Void> publishReactive(MailMessage message) {
        return Mono.just(message)
                .doOnNext(this::publish)
                .then();
    }

}
