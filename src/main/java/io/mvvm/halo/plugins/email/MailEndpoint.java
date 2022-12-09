package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.support.MailServerConfig;
import io.mvvm.halo.plugins.email.support.SimpleMailMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ConfigMap;
import run.halo.app.infra.utils.JsonUtils;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * MailEndpoint.
 *
 * @author: pan
 **/
@Configuration
public class MailEndpoint {

    private final MailService mailService;
    private final MailPublisher publisher;

    public MailEndpoint(MailService mailService, MailPublisher publisher) {
        this.mailService = mailService;
        this.publisher = publisher;
    }

    @Bean
    public RouterFunction<ServerResponse> testConnectionRouter() {
        return route(GET("/apis/io.mvvm.halo.plugins.email/testConnection"),
                request -> MailBeanContext.client.get(ConfigMap.class, MailServerConfig.NAME)
                        .map(ConfigMap::getData)
                        .map(config -> {
                            String basic = config.get(MailServerConfig.GROUP);
                            return JsonUtils.jsonToObject(basic, MailServerConfig.class);
                        })
                        .flatMap(config -> Mono.just(mailService.connection(config)))
                        .flatMap(result -> ServerResponse.ok().bodyValue(result)));
    }

}
