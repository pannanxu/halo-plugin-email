package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.process.CommentExtensionTemplateProcess;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.extension.Extension;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.SchemeManager;
import run.halo.app.extension.Watcher;
import run.halo.app.infra.SystemConfigurableEnvironmentFetcher;
import run.halo.app.plugin.BasePlugin;

/**
 * @author guqing
 * @since 2.0.0
 */
@Slf4j
@Component
public class EmailPlugin extends BasePlugin {
    public static final String PLUGIN_ID = "halo-plugin-email";


    private final SchemeManager schemeManager;
    private final ReactiveExtensionClient client;
    private final IEmailService mailService;
    private final EmailProcessManager processManager;
    private final EmailTemplateEngineManager engineManager;
    private final SystemConfigurableEnvironmentFetcher environmentFetcher;

    public EmailPlugin(PluginWrapper wrapper,
                       SchemeManager schemeManager,
                       ReactiveExtensionClient client,
                       IEmailService mailService,
                       EmailProcessManager processManager,
                       EmailTemplateEngineManager engineManager,
                       SystemConfigurableEnvironmentFetcher environmentFetcher) {
        super(wrapper);
        this.schemeManager = schemeManager;
        this.client = client;
        this.mailService = mailService;
        this.processManager = processManager;
        this.engineManager = engineManager;
        this.environmentFetcher = environmentFetcher;
    }

    @Override
    public void start() {

        schemeManager.register(EmailTemplateExtension.class);

        client.watch(new Watcher() {
            @Override
            public void onAdd(Extension extension) {
                log.info("Watcher onAdd: {} ", extension.toString());
                Watcher.super.onAdd(extension);

                mailService.send(new EmailRequestPayload(EmallSendEndpoint.ExtensionAdd.name(), extension));

            }

            @Override
            public void onUpdate(Extension oldExtension, Extension newExtension) {
                log.info("Watcher onUpdate: {}, {} ", oldExtension.toString(), newExtension.toString());
                Watcher.super.onUpdate(oldExtension, newExtension);

                mailService.send(new EmailRequestPayload(EmallSendEndpoint.ExtensionUpdate.name(), newExtension));

            }


            @Override
            public void dispose() {
                log.info("Watcher dispose");
            }
        });

        processManager.register(new CommentExtensionTemplateProcess(engineManager, environmentFetcher, client));
    }

    @Override
    public void stop() {
        schemeManager.unregister(schemeManager.get(EmailTemplateExtension.class));

        processManager.unregister(EmallSendEndpoint.ExtensionAdd.name(), CommentExtensionTemplateProcess.class);
    }
}