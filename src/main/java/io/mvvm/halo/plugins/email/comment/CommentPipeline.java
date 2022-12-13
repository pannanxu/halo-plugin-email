package io.mvvm.halo.plugins.email.comment;

import reactor.core.publisher.Mono;

/**
 * ContextLoaderPipe.
 *
 * @author: pan
 **/
@FunctionalInterface
public interface CommentPipeline {

    Mono<ReplyCommentContext> loader(ReplyCommentContext context);

}
