package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.process.CommentExtensionTemplateProcess;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.extension.Extension;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.SchemeManager;
import run.halo.app.extension.Watcher;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.app.infra.SystemConfigurableEnvironmentFetcher;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.HaloPluginManager;

/**
 * @author guqing
 * @since 2.0.0
 */
@Slf4j
@Component
public class EmailPlugin extends BasePlugin {
    public static final String PLUGIN_ID = "halo-plugin-email";

    public static SchemeManager schemeManager;
    public static ReactiveExtensionClient client;
    public static IEmailService mailService;
    public static TemplateResolver templateResolver;
    public static EmailProcessManager processManager;
    public static EmailTemplateEngineManager engineManager;
    public static SystemConfigurableEnvironmentFetcher environmentFetcher;
    public static ExternalUrlSupplier externalUrlSupplier;

    public EmailPlugin(PluginWrapper wrapper,
                       SchemeManager schemeManager,
                       ReactiveExtensionClient client,
                       IEmailService mailService,
                       EmailProcessManager processManager,
                       EmailTemplateEngineManager engineManager,
                       TemplateResolver templateResolver
                       ) {
        super(wrapper);
        EmailPlugin.schemeManager = schemeManager;
        EmailPlugin.client = client;
        EmailPlugin.mailService = mailService;
        EmailPlugin.processManager = processManager;
        EmailPlugin.engineManager = engineManager;
        EmailPlugin.templateResolver = templateResolver;
        if (getWrapper().getPluginManager() instanceof HaloPluginManager manager) {
            EmailPlugin.environmentFetcher = manager.getRootApplicationContext().getBean(SystemConfigurableEnvironmentFetcher.class);
            EmailPlugin.externalUrlSupplier = manager.getRootApplicationContext().getBean(ExternalUrlSupplier.class);

        }
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

        processManager.register(new CommentExtensionTemplateProcess());
    }

    @Override
    public void stop() {
        schemeManager.unregister(schemeManager.get(EmailTemplateExtension.class));

        processManager.unregister(EmallSendEndpoint.ExtensionAdd.name(), CommentExtensionTemplateProcess.class);
    }
}