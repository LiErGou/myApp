package com.example.licl.myapplication.binder;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

public interface IBookManager extends IInterface {
    //Binder的唯一标识
    String DESCRIPTOR="com.example.licl.myapplication.binder.IBookManager";
    //方法的标识号
    int TRANSACTION_getBookList=IBinder.FIRST_CALL_TRANSACTION;
    int TRANSACTION_addBook=IBinder.FIRST_CALL_TRANSACTION+1;
    List<Book> getBookList() throws RemoteException;
    void addBook(Book book) throws RemoteException;
}
