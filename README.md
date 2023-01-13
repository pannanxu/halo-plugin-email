# halo-plugin-email

Email plugin for Halo2.0

![img.png](doc/images/无需审核模板.png)

## 功能

- [x] 新评论邮件通知
- [x] 为第三方插件提供邮件通知API
- [x] 评论审核邮件通知
- [x] 新回复邮件通知
- [ ] 文章审核通知
- [ ] 自定义通知模板

## 使用

1. Releases 下载最新版本
2. Halo 后台插件安装并启动
3. 进入 插件设置页面 配置邮件服务器信息
4. 调用接口测试连接(true为成功,每次修改配置都需要进行测试): `http://ip:port/apis/io.mvvm.halo.plugins.email/testConnection`

## 第三方插件

邮件插件为第三方插件提供了API，可以自定义邮件发送逻辑

```java
MailContextHolder.publish(MailMessage message);
```

## 构建生产产物

```
./gradlew -x build
```
然后只需复制例如`build/libs/halo-plugin-email-1.1.0.jar` 的 `jar` 包即可使用。