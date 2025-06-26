package net.cnki.common.auth.user.utils;

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
//
//    public static <S> S createService(Class<S> serviceClass, String baseUrl, long timeout) {
//        Duration timeoutDuration = ofSeconds(timeout);
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(timeoutDuration.getSeconds(), TimeUnit.SECONDS)
//                .readTimeout(timeoutDuration.getSeconds(), TimeUnit.SECONDS)
//                .writeTimeout(timeoutDuration.getSeconds(), TimeUnit.SECONDS)
//                .build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .client(okHttpClient)
//                .addConverterFactory(JacksonConverterFactory.create())
//                .build();
//        return retrofit.create(serviceClass);
//    }
}
