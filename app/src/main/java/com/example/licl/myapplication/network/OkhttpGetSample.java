package com.example.licl.myapplication.network;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkhttpGetSample {
    OkHttpClient client=new OkHttpClient();
    public String run(String url) throws IOException{
        Request request=new Request.Builder()
                .url(url)
                .build();
        Response response=null;
        try{
            response=client.newCall(request).execute();

            String s=response.body().string();
            response.close();
            return s;
        }finally {

        }
    }
}
