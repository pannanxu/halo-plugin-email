package io.mvvm.halo.plugins.email.comment;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.content.Reply;

/**
 * 新评论发布邮件通知.
 *
 * @author: pan
 **/
@Slf4j
public class CommentSender {

    private final CommentPipelines pipelines;

    public CommentSender(CommentPipelines pipelines) {
        this.pipelines = pipelines;
    }

    public Mono<Void> pipeline(Comment comment) {
        return Mono.just(ReplyCommentContext.builder().comment(comment).build())
                // 加载评论设置
                .flatMap(pipelines.commentSetting::loader)
                // 加载邮件服务配置
                .flatMap(pipelines.mailServerConfig::loader)
                // 加载评论人
                .flatMap(pipelines.commentUser::loader)
                // 加载评论文章
                .flatMap(pipelines.commentPost::loader)
                // 加载文章作者
                .flatMap(pipelines.postOwner::loader)
                // 管理员审核通知
                .flatMap(pipelines.auditMailSender::loader)
                // 文章作者通知
                .flatMap(pipelines.commentPostOwnerMailSender::loader)
                .then();
    }

    public Mono<Void> pipeline(Reply reply) {
        return Mono.just(ReplyCommentContext.builder().replyComment(reply).build())
                // 加载邮件服务配置
                .flatMap(pipelines.mailServerConfig::loader)
                // 加载评论设置
                .flatMap(pipelines.commentSetting::loader)
                // 加载回复人
                .flatMap(pipelines.replyUser::loader)
                // 加载回复的评论
                .flatMap(pipelines.replyTargetComment::loader)
                // 加载评论人
                .flatMap(pipelines.commentUser::loader)
                // 加载评论文章
                .flatMap(pipelines.commentPost::loader)
                // 加载文章作者
                .flatMap(pipelines.postOwner::loader)
                // 审核评论/回复邮件通知管理员
                .flatMap(pipelines.auditMailSender::loader)
                // 评论/回复邮件通知文章作者
                .flatMap(pipelines.commentPostOwnerMailSender::loader)
                // 通知被回复的评论人
                .flatMap(pipelines.replyTargetCommentUserMailSender::loader)
                .then();
    }
}
