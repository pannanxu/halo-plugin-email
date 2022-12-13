package io.mvvm.halo.plugins.email.comment;

import io.mvvm.halo.plugins.email.MailBeanContext;
import io.mvvm.halo.plugins.email.MailPublisher;
import io.mvvm.halo.plugins.email.support.MailServerConfig;
import io.mvvm.halo.plugins.email.support.SimpleMailMessage;
import io.mvvm.halo.plugins.email.template.ResourceTemplateProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.ConfigMap;
import run.halo.app.infra.utils.JsonUtils;

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

    //    public final CommentPipeline mailServerConfig = (context) -> MailBeanContext.settingFetcher
//            .fetch(MailServerConfig.GROUP, MailServerConfig.class)
//            .map(Mono::just)
//            .orElse(Mono.empty())
//            .doOnNext(context::setServerConfig)
//            .thenReturn(context);
    public final CommentPipeline mailServerConfig = (context) -> MailBeanContext.client
            .get(ConfigMap.class, MailServerConfig.NAME)
            .map(ConfigMap::getData)
            .map(config -> {
                String basic = config.get(MailServerConfig.GROUP);
                return JsonUtils.jsonToObject(basic, MailServerConfig.class);
            })
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
    private ResourceTemplateProcess resourceTemplateProcess = null;
    /**
     * 审核评论/回复邮件通知管理员.
     **/
    public final CommentPipeline auditMailSender = (context) -> Mono.defer(() -> {
        if (!context.requireReviewForNew()) {
            log.debug("Comment replyTargetCommentUserMailSender pipeline: 未开启审核，暂不发送管理员审核通知");
            return Mono.just(context);
        }
        if (!StringUtils.hasLength(context.getServerConfig().getAdminMail())) {
            log.debug("Comment replyTargetCommentUserMailSender pipeline: 管理员邮箱为空，暂不发送通知");
            return Mono.just(context);
        }
        return resourceTemplateProcess.processReactive(CommentTemplateType.Audit, context)
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
            log.debug("Comment replyTargetCommentUserMailSender pipeline: 开启审核，暂不给文章作者发送通知");
            return Mono.just(context);
        }
        if (null != context.getReplyComment()
                && context.getPostOwner().getSpec().getEmail().equals(context.getReplyUser().getSpec().getEmail())) {
            log.debug("Comment replyTargetCommentUserMailSender pipeline: 文章创建人和评论人相同，无需发送");
            return Mono.just(context);
        }
        return resourceTemplateProcess.processReactive(CommentTemplateType.Comment, context)
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
            log.debug("Comment replyTargetCommentUserMailSender pipeline: 开启审核，暂不给被回复的评论人发送通知");
            return Mono.just(context);
        }
        if (!context.getComment().getSpec().getAllowNotification()) {
            log.debug("Comment replyTargetCommentUserMailSender pipeline: 评论人未开启消息提醒，无需发送");
            return Mono.just(context);
        }
        if (context.getCommentUser().getSpec().getEmail().equals(context.getReplyUser().getSpec().getEmail())) {
            log.debug("Comment replyTargetCommentUserMailSender pipeline: 回复人和评论人相同，无需发送");
            return Mono.just(context);
        }
        return resourceTemplateProcess.processReactive(CommentTemplateType.Reply, context)
                .map(html -> SimpleMailMessage.builder()
                        .to(context.getCommentUser().getSpec().getEmail())
                        .content(html)
                        .subject(CommentTemplateType.Reply.getDefinition().subject())
                        .build())
                .doOnNext(mailPublisher::publish)
                .thenReturn(context);
    });

    public CommentPipelines(MailPublisher mailPublisher) {
        this.mailPublisher = mailPublisher;
        this.resourceTemplateProcess = new ResourceTemplateProcess();
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
