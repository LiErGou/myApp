package com.example.licl.myapplication.binder;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import java.util.List;

public class BookManagerImpl extends Binder implements IBookManager {
    public BookManagerImpl(){
        this.attachInterface(this,DESCRIPTOR);
    }
    //根据传进来的IBinder返回具体的BookManager类型，如果是同一进程的Binder则返回BookManagerImpl
    // 否则返回其代理

    public static IBookManager asInterface(IBinder obj){
        if(obj==null){
            return null;
        }
        IInterface iInterface=obj.queryLocalInterface(DESCRIPTOR);
        if(iInterface!=null&&iInterface instanceof IBookManager){
            return ((IBookManager)iInterface);
        }
        return new Proxy(obj);
    }

    //该方法运行在服务端的线程池中，当客户端发起跨进程请求时，远程请求会通过系统底层封装后调用此方法来处理
    @Override
    protected boolean onTransact(int code,  Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code){
            case INTERFACE_TRANSACTION:{
                reply.writeString(DESCRIPTOR);
                return true;
            }
            case TRANSACTION_getBookList:{
                data.enforceInterface(DESCRIPTOR);
                List<Book> result=this.getBookList();
                reply.writeNoException();
                reply.writeTypedList(result);
                return true;
            }
            case TRANSACTION_addBook:{
                data.enforceInterface(DESCRIPTOR);
                Book book;
                if(0!=data.readInt()){
                    book=Book.CREATOR.createFromParcel(data);
                }else{
                    book=null;
                }
                this.addBook(book);
                reply.writeNoException();
                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }

    @Override
    public List<Book> getBookList() throws RemoteException {
        return null;
    }

    @Override
    public void addBook(Book book) throws RemoteException {

    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    //代理类
    private static class Proxy implements IBookManager{
        private IBinder mRemote;

        public Proxy(IBinder remote) {
            mRemote = remote;
        }

        public String getInterfaceDescriptor(){
            return DESCRIPTOR;
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            Parcel data=Parcel.obtain();
            Parcel reply=Parcel.obtain();
            List<Book> result;
            try{
                data.writeInterfaceToken(DESCRIPTOR);
                mRemote.transact(TRANSACTION_getBookList,data,reply,0);
                reply.readException();
                result=reply.createTypedArrayList(Book.CREATOR);
            }finally {
                reply.recycle();
                data.recycle();
            }
            return result;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Parcel data=Parcel.obtain();
            Parcel reply=Parcel.obtain();
            try{
                data.writeInterfaceToken(DESCRIPTOR);
                if(book!=null){
                    data.writeInt(1);
                    book.writeToParcel(data,0);
                }else{
                    data.writeInt(0);
                }
                mRemote.transact(TRANSACTION_addBook,data,reply,0);
                reply.readException();
            }finally {
                reply.recycle();
                data.recycle();
            }
        }

        @Override
        public IBinder asBinder() {
            return mRemote;
        }
    }
}
