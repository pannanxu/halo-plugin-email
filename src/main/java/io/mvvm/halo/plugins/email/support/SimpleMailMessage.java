package io.mvvm.halo.plugins.email.support;

import io.mvvm.halo.plugins.email.Attach;
import io.mvvm.halo.plugins.email.MailMessage;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * SimpleMessage.
 *
 * @author: pan
 **/
@Setter
@SuperBuilder
public class SimpleMailMessage implements MailMessage {

    private String to;
    private String subject;
    private String content;
    
    private String fromName;
    
    private List<Attach> attaches;

    public SimpleMailMessage(String to) {
        this.to = to;
    }

    @Override
    public String to() {
        return this.to;
    }

    @Override
    public String subject() {
        return this.subject;
    }

    @Override
    public String content() {
        return this.content;
    }

    @Override
    public String fromName() {
        return this.fromName;
    }

    @Override
    public List<Attach> attaches() {
        return this.attaches;
    }

    @Override
    public void addAttachment(Attach attach) {
        if (null == this.attaches) {
            this.attaches = new ArrayList<>();
        }
        if (!StringUtils.hasLength(attach.getName())) {
            throw new RuntimeException("附件名称不能为空");
        }
        if (null == attach.getSource()) {
            throw new RuntimeException("附件不能为空");
        }
        this.attaches.add(attach);
    }

}
