
#  Retrofit2 工具类
## 引入依赖

```xml

        <!-- Retrofit2依赖 -->
        <dependency>
            <groupId>com.squareup.retrofit2</groupId>
            <artifactId>retrofit</artifactId>
            <version>2.9.0</version> <!-- 请检查最新版本 -->
        </dependency>
        <dependency>
            <groupId>com.squareup.retrofit2</groupId>
            <artifactId>converter-jackson</artifactId>
            <version>2.9.0</version> <!-- 请检查最新版本 -->
        </dependency>
        <!-- OkHttp依赖 -->
        <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
            <version>4.9.3</version>
        </dependency>

```

## HttpRetrofitUtils.java

```java
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofSeconds;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2024/6/20 15:53
 */
@Slf4j
public class HttpRetrofitUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        //忽略不识别的字段（json属性与目标实体存在属性上的差异
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //允许原始值为null
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        //允许将枚举序列化/反序列化为数字
        objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
    }

    private static final Duration defaultTimeout = ofSeconds(60);
    private static final OkHttpClient defaultOkHttpClient = new OkHttpClient.Builder()
            .connectTimeout(defaultTimeout.getSeconds(), TimeUnit.SECONDS)
            .readTimeout(defaultTimeout.getSeconds(), TimeUnit.SECONDS)
            .writeTimeout(defaultTimeout.getSeconds(), TimeUnit.SECONDS)
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
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
        return retrofit.create(serviceClass);
    }

}

```
##  [Retrofit中https请求配置.](code/Retrofit/访问https网站ssl验证跳过-没有测试/https请求配置.md)

# 附录
## 优雅处理HTTPS中的证书问题
* [优雅处理HTTPS中的证书问题](https://segmentfault.com/a/1190000018052027)


## okhttp
[OkHttp的完整指南什么是OkHttp？ OkHttp是一个来自Square的HTTP客户端，用于Java和Andro - 掘金](https://juejin.cn/post/7068162792154464264)
