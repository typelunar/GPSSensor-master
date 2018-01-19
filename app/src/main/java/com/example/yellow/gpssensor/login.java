package com.example.yellow.gpssensor;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;

public class login extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private boolean isFirstLaunch;
    private MYSQL sql;
    private int SignMode=1;//默认1-登录模式，2-注册模式
    private  String psdFromCloud;
    private Uri uri;
    //登录微信的应用ID
    private static final String APP_ID="wxd8e1494ccbebbd1f";
    //和微信通信的openapi接口
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sql=new MYSQL(this);
        checkShouldRegister();
        init_login();
    }
    private void init_login()
    {
        final Button unlogin = (Button)findViewById(R.id.login_no_register_button);
        unlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + getResources().getResourcePackageName(R.drawable.qq_zone) + "/"
                        + getResources().getResourceTypeName(R.drawable.qq_zone) + "/"
                        + getResources().getResourceEntryName(R.drawable.qq_zone));
                sql.new_user("游客",null,uri.toString());
                Cursor c=sql.select_user_by_name("游客");
                c.moveToNext();
                DataShare ds=((DataShare)getApplicationContext());
                ds.setUserid(c.getString(0));
                ds.setUsername("游客");
                goToHome();
            }
        });
    }
    public void checkShouldRegister(){
        sharedPreferences=getApplicationContext().getSharedPreferences("MyPreference",MODE_PRIVATE);
        isFirstLaunch=sharedPreferences.getBoolean("isFirstLaunch",true);
        if(!isFirstLaunch){
            DataShare ds=((DataShare)getApplicationContext());
            ds.setUsername(sharedPreferences.getString("Username","游客"));
            ds.setUserid(sharedPreferences.getString("Usersid","游客"));
            goToHome();
        }
        else{
            //Guide The User To Register In
        }
    }
    public void goToHome(){
        Intent intent = new Intent(login.this,home_page.class);
        DataShare ds=((DataShare)getApplicationContext());
        intent.putExtra("user",ds.getUserid());
        startActivity(intent);
        login.this.finish();
    }

    public void registerVisibility(int mode){//1-exist user login,  2-new user register
        ConstraintLayout CL1=(ConstraintLayout)findViewById(R.id.login_alternative_container);
        TextView tv=(TextView)findViewById(R.id.confirm_password_text);
        EditText et=(EditText)findViewById(R.id.confirm_password_edit);
        EditText etname=(EditText)findViewById(R.id.login_name_edit);
        Button btn=(Button)findViewById(R.id.login_btn);
        if(mode==1){
            CL1.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
            et.setVisibility(View.GONE);
            //默认输入上次登录的用户名
            String str=sharedPreferences.getString("Username","游客");
            if(!str.equals("游客")) etname.setText(str);

            btn.setText("登录");
        }
        else if(mode==2){
            CL1.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            et.setVisibility(View.VISIBLE);
            btn.setText("完成");
        }
    }
    public void goToRegister(View view){
        SignMode=2;
        registerVisibility(2);
    }
    public void RegisterIn(View view){
        EditText et_confirm=(EditText)findViewById(R.id.confirm_password_edit);
        EditText et_new=(EditText)findViewById(R.id.login_password_edit);
        EditText et_name=(EditText)findViewById(R.id.login_name_edit);
        if(SignMode==2){
            if(et_confirm.getText().toString().equals("") || et_new.getText().toString().equals("")){
                Toast.makeText(this,"Password Incomplete",Toast.LENGTH_SHORT).show();
            }
            //保存用户名和密码
            else if(et_confirm.getText().toString().equals(et_new.getText().toString())){
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("isFirstLaunch",false);
                editor.putString("Password",et_confirm.getText().toString());
                editor.putString("Username",et_name.getText().toString());
                editor.apply();
                uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + getResources().getResourcePackageName(R.drawable.qq_zone) + "/"
                        + getResources().getResourceTypeName(R.drawable.qq_zone) + "/"
                        + getResources().getResourceEntryName(R.drawable.qq_zone));
                sql.new_user(et_name.getText().toString(),et_confirm.getText().toString(),uri.toString());
                //保存到云端成功自动将ID加到DataShare中
                addUserToCloud(et_name.getText().toString(),et_confirm.getText().toString());
                Intent intent=new Intent(this,label.class);
                startActivity(intent);

                //ds.setUsername(sharedPreferences.getString("Username","游客"));
                //ds.setUserid(c.getString(1));

                Toast.makeText(this,sharedPreferences.getString("Username","游客"),Toast.LENGTH_SHORT).show();
                this.finish();
            }
            else{
                Toast.makeText(this,"Inconsistent Password!",Toast.LENGTH_SHORT).show();
            }
        }
        else if(SignMode==1){
            getUserFromCloud(et_name.getText().toString());
            final String na=et_name.getText().toString();
            final String ps=et_new.getText().toString();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try{
                        Thread.sleep(3000);//休眠3秒
                        //while(psdFromCloud==null) continue;//wait for search result
                        if(!psdFromCloud.equals("登录失败")){//c.moveToNext()
                            if(psdFromCloud.equals(ps)){//c.getString(2)
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putBoolean("isFirstLaunch",false);
                                editor.putString("Password",psdFromCloud);//et_new.getText().toString()
                                editor.putString("Username",na);
                                editor.apply();
                                goToHome();//跳转到主页
                            }
                            else{
                                Toast.makeText(login.this,"密码错误",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            //psdFromCloud="登录失败" or psdFromCloud="用户名不存在"
                            Toast.makeText(login.this,psdFromCloud,Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();

        }

    }
    public void RegisterByWeiXin(View view){
        Toast.makeText(this,"--还没申请到登录权限(ㄒoㄒ)--",Toast.LENGTH_SHORT).show();

/*        //获取微信实例
        api= WXAPIFactory.createWXAPI(this,APP_ID,true);
        api.registerApp(APP_ID);//注册到微信

        String text="登录";
        WXTextObject textObject=new WXTextObject();
        textObject.text=text;

        WXMediaMessage msg=new WXMediaMessage();
        msg.mediaObject=textObject;
        msg.description=text;

        //构造Req,微信处理完返回应用
        SendMessageToWX.Req req=new SendMessageToWX.Req();
        //transaction用于唯一标识一个字段
        req.transaction=String.valueOf(System.currentTimeMillis());
        req.message=msg;

        //调用api发送消息到微信
        api.sendReq(req);

        final SendAuth.Req SAreq=new SendAuth.Req();
        SAreq.scope="snsapi_userinfo";
        SAreq.state="wechat_state_login";*/

    }
    public void addUserToCloud(String name,String psd){
        user u=new user();
        u.setName(name);
        u.setPsd(psd);
        DataShare ds=((DataShare)getApplicationContext());
        ds.setUsername(name);
        u.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null) {
                    setIDToDataShare(s);
                    Toast.makeText(login.this,"成功同步到云端\nid为："+s,Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(login.this,"同步到云端失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setIDToDataShare(String str){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("Usersid",str);
        editor.apply();
        DataShare ds=((DataShare)getApplicationContext());
        ds.setUserid(str);
        Cursor c=sql.select_user_by_name(ds.getUsername());
        c.moveToNext();
        sql.set_netid(c.getString(0),str);
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }
    public void queryFromCloud(String s_id,String r_id){
        BmobQuery<user> cond1=new BmobQuery<>();
        cond1.addWhereEqualTo("s_id",s_id);
        cond1.addWhereEqualTo("r_id",r_id);
        cond1.setLimit(50);//返回条目数量，不设置默认10
        cond1.findObjects(new FindListener<user>() {
            @Override
            public void done(List<user> list, BmobException e) {
                if(e==null){;
                    if(list!=null/*&&list.size()>0*/){

                    }
                }
                else Toast.makeText(login.this,"云端查询失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getUserFromCloud(String name){
        BmobQuery<login.user> cond2=new BmobQuery<>();
        cond2.addWhereEqualTo("name",name);
        cond2.findObjects(new FindListener<login.user>() {
            @Override
            public void done(List<login.user> list, BmobException e) {
                if(e==null){;
                    if(list!=null&&list.size()>0){
                        psdFromCloud=list.get(0).getPsd();
                        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                                + getResources().getResourcePackageName(R.drawable.qq_zone) + "/"
                                + getResources().getResourceTypeName(R.drawable.qq_zone) + "/"
                                + getResources().getResourceEntryName(R.drawable.qq_zone));
                        sql.user_from_net(list.get(0).getName(),psdFromCloud,uri.toString());
                    }
                }
                else if(list.size()==0) psdFromCloud="用户名不存在";
                else psdFromCloud="登录失败";
                //Toast.makeText(login.this,"云端查询失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public class user extends BmobObject{
        private String sign;
        private String psd;
        private String profile;
        private String name;
        //private String objectId;

        /*        public void setObjectId(String objectId) {
                    this.objectId = objectId;
                }*/
        public void setName(String name) {
            this.name = name;
        }
        public void setProfile(String profile) {
            this.profile = profile;
        }
        public void setPsd(String psd) {
            this.psd = psd;
        }
        public void setSign(String sign) {
            this.sign = sign;
        }

        /*public String getObjectId() {
            return objectId;
        }*/
        public String getName() {
            return name;
        }
        public String getProfile() {
            return profile;
        }
        public String getPsd() {
            return psd;
        }
        public String getSign() {
            return sign;
        }

    }


}