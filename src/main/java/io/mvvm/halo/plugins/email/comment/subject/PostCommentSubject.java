package io.mvvm.halo.plugins.email.comment.subject;

import io.mvvm.halo.plugins.email.comment.CommentContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.GroupVersionKind;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.Ref;

/**
 * Comment subject for post.
 *
 * @author guqing
 * @since 2.0.0
 */
@Component
public class PostCommentSubject implements MailCommentSubject<Post> {

    private final ReactiveExtensionClient client;

    public PostCommentSubject(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Mono<Post> get(CommentContext context) {
        Ref ref = context.getCommentRef().getComment().getSpec().getSubjectRef();
        return client.fetch(Post.class, ref.getName())
                .doOnNext(post -> {
                    context.getCommentSubject().setKind(Post.KIND);
                    context.getCommentSubject().setTitle(post.getSpec().getTitle());
                    context.getCommentSubject().setPermalink(post.getStatus().getPermalink());
                    context.getCommentSubject().setOriginal(post);
                })
                .flatMap(post -> client.fetch(User.class, post.getSpec().getOwner())
                        .doOnNext(user -> context.getCommentSubject().setOwner(user))
                        .thenReturn(post));
    }

    @Override
    public boolean supports(Ref ref) {
        Assert.notNull(ref, "Subject ref must not be null.");
        GroupVersionKind groupVersionKind =
            new GroupVersionKind(ref.getGroup(), ref.getVersion(), ref.getKind());
        return GroupVersionKind.fromExtension(Post.class).equals(groupVersionKind);
    }
}
