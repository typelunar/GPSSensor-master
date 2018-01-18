package com.example.yellow.gpssensor;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.text.SimpleDateFormat;
import java.util.*;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Yellow on 2018-1-2.
 */

//AppSecret: 71d51701664b52196a58c0d3a3ce37c9
//APP_ID=wxd8e1494ccbebbd1f

//app_id=wx2e4ae8285a016b51
//app_secret=98ffa0dfb13e15b8a401abaa6fa585f9

public class ShareActivity extends Activity {
    private String type;
    private String lastActivity;
    private EditText content;

    private MYSQL sql;
    private List<Map<String,Object>> mylist = new ArrayList<>();
    private ArrayList<String> savelist = new ArrayList<>();
    private SimpleAdapter pic;

    //登录微信的应用ID
    private static final String APP_ID="wx2e4ae8285a016b51";
    //和微信通信的openapi接口
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        sql=new MYSQL(this);
        type=getIntent().getStringExtra("type");
        lastActivity=getIntent().getStringExtra("last");
        if(lastActivity==null) lastActivity="";

        content=(EditText)findViewById(R.id.share_edit_text);

        shareContentInit();
    }
    public void shareContentInit(){
        Map <String,Object> m=new HashMap<>();
        m.put("img",R.drawable.icon_share_add);
        mylist.add(m);
        pic=new SimpleAdapter(this,mylist,R.layout.item_img,new String[]{"img"},new int[]{R.id.item_img_image});
        final GridView view_pic=(GridView)findViewById(R.id.share_picture_gridview);
        view_pic.setAdapter(pic);
        view_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,100);
            }
        });
    }

    public void share(View view){
        //save and update listviews and gridviews
        switch (view.getId()){
            case R.id.share_share_btn:
                SimpleDateFormat sdf=new SimpleDateFormat("hh:mm");
                String date=sdf.format(new java.util.Date());
                final EditText word=(EditText) findViewById(R.id.share_edit_text);
                //Toast.makeText(this,getIntent().getStringExtra("user"),Toast.LENGTH_SHORT).show();
                DataShare ds=((DataShare)getApplicationContext());
                sql.new_guanzhu(ds.getUserid(),sql.get_user_icon(ds.getUserid())
                        ,date,word.getText().toString(),savelist);
                addShuoShuoToCloud(ds.getUsername(),ds.getUserid(),date,word.getText().toString());
                this.finish();
                break;
            case R.id.share_others_pengyouquang:
                String contentText=content.getText().toString();
                api= WXAPIFactory.createWXAPI(this,APP_ID,true);
                //api.registerApp(APP_ID);//注册到微信
                if(!api.isWXAppInstalled()){
                    Toast.makeText(this,"未检测到微信客户端",Toast.LENGTH_SHORT).show();
                    return;
                }

                WXTextObject textObject=new WXTextObject();
                textObject.text=contentText;

                //要发送的图片
                Bitmap bmp= BitmapFactory.decodeResource(getResources(),R.drawable.jiqu_hd);
                //初始化WXImageObject对象
                WXImageObject imageObject=new WXImageObject(bmp);
                //初始化WXMediaMessage对象
                WXMediaMessage msg=new WXMediaMessage();
                msg.mediaObject=imageObject;
                msg.description=contentText;
                //msg.setThumbImage(bmp);

                //构造Req,微信处理完返回应用
                SendMessageToWX.Req req=new SendMessageToWX.Req();
                //设置发送场景，WXSceneSession聊天界面，WXSceneTimeline朋友圈，WXSceneFavorite收藏
                req.scene=SendMessageToWX.Req.WXSceneTimeline;
                //transaction用于唯一标识一个字段
                req.transaction=String.valueOf(System.currentTimeMillis());
                req.message=msg;

                //调用api发送消息到微信
                api.sendReq(req);

                break;
        }
    }
    public void backToLast(View view){
/*        switch (lastActivity){
            case "Map":
                Intent intent1=new Intent(this,MapActivity.class);
                startActivity(intent1);
                this.finish();
                break;
            case "Home":
                Intent intent2=new Intent(this,home_page.class);
                startActivity(intent2);
                this.finish();
                break;
            case "Quanzi":
                Intent intent3=new Intent(this,GroupActivity.class);
                startActivity(intent3);
                this.finish();
                break;
            default:
                Intent intent4=new Intent(this,home_page.class);
                startActivity(intent4);
                this.finish();
                break;
        }*/
        this.finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&resultCode==RESULT_OK&&data!=null){//people_img
            Uri imageData = data.getData();
            Map<String,Object> map=new HashMap<>();
            map.put("img",imageData.toString());
            mylist.add(map);
            savelist.add(imageData.toString());
            pic.notifyDataSetChanged();
        }
    }

    public void addShuoShuoToCloud(String name,String id,String time,String content){
        ShuoShuo s=new ShuoShuo();
        s.setContent(content);
        s.setName(name);
        s.setTime(time);
        s.setUserid(id);
        s.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null) Toast.makeText(ShareActivity.this,"成功发布到云端",Toast.LENGTH_SHORT).show();
                else Toast.makeText(ShareActivity.this,"同步到云端失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void queryFromCloud(){
        BmobQuery<ShuoShuo> query=new BmobQuery<ShuoShuo>();
        String bql="select * from ShuoShuo";
        query.setSQL(bql);
        query.doSQLQuery(new SQLQueryListener<ShuoShuo>() {
            @Override
            public void done(BmobQueryResult<ShuoShuo> bmobQueryResult, BmobException e) {
                if(e==null){
                    List<ShuoShuo> list=bmobQueryResult.getResults();
                    if(list!=null&&list.size()>0){
                        resultOut(list);
                    }
                }
                else Toast.makeText(ShareActivity.this,"云端查询失败"+e.getErrorCode(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public List<ShuoShuo> resultOut(List<ShuoShuo> l){
        return l;
    }


    public class ShuoShuo extends BmobObject{
        private String name;
        private String userid;
        private String content;
        private String time;

        public void setTime(String time) {
            this.time = time;
        }

        public String getTime() {
            return time;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUserid() {
            return userid;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
