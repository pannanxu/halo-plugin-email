package io.mvvm.halo.plugins.email.comment.fetcher;

import io.mvvm.halo.plugins.email.comment.CommentContext;
import io.mvvm.halo.plugins.email.comment.subject.MailCommentSubject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Ref;

import java.util.List;

/**
 * SubjectFetcher.
 *
 * @author: pan
 **/
@Component
public class SubjectFetcher {

    private final List<MailCommentSubject> commentSubjects;

    public SubjectFetcher(List<MailCommentSubject> commentSubjects) {
        this.commentSubjects = commentSubjects;
    }


    @SuppressWarnings("unchecked")
    public Mono<CommentContext> fetch(CommentContext context) {
        Ref ref = context.getCommentRef().getComment().getSpec().getSubjectRef();
        return commentSubjects.stream()
                .filter(commentSubject -> commentSubject.supports(ref))
                .findFirst()
                .map(commentSubject -> commentSubject.get(context))
                .orElseGet(Mono::empty)
                .thenReturn(context);
    }
}
