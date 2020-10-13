package com.plantapp.data;

import com.plantapp.model.CityListModel;
import com.plantapp.model.DistrictModel;
import com.plantapp.model.ResponseResult;
import com.plantapp.model.TalukaListModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService
{


    //The login call
    @FormUrlEncoded
    @POST("Login.php")
    Call<ResponseResult> callLoginApi(
            @Field("key") String key,
            @Field("fcmcode") String fcmcode,
            @Field("cus_mob") String gar_mobi,
            @Field("cus_password") String gar_password);


    @FormUrlEncoded
    @POST("state.php")
    Call<DistrictModel> getState(@Field("key") String key);

    //The city call
    @FormUrlEncoded
    @POST("city.php")
    Call<CityListModel> getCity(@Field("key") String key,
                                @Field("state_id") String state_id);

    //The taluka call
    @FormUrlEncoded
    @POST("taluka.php")
    Call<TalukaListModel> getTaluka(@Field("key") String key,
                                    @Field("city_id") String city_id);

    @FormUrlEncoded
    @POST("RegUser.php")
    Call<ResponseResult> callRegister(
            @Field("key") String key,
            @Field("cus_name") String gar_name,
            @Field("cus_email") String gar_email,
            @Field("cus_mob") String gar_mobi,
            @Field("cus_password") String gar_password,
            @Field("cus_address") String cus_address,
            @Field("cus_lat") String gar_lat,
            @Field("cus_long") String gar_long,
            @Field("cus_district") String city,
            @Field("cus_taluka") String taluka,
            @Field("cus_state") String state
    );

    @FormUrlEncoded
    @POST("MobileVerify.php")
    Call<ResponseResult> mobileVerification(
            @Field("key") String key,
            @Field("cus_mob") String gar_mobi);

    //The change password call
    @FormUrlEncoded
    @POST("ChangePass.php")
    Call<ResponseResult> callChangePassword(
            @Field("key") String key,
            @Field("cus_id") String cus_id,
            @Field("cus_password") String cus_password);

    @FormUrlEncoded
    @POST("version_update.php")
    Call<ResponseResult> card_Versioncode(
            @Field("key") String key,
            @Field("version_code") String version_code
    );

}
