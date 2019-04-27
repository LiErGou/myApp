package com.example.licl.myapplication.image;

import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.reflect.Field;

public class ImageUtils {
    /**
     * 根据InputStream获取图片实际尺寸
     * @param inputStream
     * @return
     */
    public static ImageSize getImageSize(InputStream inputStream){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeStream(inputStream,null,options);
        return new ImageSize(options.outWidth,options.outHeight);
    }
    public static class ImageSize{
        int width;
        int height;
        public ImageSize(){

        }
        public ImageSize(int width,int height){
            this.width=width;
            this.height=height;
        }
    }

    /**
     * 计算图片需要的缩小比例
     * @param srcSize
     * @param targetSize
     * @return
     */
    public static int calculateInSampleSize(ImageSize srcSize,ImageSize targetSize){
        int width=srcSize.width;
        int height=srcSize.height;
        int inSampleSize=1;

        int reqWidth=targetSize.width;
        int reqHeight=targetSize.height;

        if(width>reqWidth&&height>reqHeight){
            int widthRatio=Math.round((float)width/(float)reqWidth);
            int heightRatio=Math.round((float)height/(float)reqHeight);
            inSampleSize=Math.max(widthRatio,heightRatio);
        }
        return inSampleSize;
    }


    public static ImageSize getImageViewSize(ImageView view){

        ImageSize imageSize=new ImageSize();
        imageSize.width=getExpectWidth(view);
        imageSize.height=getExpectHeight(view);

        return imageSize;


    }
    /**
     *
     * @param view
     * @return 获得view期望得到的高度
     */
    private static int getExpectHeight(ImageView view){
        int height=0;
        if(view == null) return 0;

        ViewGroup.LayoutParams params=view.getLayoutParams();
        //当不是包裹内容的时候获得view的高度
        if(params!=null&&params.height!=ViewGroup.LayoutParams.WRAP_CONTENT){
            height=view.getHeight();
        }
        //去获取设置的高度
        if(height<=0&&params!=null){
            height=params.height;
        }
        //通过反射获得最大高度
        if(height<=0){
            height=getImageViewFieldValue(view,"mMaxHeight");
        }
        //使用屏幕宽度
        if(height<=0){
            DisplayMetrics displayMetrics=view.getContext()
                    .getResources().getDisplayMetrics();
            height=displayMetrics.heightPixels;
        }
        return height;
    }

    /**
     *
     * @param view
     * @return 获得view期望得到的宽度
     */
    private static int getExpectWidth(ImageView view){
        int width=0;
        if(view == null) return 0;

        ViewGroup.LayoutParams params=view.getLayoutParams();
        //当不是包裹内容的时候获得view的高度
        if(params!=null&&params.width!=ViewGroup.LayoutParams.WRAP_CONTENT){
            width=view.getWidth();
        }
        //去获取设置的高度
        if(width<=0&&params!=null){
            width=params.width;
        }
        //通过反射获得最大高度
        if(width<=0){
            width=getImageViewFieldValue(view,"mMaxWidth");
        }
        //使用屏幕宽度
        if(width<=0){
            DisplayMetrics displayMetrics=view.getContext()
                    .getResources().getDisplayMetrics();
            width=displayMetrics.widthPixels;
        }
        return width;
    }

    /**
     * 通过反射获取view的属性值
     * @param view
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(ImageView view, String fieldName){
        int value=0;
        try{
            Field field=ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue=field.getInt(view);
            if(fieldValue>0&&fieldValue<Integer.MAX_VALUE){
                value=fieldValue;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }
}
