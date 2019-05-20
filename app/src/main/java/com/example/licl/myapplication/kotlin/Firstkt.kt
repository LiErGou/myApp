package com.example.licl.myapplication.kotlin

import kotlin.math.min


fun main(args: Array<String>) {    // 包级可见的函数，接受一个字符串数组作为参数
//    var tmp=Array(10,{0})
    coinChange(intArrayOf(1,15,3,2,4,6),10)

}

fun minDistance(word1: String, word2: String): Int {
    var dp=Array(word1.length+1){IntArray(word2.length+1)}
    for(i in 0.. word1.length-1){
        dp[i][0]=i
    }
    for(i in 0.. word2.length-1){
        dp[0][i]=i
    }
    for(i in 1..word1.length-1){
        for(j in 1..word2.length-1){
            if(word1[i-1]==word2[j-1]){
                dp[i][j]=dp[i-1][j-1]
            }else{
                dp[i][j]=min(dp[i-1][j-1],min(dp[i][j-1],dp[i-1][j]))+1
            }
        }
    }
    return dp[word1.length][word2.length]
}

fun longestPalindrome(s: String): String {
    var count=1
    var res:String=s.substring(0,1)
    for(i in 0..s.length-1){
        for(detal in 0..s.length/2){
            if(i-detal>=0&&i+detal<s.length){
                if(isPalindrome(s.substring(i-detal,i+detal+1))){
                    if(count<2*detal+1){
                        count=2*detal+1
                        res=s.substring(i-detal,i+detal+1)
                    }
                }
                if(isPalindrome(s.substring(i-detal,i+detal))){
                    if(count<2*detal){
                        count=2*detal
                        res=s.substring(i-detal,i+detal)
                    }
                }
            }
        }
    }
    return res

}
fun isPalindrome(s:String):Boolean{
    var i=0
    var j=s.length-1
    while(i<j){
        if(s[i++]!=s[j--]) return false
    }
    return true
}

fun coinChange(coins: IntArray, amount: Int): Int {
    var count=Array(amount+1,{amount+1})
    count[0]=0
    for(i in 0..count.size-1){
//        var min=amount+1
        for(coin in coins){
            if(coin<i){
                count[i]=if(count[i-coin]+1>count[i]) count[i] else count[i-coin]+1
            }
        }
    }
    return if(count.last()>amount) -1 else count.last()
}



fun maxProduct(words: Array<String>): Int {

    var len=words.size
    var maps=Array(len,{0})
    for(i in 0.. len){
        for(c in words[i])
            maps[i] =maps[i] or (1 shl (c.toInt()-'a'.toInt()))
    }
    var res=0
    for(i in 0.. len){
        for(j in i+1..len){
            if((maps[i] and maps[j])==0&&words[i].length*words[j].length>res){
                res=words[i].length*words[j].length
            }
        }
    }
    return res
}

fun countSmaller(nums: IntArray): List<Int> {
    // var res:MutableList<Int> =mutableListOf<Int>()
    var size:Int = nums.lastIndex
    var tmp=Array(size+1,{0})
    for(i in size downTo 0){
        for(j in i.. size){

            if(nums[i]>nums[j]){
                tmp[i]++

            }
        }
    }
    return tmp.toList()
}

fun getStringLength(obj:Any):Int ?{
    if(obj is String){
        return obj.length
    }
    return null
}

fun loop(){
    val items=listOf("apple","banana","kiwifruit")
    for(item in items){

    }

    for(index in items.indices){
        println(items[index])
    }
    var index=0

    while(index<items.size){
        println(index)
        index++
    }

    for(x in 0..items.size){
        println(x)
    }

    when{
        "orange" in items->println("juicy")
        "apple" in items-> println("apple is good")
    }
}

fun describe(obj: Any):String =
        when(obj){
            1->"One"
            "Hello"->"Greeting"
            else ->"Unknown"
        }

fun rangeSample(){
    val x=10
    val y=9
    if(x in 1..y+1){

    }
}

fun Sample(){

}

fun maxOf(a:Int,b:Int)=if(a>b) a else b
