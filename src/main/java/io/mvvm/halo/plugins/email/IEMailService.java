package io.mvvm.halo.plugins.email;

import reactor.core.publisher.Mono;
import run.halo.app.extension.ConfigMap;

/**
 * @description:
 * @author: pan
 **/
public interface IEMailService {

    Mono<Boolean> testConnection();

    void send(EMailRequestPayload payload);

    void refreshCache(ConfigMap configMap);
}