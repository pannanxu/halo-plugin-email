package io.mvvm.halo.plugins.email.process;

import io.mvvm.halo.plugins.email.EmailTemplateEngineManager;
import io.mvvm.halo.plugins.email.ExtensionTemplateProcess;
import io.mvvm.halo.plugins.email.EmallSendEndpoint;
import io.mvvm.halo.plugins.email.EmailMessage;
import io.mvvm.halo.plugins.email.EmailTemplateOptionEnum;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Flux;
import run.halo.app.core.extension.Reply;

/**
 * @description:
 * @author: pan
 **/
//@Component
public class ReplyExtensionTemplateProcess implements ExtensionTemplateProcess {

    private final EmailTemplateEngineManager engineManager;

    public ReplyExtensionTemplateProcess(EmailTemplateEngineManager engineManager) {
        this.engineManager = engineManager;
    }

    @Override
    public String getEndpoint() {
        return EmallSendEndpoint.ExtensionAdd.name();
    }

    @Override
    public Flux<EmailMessage> process(Object extension) {
        if (extension instanceof Reply reply) {
            Context context = new Context();
            context.setVariable("comment", reply);
            String process = engineManager.getTemplateEngine().process(EmailTemplateOptionEnum.Reply.getOption().name(), context);
            return Flux.just(new EmailMessage("2369701264@qq.com", "评论收到新的回复", process));
        }
        return Flux.empty();
    }
}