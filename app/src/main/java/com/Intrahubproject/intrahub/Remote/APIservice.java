package com.Intrahubproject.intrahub.Remote;


import com.Intrahubproject.intrahub.Model.Myresponce;
import com.Intrahubproject.intrahub.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by HASIB on 12/16/2017.
 */

public interface APIservice {
    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA9lLrxco:APA91bF7iCTpV1dU2c--x8GMD2bCGP6GlWjyPWZxT7AbE_AO-HF41YcnpzR4VqkvteoLzyAFZGM-7f_j3979ZUOJC7YNQP5YY4uq8IB-ccTsgLM7yxiA6xZ7BT_O7JYi4qW_NCOfiP-f"
            }
    )
    @POST("fcm/send")
    Call<Myresponce> sendNotification(@Body Sender body);



    //Call<Myresponce>

}