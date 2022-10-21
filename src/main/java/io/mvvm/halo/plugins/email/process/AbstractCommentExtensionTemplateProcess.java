package io.mvvm.halo.plugins.email.process;

import io.mvvm.halo.plugins.email.AbstractTemplateProcess;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import run.halo.app.core.extension.Post;
import run.halo.app.core.extension.User;

/**
 * @description:
 * @author: pan
 **/
public abstract class AbstractCommentExtensionTemplateProcess extends AbstractTemplateProcess {

    protected Mono<Tuple2<User, Post>> fetchPostAndOwner(String name) {
        return extensionClient.fetch(Post.class, name)
                .flatMap(post ->
                        extensionClient.fetch(User.class, post.getSpec().getOwner()).zipWith(Mono.just(post)));
    }
}