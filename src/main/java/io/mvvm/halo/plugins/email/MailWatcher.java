package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.comment.CommentSender;
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

    public MailWatcher(MailPublisher mailPublisher) {
        this.commentSender = new CommentSender(mailPublisher);
    }

    @Override
    public void onAdd(Extension extension) {
        Watcher.super.onAdd(extension);
        if (extension instanceof Comment comment) {
            commentSender.pipeline(comment).subscribe();
        }
        if (extension instanceof Reply reply) {
            commentSender.pipeline(reply).subscribe();
        }
    }

    @Override
    public void onUpdate(Extension oldExtension, Extension newExtension) {
        Watcher.super.onUpdate(oldExtension, newExtension);
        if (oldExtension instanceof Comment comment && newExtension instanceof Comment newComment) {
            commentSender.pipeline(comment, newComment).subscribe();
        }
        if (oldExtension instanceof Reply reply && newExtension instanceof Reply newReply) {
            commentSender.pipeline(reply, newReply).subscribe();
        }
    }

    @Override
    public void dispose() {

    }
}
