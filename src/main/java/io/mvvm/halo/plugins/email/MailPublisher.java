package io.mvvm.halo.plugins.email;

import reactor.core.publisher.Mono;

/**
 * MailPublisher.
 *
 * @author: pan
 **/
public interface MailPublisher {

    /**
     * 发布邮件
     *
     * @param to       接收人
     * @param template 邮件模板
     * @return true or false
     */
    boolean publish(MailMessage message);

    default Mono<Boolean> publishReactive(MailMessage message) {
        return Mono.just(publish(message));
    }

}
