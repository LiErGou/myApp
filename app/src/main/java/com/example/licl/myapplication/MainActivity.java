package com.example.licl.myapplication;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.licl.myapplication.db.MyDatabaseHelper;
import com.example.licl.myapplication.network.LiclHttpUtils;
import com.example.licl.myapplication.network.OkhttpGetSample;
import com.example.licl.myapplication.network.OkhttpPostExample;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private MyDatabaseHelper databaseHelper;
    Button start_btn=null;
    TextView mTextView=null;
    Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_btn=(Button)findViewById(R.id.start_btn);
        mTextView=(TextView)findViewById(R.id.res_tv);
        init();
    }

    private void init(){
        mHandler=new Handler();
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String res=getMethod();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setText(res);
                            }
                        });
                    }
                }).start();
            }
        });
    }



    private String getMethod(){

        try{
           return LiclHttpUtils.getSynString("http://gank.io/api/xiandu/categories");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Fail";
    }

    private void postMethod(){
        try{
            Log.d("Main",new OkhttpPostExample("Tom","Jessica").post("http://www.roundsapp.com/post"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
