package com.example.licl.myapplication.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;


import com.alibaba.fastjson.JSONObject;
import com.example.licl.myapplication.image.ImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LiclHttpUtils {
    private static volatile LiclHttpUtils sliclHttpUtils;
    private OkHttpClient mOkHttpClient;
    //还未实现设置超时
    private int CONNECT_TIME_OUT=10;
    private int WRITE_TIME_OUT=10;
    private int READ_TIME_OUT=30;
    //重发的最大次数
    private int MAX_REMAKE_TIMES=3;
    private Handler mHandler;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
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
    private void _getAsyn(String url, final ResultCallback callback){
        final Request request=new Request.Builder().url(url).build();
        deliveryResult(callback,request);
    }

    /**
     * 同步get请求
     * @param url
     * @return
     * @throws IOException
     */
    private Response _getAsyn(String url) throws IOException{
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
     * @return 同步获取的String
     * @throws IOException
     */
    private String _getSynString(String url) throws IOException{
        return _getAsyn(url).body().string();
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

    private Response _postSyn(String url,String json) throws IOException{
        Response response=mOkHttpClient.newCall(buildPostRequest(url,json)).execute();
        return response;
    }

    private String _postSynString(String url,String json) throws IOException{
        return _postSyn(url,json).body().string();
    }

    private <T> Response _postSyn(String url,T t) throws IOException{
        return mOkHttpClient.newCall(buildPostRequest(url,t)).execute();
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

    private void _postAsyn(String url,final ResultCallback callback,String json){
        Request request=buildPostRequest(url, json);
        deliveryResult(callback,request);
    }

    private <T> void _postAsyn(String url,final ResultCallback callback,T t){
        _postAsyn(url,callback,JSONObject.toJSONString(t));
    }

    /**
     * 用于在imageView上展示网络图片
     * @param view
     * @param url
     * @param errorResId
     */
    private void _displayImage(final ImageView view, final String url, final int errorResId){
        Request request=new Request.Builder()
                .url(url)
                .build();
        Call call=mOkHttpClient.newCall(request);
        final int[] serverLoadTimes = {0};
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //超时重连
                if(e instanceof SocketTimeoutException&& serverLoadTimes[0] <MAX_REMAKE_TIMES){
                    serverLoadTimes[0]++;
                    call.enqueue(this);
                }else{
                    setErrorResId(view,errorResId);
                }

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is=null;
                try{
                    is=response.body().byteStream();
                    ImageUtils.ImageSize actualImageSize=ImageUtils.getImageSize(is);
                    ImageUtils.ImageSize imageViewSize=ImageUtils.getImageViewSize(view);
                    int inSampleSize=ImageUtils.calculateInSampleSize(actualImageSize,imageViewSize);
                    try{
                        is.reset();
                    }catch (IOException e){
                        response= _getAsyn(url);
                        is=response.body().byteStream();
                    }
                    BitmapFactory.Options ops=new BitmapFactory.Options();
                    ops.inJustDecodeBounds=false;
                    ops.inSampleSize=inSampleSize;
                    final Bitmap bm=BitmapFactory.decodeStream(is,null,ops);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setImageBitmap(bm);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                    setErrorResId(view,errorResId);
                }finally {
                    if(is!=null)
                        is.close();
                }
            }
        });
    }

    private void _downloadAsyn(final String url,final String destFileDir,final ResultCallback callback){
        final Request request=new Request.Builder()
                .url(url)
                .build();
        final Call call=mOkHttpClient.newCall(request);
        final int[] serverLoadTimes=new int[1];
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //超时重连
                if(e instanceof SocketTimeoutException&& serverLoadTimes[0] <MAX_REMAKE_TIMES){
                    serverLoadTimes[0]++;
                    call.enqueue(this);
                }else{
                    sendFailStringCallback(callback,e);
                }

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is=null;
                byte[] buf=new byte[2048];
                int len=0;
                FileOutputStream fos=null;
                try{
                    is=response.body().byteStream();
                    File file=new File(destFileDir+getFileName(url));
                    file.createNewFile();
                    fos=new FileOutputStream(file);
                    while((len=is.read(buf))!=-1){
                        fos.write(buf,0,len);
                    }
                    fos.flush();
                    sendSuccessCallBackString(callback,file.getAbsolutePath());
                }catch (IOException e){
                    sendFailStringCallback(callback,e);
                }finally {
                    if(is!=null){
                        is.close();
                    }
                    if(fos!=null){
                        fos.close();
                    }
                }
            }
        });
    }
    private String getFileName(String path){
        int separatorIndex=path.lastIndexOf("/");
        return (separatorIndex<0)?path:path.substring(separatorIndex+1,path.length());
    }
    private void setErrorResId(final ImageView view, final int errorResId){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                view.setImageResource(errorResId);
            }
        });
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

    private Request buildPostRequest(String url,String json){
        RequestBody body=RequestBody.create(JSON,json);
        Request request=new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return request;
    }

    private <T> Request buildPostRequest(String url,T t){
        String JSONString= JSONObject.toJSONString(t);
        return buildPostRequest(url,JSONString);
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

    private  void deliveryResult(final ResultCallback resultCallback,Request request){
        final int[] serverLoadTimes={0};
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //超时重连
                if(e instanceof SocketTimeoutException&& serverLoadTimes[0] <MAX_REMAKE_TIMES){
                    serverLoadTimes[0]++;
                    call.enqueue(this);
                }else{
                    sendFailStringCallback(resultCallback,e);
                }

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    Class clazz=resultCallback.getType();
                    if(clazz==String.class){
                        sendSuccessCallBackString(resultCallback,response.body().string());
                    }else{
                        sendSuccessCallBack(resultCallback,JSONObject.parseObject(response.body().string(),clazz));
                    }
                }else{
                    sendFailStringCallback(resultCallback,"error code:"+response.code());
                }


            }
        });
    }

    private void sendFailStringCallback(final ResultCallback<String> callback,final String msg){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(callback!=null){
                    callback.onFailure(new Exception(msg));
                }
            }
        });
    }

    private void sendFailStringCallback(final ResultCallback<String> callback, final Exception e){
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
    private  void sendSuccessCallBackString(final ResultCallback<String> callback, final String r){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(callback!=null){
                    callback.onSuccess(r);
                }
            }
        });
    }

    private <T> void sendSuccessCallBack(final ResultCallback<T> callback, final T r){
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
        Class<T> mType;
        public ResultCallback(Class<T> c){
            mType=c;
        }
        public Class getType(){
            return mType;
        }
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
        return getInstance()._getAsyn(url);
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
        getInstance()._getAsyn(url,callback);
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

    public static void postAsyn(String url,ResultCallback callback,String json){
        getInstance()._postAsyn(url, callback, json);
    }

    public static <T> void postAsyn(String url,ResultCallback callback,T t){
        getInstance()._postAsyn(url,callback,t);
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

    public static Response postSyn(String url,String json) throws IOException {
        return getInstance()._postSyn(url,json);
    }

    public static String postSynString(String url,String json) throws IOException{
        return getInstance()._postSynString(url,json);
    }

    public static <T> Response postSyn(String url,T t) throws IOException {
        return getInstance()._postSyn(url,t);
    }

    public static <T> String postSynString(String url,T t) throws IOException{
        return getInstance()._postSyn(url,t).body().string();
    }


    /**
     * imageview展示图片
     * @param imageView
     * @param url
     */
    public static void displayImage(final ImageView imageView,String url){
        getInstance()._displayImage(imageView,url,-1);
    }

    /**
     *
     * @param imageView 目标imageView
     * @param url   图片url
     * @param errorImageid 发生错误时的错误图片
     */
    public static void displayImage(final ImageView imageView,String url,int errorImageid){
        getInstance()._displayImage(imageView,url,errorImageid);
    }

    /**
     * 下载文件
     * @param url 目标文件的url
     * @param desDir 下载的目标路径
     * @param callback
     */
    public static void downloadAsyn(String url,String desDir,ResultCallback callback){
        getInstance()._downloadAsyn(url,desDir,callback);
    }

}
