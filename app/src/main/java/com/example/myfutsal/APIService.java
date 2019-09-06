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
                    "Authorization:key=AAAA_OSkjKc:APA91bG-7vBQG3vMHSFSkj4ybPDZlJmbwugXKdoAIuIMl8Ee_3NwfESw8bhTJxgKDm7W4_fSFy2aCQ-1SJsCjIvqDWl2bLHOoQ4dmnRKYyyzd9pDeHPrN1bgAf_8ijVm4BqeX7dEl-KG"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}