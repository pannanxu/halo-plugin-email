package io.mvvm.halo.plugins.email.comment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.Reply;

/**
 * ReplyCommentContext.
 *
 * @author: pan
 **/
@SuperBuilder
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReplyCommentContext extends CommentContext {
    /**
     * 回复评论
     */
    private Reply replyComment;
    /**
     * 回复用户
     */
    private User replyUser;
    
    
}
