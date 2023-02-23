package io.mvvm.halo.plugins.email.comment.subject;

import io.mvvm.halo.plugins.email.comment.CommentContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.SinglePage;
import run.halo.app.extension.GroupVersionKind;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.Ref;

/**
 * Comment subject for {@link SinglePage}.
 *
 * @author guqing
 * @since 2.0.0
 */
@Component
public class SinglePageCommentSubject implements MailCommentSubject<SinglePage> {

    private final ReactiveExtensionClient client;

    public SinglePageCommentSubject(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Mono<SinglePage> get(CommentContext context) {
        Ref ref = context.getCommentRef().getComment().getSpec().getSubjectRef();
        return client.fetch(SinglePage.class, ref.getName())
                .doOnNext(page -> {
                    context.getCommentSubject().setKind(SinglePage.KIND);
                    context.getCommentSubject().setTitle(page.getSpec().getTitle());
                    context.getCommentSubject().setPermalink(page.getStatus().getPermalink());
                    context.getCommentSubject().setOriginal(page);
                })
                .flatMap(page -> client.fetch(User.class, page.getSpec().getOwner())
                        .doOnNext(user -> context.getCommentSubject().setOwner(user))
                        .thenReturn(page));
    }

    @Override
    public boolean supports(Ref ref) {
        Assert.notNull(ref, "Subject ref must not be null.");
        GroupVersionKind groupVersionKind =
                new GroupVersionKind(ref.getGroup(), ref.getVersion(), ref.getKind());
        return GroupVersionKind.fromExtension(SinglePage.class).equals(groupVersionKind);
    }
}
