package io.mvvm.halo.plugins.email.comment;

import io.mvvm.halo.plugins.email.support.MailServerConfig;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.content.Post;
import run.halo.app.core.extension.content.Reply;
import run.halo.app.extension.Extension;
import run.halo.app.infra.SystemSetting;

/**
 * CommentContext.
 *
 * @author: pan
 **/
@Data
@SuperBuilder
public class CommentContext {

    private Base base;

    private CommentRef commentRef;

    private ReplyRef replyRef;

    private CommentSubject commentSubject;

    public Base getBase() {
        if (null == this.base) {
            this.base = Base.builder().build();
        }
        return base;
    }

    public CommentRef getCommentRef() {
        if (null == this.commentRef) {
            this.commentRef = CommentRef.builder().build();
        }
        return commentRef;
    }

    public ReplyRef getReplyRef() {
        if (null == this.replyRef) {
            this.replyRef = ReplyRef.builder().build();
        }
        return replyRef;
    }

    public CommentSubject getCommentSubject() {
        if (null == this.commentSubject) {
            this.commentSubject = CommentSubject.builder().build();
        }
        return commentSubject;
    }

    @Data
    @SuperBuilder
    public static class Base {
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
            return null != this.commentSetting
                   && Boolean.TRUE.equals(this.commentSetting.getRequireReviewForNew());
        }

    }

    @Data
    @SuperBuilder
    public static class CommentRef {
        /**
         * 评论内容
         */
        private Comment comment;
        /**
         * 评论用户
         */
        private User owner;
    }

    @Data
    @SuperBuilder
    public static class ReplyRef {
        /**
         * 回复评论
         */
        private Reply reply;
        /**
         * 回复用户
         */
        private User owner;
    }

    @Data
    @SuperBuilder
    public static class CommentSubject {
        /**
         * {@link Post#KIND}
         * <p>
         * {@link run.halo.app.core.extension.content.SinglePage#KIND}
         */
        private String kind;
        private Extension original;
        /**
         * 标题
         */
        private String title;
        /**
         * 作者
         */
        private User owner;
        /**
         * 永久链接
         */
        private String permalink;

    }

}
