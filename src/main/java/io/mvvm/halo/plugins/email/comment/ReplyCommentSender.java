package io.mvvm.halo.plugins.email.comment;

import io.mvvm.halo.plugins.email.MailBeanContext;
import io.mvvm.halo.plugins.email.MailPublisher;
import io.mvvm.halo.plugins.email.MailService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.content.Post;
import run.halo.app.core.extension.content.Reply;

import java.util.HashMap;
import java.util.Map;

/**
 * 回复评论邮件通知.
 *
 * @author: pan
 **/
@Slf4j
public class ReplyCommentSender extends BaseCommentSender {

    public ReplyCommentSender(MailPublisher mailPublisher, MailService mailService) {
        super(mailPublisher, mailService);
    }

    public Mono<Void> send(Reply reply) {
        if (reply.getMetadata().getVersion() > 1) {
            return Mono.empty();
        }
        return MailBeanContext.environmentFetcher
                .fetchComment()
                .flatMap(commentSetting -> fetchComment(reply)
                        .flatMap(tuple -> {
                            Comment parentComment = tuple.getT1();
                            Post replyPost = tuple.getT2();
                            User replyUser = tuple.getT3();
                            User postOwner = tuple.getT4();

                            Map<String, Object> var = new HashMap<>(Map.of(
                                    "parentComment", parentComment,
                                    "comment", reply,
                                    "owner", replyUser,
                                    "post", replyPost,
                                    "postOwner", postOwner,
                                    "postUrl", replyPost.getStatus().getPermalink()
                            ));

                            if (Boolean.TRUE.equals(commentSetting.getRequireReviewForNew())) {
                                // 需要审核的评论, 给管理员发送邮件通知审核
                                var.put("checkUrl", "");
                                publish(null, "您的博客日志有了新的回复需要审核！",
                                        CommentTemplateType.Audit, var);
                            } else {
                                // 文章创建人和评论者是同一个则不发送
                                if (!postOwner.getSpec().getEmail().equals(replyUser.getSpec().getEmail())) {
                                    // 无需审核的评论, 通知文章创建人
                                    publish(postOwner.getSpec().getEmail(), "您的博客日志有了新的回复！",
                                            CommentTemplateType.Reply, var);
                                }
                                // 通知被回复的评论创建人
                                return fetchCommentUser(parentComment)
                                        .doOnNext(user -> {
                                            // 如果被回复的是同一个人则不发送此消息
                                            if (!postOwner.getSpec().getEmail().equals(user.getSpec().getEmail())) {
                                                publish(user.getSpec().getEmail(),
                                                        "您在博客留下的评论有了新的回复！",
                                                        CommentTemplateType.Reply, var);
                                            }
                                        })
                                        .then();
                            }
                            return Mono.empty();
                        })
                )
                .then();
    }

    public Mono<Tuple4<Comment, Post, User, User>> fetchComment(Reply reply) {
        return MailBeanContext.client.fetch(Comment.class, reply.getSpec().getCommentName())
                .flatMap(comment -> MailBeanContext.client.fetch(Post.class, comment.getSpec().getSubjectRef().getName())
                        .flatMap(post -> Mono.just(Tuples.of(comment, post)))
                )
                .flatMap(tuple -> {
                    Comment parentComment = tuple.getT1();
                    Post replyPost = tuple.getT2();

                    return fetchReplyUser(reply)
                            .flatMap(replyUser -> Mono.just(Tuples.of(parentComment, replyPost, replyUser)));
                })
                .flatMap(tuple -> {
                    Comment parentComment = tuple.getT1();
                    Post replyPost = tuple.getT2();
                    User replyUser = tuple.getT3();
                    return MailBeanContext.client.fetch(User.class, replyPost.getSpec().getOwner())
                            .flatMap(postOwner -> Mono.just(Tuples.of(parentComment, replyPost,
                                    replyUser, postOwner)));
                });
    }

}
