package com.example.yellow.gpssensor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Yellow on 2018-1-3.
 */

public class SettingActivity extends Activity {
    private String id;
    private SharedPreferences sharedPreferences;
    private MYSQL sql;
    private File output;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sql=new MYSQL(this);
        initUserInfo();
    }
    public void initUserInfo(){
        //获取用户头像、昵称和签名
        CircleImageView icon = (CircleImageView)findViewById(R.id.setting_profile_photo);
        TextView myname = (TextView) findViewById(R.id.setting_username);
        TextView myword = (TextView) findViewById(R.id.setting_charateristic_signature) ;
        DataShare ds=((DataShare)getApplicationContext());
        id=ds.getUserid();
        Cursor me = sql.select_user_by_net_id(id);
        me.moveToNext();
        icon.setImageURI(Uri.parse(me.getString(3)));
        myname.setText(me.getString(1));
        myword.setText(me.getString(4));
    }


    public void setProfilePhoto(View view){
        LayoutInflater factor=LayoutInflater.from(SettingActivity.this);
        View dialog=factor.inflate(R.layout.dialog_set_photo,null);
        final AlertDialog.Builder alertdialog=new AlertDialog.Builder(SettingActivity.this);
        alertdialog.setView(dialog);
        alertdialog.setTitle("修改头像");
        final ConstraintLayout from_phone = (ConstraintLayout) dialog.findViewById(R.id.update_from_phone);
        final ConstraintLayout from_new = (ConstraintLayout) dialog.findViewById(R.id.update_from_new);
        from_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,100);
            }
        });
        from_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        alertdialog.show();
    }
    public void setNameAndSig(View view){
        LayoutInflater factor=LayoutInflater.from(SettingActivity.this);
        View dialog=factor.inflate(R.layout.dialog_set_name,null);
        final AlertDialog.Builder alertdialog=new AlertDialog.Builder(SettingActivity.this);
        alertdialog.setView(dialog);
        alertdialog.setTitle("修改个人信息");
        final TextView myname = (TextView) findViewById(R.id.setting_username);
        final TextView myword = (TextView) findViewById(R.id.setting_charateristic_signature) ;
        final EditText etn=(EditText)dialog.findViewById(R.id.dialog_name_edit);
        final EditText etc=(EditText)dialog.findViewById(R.id.characteristis_edit);
        etn.setText(myname.getText());
        etc.setText(myword.getText());
        alertdialog.setPositiveButton("确认修改",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myname.setText(etn.getText());
                myword.setText(etc.getText());
                sql.update_username(id,etn.getText().toString(),etc.getText().toString());
            }
        });
        alertdialog.setNegativeButton("放弃修改",null);

        alertdialog.show();
    }
    public void aboutUsDialog(View view){
        LayoutInflater factor=LayoutInflater.from(SettingActivity.this);
        View dialog=factor.inflate(R.layout.dialog_about_us,null);
        final AlertDialog.Builder alertdialog=new AlertDialog.Builder(SettingActivity.this);
        alertdialog.setView(dialog);
        alertdialog.show();
    }


    public void logOut(View view){
        sharedPreferences=getApplicationContext().getSharedPreferences("MyPreference",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent=new Intent(this,login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }
    public void backToMap(View view){
        this.finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&resultCode==RESULT_OK&&data!=null){//people_img
            Uri imageData = data.getData();
            CircleImageView icon = (CircleImageView)findViewById(R.id.setting_profile_photo);
            icon.setImageURI(imageData);
            sql.update_usericon(id,imageData.toString());
        }
        else if(requestCode==200&&resultCode==RESULT_OK){//people_img
            Uri imageData = Uri.parse(output.getPath());
            CircleImageView icon = (CircleImageView)findViewById(R.id.setting_profile_photo);
            icon.setImageURI(imageData);
            sql.update_usericon(id,imageData.toString());
        }
    }
    void takePhoto(){
        /**
         * 最后一个参数是文件夹的名称，可以随便起
         */
        File file=new File(Environment.getExternalStorageDirectory(),"jiqu_img");
        if(!file.exists()){
            file.mkdir();
        }
        /**
         * 这里将时间作为不同照片的名称
         */
        output=new File(file,System.currentTimeMillis()+".jpg");

        /**
         * 如果该文件夹已经存在，则删除它，否则创建一个
         */
        try {
            if (output.exists()) {
                output.delete();
            }
            output.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 隐式打开拍照的Activity，并且传入CROP_PHOTO常量作为拍照结束后回调的标志
         * 将文件转化为uri
         */
        Uri imageUri = Uri.fromFile(output);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent,200);
    }
}