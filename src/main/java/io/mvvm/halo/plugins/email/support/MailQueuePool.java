package io.mvvm.halo.plugins.email.support;

import io.mvvm.halo.plugins.email.MailMessage;
import io.mvvm.halo.plugins.email.MailService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * MailQueueHandler.
 *
 * @author: pan
 **/
public class MailQueuePool {

    private final BlockingQueue<MailMessage> queue = new LinkedBlockingQueue<>();

    public void put(MailMessage message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void start(MailService service) {
        new Thread(() -> {
            while (true) {
                try {
                    MailMessage take = queue.take();
                    service.send(take);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, "MAIL-QUEUE").start();
    }

}
