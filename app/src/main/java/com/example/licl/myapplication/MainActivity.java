package com.example.licl.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licl.myapplication.bean.GBean;
import com.example.licl.myapplication.db.MyDatabaseHelper;
import com.example.licl.myapplication.network.LiclHttpUtils;
import com.example.licl.myapplication.network.OkhttpGetSample;
import com.example.licl.myapplication.network.OkhttpPostExample;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    String BASE_URL="http://gank.io/api/data/福利/1/";
    private MyDatabaseHelper databaseHelper;
    Button start_btn=null;
    TextView mTextView=null;
    Handler mHandler;
    ImageView mImageView;
    GBean curGBean;
    private static final int READ_FILE_REQUESTCODE = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_btn=(Button)findViewById(R.id.start_btn);
        mTextView=(TextView)findViewById(R.id.res_tv);
        mImageView=(ImageView)findViewById(R.id.test_iv);
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(curGBean!=null){
                    savePic(Constant.path,curGBean.getResults().get(0).getUrl());
                }
                return true;
            }
        });
        init();
        initPermission();
    }

    private void init(){
        mHandler=new Handler();
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get(BASE_URL+getRandom());
            }
        });
    }



//    private String getMethod(){
//
//        try{
//           return LiclHttpUtils.getSynString("http://gank.io/api/xiandu/categories");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "Fail";
//    }
//
//    private void postMethod(){
//        try{
//            Log.d("Main",new OkhttpPostExample("Tom","Jessica").post("http://www.roundsapp.com/post"));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void get(String url){
        LiclHttpUtils.getAsyn(url, new LiclHttpUtils.ResultCallback<GBean>(GBean.class) {
            @Override
            public void onSuccess(final GBean response) {
                curGBean=response;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url=response.getResults().get(0).getUrl();
                        LiclHttpUtils.displayImage(mImageView,url,R.drawable.ic_launcher_foreground);
                    }
                }).run();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    //请求读取联系人权限
    private void initPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int hasReadContactsPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showMessageOKCancel("应用需要读取文件的权限，请同意",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                READ_FILE_REQUESTCODE);
                                    }
                                }
                            });
                    return;
                }else{
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_FILE_REQUESTCODE);
                }

            }

        } else {
            return;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this).setMessage(message)
                .setPositiveButton("同意", okListener)
                .setNegativeButton("拒绝", null)
                .create().show();
    }

    //获得对于请求的反馈
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case READ_FILE_REQUESTCODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(MainActivity.this, "READ_CONTACTS GRANTED", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "READ_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
        }
    }

    private void savePic(String dir,String url){
        LiclHttpUtils.downloadAsyn(url, dir, new LiclHttpUtils.ResultCallback<String>(String.class) {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(MainActivity.this,response+"success",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this,"save failed",Toast.LENGTH_LONG).show();
            }
        });
    }

    private int getRandom() {
        int max=500,min=1;
        long randomNum = System.currentTimeMillis();
        int ran3 = (int) (randomNum%(max-min)+min);
        return ran3;
    }
}
