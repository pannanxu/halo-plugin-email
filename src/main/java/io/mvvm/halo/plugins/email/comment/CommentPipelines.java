package io.mvvm.halo.plugins.email.comment;

import io.mvvm.halo.plugins.email.MailPublisher;
import io.mvvm.halo.plugins.email.support.MailEnvironmentFetcher;
import io.mvvm.halo.plugins.email.support.SimpleMailMessage;
import io.mvvm.halo.plugins.email.template.ProcessTemplateResolver;
import io.mvvm.halo.plugins.email.template.TemplateResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.ReactiveExtensionClient;

import java.util.concurrent.atomic.AtomicReference;

/**
 * CommentPipelines.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class CommentPipelines {

    public final CommentPipeline commentSetting = commentSetting();
    public final CommentPipeline mailServerConfig = mailServerConfig();
    /**
     * 评论人上下文加载器.
     */
    public final CommentPipeline commentUser = commentUser();
    /**
     * 评论文章上下文加载器.
     */
    public final CommentPipeline commentPost = commentPost();
    /**
     * 文章作者上下文加载器.
     */
    public final CommentPipeline postOwner = postOwner();
    /**
     * 回复的评论上下文加载器.
     */
    public final CommentPipeline replyTargetComment = replyTargetComment();
    /**
     * 回复评论人上下文加载器.
     */
    public final CommentPipeline replyUser = replyUser();

    private final ReactiveExtensionClient client;
    private final MailEnvironmentFetcher environmentFetcher;
    private final MailPublisher mailPublisher;
    private final TemplateResolver resolver;

    /**
     * 审核评论/回复邮件通知管理员.
     **/
    public final CommentPipeline auditMailSender = auditMailSender();
    /**
     * 评论/回复邮件通知文章作者.
     */
    public final CommentPipeline commentPostOwnerMailSender = commentPostOwnerMailSender();
    /**
     * 通知被回复的评论人.
     */
    public final CommentPipeline replyTargetCommentUserMailSender = replyTargetCommentUserMailSender();
    /**
     * 评论审核通过通知。
     */
    public final CommentPipeline commentUserAuditSuccessMailSender = commentUserAuditSuccessMailSender();

    public CommentPipelines(ReactiveExtensionClient client,
                            MailEnvironmentFetcher environmentFetcher, 
                            MailPublisher mailPublisher,
                            ProcessTemplateResolver processTemplateResolver) {
        this.client = client;
        this.environmentFetcher = environmentFetcher;
        this.mailPublisher = mailPublisher;
        this.resolver = processTemplateResolver;
    }

    private CommentPipeline commentSetting() {
        return (context) -> environmentFetcher.fetchComment()
                .doOnNext(context::setCommentSetting)
                .thenReturn(context);
    }

    private CommentPipeline mailServerConfig() {
        return (context) -> environmentFetcher.fetchMailServer()
                .doOnNext(context::setServerConfig)
                .thenReturn(context);
    }

    private CommentPipeline commentUser() {
        return (context) -> buildEmailUser(context.getComment().getSpec().getOwner())
                .switchIfEmpty(Mono.defer(() -> fetchUser(context.getComment().getSpec().getOwner().getName())))
                .doOnNext(context::setCommentUser)
                .thenReturn(context);
    }

    private CommentPipeline commentPost() {
        return (context) -> client.fetch(Post.class, context.getComment().getSpec().getSubjectRef().getName())
                .doOnNext(context::setCommentPost)
                .thenReturn(context);
    }

    private CommentPipeline postOwner() {
        return (context) -> client.fetch(User.class, context.getCommentPost().getSpec().getOwner())
                .doOnNext(context::setPostOwner)
                .thenReturn(context);
    }

    private CommentPipeline replyTargetComment() {
        return (context) -> client.fetch(Comment.class, context.getReplyComment().getSpec().getCommentName())
                .doOnNext(context::setComment)
                .thenReturn(context);
    }

    private CommentPipeline replyUser() {
        return (context) -> buildEmailUser(context.getReplyComment().getSpec().getOwner())
                .switchIfEmpty(Mono.defer(() -> fetchUser(context.getReplyComment().getSpec().getOwner().getName())))
                .doOnNext(context::setReplyUser)
                .thenReturn(context);
    }

    private CommentPipeline auditMailSender() {
        return (context) -> Mono.defer(() -> {
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
                            .fromName(context.getServerConfig().getFromName())
                            .build())
                    .flatMap(mailPublisher::publishReactive)
                    .thenReturn(context);
        });
    }

    private CommentPipeline commentPostOwnerMailSender() {
        return (context) -> Mono.defer(() -> {
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
                            .fromName(context.getServerConfig().getFromName())
                            .build())
                    .flatMap(mailPublisher::publishReactive)
                    .thenReturn(context);
        });
    }

    private CommentPipeline replyTargetCommentUserMailSender() {
        return (context) -> Mono.defer(() -> {
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
                            .fromName(context.getServerConfig().getFromName())
                            .build())
                    .flatMap(mailPublisher::publishReactive)
                    .thenReturn(context);
        });
    }

    private CommentPipeline commentUserAuditSuccessMailSender() {
        return (context) -> Mono.defer(() -> {
            AtomicReference<String> email = new AtomicReference<>(context.getCommentUser().getSpec().getEmail());
            if (null != context.getReplyComment()) {
                email.set(context.getReplyUser().getSpec().getEmail());
            }
            return resolver.processReactive(CommentTemplateType.AuditSuccess, context)
                    .map(html -> SimpleMailMessage.builder()
                            .to(email.get())
                            .content(html)
                            .subject(CommentTemplateType.AuditSuccess.getDefinition().subject())
                            .fromName(context.getServerConfig().getFromName())
                            .build())
                    .flatMap(mailPublisher::publishReactive)
                    .thenReturn(context);
        });
    }

    Mono<User> fetchUser(String username) {
        return client.fetch(User.class, username);
    }

    /**
     * 匿名评论用户结构映射
     */
    Mono<User> buildEmailUser(Comment.CommentOwner owner) {
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
