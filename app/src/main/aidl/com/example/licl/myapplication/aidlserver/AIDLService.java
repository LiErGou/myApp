package com.example.licl.myapplication.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.licl.myapplication.AppleManager;

import java.util.ArrayList;
import java.util.List;

public class AIDLService extends Service {
    private List<Apple> Apples;

    @Override
    public void onCreate() {
        super.onCreate();
        Apples=new ArrayList<>();
        Apples.add(new Apple("1",1,"11"));
        Apples.add(new Apple("2",2,"22"));
    }

    private final AppleManager.Stub mStub=new AppleManager.Stub(){


        @Override
        public void addApple(Apple apple) throws RemoteException {
            Log.e("AIDLService","apple name:"+apple.getName());
            Apples.add(apple);
        }

        @Override
        public List<Apple> getApples() throws RemoteException {
            return Apples;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("AIDLService","AIDLService onBind");
        return mStub;
    }
}
