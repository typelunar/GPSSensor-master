package com.example.yellow.gpssensor;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.widget.SimpleCursorAdapter;

import com.baidu.mapapi.model.LatLng;

import java.sql.SQLDataException;
import java.util.ArrayList;

/**
 * Created by asus2 on 2018/1/2.
 */


public class MYSQL{
    private Context context;
    //   init
    private SQLiteDatabase db;
    public MYSQL(Context c){
        context=c;
        openSQL(c);
        createtable();
    }
    public void openSQL(Context c){
        db = SQLiteDatabase.openOrCreateDatabase(c.getFilesDir().toString()+"/mysql.db3",null);
    }
    public void closeSQL(){
        if(db != null && db.isOpen()){
            db.close();
        }
    }
    public void useSQL(String s){
        db.execSQL(s);
    }
    public void createtable(){
        db.execSQL("create table if not exists drawLatLngs(_id integer primary key autoincrement," +
                "users_id varchar(10),drawLatLngs_id varchar(20),time varchar(20))");
        db.execSQL("create table if not exists users(_id integer primary key autoincrement," +
                "name varchar(20),password varchar(20),icon varchar(50),word varchar(20),net_id varchar(20))");
        db.execSQL("create table if not exists friends(_id integer primary key autoincrement," +
                "user_id varchar(20),friend_id varchar(20),last_word varchar(50))");
        db.execSQL("create table if not exists speaks(_id integer primary key autoincrement," +
                "user_id varchar(20),i_or_not char (2),friend_id varchar(20),word varchar(50))");
        db.execSQL("create table if not exists guanzhu(_id integer primary key autoincrement," +
                "user_id varchar(20),user_icon char (20),time varchar(20)" +
                ",data varchar(50),zan varchar(5),tiaozhuan varchar(5),guanzhu_id varchar(20))");
        db.execSQL("create table if not exists pics(_id integer primary key autoincrement," +
                "guanzhu_id varchar(20),pic_uir varchar (50))");
        db.execSQL("create table if not exists pingluns(_id integer primary key autoincrement," +
                "guanzhu_id varchar(20),name varchar(20),word varchar(50))");
        try{
            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + context.getResources().getResourcePackageName(R.drawable.jiqu) + "/"
                    + context.getResources().getResourceTypeName(R.drawable.jiqu) + "/"
                    + context.getResources().getResourceEntryName(R.drawable.jiqu));
            db.execSQL("insert into users values(?,?,?,?,?)",
                    new String[]{"9999999999","迹趣官方","jiquguangfang",uri.toString(),null});
        }catch (SQLiteException e) {}
    }
    //   guanzhu
    public void new_guanzhu(String user_id,String user_icon,String time,String data,ArrayList<String> pic)
    {
        Cursor c = select_guanzhu(user_id);
        String guanzhu_id = user_id + "_" + Integer.toString(c.getCount()+1);
        db.execSQL("insert into guanzhu values(?,?,?,?,?,?,?,?)",new String[]{null,user_id,user_icon,time,data,null,null,guanzhu_id});
        for (int i = 0;i<pic.size();i++)
        {
            db.execSQL("insert into pics values(?,?,?)",new String[]{null,guanzhu_id,pic.get(i)});
        }
    }
    public void new_pinglun(String guanzhu_id,String name,String commit)
    {
        db.execSQL("insert into pingluns values(?,?,?,?)",new String[]{null,guanzhu_id,name,commit});
    }
    public Cursor select_guanzhu(String user_id)
    {
        return db.rawQuery("select * from guanzhu where user_id = ?",new String[]{user_id});
    }
    public Cursor select_guanzhu_all()
    {
        return db.rawQuery("select * from guanzhu",null);
    }
    public Cursor select_pic(String guanzhu_id)
    {
        return db.rawQuery("select * from pics where guanzhu_id = ?",new String[]{guanzhu_id});
    }
    public Cursor select_pinglun(String guanzhu_id)
    {
        return db.rawQuery("select * from pingluns where guanzhu_id = ?",new String[]{guanzhu_id});
    }
    //   chat
    public void new_chat(String user_id,String friend_id,String chat)
    {
        db.execSQL("insert into speaks values(?,?,?,?,?)",new String[]{null,user_id,"I",friend_id,chat});
        db.execSQL("insert into speaks values(?,?,?,?,?)",new String[]{null,friend_id,"O",user_id,chat});
        if(db.rawQuery("select * from friends where user_id = ? and friend_id = ?",new String[]{user_id,friend_id}).moveToNext())
        {
            db.execSQL("update friends set last_word = ? where user_id = ? and friend_id = ?",new String[]{chat,user_id,friend_id});
            db.execSQL("update friends set last_word = ? where user_id = ? and friend_id = ?",new String[]{chat,friend_id,user_id});
        }
        else
        {
            db.execSQL("insert into friends values(?,?,?,?)",new String[]{null,friend_id,user_id,chat});
            db.execSQL("insert into friends values(?,?,?,?)",new String[]{null,user_id,friend_id,chat});
        }
    }
    public Cursor get_speaks(String user_id,String friend_id)
    {
        return db.rawQuery("select * from speaks where user_id = ? and friend_id = ?",new String[]{user_id,friend_id});
    }
    public Cursor get_chat_list(String user_id)
    {
        return db.rawQuery("select * from friends where user_id = ?",new String[]{user_id});
    }
    public Cursor get_chat_list_by_net_id(String user_id)
    {
        return db.rawQuery("select * from friends,user where net_id = ? and users_id=_id",new String[]{user_id});
    }
    //   user
    public int user_getCount()
    {
        return db.rawQuery("select * from users",null).getCount();
    }
    public void set_netid(String id,String net_id)
    {
        try {
            db.execSQL("update users set net_id = ? where _id = ?",new String[]{net_id,id});
        }
        catch (SQLiteException e) {}
    }
    public void new_user(String name,String password,String icon)
    {
        try {
            String s=Integer.toString(user_getCount());
            db.execSQL("insert into users values(?,?,?,?,?,?)",
                    new String[]{s,name,password,icon,"设置你的个性签名","0"});
            new_chat("9999999999",s,"欢迎使用迹趣，感谢您的支持。");
        }
        catch (SQLiteException e) {}
    }
    public Cursor select_user(String id)
    {
        return db.rawQuery("select * from users where _id = ?",new String[]{id});
    }
    public String get_user_name(String user_id)
    {
        Cursor c=select_user(user_id);
        c.moveToFirst();
        return c.getString(1);
    }
    public String get_user_icon(String user_id)
    {
        Cursor c=select_user(user_id);
        c.moveToFirst();
        return c.getString(3);
    }
    public Cursor select_user_by_name(String name)
    {
        return db.rawQuery("select * from users where name = ?",new String[]{name});
    }
    public Cursor select_user_by_net_id(String id)
    {
        return db.rawQuery("select * from users where net_id = ?",new String[]{id});
    }
    public void update_username(String users_id,String name,String word)
    {
        try {
            db.execSQL("update users set name = ? where _id = ?",new String[]{name,users_id});
            db.execSQL("update users set word = ? where _id = ?",new String[]{word,users_id});
        }
        catch (SQLiteException e) {}
    }
    public void update_userpasswor(String users_id,String password)
    {
        try {
            db.execSQL("update users set password = ? where _id = ?",new String[]{password,users_id});
        }
        catch (SQLiteException e) {}
    }
    public void update_usericon(String users_id,String icon)
    {
        try {
            db.execSQL("update users set icon = ? where _id = ?",new String[]{icon,users_id});
        }
        catch (SQLiteException e) {}
    }
    //    drawLatLngs
    public void save_drawLatLngs(String users_id, ArrayList<LatLng> drawLatLngs, String time)
    {
        String drawLatLngs_id;
        Cursor user=select_user(users_id);
        user.moveToNext();
        drawLatLngs_id=users_id+"_"+Integer.toString(user.getInt(4));
        try {
            db.execSQL("insert into drawLatLngs values(?,?,?,?)", new String[]{null, users_id, drawLatLngs_id,time});
            db.execSQL("create table if not exists " + users_id + "(_id integer primary key autoincrement," +
                    "x varchar(10)),y varchar(10)");
            for (int i = 0; i < drawLatLngs.size(); i++) {
                db.execSQL("insert into " + users_id + " values(?,?,?)",
                        new String[]{null, Double.toString(drawLatLngs.get(i).latitude), Double.toString(drawLatLngs.get(i).longitude)});
            }
        }
        catch (SQLiteException e) {}
    }
    public void drop_drawLatLngs(String drawLatLngs_id)
    {
        try{
            db.execSQL("delete from drawLatLngs where drawLatLngs_id = ?",new String[]{drawLatLngs_id});
            db.execSQL("drop table "+drawLatLngs_id);
        }
        catch (SQLiteException e) {}
    }
    public Cursor select_drawLatLngs(String drawLatLngs_id)
    {
        return db.rawQuery("select * from "+drawLatLngs_id,null);
    }
    public Cursor select_drawLatLngs_list(String users_id)
    {
        return db.rawQuery("select * from drawLatLngs where users_id = ?",new String[]{users_id});
    }

}