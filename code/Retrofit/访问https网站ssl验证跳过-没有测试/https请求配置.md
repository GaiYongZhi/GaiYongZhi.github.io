* [Feign忽略Https的SSL最佳方案（且保证负载均衡将失效）_feign ssl-CSDN博客](https://blog.csdn.net/mengting2040/article/details/132229756?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-1-132229756-blog-120291353.235^v43^pc_blog_bottom_relevance_base5&spm=1001.2101.3001.4242.2&utm_relevant_index=4)
* [java - 优雅处理HTTPS中的证书问题 - 个人文章 - SegmentFault 思否](https://segmentfault.com/a/1190000018052027)

## RetrofitFactory配置

```java
package com.gai.kbase.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofSeconds;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2024/6/20 15:53
 */
@Slf4j
public class RetrofitFactory {

    {
        //https访问时如果出现下面错误 可以放开尝试一下
        //错误 1
//        Received fatal alert: handshake_failure
        //原因是JDK中的JCE安全机制导致的问题解决方法如下
//        方法1：如果你使用的是JDK8，请升级到JDK8的最新版本（例如jdk1.8.0_181）。
//        方法2：尝试添加以下代码：
//        System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");

        //错误2 Exception: server certificate change is restricted during renegotiation
        //https://stackoverflow.com/questions/44996748/exception-server-certificate-change-is-restricted-during-renegotiation
//      JVM设置
//        Djdk.tls.allowUnsafeServerCertChange=true
//        Dsun.security.ssl.allowUnsafeRenegotiation=true
        //或者
//        System.setProperty("jdk.tls.allowUnsafeServerCertChange", "true");
//        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");

        //错误 3 错误【clientBuilder.sslSocketFactory(SSLSocketFactory) not supported on JDK 9+】
        //https://blog.csdn.net/qq_35511685/article/details/120291353?utm_source=app&app_version=4.19.1&code=app_1562916241&uLinkId=usr1mkqgl919blen

    }

    private static final Duration defaultTimeout = ofSeconds(60);
    private static final OkHttpClient defaultOkHttpClient = new OkHttpClient.Builder()
            .connectTimeout(defaultTimeout.getSeconds(), TimeUnit.SECONDS)
            .readTimeout(defaultTimeout.getSeconds(), TimeUnit.SECONDS)
            .writeTimeout(defaultTimeout.getSeconds(), TimeUnit.SECONDS)
            //使用时https地方请求时 这里加上
//            .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
//            .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
            .build();

    /**
     * 构造Retrofit实例Service,默认60s
     * @param serviceClass
     * @param baseUrl
     * @return
     * @param <S>
     */
    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(defaultOkHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }


    private static class SSLSocketClient {

        //获取这个SSLSocketFactory
        public static SSLSocketFactory getSSLSocketFactory() {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, getTrustManager(), new SecureRandom());
                return sslContext.getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //获取TrustManager
        private static TrustManager[] getTrustManager() {
            return new TrustManager[]{
                    new X509TrustManager(){
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
        }

        //获取HostnameVerifier
        public static HostnameVerifier getHostnameVerifier() {
            return (s, sslSession) -> true;
        }

        public static X509TrustManager getX509TrustManager() {
            X509TrustManager trustManager = null;
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
                trustManager = (X509TrustManager) trustManagers[0];
            } catch (Exception e) {
                e.printStackTrace();
            }

            return trustManager;
        }
    }
}

```
