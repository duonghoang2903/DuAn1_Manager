package l.com.newapp.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("auth/generate_auth_cookie/")
    Call<JsonElement> callLogin(@Query("username") String username,
                                    @Query("password") String password);


}
