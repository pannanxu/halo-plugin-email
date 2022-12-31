package io.mvvm.halo.plugins.email.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.content.Reply;

/**
 * 新评论发布邮件通知.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class CommentSender {

    private final CommentPipelines pipelines;

    public CommentSender(CommentPipelines pipelines) {
        this.pipelines = pipelines;
    }

    /**
     * 新评论.
     */
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
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    /**
     * 新回复.
     */
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
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    /**
     * 评论审核通过
     *
     * @param comment    修改前的评论
     * @param newComment 修改后的评论
     */
    public Mono<Void> pipeline(Comment comment, Comment newComment) {
        // 修改前状态是未审核，修改后状态是已审核才是审核通过
        if (!comment.getSpec().getApproved()
            && newComment.getSpec().getApproved()
            && null == comment.getSpec().getApprovedTime()
            && null != newComment.getSpec().getApprovedTime()) {
            return Mono.just(ReplyCommentContext.builder().comment(newComment).build())
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
                    // 通知评论创建人审核通过
                    .flatMap(pipelines.commentUserAuditSuccessMailSender::loader)
                    // 文章作者通知
                    .flatMap(pipelines.commentPostOwnerMailSender::loader)
                    .subscribeOn(Schedulers.boundedElastic())
                    .then();
        }
        return Mono.empty();
    }

    /**
     * 回复审核通过
     *
     * @param reply    修改前的回复
     * @param newReply 修改后的回复
     */
    public Mono<Void> pipeline(Reply reply, Reply newReply) {
        if (!reply.getSpec().getApproved()
            && newReply.getSpec().getApproved()
            && null == reply.getSpec().getApprovedTime()
            && null != newReply.getSpec().getApprovedTime()) {
            return Mono.just(ReplyCommentContext.builder().replyComment(newReply).build())
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
                    // 通知回复人审核通过
                    .flatMap(pipelines.commentUserAuditSuccessMailSender::loader)
                    // 评论/回复邮件通知文章作者
                    .flatMap(pipelines.commentPostOwnerMailSender::loader)
                    // 通知被回复的评论人
                    .flatMap(pipelines.replyTargetCommentUserMailSender::loader)
                    .subscribeOn(Schedulers.boundedElastic())
                    .then();
        }
        return Mono.empty();
    }
}
