package io.mvvm.halo.plugins.email.process;

import io.mvvm.halo.plugins.email.EMailTemplateEngineManager;
import io.mvvm.halo.plugins.email.ExtensionTemplateProcess;
import io.mvvm.halo.plugins.email.EMallSendEndpoint;
import io.mvvm.halo.plugins.email.EmailMessage;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Flux;
import run.halo.app.core.extension.Reply;

/**
 * @description:
 * @author: pan
 **/
//@Component
public class ReplyExtensionTemplateProcess implements ExtensionTemplateProcess {

    private final EMailTemplateEngineManager engineManager;

    public ReplyExtensionTemplateProcess(EMailTemplateEngineManager engineManager) {
        this.engineManager = engineManager;
    }

    @Override
    public String getEndpoint() {
        return EMallSendEndpoint.ExtensionAdd.name();
    }

    @Override
    public Flux<EmailMessage> process(Object extension) {
        if (extension instanceof Reply reply) {
            Context context = new Context();
            context.setVariable("comment", reply);
            String process = engineManager.getTemplateEngine().process(ExtensionTemplateProcessEnum.Reply.getValue(), context);
            return Flux.just(new EmailMessage("2369701264@qq.com", "评论收到新的回复", process));
        }
        return Flux.empty();
    }
}