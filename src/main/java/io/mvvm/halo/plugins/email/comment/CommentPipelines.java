package io.mvvm.halo.plugins.email.comment;

import io.mvvm.halo.plugins.email.MailBeanContext;
import io.mvvm.halo.plugins.email.MailPublisher;
import io.mvvm.halo.plugins.email.support.SimpleMailMessage;
import io.mvvm.halo.plugins.email.template.ComposeThemeResolver;
import io.mvvm.halo.plugins.email.template.ProcessTemplateResolver;
import io.mvvm.halo.plugins.email.template.TemplateResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.content.Post;

import java.util.concurrent.atomic.AtomicReference;

/**
 * CommentPipelines.
 *
 * @author: pan
 **/
@Slf4j
public class CommentPipelines {

    public final CommentPipeline commentSetting = (context) -> MailBeanContext.environmentFetcher
            .fetchComment()
            .doOnNext(context::setCommentSetting)
            .thenReturn(context);

    public final CommentPipeline mailServerConfig = (context) -> MailBeanContext.environmentFetcher
            .fetchMailServer()
            .doOnNext(context::setServerConfig)
            .thenReturn(context);

    /**
     * 评论人上下文加载器.
     */
    public final CommentPipeline commentUser = (context) -> buildEmailUser(context.getComment().getSpec().getOwner())
            .switchIfEmpty(Mono.defer(() -> fetchUser(context.getComment().getSpec().getOwner().getName())))
            .doOnNext(context::setCommentUser)
            .thenReturn(context);
    /**
     * 评论文章上下文加载器.
     */
    public final CommentPipeline commentPost = (context) -> MailBeanContext.client
            .fetch(Post.class, context.getComment().getSpec().getSubjectRef().getName())
            .doOnNext(context::setCommentPost)
            .thenReturn(context);

    /**
     * 文章作者上下文加载器.
     */
    public final CommentPipeline postOwner = (context) -> MailBeanContext.client
            .fetch(User.class, context.getCommentPost().getSpec().getOwner())
            .doOnNext(context::setPostOwner)
            .thenReturn(context);
    /**
     * 回复的评论上下文加载器.
     */
    public final CommentPipeline replyTargetComment = (context) -> MailBeanContext.client
            .fetch(Comment.class, context.getReplyComment().getSpec().getCommentName())
            .doOnNext(context::setComment)
            .thenReturn(context);
    /**
     * 回复评论人上下文加载器.
     */
    public final CommentPipeline replyUser = (context) -> buildEmailUser(context.getReplyComment().getSpec().getOwner())
            .switchIfEmpty(Mono.defer(() -> fetchUser(context.getReplyComment().getSpec().getOwner().getName())))
            .doOnNext(context::setReplyUser)
            .thenReturn(context);
    private MailPublisher mailPublisher = null;
    private TemplateResolver resolver = null;
    /**
     * 审核评论/回复邮件通知管理员.
     **/
    public final CommentPipeline auditMailSender = (context) -> Mono.defer(() -> {
        if (!context.requireReviewForNew()) {
            return Mono.just(context);
        }
        if (!StringUtils.hasLength(context.getServerConfig().getAdminMail())) {
            return Mono.just(context);
        }
        return resolver.processReactive(CommentTemplateType.Audit, context)
                .map(html -> SimpleMailMessage.builder()
                        .to(context.getServerConfig().getAdminMail())
                        .content(html)
                        .subject(CommentTemplateType.Audit.getDefinition().subject())
                        .build())
                .doOnNext(mailPublisher::publish)
                .thenReturn(context);
    });
    /**
     * 评论/回复邮件通知文章作者.
     */
    public final CommentPipeline commentPostOwnerMailSender = (context) -> Mono.defer(() -> {
        if (context.requireReviewForNew()) {
            return Mono.just(context);
        }
        if (null != context.getReplyComment()
                && context.getPostOwner().getSpec().getEmail().equals(context.getReplyUser().getSpec().getEmail())) {
            return Mono.just(context);
        }
        return resolver.processReactive(CommentTemplateType.Comment, context)
                .map(html -> SimpleMailMessage.builder()
                        .to(context.getPostOwner().getSpec().getEmail())
                        .content(html)
                        .subject(CommentTemplateType.Comment.getDefinition().subject())
                        .build())
                .doOnNext(mailPublisher::publish)
                .thenReturn(context);
    });
    /**
     * 通知被回复的评论人.
     */
    public final CommentPipeline replyTargetCommentUserMailSender = (context) -> Mono.defer(() -> {
        if (context.requireReviewForNew()) {
            return Mono.just(context);
        }
        if (!context.getComment().getSpec().getAllowNotification()) {
            return Mono.just(context);
        }
        if (context.getCommentUser().getSpec().getEmail().equals(context.getReplyUser().getSpec().getEmail())) {
            return Mono.just(context);
        }
        return resolver.processReactive(CommentTemplateType.Reply, context)
                .map(html -> SimpleMailMessage.builder()
                        .to(context.getCommentUser().getSpec().getEmail())
                        .content(html)
                        .subject(CommentTemplateType.Reply.getDefinition().subject())
                        .build())
                .doOnNext(mailPublisher::publish)
                .thenReturn(context);
    });

    /**
     * 评论审核通过通知。
     */
    public final CommentPipeline commentUserAuditSuccessMailSender = (context) -> Mono.defer(() -> {
        AtomicReference<String> email = new AtomicReference<>(context.getCommentUser().getSpec().getEmail());
        if (null != context.getReplyComment()) {
            email.set(context.getReplyUser().getSpec().getEmail());
        }
        return resolver.processReactive(CommentTemplateType.AuditSuccess, context)
                .map(html -> SimpleMailMessage.builder()
                        .to(email.get())
                        .content(html)
                        .subject(CommentTemplateType.AuditSuccess.getDefinition().subject())
                        .build())
                .doOnNext(mailPublisher::publish)
                .thenReturn(context);
    });

    public CommentPipelines(MailPublisher mailPublisher) {
        this.mailPublisher = mailPublisher;
        this.resolver = new ProcessTemplateResolver(new ComposeThemeResolver());
    }

    Mono<User> fetchUser(String username) {
        return MailBeanContext.client.fetch(User.class, username);
    }

    /**
     * 匿名评论用户结构映射
     */
    Mono<User> buildEmailUser(Comment.CommentOwner owner) {
        if (Comment.CommentOwner.KIND_EMAIL.equals(owner.getKind())) {
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
