# halo-plugin-email

Email plugin for Halo2.0

![img.png](doc/images/无需审核模板.png)

![img.png](doc/images/需要审核模板.png)

## 功能

- [x] 新评论邮件通知
- [x] 为第三方插件提供邮件通知API
- [ ] 评论审核邮件通知
- [ ] 新回复邮件通知
- [ ] 文章审核通知
- [ ] 自定义通知模板

## 使用

1. Releases 下载最新版本
2. Halo 后台插件安装并启动
3. 进入 插件设置页面 配置邮件服务器信息
4. 调用接口测试连接(true为成功,每次修改配置都需要进行测试): `http://ip:port/api/api.plugin.halo.run/v1alpha1/plugins/halo-plugin-email/io.mvvm.halo.plugins.email/testConnection"`

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