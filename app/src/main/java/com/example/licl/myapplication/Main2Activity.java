package com.example.licl.myapplication;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    private void readContacts(){
        Cursor cursor=null;
        try{
            //第二个参数为查询的列名，第三个参数为where约束条件，第四个参数为where占位符提供具体的值，最后一个参数为查询结果的排序方式。
            cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,null,null,null);
            if(cursor!=null){
                while(cursor.moveToNext()){
                    String displayName=cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }
}
