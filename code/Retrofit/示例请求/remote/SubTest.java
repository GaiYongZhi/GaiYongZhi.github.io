package com.gai.kbase.remote;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2024/6/20 16:08
 */
public interface SubTest {
    @POST("isExist")
    Call<Resp<ExistResult>> isExist(@Body SubNameReq req, @Header("Token") String token);

}
