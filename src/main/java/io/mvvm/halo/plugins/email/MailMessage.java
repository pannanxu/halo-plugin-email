package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.support.SimpleMailMessage;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Message.
 *
 * @author: pan
 **/
public interface MailMessage {

    Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");

    static SimpleMailMessage of(String to) {
        return new SimpleMailMessage(to);
    }

    String to();

    String subject();

    String content();

    String fromName();

    default List<Attach> attaches() {
        return new ArrayList<>();
    }

    void setTo(String to);

    void setSubject(String subject);

    void setContent(String content);
    
    default void addAttachment(Attach attach) {
        
    }
    
    default boolean checkParams() {
        return StringUtils.hasLength(to()) && EMAIL_PATTERN.matcher(to()).matches();
    }
}
