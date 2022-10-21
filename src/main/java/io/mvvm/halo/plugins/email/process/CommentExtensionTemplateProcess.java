package io.mvvm.halo.plugins.email.process;

import io.mvvm.halo.plugins.email.EMailTemplateEngineManager;
import io.mvvm.halo.plugins.email.EMallSendEndpoint;
import io.mvvm.halo.plugins.email.EmailMessage;
import io.mvvm.halo.plugins.email.EmailTemplateOptionEnum;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Flux;
import run.halo.app.core.extension.Comment;
import run.halo.app.core.extension.Post;
import run.halo.app.core.extension.User;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.SystemConfigurableEnvironmentFetcher;
import run.halo.app.infra.SystemSetting;

/**
 * @description:
 * @author: pan
 **/
public class CommentExtensionTemplateProcess extends AbstractCommentExtensionTemplateProcess {
    public CommentExtensionTemplateProcess(EMailTemplateEngineManager engineManager,
                                           SystemConfigurableEnvironmentFetcher environmentFetcher,
                                           ReactiveExtensionClient extensionClient) {
        setEnvironmentFetcher(environmentFetcher);
        setExtensionClient(extensionClient);
        setEngineManager(engineManager);
    }

    @Override
    public String getEndpoint() {
        return EMallSendEndpoint.ExtensionAdd.name();
    }

    @Override
    public Flux<EmailMessage> process(Object extension) {
        if (extension instanceof Comment comment) {
            return fetchSystemSetting(SystemSetting.Comment.GROUP, SystemSetting.Comment.class)
                    .flatMapMany(commentSetting -> flatMapManyCommentSetting(comment, commentSetting));
        }
        return Flux.empty();
    }

    Flux<EmailMessage> flatMapManyCommentSetting(Comment comment, SystemSetting.Comment commentSetting) {
        return commentSetting.getRequireReviewForNew()
                ? auditComment(comment)
                : noNeedAuditComment(comment);
    }

    /**
     * 构建审核邮件消息
     * <p>
     * 如果系统设置是需要审核新评论的，则会构建一条邮件消息发送给系统管理员的邮箱中
     *
     * @param comment 评论内容
     * @return 需要给系统管理员推送的邮件
     */
    Flux<EmailMessage> auditComment(Comment comment) {
        return getEmailServerConfig().flatMapMany(serverConfig -> {
            Context context = new Context();
            context.setVariable("comment", comment);
            String process = process(EmailTemplateOptionEnum.Audit.getOption().name(), context);
            return Flux.just(new EmailMessage(serverConfig.getAdminEmail(), "您的博客日志有了新的评论需要审核", process));
        });
    }

    /**
     * 构建无需审核的邮件消息
     * <p>
     * 如果系统设置无需审核
     * <p>
     * 则给文章发布者发送通知, 如果发布者和评论者是同一个邮箱，则不发送通知
     *
     * @param comment 评论内容
     * @return 需要给文章发布者推送的邮件
     */
    Flux<EmailMessage> noNeedAuditComment(Comment comment) {
        return fetchPostAndOwner(comment.getSpec().getSubjectRef().getName())
                .filter(tuple -> !tuple.getT1().getSpec().getDisplayName().equals(comment.getSpec().getOwner().getName()))
                .flatMapMany(tuple -> {
                    User postOwner = tuple.getT1();
                    Post post = tuple.getT2();

                    Context context = new Context();
                    context.setVariable("post", post);
                    context.setVariable("postOwner", postOwner);

                    String process = process(EmailTemplateOptionEnum.Comment.getOption().name(), context);
                    return Flux.just(new EmailMessage(postOwner.getSpec().getEmail(), "您的日志有了新的评论", process));
                });
    }

}