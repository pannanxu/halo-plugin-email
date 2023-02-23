package io.mvvm.halo.plugins.email.comment.fetcher;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.extension.ReactiveExtensionClient;

/**
 * UserFetcher.
 *
 * @author: pan
 **/
@Component
public class UserFetcher {

    private final ReactiveExtensionClient client;

    public UserFetcher(ReactiveExtensionClient client) {
        this.client = client;
    }

    public Mono<User> fetch(Comment.CommentOwner owner) {
        return buildEmailUser(owner)
                .switchIfEmpty(Mono.defer(() -> client.fetch(User.class, owner.getName())));
    }


    private Mono<User> buildEmailUser(Comment.CommentOwner owner) {
        if (!Comment.CommentOwner.KIND_EMAIL.equals(owner.getKind())) {
            return Mono.empty();
        }
        User user = new User();
        User.UserSpec spec = new User.UserSpec();
        spec.setDisplayName(owner.getDisplayName());
        spec.setEmail(owner.getName());
        spec.setAvatar(owner.getAnnotation(Comment.CommentOwner.AVATAR_ANNO));
        user.setSpec(spec);
        return Mono.just(user);
    }
}
