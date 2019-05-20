package com.example.licl.myapplication;

import android.os.Environment;

import java.io.File;

public class Constant {
    public static String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/gImage/";
    static {
        File file=new File(path);
        if(!file.exists()) file.mkdir();
    }
}
