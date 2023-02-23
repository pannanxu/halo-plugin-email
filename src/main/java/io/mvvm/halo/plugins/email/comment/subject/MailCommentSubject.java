package io.mvvm.halo.plugins.email.comment.subject;

import io.mvvm.halo.plugins.email.comment.CommentContext;
import org.pf4j.ExtensionPoint;
import reactor.core.publisher.Mono;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.Ref;

/**
 * Comment subject.
 *
 * @author guqing
 * @since 2.0.0
 */
public interface MailCommentSubject<T extends AbstractExtension> extends ExtensionPoint {

    Mono<T> get(CommentContext context);

    boolean supports(Ref ref);
}
