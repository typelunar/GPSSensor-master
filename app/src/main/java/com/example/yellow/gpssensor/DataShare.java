package com.example.yellow.gpssensor;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by Yellow on 2018-1-3.
 */

public class DataShare extends Application {
    private String username;
    private String userid;

    private Bitmap snapShot;
    private String path;

    public DataShare(){

    }
    public void setUsername(String name){
        username=name;
    }
    public String getUserid(){
        return userid;
    }
    public void setUserid(String id){
        userid=id;
    }
    public String getUsername(){
        return username;
    }
    public void setSnapShot(Bitmap ss){
        snapShot=ss;
    }
    public Bitmap getSnapShot(){
        return snapShot;
    }
    public void setPath(String p){
        path=p;
    }
    public String getPath(){
        return path;
    }
}