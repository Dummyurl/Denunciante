package com.uca.apps.isi.taken.api;

/**
 * Created by Mario Arce on 16/10/2017.
 */

import com.uca.apps.isi.taken.models.AccessToken;
import com.uca.apps.isi.taken.models.Category;
import com.uca.apps.isi.taken.models.Complaint;
import com.uca.apps.isi.taken.models.ComplaintCreate;
import com.uca.apps.isi.taken.models.Picture;
import com.uca.apps.isi.taken.models.User;
import com.uca.apps.isi.taken.models.UserCreate;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by isi3 on 17/4/2017.
 */

public interface ApiInterface {

    @GET("Categories")
    Call<List<Category>> getCategories(@Header("Authorization") String authorization);

    @POST("Complaints")
    Call<Complaint> createComplaint(@Body ComplaintCreate complaints, @Header("Authorization") String authorization);

    @GET("Complaints/{id}?filter={\"include\":[\"pictures\",\"category\"]}")
    Call<Complaint> getComplaint(@Path("id") int complaintId, @Header("Authorization") String authorization);

    @POST("Users/login")
    Call<AccessToken> login(@Body User user);

    @POST("Users")
    Call<User> signUp(@Body UserCreate user);

    @GET("Complaints?filter={\"include\":[\"category\",\"pictures\",\"user\"]}")
    Call<List<Complaint>> getComplaints(@Header("Authorization") String authorization);

    @POST("Pictures")
    Call<Picture> createPicture(@Body Picture picture, @Header("Authorization") String authorization);

    @DELETE("Complaints/{id}")
    Call<ResponseBody>  deleteComplaint(@Path("id") int complaintId, @Header("Authorization") String authorization);

    @GET("Complaints/me?filter={\"include\":[\"category\",\"pictures\",\"user\"]}")
    Call<List<Complaint>> getMyComplaints(@Header("Authorization") String authorization);
}