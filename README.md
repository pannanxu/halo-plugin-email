# halo-plugin-email

Email plugin for Halo2.0

## 功能

- 新评论邮件通知
- 评论回复邮件通知
- 评论审核邮件通知
- 文章审核通知
- 自定义通知模板
- 为第三方插件提供邮件通知API

## 第三方插件

邮件插件为第三方插件提供了API，可以自定义邮件发送逻辑

1、注册Option，注册之后可以在后台动态模板内容和开启或关闭

```java
EmailTemplateOptionManager manager;
manager.registry(new EmailTemplateOption("comment", "评论模板", "文章收到新的评论时通知文章创建人, 如果需要审核则发送: 「审核模板」"));
```

2、定义并注册Process

```java
public class CommentExtensionTemplateProcess extends AbstractTemplateProcess {
    public CommentExtensionTemplateProcess(EMailTemplateEngineManager engineManager) {
        setEngineManager(engineManager);
    }

    @Override
    public String getEndpoint() {
        // 此处可自定义
        return EMallSendEndpoint.ExtensionAdd.name();
    }

    @Override
    public Flux<EmailMessage> process(Object extension) {
        if (extension instanceof Comment comment) {
            Context context = new Context();
            context.setVariable("comment", comment);
            String process = process("comment", context);
            return Flux.just(new EmailMessage("to", "收到新的评论", process));
        }
        return Flux.empty();
    }
}
```

```java
EmailProcessManager manager;
manager.registry(new CommentExtensionTemplateProcess(engineManager));
```

3、发送邮件

```java
IEMailService mailService;
mailService.send(new EMailRequestPayload(EMallSendEndpoint.ExtensionAdd.name(), extension))
```

## 构建生产产物

```
./gradlew -x build
```

然后只需复制例如`build/libs/halo-plugin-email-0.0.1-SNAPSHOT-plain.jar` 的 `jar` 包即可使用。