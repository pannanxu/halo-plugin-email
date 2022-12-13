package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.comment.CommentTemplateType;
import io.mvvm.halo.plugins.email.comment.ReplyCommentContext;
import reactor.core.publisher.Mono;

/**
 * TemplateResolver.
 *
 * @author: pan
 **/
public interface TemplateResolver {

    Mono<String> processReactive(CommentTemplateType template, ReplyCommentContext variables);

}
