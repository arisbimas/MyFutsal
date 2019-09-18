package com.example.myfutsal;


import com.example.myfutsal.Notifications.MyResponse;
import com.example.myfutsal.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAKJmZJG8:APA91bE-1sNFEWopjJzGxknrrbs_o_x7aEPUrJvo2J-M_eYU-oKUWJHIE7RSxz65gtzL94X6rqQ4VfjnQLc09Pzhp_AHGTCkgpA0xcIWihS5EtqOhDRfCrBbcMeuihs2vbisFHvyloCe"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}