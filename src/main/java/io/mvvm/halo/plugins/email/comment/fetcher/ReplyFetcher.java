package io.mvvm.halo.plugins.email.comment.fetcher;

import io.mvvm.halo.plugins.email.comment.CommentContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Reply;
import run.halo.app.extension.ReactiveExtensionClient;

/**
 * ReplyFetcher.
 *
 * @author: pan
 **/
@Component
public class ReplyFetcher {

    private final ReactiveExtensionClient client;
    private final UserFetcher userFetcher;

    public ReplyFetcher(ReactiveExtensionClient client, UserFetcher userFetcher) {
        this.client = client;
        this.userFetcher = userFetcher;
    }

    public Mono<CommentContext> fetch(CommentContext context) {
        return client.fetch(Reply.class, context.getCommentRef().getComment().getMetadata().getName())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无此评论"))))
                .flatMap(reply -> {
                    context.getReplyRef().setReply(reply);
                    return userFetcher.fetch(reply.getSpec().getOwner())
                            .doOnNext(commentUser -> context.getCommentRef().setOwner(commentUser));
                })
                .map(e -> context);
    }

}
