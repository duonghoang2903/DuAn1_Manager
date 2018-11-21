package l.com.newapp.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface2 {

    @Multipart
    @POST("media/")
    Call<JsonElement> uploadImage(@Header("Authorization") String auth, @Part MultipartBody.Part file,@Query("title") String title);

    @GET("media/")
    Call<JsonElement> getAllQuest(@Query("per_page") int per_page, @Query("page") int page);

    @DELETE("media/{id}/")
    Call<JsonElement> deleteMediabyID(@Path(value = "id", encoded = true) int id, @Query("force") String force, @Header("Authorization") String auth);

    @POST ("media/{id}/")
    Call<JsonElement> updateMediabyTitle(@Path(value = "id", encoded = true) int id, @Query("title") String name, @Header("Authorization") String auth);
}
