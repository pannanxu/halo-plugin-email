package io.mvvm.halo.plugins.email.comment;

import io.mvvm.halo.plugins.email.MailBeanContext;
import io.mvvm.halo.plugins.email.MailPublisher;
import io.mvvm.halo.plugins.email.MailService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 新评论发布邮件通知.
 *
 * @author: pan
 **/
@Slf4j
public class CommentSender extends BaseCommentSender {

    public CommentSender(MailPublisher mailPublisher, MailService mailService) {
        super(mailPublisher, mailService);
    }

    public Mono<Void> send(Comment comment) {
        if (comment.getMetadata().getVersion() > 1) {
            return Mono.empty();
        }
        ReactiveExtensionClient client = MailBeanContext.client;

        log.info("Comment: {}", JsonUtils.objectToJson(comment));
        return MailBeanContext.environmentFetcher.fetchComment()
                .flatMap(commentSetting -> fetchCommentUser(comment)
                        .flatMap(owner -> client.fetch(Post.class, comment.getSpec().getSubjectRef().getName())
                                .flatMap(post -> Mono.just(Tuples.of(owner, post))))
                        .flatMap(tuple -> {
                            User commentUser = tuple.getT1();
                            Post commentPost = tuple.getT2();

                            return client.fetch(User.class, commentPost.getSpec().getOwner())
                                    .flatMap(postOwner -> Mono.just(Tuples.of(commentUser, commentPost, postOwner)));
                        })
                        .flatMap(tuple -> {
                            User commentUser = tuple.getT1();
                            Post commentPost = tuple.getT2();
                            User postOwner = tuple.getT3();

                            Map<String, Object> var = new HashMap<>(Map.of(
                                    "comment", comment,
                                    "owner", commentUser,
                                    "post", commentPost,
                                    "postOwner", postOwner,
                                    "postUrl", commentPost.getStatus().getPermalink()
                            ));

                            if (Boolean.TRUE.equals(commentSetting.getRequireReviewForNew())) {
                                // 需要审核的评论, 给管理员发送邮件通知审核
                                var.put("checkUrl", "");
                                publish(null, "您的博客有了新的评论需要审核", CommentTemplateType.Audit, var);
                            } else {
                                // 文章发布者和评论人是同一个则不通知
                                if (!postOwner.getSpec().getEmail().equals(commentUser.getSpec().getEmail())) {
                                    // 无需审核的评论, 通知文章创建人
                                    publish(postOwner.getSpec().getEmail(), "您的博客有了新的评论", CommentTemplateType.Comment, var);
                                }
                            }
                            return Mono.empty();
                        })
                )
                .then();
    }

}
