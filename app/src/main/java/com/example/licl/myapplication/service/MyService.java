package com.example.licl.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private DownloadBinder mBinder=new DownloadBinder();
    public MyService() {
    }

    class DownloadBinder extends Binder{
        public void startDownload(){
            Log.d("MyService","startDownload executed");
        }
        public void getProgress(){
            Log.d("MyService","getProgress executed");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
