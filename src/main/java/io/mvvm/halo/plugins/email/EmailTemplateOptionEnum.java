package io.mvvm.halo.plugins.email;

import lombok.Getter;

/**
 * @description:
 * @author: pan
 **/
public enum EmailTemplateOptionEnum {

    /**
     * 评论模板
     */
    Comment(new EmailTemplateOption("comment", "评论模板", "文章收到新的评论时通知文章创建人, 如果需要审核则发送: 「审核模板」")),
    /**
     * 回复模板
     */
    Reply(new EmailTemplateOption("reply", "评论回复模板", "评论收到新的回复时通知评论人, 如果需要审核则发送: 「审核模板」")),
    /**
     * 审核模板
     */
    Audit(new EmailTemplateOption("audit", "评论审核模板", "评论需要审核时通知系统管理员")),
    /**
     * 审核通过模板
     */
    AuditSuccess(new EmailTemplateOption("auditSuccess", "评论审核通过模板", "评论审核通过后通知, 通知评论人。" +
            " 同时如果是回复评论则使用：「回复模板」进行通知；如果是新评论则使用：「评论模板」通知")),
    /**
     * 审核拒绝模板
     */
    AuditReject(new EmailTemplateOption("auditReject", "评论审核拒绝模板", "评论审核拒绝后通知评论评论人")),
    ;

    @Getter
    private final EmailTemplateOption option;

    EmailTemplateOptionEnum(EmailTemplateOption option) {
        this.option = option;
    }

}