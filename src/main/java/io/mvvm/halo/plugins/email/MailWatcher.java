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

        // TODO 目前无法在此处感知操作是否是审核通过
//        if ("Comment".equals(oldExtension.getKind())) {
//            Comment oldComment = JsonUtils.jsonToObject(JsonUtils.objectToJson(oldExtension), Comment.class);
//            Comment newComment = JsonUtils.jsonToObject(JsonUtils.objectToJson(newExtension), Comment.class);
//            commentSender.pipeline(oldComment, newComment).subscribe();
//        } else if ("Reply".equals(oldExtension.getKind())) {
//            Reply oldReply = JsonUtils.jsonToObject(JsonUtils.objectToJson(oldExtension), Reply.class);
//            Reply newReply = JsonUtils.jsonToObject(JsonUtils.objectToJson(newExtension), Reply.class);
//            commentSender.pipeline(oldReply, newReply).subscribe();
//        }
    }

    @Override
    public void dispose() {

    }
}
