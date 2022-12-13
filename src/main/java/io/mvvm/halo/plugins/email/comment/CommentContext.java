package io.mvvm.halo.plugins.email.comment;

import io.mvvm.halo.plugins.email.support.MailServerConfig;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.content.Post;
import run.halo.app.infra.SystemSetting;

/**
 * CommentContext.
 *
 * @author: pan
 **/
@Data
@SuperBuilder
public class CommentContext {
    /**
     * 评论内容
     */
    private Comment comment;
    /**
     * 评论用户
     */
    private User commentUser;
    /**
     * 评论文章
     */
    private Post commentPost;
    /**
     * 评论文章的作者
     */
    private User postOwner;
    /**
     * 评论系统设置
     */
    private SystemSetting.Comment commentSetting;
    /**
     * 邮件服务器配置
     */
    private MailServerConfig serverConfig;

    /**
     * @return 是否需要审核评论
     */
    public boolean requireReviewForNew() {
        return null != this.commentSetting && Boolean.TRUE.equals(this.commentSetting.getRequireReviewForNew());
    }

}
