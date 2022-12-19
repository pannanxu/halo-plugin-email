package io.mvvm.halo.plugins.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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

    public MailEndpoint(MailService mailService) {
        this.mailService = mailService;
    }

    @Bean
    public RouterFunction<ServerResponse> testConnectionRouter() {
        return route(GET("/apis/io.mvvm.halo.plugins.email/testConnection"),
                request -> testConnection().flatMap(result -> ServerResponse.ok().bodyValue(result)));
    }

    Mono<Boolean> testConnection() {
        return MailBeanContext.environmentFetcher
                .fetchMailServer()
                .publishOn(Schedulers.boundedElastic())
                .map(mailService::connection);
    }

}
