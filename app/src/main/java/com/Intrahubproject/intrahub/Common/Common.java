package com.Intrahubproject.intrahub.Common;


import com.Intrahubproject.intrahub.Remote.APIservice;
import com.Intrahubproject.intrahub.Remote.FCMretrofitClient;

public class Common {


    public static final String BaseUrl="https://fcm.googleapis.com/";




    public static APIservice getFCMClient(){
        return FCMretrofitClient.getClint(BaseUrl).create(APIservice.class);
    }

}
