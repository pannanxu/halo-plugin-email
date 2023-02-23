package io.mvvm.halo.plugins.email.comment.fetcher;

import io.mvvm.halo.plugins.email.MailMessage;
import io.mvvm.halo.plugins.email.comment.CommentContext;
import io.mvvm.halo.plugins.email.comment.CommentTemplateType;
import io.mvvm.halo.plugins.email.support.SimpleMailMessage;
import io.mvvm.halo.plugins.email.template.TemplateResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.User;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * SenderFetcher.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class SenderFetcher {

    private final TemplateResolver resolver;

    public SenderFetcher(TemplateResolver resolver) {
        this.resolver = resolver;
    }

    public Flux<MailMessage> fetch(CommentContext context) {
        Consumer<FluxSink<Mono<MailMessage>>> consumer = sink -> {
            // 通知 <管理员审核>
            adminReview(context, sink);
            // 通知 <文章作者>
            subjectAuthor(context, sink);
            // 通知 <评论的创建人>
            replyComment(context, sink);
            sink.complete();
        };
        return Flux.create(consumer).flatMap(Function.identity());
    }

    private void adminReview(CommentContext context, FluxSink<Mono<MailMessage>> sink) {
        CommentContext.Base base = context.getBase();
        log.debug("SenderFetcher.adminReview requireReviewForNew: {}", base.requireReviewForNew());
        // 通知 <管理员审核>
        if (base.requireReviewForNew()) {
            if (StringUtils.hasLength(base.getServerConfig().getAdminMail())) {
                log.debug("SenderFetcher.adminReview. 通知管理员邮箱: {}", base.getServerConfig().getAdminMail());
                sink.next(resolver.process(CommentTemplateType.Audit.getDefinition().path(), context)
                        .map(html -> SimpleMailMessage.builder()
                                .to(base.getServerConfig().getAdminMail())
                                .content(html)
                                .subject(CommentTemplateType.Audit.getDefinition().subject())
                                .fromName(base.getServerConfig().getFromName())
                                .build()));
            }
        }
    }

    private void subjectAuthor(CommentContext context, FluxSink<Mono<MailMessage>> sink) {
        // 通知 <文章作者>
        CommentContext.Base base = context.getBase();
        if (!base.requireReviewForNew()) {
            User owner = context.getCommentRef().getOwner();
            User subjectOwner = context.getCommentSubject().getOwner();
            User replyOwner = context.getReplyRef().getOwner();

            if (null == context.getReplyRef().getReply()) {
                // 新评论: 主题作者的邮箱 == 评论人的邮箱
                if (subjectOwner.getSpec().getEmail().equals(owner.getSpec().getEmail())) {
                    log.debug("SenderFetcher.subjectAuthor. 通知 <文章作者>. 新评论: 主题作者的邮箱[{}] == 评论人的邮箱[{}].",
                            subjectOwner.getSpec().getEmail(), owner.getSpec().getEmail());
                    return;
                }
            } else {
                // 新回复: 主题作者的邮箱 == 回复人的邮箱
                if (subjectOwner.getSpec().getEmail().equals(replyOwner.getSpec().getEmail())) {
                    log.debug("SenderFetcher.subjectAuthor. 通知 <文章作者>. 新回复: 主题作者的邮箱[{}] == 回复人的邮箱[{}]",
                            subjectOwner.getSpec().getEmail(), owner.getSpec().getEmail());
                    return;
                }
            }
            log.debug("SenderFetcher.subjectAuthor. 通知 {} 作者: {}",
                    context.getCommentSubject().getKind(), subjectOwner.getSpec().getEmail());
            sink.next(resolver.process(CommentTemplateType.Comment.getDefinition().path(), context)
                    .map(html -> SimpleMailMessage.builder()
                            .to(subjectOwner.getSpec().getEmail())
                            .content(html)
                            .subject(CommentTemplateType.Comment.getDefinition().subject())
                            .fromName(base.getServerConfig().getFromName())
                            .build()));

        }
    }

    private void replyComment(CommentContext context, FluxSink<Mono<MailMessage>> sink) {
        // 通知 <评论的创建人>
        CommentContext.Base base = context.getBase();
        if (!base.requireReviewForNew()) {
            User owner = context.getCommentRef().getOwner();
            User replyOwner = context.getReplyRef().getOwner();
            if (null == replyOwner) {
                return;
            }

            // 评论人邮箱 == 回复人邮箱
            if (owner.getSpec().getEmail().equals(replyOwner.getSpec().getEmail())) {
                log.debug("SenderFetcher.replyComment. 通知 <评论的创建人>. 新回复: 评论人邮箱[{}] == 回复人邮箱[{}]",
                        owner.getSpec().getEmail(), replyOwner.getSpec().getEmail());
                return;
            }
            log.debug("SenderFetcher.replyComment. 通知评论人: {}", owner.getSpec().getEmail());
            sink.next(resolver.process(CommentTemplateType.Reply.getDefinition().path(), context)
                    .map(html -> SimpleMailMessage.builder()
                            .to(owner.getSpec().getEmail())
                            .content(html)
                            .subject(CommentTemplateType.Reply.getDefinition().subject())
                            .fromName(base.getServerConfig().getFromName())
                            .build()));
        }
    }
}
