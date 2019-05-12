// AppleManager.aidl
package com.example.licl.myapplication;
import com.example.licl.myapplication.aidlserver.Apple;
// Declare any non-default types here with import statements

interface AppleManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void addApple(inout Apple apple);
    List<Apple> getApples();
}
