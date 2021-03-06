package l.com.newapp.rest;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient2 {
    public static final String BASE_API = "https://nhom5.dotplays.com/wp-json/wp/v2/";
    private  static Retrofit retrofit;
    static OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
    public static Retrofit getRestClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static ApiInterface2 getApiInterface(){
        return getRestClient().create(ApiInterface2.class);
    }
}
