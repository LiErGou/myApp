package com.example.licl.myapplication.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.licl.myapplication.db.MyDatabaseHelper;

public class MyContentProvider extends ContentProvider {

    public static final int BOOK_DIR=0;
    public static final int BOOK_ITEM=1;
    public static final int CATEGORY_DIR=2;
    public static final int CATEGORY_ITEM=3;
    public static final String AUTHORITY_ITEM="com.example.licl.myapplocation.provider";
    private static UriMatcher uriMatcher;
    private MyDatabaseHelper dbHelper;
    static {
        uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY_ITEM,"book",BOOK_DIR);
        uriMatcher.addURI(AUTHORITY_ITEM,"book/#",BOOK_ITEM);
        uriMatcher.addURI(AUTHORITY_ITEM,"category",CATEGORY_DIR);
        uriMatcher.addURI(AUTHORITY_ITEM,"category/#",CATEGORY_ITEM);
    }
    public MyContentProvider() {
    }
    //初始化内容提供器时会调用，通常完成对数据库的创建升级等工作
    @Override
    public boolean onCreate() {
        dbHelper=new MyDatabaseHelper(getContext(),"BookStore.db",null,2);
        return true;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=null;
        switch (uriMatcher.match(uri)){
            case BOOK_DIR:
                //访问数据库的query与contentprovider不同
                cursor=db.query("Book",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case BOOK_ITEM:
                //得到uri中斜线分开的字符串
                String bookId=uri.getPathSegments().get(1);
                cursor=db.query("Book",projection,"id=?",new String[]{bookId},null,null,sortOrder);
                break;
            case CATEGORY_DIR:
                //访问数据库的query与contentprovider不同
                cursor=db.query("Category",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case CATEGORY_ITEM:
                //得到uri中斜线分开的字符串
                String categoryId=uri.getPathSegments().get(1);
                cursor=db.query("Category",projection,"id=?",new String[]{categoryId},null,null,sortOrder);
                break;
                default:
                    break;
        }
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //根据传入的内容URI来返回相应的MIME类型
    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }






    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
