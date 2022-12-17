package io.mvvm.halo.plugins.email.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论模板.
 *
 * @author: pan
 **/
@AllArgsConstructor
public enum CommentTemplateType {

    /**
     * 评论模板
     */
    Comment(new MailTemplateDefinition("评论模板", "您的文章有新的评论！", "template/comment.html")),
    /**
     * 回复模板
     */
    Reply(new MailTemplateDefinition("评论回复模板", "您的评论有新的回复！", "template/reply.html")),
    /**
     * 审核模板
     */
    Audit(new MailTemplateDefinition("评论审核模板", "您的博客日志有了新的评论需要审核！", "template/audit.html")),
    /**
     * 审核通过模板
     */
    AuditSuccess(new MailTemplateDefinition("评论审核通过模板", "您的评论审核已通过！", "template/auditSuccess.html")),
//    /**
//     * 审核拒绝模板
//     */
//    AuditReject(new MailTemplateDefinition("auditReject", "评论审核拒绝模板", "评论审核拒绝后通知评论评论人")),
    ;

    @Getter
    private final MailTemplateDefinition definition;

}