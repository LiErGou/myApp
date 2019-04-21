package com.example.licl.myapplication.network;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LiclHttpUtils {
    private static volatile LiclHttpUtils sliclHttpUtils;
    private OkHttpClient mOkHttpClient;
    //还未实现设置超时
    private int CONNECT_TIME_OUT=10;
    private int WRITE_TIME_OUT=10;
    private int READ_TIME_OUT=30;
    private Handler mHandler;
    private LiclHttpUtils(){
        mOkHttpClient=new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIME_OUT,TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT,TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT,TimeUnit.SECONDS)
                .build();
        mHandler=new Handler(Looper.getMainLooper());
    }

    /**
     * 异步的get请求
     * @param url
     * @param callback
     */
    private void _getSyn(String url, final ResultCallback callback){
        final Request request=new Request.Builder().url(url).build();
        deliveryResult(callback,request);
    }

    /**
     * 同步get请求
     * @param url
     * @return
     * @throws IOException
     */
    private Response _getSyn(String url) throws IOException{
        Request request=new Request.Builder()
                .url(url)
                .build();
        Call call=mOkHttpClient.newCall(request);
        Response response=call.execute();
        return response;
    }

    /**
     *
     * @param url
     * @return同步获取的String
     * @throws IOException
     */
    private String _getSynString(String url) throws IOException{
        return _getSyn(url).body().string();
    }

    /**
     * 同步的post请求
     * @param url
     * @param params
     * @return Response
     * @throws IOException
     */
    private Response _postSyn(String url, Param... params)throws IOException{
        Request request=buildPostRequest(url,params);
        Response response=mOkHttpClient.newCall(request).execute();
        return response;
    }

    private String _postSynString(String url, Param... params)throws IOException{
        return _postSyn(url, params).body().string();
    }

    private Response _postSyn(String url, Map<String,String> map)throws IOException{
        return _postSyn(url,map2Params(map));
    }

    private String _postSynString(String url, Map<String,String> map)throws IOException{
        return _postSyn(url, map).body().string();
    }

    /**
     * 异步的post请求
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final ResultCallback callback,Param... params){
        Request request=buildPostRequest(url,params);
        deliveryResult(callback,request);
    }

    private void _postAsyn(String url,final ResultCallback callback,Map<String,String> params){
        _postAsyn(url, callback, map2Params(params));
    }

    /**
     * 构造post请求
     * @param url
     * @param params
     * @return
     */
    private Request buildPostRequest(String url,Param[] params){
        if(params==null){
            params=new Param[0];
        }
        FormBody.Builder builder=new FormBody.Builder();
        for(Param param:params){
            builder.add(param.key,param.value);
        }
        FormBody body=builder.build();
        return new Request.Builder().url(url).post(body).build();
    }

    private Param[] map2Params(Map<String,String> params){
        if(params==null) return new Param[0];
        Param[] res=new Param[params.size()];
        int i=0;
        for(Map.Entry<String,String> entry:params.entrySet()){
            res[i++]=new Param(entry.getKey(),entry.getValue());
        }
        return res;
    }

    private void deliveryResult(final ResultCallback resultCallback,Request request){
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailCallback(resultCallback,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendSuccessCallBack(resultCallback,response);
            }
        });
    }


    private void sendFailCallback(final ResultCallback callback,final Exception e){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(callback!=null){
                    callback.onFailure(e);
                }
            }
        });
    }

    //???????????为什么是obj 如果返回值不是str怎么办？？
    private  void sendSuccessCallBack(final ResultCallback callback,final Response r){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(callback!=null){
                    callback.onSuccess(r);
                }
            }
        });
    }

    public static abstract class ResultCallback<T>{
        //用于记录泛型的类型
        Type mType;
        public abstract void onSuccess(T response);
        public abstract void onFailure(Exception e);

    }
    public static LiclHttpUtils getInstance(){
        if(sliclHttpUtils==null){
            synchronized (LiclHttpUtils.class){
                if(sliclHttpUtils==null){
                    sliclHttpUtils=new LiclHttpUtils();
                }
            }
        }
        return sliclHttpUtils;
    }

    public static class Param{
        String key;
        String value;
        public Param(){}
        public Param(String key,String value){
            this.key=key;
            this.value=value;
        }
    }

    /*************************对外接口************************/
    /**
     * 同步的get请求
     * @param url
     * @return
     * @throws IOException
     */

    public static Response getSyn(String url) throws IOException {
        return getInstance()._getSyn(url);
    }

    public static String getSynString(String url) throws IOException{
        return getInstance()._getSynString(url);
    }

    /**
     * 异步的get请求
     * @param url
     * @param callback
     */
    public static void getAsyn(String url,ResultCallback callback){
        getInstance()._getSyn(url,callback);
    }

    /**
     * 异步的post请求
     * @param url
     * @param callback
     * @param params
     */
    public static void postAsyn(String url,ResultCallback callback,Param... params){
        getInstance()._postAsyn(url,callback,params);
    }

    public static void postAsyn(String url,ResultCallback callback,Map<String,String> map){
        getInstance()._postAsyn(url,callback,map);
    }

    /**
     * 同步的post请求
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static Response postSyn(String url,Param... params) throws IOException {
        return getInstance()._postSyn(url,params);
    }

    public static String postSynString(String url,Param... params) throws IOException{
        return getInstance()._postSynString(url,params);
    }

    public static Response postSyn(String url,Map<String,String> map) throws IOException {
        return getInstance()._postSyn(url,map);
    }

    public static String postSynString(String url,Map<String,String> map) throws IOException{
        return getInstance()._postSynString(url,map);
    }

}