package com.example.yellow.gpssensor;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by asus2 on 2017/12/31.
 */

public class GroupActivity extends AppCompatActivity {
    private MYSQL sql;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        sql=new MYSQL(this);
        init_group();
        init_adapter();
    }
    private void init_group()
    {
        CircleImageView icon = (CircleImageView)findViewById(R.id.group_profile_photo);
        TextView name = (TextView)findViewById(R.id.group_title_name);
        DataShare ds = ((DataShare)getApplicationContext());
        String id = ds.getUserid();
        Toast.makeText(this,id,Toast.LENGTH_LONG).show();
        Cursor c = sql.select_user_by_net_id(id);
        c.moveToNext();
        //icon.setImageURI(Uri.parse(c.getString(3)));
        name.setText(c.getString(1));
        final TextView dongtai_text = (TextView)findViewById(R.id.group_title_dongtai);
        final TextView withme_text = (TextView)findViewById(R.id.group_title_withme);
        final TextView sixin_text = (TextView)findViewById(R.id.group_title_sixin);
        final LinearLayout dongtai_view = (LinearLayout) findViewById(R.id.group_view_dongtai);
        final LinearLayout withme_view = (LinearLayout) findViewById(R.id.group_view_withme);
        final LinearLayout sixin_view = (LinearLayout) findViewById(R.id.group_view_sixin);
        final ImageButton group_title_add_icon = (ImageButton)findViewById(R.id.group_title_add_icon) ;
        View.OnClickListener dongtai_OnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dongtai_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.listbgc_half));
                withme_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.fade_text));
                sixin_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.fade_text));
                dongtai_view.setVisibility(View.VISIBLE);
                withme_view.setVisibility(View.INVISIBLE);
                sixin_view.setVisibility(View.INVISIBLE);
            }
        };
        View.OnClickListener withme_OnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dongtai_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.fade_text));
                withme_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.listbgc_half));
                sixin_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.fade_text));
                dongtai_view.setVisibility(View.INVISIBLE);
                withme_view.setVisibility(View.VISIBLE);
                sixin_view.setVisibility(View.INVISIBLE);
            }
        };
        View.OnClickListener sixin_OnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dongtai_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.fade_text));
                withme_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.fade_text));
                sixin_text.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.listbgc_half));
                dongtai_view.setVisibility(View.INVISIBLE);
                withme_view.setVisibility(View.INVISIBLE);
                sixin_view.setVisibility(View.VISIBLE);
            }
        };
        group_title_add_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupActivity.this,ShareActivity.class);
                startActivity(intent);
            }
        });
        dongtai_text.setOnClickListener(dongtai_OnClick);
        withme_text.setOnClickListener(withme_OnClick);
        sixin_text.setOnClickListener(sixin_OnClick);
    }
    private void init_adapter()
    {
        ListView dongtai_view = (ListView) findViewById(R.id.group_list_dongtai);
        ListView withme_view = (ListView) findViewById(R.id.group_list_withme);
        ListView sixin_view = (ListView) findViewById(R.id.group_list_sixin);
        FatherViewAdapter dongtai_adapter = new FatherViewAdapter(this,null);
        DataShare ds=((DataShare)getApplicationContext());
        final ChatViewAdapter sixin_adapter = new ChatViewAdapter(this,sql.get_chat_list(ds.getUserid()));//intentin.getStringExtra("user")
        dongtai_view.setAdapter(dongtai_adapter);
        dongtai_adapter.mList=sql.select_guanzhu_all();
        dongtai_adapter.notifyDataSetChanged();
        sixin_view.setAdapter(sixin_adapter);
        sixin_adapter.notifyDataSetChanged();
        sixin_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = sixin_adapter.mList;
                c.moveToFirst();
                c.move(position);
                Intent in=new Intent(GroupActivity.this,ChatActivity.class);
                in.putExtra("user_id",c.getString(1));
                in.putExtra("friend_id",c.getString(2));
                startActivity(in);
            }
        });
    }
    public void goToI(View view){
        Intent intent=new Intent(this,I_Activity.class);
        Intent this_intent = getIntent();
        intent.putExtra("user",this_intent.getStringExtra("user"));
        startActivity(intent);
        this.finish();
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
                holder.gadapter.setmList(sql.select_pic(i.l7));
                holder.ladapter.setmList(sql.select_pinglun(i.l8));
                holder.img.setImageURI(Uri.parse(sql.get_user_icon(i.l2)));
                holder.name.setText(sql.get_user_name(i.l2));
                holder.time.setText(i.l3);
                holder.data.setText(i.l4);
                holder.zan.setText(i.l5);
                holder.tiaozhuan.setText(i.l6);
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

            public ViewHolder(View view) {
                tiaozhuan=(TextView) findViewById(R.id.care_do_num) ;
                listView = (ListView) view.findViewById(R.id.care_pinglun_list);
                ladapter = new ListViewAdapter(mContext);
                listView.setAdapter(ladapter);
                gridView = (GridView) view.findViewById(R.id.care_gridview_picture);
                gadapter = new GridViewAdapter(mContext);
                gridView.setAdapter(gadapter);
                img=(ImageView) view.findViewById(R.id.care_profile_photo);
                name=(TextView) view.findViewById(R.id.care_nick_name) ;
                time=(TextView) view.findViewById(R.id.care_share_time) ;
                data=(TextView) view.findViewById(R.id.care_share_content) ;
                zan=(TextView) view.findViewById(R.id.care_zan_num) ;
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
            getItem(position);
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

    public class ChatViewAdapter extends BaseAdapter {

        //数据源
        private Cursor mList;

        //列数

        private Context mContext;

        public ChatViewAdapter(Context context, Cursor list) {
            super();
            this.mContext = context;
            this.mList = list;
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
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_in_homepage, parent, false);
                //holder = new ViewHolder(convertView);
            } else {
                //holder = (ViewHolder) convertView.getTag();
            }
            TextView I_word;
            TextView name;
            CircleImageView I_icon;
            name = (TextView)convertView.findViewById(R.id.name);
            I_word =(TextView)convertView.findViewById(R.id.send_message);
            I_icon =(CircleImageView) convertView.findViewById(R.id.avatar2);
            int kp=10;
            getItem(position);
            name.setText(sql.get_user_name(mList.getString(2)));
            I_icon.setImageURI(Uri.parse(sql.get_user_icon(mList.getString(2))));
            I_word.setText(mList.getString(3));
            return convertView;
        }

    }
}