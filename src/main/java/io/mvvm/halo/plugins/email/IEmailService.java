package io.mvvm.halo.plugins.email;

import reactor.core.publisher.Mono;

/**
 * @description:
 * @author: pan
 **/
public interface IEmailService {

    Mono<Boolean> testConnection();

    void send(EmailRequestPayload payload);

}