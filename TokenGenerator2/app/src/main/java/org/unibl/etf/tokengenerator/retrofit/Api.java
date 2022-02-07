package org.unibl.etf.tokengenerator.retrofit;

import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {

    static String BASE_URL = "https://192.168.100.15:8443/SNI-ServerTokenGenerator/api/";
  //  static String BASE_URL="https://aa40-31-223-129-188.ngrok.io/SNI-ServerTokenGenerator/api/";

    @POST("token")
    Call<ResponseBody> createPost(@Body RequestBody body );
}
