package io.mvvm.halo.plugins.email.process;

import io.mvvm.halo.plugins.email.EmailMessage;
import io.mvvm.halo.plugins.email.EmailTemplateOptionEnum;
import io.mvvm.halo.plugins.email.EmallSendEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import run.halo.app.core.extension.Comment;
import run.halo.app.core.extension.Post;
import run.halo.app.core.extension.User;

/**
 * @description:
 * @author: pan
 **/
@Slf4j
public class CommentExtensionTemplateProcess extends AbstractCommentExtensionTemplateProcess {

    @Override
    public String getEndpoint() {
        return EmallSendEndpoint.ExtensionAdd.name();
    }

    @Override
    public Flux<EmailMessage> process(Object extension) {
        if (extension instanceof Comment comment) {
            return flatMapManyCommentSetting(comment);
        }
        return Flux.empty();
    }

    Flux<EmailMessage> flatMapManyCommentSetting(Comment comment) {
        return !comment.getSpec().getApproved()
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
            return fetchPostAndOwner(comment.getSpec().getSubjectRef().getName())
                    .flatMapMany(tuple -> {
                        Context context = buildCommentContext(comment, tuple);
                        context.setVariable("comment", comment);
                        context.setVariable("checkUrl", "https://github.com/pannanxu/halo-plugin-email"); // TODO 一键审核
                        return templateToHtml(EmailTemplateOptionEnum.Audit.getOption().name(), context)
                                .flatMapMany(templateHtml ->
                                        Flux.just(new EmailMessage(serverConfig.getAdminEmail(),
                                                "您的博客日志有了新的评论需要审核", templateHtml)));
                    });
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
                    Context context = buildCommentContext(comment, tuple);
                    return templateToHtml(EmailTemplateOptionEnum.Comment.getOption().name(), context)
                            .flatMapMany(templateHtml ->
                                    Flux.just(new EmailMessage(postOwner.getSpec().getEmail(),
                                            "您的日志有了新的评论", templateHtml)));
                });
    }

    private Context buildCommentContext(Comment comment, Tuple2<User, Post> tuple) {
        User postOwner = tuple.getT1();
        Post post = tuple.getT2();

        Context context = new Context();
        context.setVariable("post", post);
        context.setVariable("postOwner", postOwner);
        context.setVariable("comment", comment);
        return context;
    }

}