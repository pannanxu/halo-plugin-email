package io.mvvm.halo.plugins.email;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * MailPublisher.
 *
 * @author: pan
 **/
public interface MailPublisher {

    Scheduler SCHEDULER = Schedulers.newSingle("mail-publish");

    void publish(MailMessage message);

    default Mono<Void> publishReactive(MailMessage message) {
        return Mono.just(message)
                .doOnNext(this::publish)
                .subscribeOn(SCHEDULER)
                .then();
    }

}
