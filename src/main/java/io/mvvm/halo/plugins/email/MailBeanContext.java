package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.support.MailQueuePool;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.app.infra.SystemConfigurableEnvironmentFetcher;

/**
 * MailBeanContext.
 *
 * @author: pan
 **/
public final class MailBeanContext {

    public static final MailQueuePool MAIL_QUEUE_POOL = new MailQueuePool();
    public static SystemConfigurableEnvironmentFetcher environmentFetcher;
    public static ExternalUrlSupplier externalUrlSupplier;
    public static ReactiveExtensionClient client;
}
