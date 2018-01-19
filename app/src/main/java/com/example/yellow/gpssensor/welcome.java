package com.example.yellow.gpssensor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.bmob.v3.Bmob;

//APP_ID="wxd8e1494ccbebbd1f"
//
public class welcome extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private boolean isFirstLaunch;
    private MYSQL sql;

    private String bmobID="a8a331e7783012a0cb3948400e75956f";

    //登录微信的应用ID
    private static final String APP_ID="wx2e4ae8285a016b51";
    //和微信通信的openapi接口
    private IWXAPI api;

    final private Handler welcome_bar_handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what==233)
            {
                final ProgressBar progressBar = (ProgressBar)findViewById(R.id.welcome_bar_progress);
                progressBar.setProgress(progressBar.getProgress()+1);
                if (progressBar.getProgress()==progressBar.getMax()-1)
                {
                    progressBar.setProgress(progressBar.getProgress()+1);
                    try {
                        Thread.sleep(200);
                    }catch (InterruptedException e){}
                    if(isFirstLaunch){
                        Intent intent =new Intent(welcome.this,login.class);
                        startActivity(intent);
                    }
                    else{
                        goToHome();
                    }
                    welcome.this.finish();

                }
            }
        }
    };
    public void welcome_bar_animation()
    {
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.welcome_bar_progress);
        Thread thread = new Thread(){
            @Override
            public void run(){
                while (true){
                    try {
                        Thread.sleep(5);
                    }catch (InterruptedException e){}
                    Message mas = welcome_bar_handler.obtainMessage(233);
                    mas.sendToTarget();
                }
            }
        };
        thread.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcome_bar_animation();
        sql=new MYSQL(this);

        Bmob.initialize(this,"a8a331e7783012a0cb3948400e75956f");

        api= WXAPIFactory.createWXAPI(this,APP_ID,true);
        api.registerApp(APP_ID);//注册到微信

        checkShouldRegister();
    }

    public void checkShouldRegister(){
        sharedPreferences=getApplicationContext().getSharedPreferences("MyPreference",MODE_PRIVATE);
        isFirstLaunch=sharedPreferences.getBoolean("isFirstLaunch",true);
        if(!isFirstLaunch){
            DataShare ds=((DataShare)getApplicationContext());
            ds.setUsername(sharedPreferences.getString("Username","游客"));
            ds.setUserid(sharedPreferences.getString("Usersid","游客"));
            //ds.setUserid(c.getString(0));
        }
        else{
            //Guide The User To Register In
        }
    }
    public void goToHome(){
        Intent intent = new Intent(this,home_page.class);
        DataShare ds=((DataShare)getApplicationContext());
        intent.putExtra("user",ds.getUserid());
        startActivity(intent);
        this.finish();
    }


}
