package io.mvvm.halo.plugins.email;

import reactor.core.publisher.Mono;

/**
 * @description:
 * @author: pan
 **/
public interface IEMailService {

    Mono<Boolean> testConnection();

    void send(EMailRequestPayload payload);

}