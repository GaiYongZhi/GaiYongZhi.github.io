package net.cnki.fileconvertservice.remoteApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * @author GYZ14576
 * @description xml文件的oss  Api远程接口
 * @date 2024/6/21 16:36
 */
public interface XmlFileRemoteApi {
    /**
     * 获取token
     * @param clientId
     * @param clientSecret
     * @param grantType
     * @return
     */
    @POST("auth/oauth/token")
    @FormUrlEncoded
    Call<TokenResp> getToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("grant_type") String grantType
            );


    @GET("/oss-api/v1/fulltext/stream")
    @Streaming
    Call<ResponseBody> getXmlFile(
            @Header("Authorization") String Authorization,
            @Query("objecttype") String objecttype,
            @Query("resourcetype") String resourcetype,
            @Query("objectid") String objectid,
            @Query("discno") String discno,
            @Query("pages") String pages
    );

}
