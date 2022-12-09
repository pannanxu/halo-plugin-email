package io.mvvm.halo.plugins.email.comment;

import io.mvvm.halo.plugins.email.MailBeanContext;
import io.mvvm.halo.plugins.email.MailMessage;
import io.mvvm.halo.plugins.email.MailPublisher;
import io.mvvm.halo.plugins.email.MailService;
import io.mvvm.halo.plugins.email.support.MailServerConfig;
import io.mvvm.halo.plugins.email.support.SimpleMailMessage;
import io.mvvm.halo.plugins.email.template.ResourceTemplateProcess;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.content.Reply;

import java.util.Map;

/**
 * BaseCommentSender.
 *
 * @author: pan
 **/
@Slf4j
public class BaseCommentSender {

    private final MailPublisher mailPublisher;
    private final ResourceTemplateProcess resourceTemplateProcess;
    private final MailService mailService;

    public BaseCommentSender(MailPublisher mailPublisher, MailService mailService) {
        this.mailPublisher = mailPublisher;
        this.mailService = mailService;
        this.resourceTemplateProcess = new ResourceTemplateProcess(this.getClass().getClassLoader());
    }

    protected void publish(String email, String subject, CommentTemplateType template, Map<String, Object> var) {
        MailServerConfig config = mailService.getCachedConfig();

        String content = resourceTemplateProcess.process("template/" + template.getOption().name() + ".html", var);
        SimpleMailMessage message = MailMessage.of(email == null ? config.getForm() : email);
        message.setContent(content);
        message.setSubject(subject);
        mailPublisher.publish(message);
    }

    protected Mono<User> fetchCommentUser(Comment comment) {
        if (Comment.CommentOwner.KIND_EMAIL.equals(comment.getSpec().getOwner().getKind())) {
            // 评论人是 匿名用户
            Comment.CommentOwner owner = comment.getSpec().getOwner();
            User user = new User();
            User.UserSpec spec = new User.UserSpec();
            spec.setDisplayName(owner.getDisplayName());
            spec.setEmail(owner.getName());
            spec.setAvatar(owner.getAnnotation(Comment.CommentOwner.AVATAR_ANNO));
            user.setSpec(spec);
            return Mono.just(user);
        }
        // 评论人是 认证用户
        return fetchCurrentUser(comment.getSpec().getOwner().getName());
    }

    protected Mono<User> fetchReplyUser(Reply reply) {
        if (Comment.CommentOwner.KIND_EMAIL.equals(reply.getSpec().getOwner().getKind())) {
            // 评论人是 匿名用户
            Comment.CommentOwner owner = reply.getSpec().getOwner();
            User replyUser = new User();
            User.UserSpec spec = new User.UserSpec();
            spec.setDisplayName(owner.getDisplayName());
            spec.setEmail(owner.getName());
            spec.setAvatar(owner.getAnnotation(Comment.CommentOwner.AVATAR_ANNO));
            replyUser.setSpec(spec);
            return Mono.just(replyUser);
        }
        // 评论人是 认证用户
        return fetchCurrentUser(reply.getSpec().getOwner().getName());
    }
    
    protected Mono<User> fetchCurrentUser(String username) {
        return MailBeanContext.client.fetch(User.class, username);
    }

}
