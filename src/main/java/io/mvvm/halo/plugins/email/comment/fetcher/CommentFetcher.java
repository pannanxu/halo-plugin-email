package io.mvvm.halo.plugins.email.comment.fetcher;

import io.mvvm.halo.plugins.email.comment.CommentContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.content.Reply;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;

/**
 * CommentFetcher.
 *
 * @author: pan
 **/
@Component
public class CommentFetcher {

    private final ReactiveExtensionClient client;
    private final UserFetcher userFetcher;

    public CommentFetcher(ReactiveExtensionClient client, UserFetcher userFetcher) {
        this.client = client;
        this.userFetcher = userFetcher;
    }

    public Mono<CommentContext> fetch(CommentContext context) {
        if (null != context.getReplyRef().getReply()) {
            Comment comment = new Comment();
            Metadata metadata = new Metadata();
            metadata.setName(context.getReplyRef().getReply().getSpec().getCommentName());
            comment.setMetadata(metadata);
            context.getCommentRef().setComment(comment);
        }
        if (null == context.getCommentRef().getComment()) {
            return Mono.error(new RuntimeException("暂无评论内容"));
        }
        return client.fetch(Comment.class, context.getCommentRef().getComment().getMetadata().getName())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无此评论"))))
                .doOnNext(comment -> context.getCommentRef().setComment(comment))
                .flatMap(comment -> userFetcher.fetch(comment.getSpec().getOwner())
                        .doOnNext(commentUser -> context.getCommentRef().setOwner(commentUser)))
                .map(e -> context);
    }

    public Mono<CommentContext> fetchReply(CommentContext context) {
        CommentContext.ReplyRef replyRef = context.getReplyRef();
        if (null == replyRef.getReply()) {
            return Mono.error(new RuntimeException("暂无回复内容"));
        }
        return client.fetch(Reply.class, replyRef.getReply().getMetadata().getName())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无此回复"))))
                .doOnNext(reply -> context.getReplyRef().setReply(reply))
                .flatMap(reply -> userFetcher.fetch(reply.getSpec().getOwner())
                        .doOnNext(replyUser -> context.getReplyRef().setOwner(replyUser)))
                .map(e -> context);
    }

}
