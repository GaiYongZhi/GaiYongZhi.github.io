package com.gai.kbase.remote;

import com.gai.kbase.config.RetrofitFactory;
import retrofit2.Response;

import java.io.IOException;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2024/6/20 16:37
 */
public class Test {

    public static void main(String[] args) throws IOException {
        SubTest subTest = RetrofitFactory.createService(SubTest.class, "http://127.0.0.1/");

        SubNameReq subNameReq = new SubNameReq();
        subNameReq.setName("123");

        Response<Resp<ExistResult>> execute = subTest.isExist(subNameReq,
                        "eyEEUyMA")
                .execute();
        if (execute.isSuccessful()) {
            Resp<ExistResult> resp = execute.body();
            System.out.println(resp);
        }
    }

}
