# https请求说明


应该是服务端整数验证错误。

按理说Hutool默认忽略了所有SSL证书验证，不应该出现此类问题。

可以尝试：

按照https://stackoverflow.com/questions/44996748/exception-server-certificate-change-is-restricted-during-renegotiation 方法设置参数：
Djdk.tls.allowUnsafeServerCertChange=true
Dsun.security.ssl.allowUnsafeRenegotiation=true
如果链接公开，我这边测试。
如有补充请再打开此issue。


Received fatal alert: handshake_failure 错误


用户错误如图，场景为使用Hutool-http请求https服务器，原因是JDK中的JCE安全机制导致的问题解决方法如下：

- 方法1：如果你使用的是JDK8，请升级到JDK8的最新版本（例如jdk1.8.0_181）。
- 方法2：尝试添加以下代码：
```java
System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");
```
