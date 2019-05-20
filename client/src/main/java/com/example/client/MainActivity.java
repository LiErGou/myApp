package com.example.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.licl.myapplication.AppleManager;
import com.example.licl.myapplication.aidlserver.Apple;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG="Client";
    private AppleManager mAppleManager;
    private boolean connected;
    private List<Apple> mAppleList;
    private ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAppleManager=AppleManager.Stub.asInterface(service);
            connected=true;
            try {
                addApple();
                getApples();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected=false;
        }
    };


    private void getApples() throws RemoteException {
        mAppleList=mAppleManager.getApples();
    }

    private void addApple() throws RemoteException {
        mAppleManager.addApple(new Apple("3",3,"3"));

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        if(connected){
            unbindService(mServiceConnection);
        }
    }

    private void bindService(){
        Intent intent =new Intent();
        intent.setPackage("com.example.licl.myapplication.aidlserver");
//        intent.setClassName("com.example.licl.myapplication.aidlserver","com.example.licl.myapplication.aidlserver.AIDLService");
        intent.setAction("com.licl.server");
        connected= bindService(intent,mServiceConnection,Context.BIND_AUTO_CREATE);
        if(connected){

        }
    }


}
