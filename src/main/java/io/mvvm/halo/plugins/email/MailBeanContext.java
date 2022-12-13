package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.support.MailQueuePool;
import io.mvvm.halo.plugins.email.support.MailSystemConfigurableEnvironmentFetcher;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.app.plugin.SettingFetcher;

/**
 * MailBeanContext.
 *
 * @author: pan
 **/
public final class MailBeanContext {

    public static final MailQueuePool MAIL_QUEUE_POOL = new MailQueuePool();
    public static MailSystemConfigurableEnvironmentFetcher environmentFetcher;
    public static ExternalUrlSupplier externalUrlSupplier;
    public static ReactiveExtensionClient client;
    public static SettingFetcher settingFetcher;
}
