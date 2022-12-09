package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.comment.CommentSender;
import io.mvvm.halo.plugins.email.comment.ReplyCommentSender;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.content.Reply;
import run.halo.app.extension.Extension;
import run.halo.app.extension.Watcher;

/**
 * MailWatcher.
 *
 * @author: pan
 **/
public class MailWatcher implements Watcher {

    private final CommentSender commentSender;
    private final ReplyCommentSender replyCommentSender;

    public MailWatcher(MailPublisher mailPublisher, MailService mailService) {
        this.commentSender = new CommentSender(mailPublisher, mailService);
        this.replyCommentSender = new ReplyCommentSender(mailPublisher, mailService);
    }

    @Override
    public void onAdd(Extension extension) {
        Watcher.super.onAdd(extension);
        if (extension instanceof Comment comment) {
            commentSender.send(comment).subscribe();
        }
        if (extension instanceof Reply reply) {
            replyCommentSender.send(reply).subscribe();
        }
    }

    @Override
    public void onUpdate(Extension oldExtension, Extension newExtension) {
        Watcher.super.onUpdate(oldExtension, newExtension);
    }

    @Override
    public void dispose() {

    }
}
