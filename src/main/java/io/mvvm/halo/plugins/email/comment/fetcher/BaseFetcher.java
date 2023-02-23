package io.mvvm.halo.plugins.email.comment.fetcher;

import io.mvvm.halo.plugins.email.comment.CommentContext;
import io.mvvm.halo.plugins.email.support.MailEnvironmentFetcher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * BaseFetcher.
 *
 * @author: pan
 **/
@Component
public class BaseFetcher {
    private final MailEnvironmentFetcher environmentFetcher;

    public BaseFetcher(MailEnvironmentFetcher environmentFetcher) {
        this.environmentFetcher = environmentFetcher;
    }

    public Mono<CommentContext> fetch(CommentContext context) {
        return environmentFetcher.fetchComment()
                .doOnNext(e -> context.getBase().setCommentSetting(e))
                .flatMap(e -> environmentFetcher.fetchMailServer()
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无邮件服务器配置"))))
                        .doOnNext(x -> context.getBase().setServerConfig(x)))
                .map(e -> context);
    }
}
