package com.example.yellow.gpssensor;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by asus2 on 2018/1/8.
 */

public class I_Activity extends AppCompatActivity {
    private MYSQL sql;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        sql=new MYSQL(this);
        init_I();
        init_adapter();
    }
    private void init_I()
    {
        CircleImageView icon = (CircleImageView)findViewById(R.id.me_avatar);
        TextView name = (TextView)findViewById(R.id.me_name);
        TextView word = (TextView)findViewById(R.id.me_sign);
        DataShare ds = ((DataShare)getApplicationContext());
        TextView ID = (TextView)findViewById(R.id.jiqu_id);
        id = ds.getUserid();
        ID.setText("ID："+id);
        Cursor c = sql.select_user_by_net_id(id);
        c.moveToNext();
        icon.setImageURI(Uri.parse(c.getString(3)));
        name.setText(c.getString(1));
        word.setText(c.getString(4));
        final TextView chuangjian_text = (TextView)findViewById(R.id.me_establish);
        final TextView tiaozhan_text = (TextView)findViewById(R.id.me_challenge);
        final ListView chuangjian = (ListView)findViewById(R.id.chuangjian);
        chuangjian_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chuangjian_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.listbgc));
                tiaozhan_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.listbgc_half));
                FatherViewAdapter chuangjian_ada = new FatherViewAdapter(I_Activity.this,sql.select_guanzhu(id));
                chuangjian.setAdapter(chuangjian_ada);
            }
        });
        tiaozhan_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chuangjian_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.listbgc_half));
                tiaozhan_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.listbgc));
                Father2ViewAdapter tiaozhan_ada = new Father2ViewAdapter(I_Activity.this,sql.select_guanzhu(id));
                chuangjian.setAdapter(tiaozhan_ada);
            }
        });
    }
    private void init_adapter()
    {
        final ListView chuangjian = (ListView)findViewById(R.id.chuangjian);
        FatherViewAdapter chuangjian_ada = new FatherViewAdapter(this,sql.select_guanzhu(id));
        chuangjian.setAdapter(chuangjian_ada);
    }
    public void goToMap(View view){
        Intent intent=new Intent(this,MapActivity.class);
        Intent this_intent = getIntent();
        intent.putExtra("user",this_intent.getStringExtra("user"));
        startActivity(intent);
        this.finish();
    }
    public void goToHome(View view){
        Intent intent=new Intent(this,home_page.class);
        Intent this_intent = getIntent();
        intent.putExtra("user",this_intent.getStringExtra("user"));
        startActivity(intent);
        this.finish();
    }
    public void goToGroup(View view){
        Intent intent=new Intent(this,GroupActivity.class);
        Intent this_intent = getIntent();
        intent.putExtra("user",this_intent.getStringExtra("user"));
        startActivity(intent);
        this.finish();
    }
    public void goToSetting(View view){
        Intent intent=new Intent(this,SettingActivity.class);
        startActivity(intent);
    }
    public class FatherViewAdapter extends BaseAdapter {

        //数据源
        private Cursor mList;

        //列数

        private Context mContext;

        public FatherViewAdapter(Context context,Cursor item) {
            super();
            this.mContext = context;
            this.mList = item;
        }

        /**
         * 这部很重要
         *(核心)
         * @return listview的行数
         */
        @Override
        public int getCount() {
            try{
                return mList.getCount();
            }catch (Exception e){
                return 0;
            }
        }
        /*
        @Override
        public int getCount() {
            int count = mList.size() / mColumn;
            if (mList.size() % mColumn > 0) {
                count++;
            }
            return count;
        }*/
        private class iitem{
            String l1;
            String l2;
            String l3;
            String l4;
            String l5;
            String l6;
            String l7;
            String l8;
        }
        @Override
        public iitem getItem(int position) {
            mList.moveToFirst();
            mList.move(position);
            iitem i=new iitem();
            i.l1=mList.getString(2);
            i.l2=mList.getString(1);
            i.l3=mList.getString(3);
            i.l4=mList.getString(4);
            i.l5=mList.getString(5);
            i.l6=mList.getString(6);
            i.l7=mList.getString(7);
            i.l8=mList.getString(7);
            return i;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.care_item, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            iitem i=getItem(position);
            //更新数据源(核心)
            try{
                holder.img.setImageURI(Uri.parse(sql.get_user_icon(i.l2)));
                holder.name.setText(sql.get_user_name(i.l2));
                holder.time.setText(i.l3);
                holder.data.setText(i.l4);
                holder.zan.setText(i.l5);
                holder.tiaozhuan.setText(i.l6);
                holder.gadapter.setmList(sql.select_pic(i.l7));
                holder.ladapter.setmList(sql.select_pinglun(i.l8));
            }catch (Exception e){}
            holder.gadapter.notifyDataSetChanged();
            holder.ladapter.notifyDataSetChanged();
            return convertView;
        }

        class ViewHolder {
            ImageView img;
            TextView  name;
            TextView  time;
            TextView  data;
            TextView  zan;
            TextView  tiaozhuan;
            GridView gridView;
            ListView listView;
            ListViewAdapter ladapter;
            GridViewAdapter gadapter;
            Button delete;

            public ViewHolder(View view) {
                img=(ImageView) view.findViewById(R.id.care_profile_photo);
                name=(TextView) view.findViewById(R.id.care_nick_name) ;
                time=(TextView) view.findViewById(R.id.care_share_time) ;
                data=(TextView) view.findViewById(R.id.care_share_content) ;
                zan=(TextView) view.findViewById(R.id.care_zan_num) ;
                tiaozhuan=(TextView) view.findViewById(R.id.care_do_num) ;
                listView = (ListView) view.findViewById(R.id.care_pinglun_list);
                ladapter = new ListViewAdapter(mContext);
                listView.setAdapter(ladapter);
                gridView = (GridView) view.findViewById(R.id.care_gridview_picture);
                gadapter = new GridViewAdapter(mContext);
                gridView.setAdapter(gadapter);
                delete=(Button)view.findViewById(R.id.care_friend_setting) ;
                delete.setBackgroundResource(R.drawable.delete_8e);
                view.setTag(this);
            }
        }
    }
    public class Father2ViewAdapter extends BaseAdapter {

        //数据源
        private Cursor mList;

        //列数

        private Context mContext;

        public Father2ViewAdapter(Context context,Cursor item) {
            super();
            this.mContext = context;
            this.mList = item;
        }

        /**
         * 这部很重要
         *(核心)
         * @return listview的行数
         */
        @Override
        public int getCount() {
            try{
                return mList.getCount();
            }catch (Exception e){
                return 0;
            }
        }
        /*
        @Override
        public int getCount() {
            int count = mList.size() / mColumn;
            if (mList.size() % mColumn > 0) {
                count++;
            }
            return count;
        }*/
        private class iitem{
            String l1;
            String l2;
            String l3;
            String l4;
            String l5;
            String l6;
            String l7;
            String l8;
        }
        @Override
        public iitem getItem(int position) {
            mList.moveToFirst();
            mList.move(position);
            iitem i=new iitem();
            i.l1=mList.getString(2);
            i.l2=mList.getString(1);
            i.l3=mList.getString(3);
            i.l4=mList.getString(4);
            i.l5=mList.getString(5);
            i.l6=mList.getString(6);
            i.l7=mList.getString(7);
            i.l8=mList.getString(7);
            return i;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.card_tiaozhan, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            iitem i=getItem(position);
            //更新数据源(核心)
            try{
                holder.img.setImageURI(Uri.parse(sql.get_user_icon(i.l2)));
                holder.name.setText(sql.get_user_name(i.l2));
                holder.time.setText(i.l3);
                holder.data.setText(i.l4);
                holder.zan.setText(i.l5);
                holder.tiaozhuan.setText(i.l6);
                Cursor cc =sql.select_pic(i.l7);
                if(cc.moveToNext())
                    holder.gridView.setImageURI(Uri.parse(cc.getString(2)));
                else
                    holder.gridView.setImageResource(R.drawable.jiqu);
                holder.ladapter.setmList(sql.select_pinglun(i.l8));
            }catch (Exception e){}
            holder.ladapter.notifyDataSetChanged();
            return convertView;
        }

        class ViewHolder {
            ImageView img;
            TextView  name;
            TextView  time;
            TextView  data;
            TextView  zan;
            TextView  tiaozhuan;
            ListView listView;
            ListViewAdapter ladapter;
            ImageView gridView;

            public ViewHolder(View view) {
                img=(ImageView) view.findViewById(R.id.care_profile_photo);
                name=(TextView) view.findViewById(R.id.care_nick_name) ;
                time=(TextView) view.findViewById(R.id.care_share_time) ;
                data=(TextView) view.findViewById(R.id.care_share_content) ;
                zan=(TextView) view.findViewById(R.id.care_zan_num) ;
                tiaozhuan=(TextView) view.findViewById(R.id.care_do_num) ;
                listView = (ListView) view.findViewById(R.id.care_pinglun_list);
                ladapter = new ListViewAdapter(mContext);
                listView.setAdapter(ladapter);
                gridView = (ImageView) view.findViewById(R.id.care_picture);
                view.setTag(this);
            }
        }
    }

    public class ListViewAdapter extends BaseAdapter {

        //数据源
        private Cursor mList;

        private Context mContext;

        public ListViewAdapter(Context context) {
            super();
            this.mContext = context;
        }

        public Cursor getmList() {
            return mList;
        }

        public void setmList(Cursor mList) {
            this.mList = mList;
        }

        @Override
        public int getCount() {
            try{
                return mList.getCount();
            }catch (Exception e){
                return 0;
            }
        }
        @Override
        public Cursor getItem(int position) {
            mList.moveToFirst();
            mList.move(position);
            return mList;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            getItem(position+1);
            try{
                holder.comment.setText(mList.getString(3));
                holder.name.setText(mList.getString(2));
            }catch (Exception e){}
            return convertView;
        }

        class ViewHolder {

            TextView name;
            TextView comment;

            public ViewHolder(View view) {
                name = (TextView) view.findViewById(R.id.comment_nick_name);
                comment = (TextView) view.findViewById(R.id.comment_content);
                view.setTag(this);
            }
        }
    }
    public class GridViewAdapter extends BaseAdapter {

        //数据源
        private Cursor mList ;

        private Context mContext;

        public GridViewAdapter(Context context) {
            super();
            this.mContext = context;
        }

        public Cursor getmList() {
            return mList;
        }

        public void setmList(Cursor mList) {
            this.mList = mList;
        }

        @Override
        public int getCount() {
            try{
                return mList.getCount();
            }catch (Exception e){
                return 0;
            }
        }

        @Override
        public Cursor getItem(int position) {
            mList.moveToFirst();
            mList.move(position);
            return mList;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_img, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder = new ViewHolder(convertView);
            getItem(position);
            holder.iv.setImageURI(Uri.parse(mList.getString(2)));
            return convertView;
        }

        class ViewHolder {

            ImageView iv;

            public ViewHolder(View view) {
                iv = (ImageView) view.findViewById(R.id.item_img_image);
                view.setTag(this);
            }
        }
    }
}