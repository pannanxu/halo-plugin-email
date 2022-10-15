package io.mvvm.halo.plugins.email.process;

import io.mvvm.halo.plugins.email.AbstractTemplateProcess;
import io.mvvm.halo.plugins.email.EMailTemplateEngineManager;
import io.mvvm.halo.plugins.email.EMallSendEndpoint;
import io.mvvm.halo.plugins.email.EmailMessage;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Flux;
import run.halo.app.core.extension.Comment;

/**
 * @description:
 * @author: pan
 **/
public class CommentExtensionTemplateProcess extends AbstractTemplateProcess {
    public CommentExtensionTemplateProcess(EMailTemplateEngineManager engineManager) {
        setEngineManager(engineManager);
    }

    @Override
    public String getEndpoint() {
        return EMallSendEndpoint.ExtensionAdd.name();
    }

    @Override
    public Flux<EmailMessage> process(Object extension) {
        if (extension instanceof Comment comment) {

            // 新评论是否开启审核
            // 开启: 给管理员发送待审核通知
            //     审核通过: 执行: 1. 未开启();
            //                   2. 给评论创建人发送审核通过通知
            //              校验：如果创建人和评论创建人一致则只发送审核通过通知
            //     审核拒绝: 给评论创建人发送审核拒绝通知
            // 未开启: 给 Post 创建人发送新评论通知
            // 校验：如果是管理员评论则审核后不通知评论创建人。如果Post创建人是管理员则不通知

            Context context = new Context();
            context.setVariable("comment", comment);
            String process = process(ExtensionTemplateProcessEnum.Comment.getValue(), context);
            return Flux.just(new EmailMessage("2369701264@qq.com", "收到新的评论", process));
        }
        return Flux.empty();
    }
}