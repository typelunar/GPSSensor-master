package com.example.yellow.gpssensor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.StackView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by asus2 on 2017/12/29.
 */

public class home_page extends AppCompatActivity {
    private MYSQL sql;
    private List<Map<String,Object>> searchResultList;
    private SimpleAdapter adapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        sql=new MYSQL(this);
        init_homepage();
        initBanner();

        initSearch();
    }
    public void initBanner(){
        List<URL> images=new ArrayList<>();
        try{
            URL u1=new URL("http://m.qpic.cn/psb?/V13oePxu4IH3ty/*5PImr7SOBIiSnW5RuGul4EfUSt4yV.GQUC32.OVXIM!/b/dPIAAAAAAAAA&bo=YAWAAgAAAAARB9c!&rf=viewer_4");
            URL u2=new URL("http://m.qpic.cn/psb?/V13oePxu4IH3ty/WzCI90*.j*AgMLVDF96U2PIdOuVDUXwJfYQUMNMA37g!/b/dPIAAAAAAAAA&bo=1AH6AAAAAAARFw8!&rf=viewer_4");
            URL u3=new URL("http://m.qpic.cn/psb?/V13oePxu4IH3ty/T9fsFE2QmpAuiZ8UUeuSvcMOvEeF7UMMANhCFxD3hsg!/b/dPMAAAAAAAAA&bo=rwH0AAAAAAARB2o!&rf=viewer_4");
            URL u4=new URL("http://m.qpic.cn/psb?/V13oePxu4IH3ty/WbEaxDosVvfA5hk4m*niR0YmHwtg4vpnfE9yvBwIYbs!/b/dGUBAAAAAAAA&bo=WAJSAQAAAAARFyk!&rf=viewer_4");
            URL u5=new URL("http://m.qpic.cn/psb?/V13oePxu4IH3ty/Fv8TK9UCSBuPlaz9x8AhEViPoDXi3su5hkr4EQBCLu0!/b/dD8BAAAAAAAA&bo=*AOAAgAAAAARF10!&rf=viewer_4");

            images.add(u1);
            images.add(u2);
            images.add(u3);
            images.add(u4);
            images.add(u5);
        }catch (Exception e){
            e.printStackTrace();
        }

        Banner banner = (Banner) findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //设置间隔时间
        banner.setDelayTime(3000);
        //设置动画样式
        banner.setBannerAnimation(Transformer.Tablet);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }
    public void initStackView(){
        StackView stackView =(StackView)findViewById(R.id.homepage_stackview);
        List<Map<String,Object>> listitems = new ArrayList<>();
        Map<String,Object> item1 = new HashMap<>();
        item1.put("img",R.mipmap.qq);
        listitems.add(item1);
        listitems.add(item1);
        listitems.add(item1);
        Map<String,Object> item2 = new HashMap<>();
        item2.put("img",R.mipmap.weibo);
        listitems.add(item2);
        listitems.add(item2);
        listitems.add(item2);
        Map<String,Object> item3 = new HashMap<>();
        item3.put("img",R.mipmap.weixin);
        listitems.add(item3);
        listitems.add(item3);
        listitems.add(item3);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,listitems,R.layout.stackview_layout,new String[]{"img"},new int[]{R.id.stackview_image});
        stackView.setAdapter(simpleAdapter);
    }

    private void init_homepage()
    {
        final TextView remen= (TextView)findViewById(R.id.homepage_remen_text);
        final TextView guanzhu= (TextView)findViewById(R.id.homepage_guanzhu_text);
        final GridView view_remen=(GridView)findViewById(R.id.homepage_rimen_grid);
        final ListView list_guanzhu=(ListView)findViewById(R.id.homepage_guanzhu_list);
        remen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remen.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.colorAccent));
                guanzhu.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.colorPrimary));
                view_remen.setVisibility(View.VISIBLE);
                list_guanzhu.setVisibility(View.INVISIBLE);
            }
        });
        guanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guanzhu.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.colorAccent));
                remen.setTextColor((ColorStateList) ((Resources) getBaseContext().getResources()).getColorStateList(R.color.colorPrimary));
                list_guanzhu.setVisibility(View.VISIBLE);
                view_remen.setVisibility(View.INVISIBLE);
            }
        });
        final FatherViewAdapter guanzhu_adapter = new FatherViewAdapter(this,sql.select_guanzhu_all());
        remenViewAdapter remen_adapter = new remenViewAdapter(this,sql.select_guanzhu_all());
        view_remen.setAdapter(remen_adapter);
        remen_adapter.notifyDataSetChanged();
        list_guanzhu.setAdapter(guanzhu_adapter);
        guanzhu_adapter.notifyDataSetChanged();
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
            }catch (Exception e){e.printStackTrace();}
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

    public void goToMap(View view){
        Intent intent=new Intent(this,MapActivity.class);
        Intent this_intent = getIntent();
        intent.putExtra("user",this_intent.getStringExtra("user"));
        startActivity(intent);
        //this.finish();
    }
    public void goToGroup(View view){
        Intent intent=new Intent(this,GroupActivity.class);
        Intent this_intent = getIntent();
        intent.putExtra("user",this_intent.getStringExtra("user"));
        startActivity(intent);
        //this.finish();
    }
    public void goToI(View view){
        Intent intent=new Intent(this,I_Activity.class);
        Intent this_intent = getIntent();
        intent.putExtra("user",this_intent.getStringExtra("user"));
        startActivity(intent);
        //this.finish();
    }

    public class remenViewAdapter extends BaseAdapter {

        //数据源
        private Cursor mList;

        //列数

        private Context mContext;

        public remenViewAdapter(Context context,Cursor item) {
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
            String i1;
            String i2;
            String i3;
            String i4;
        }
        @Override
        public iitem getItem(int position) {
            mList.moveToFirst();
            mList.move(position);
            iitem i=new iitem();
            i.i1=mList.getString(7);
            i.i2=mList.getString(2);
            i.i3=mList.getString(4);
            i.i4=mList.getString(5);

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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_hot, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //更新数据源(核心)

            iitem i=getItem(position);
            String ss;
            try {
                ss=i.i1;
            }catch (Exception e){ss="0";}
            Cursor c=sql.select_pic(ss);
            c.moveToNext();
            int kp= 10;
            while(kp>0)
            {
                try{
                    holder.img.setImageURI(Uri.parse(c.getString(2)));
                    holder.icon.setImageURI(Uri.parse(sql.get_user_icon(mList.getString(1))));
                    holder.word.setText(i.i3);
                    holder.zan.setText(i.i4);
                    break;
                }catch (Exception e){kp--;}
            }
            return convertView;
        }

        class ViewHolder {
            ImageView img;
            ImageView icon;
            TextView  word;
            TextView  zan;

            public ViewHolder(View view) {
                img=(ImageView) view.findViewById(R.id.hot_item_picture);
                icon=(ImageView) view.findViewById(R.id.hot_item_profile_photo);
                word=(TextView) view.findViewById(R.id.hot_item_content_shortcut) ;
                zan=(TextView) view.findViewById(R.id.hot_zan_num) ;
                view.setTag(this);
            }
        }
    }



    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            /**
             注意：
             1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
             2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
             传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
             切记不要胡乱强转！
             */

            //Glide 加载图片简单用法
            //Glide.with(context).load(path).into(imageView);

            //Picasso 加载图片简单用法
            //Picasso.with(context).load(path).into(imageView);

            //用fresco加载图片简单用法，记得要写下面的createImageView方法
            Uri uri = Uri.parse(path.toString());
            imageView.setImageURI(uri);
        }

        //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
        @Override
        public ImageView createImageView(Context context) {
            //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
            //SimpleDraweeView simpleDraweeView=new SimpleDraweeView(context);
            //return simpleDraweeView;
            Fresco.initialize(context);
            return new SimpleDraweeView(context);
        }
    }

    public void initSearch(){
        final EditText et_search=(EditText)findViewById(R.id.search_view);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    String str=et_search.getText().toString();

                    queryFromCloud(str);//通过ID或用户名搜索

                    return true;
                }
                return false;
            }
        });
        lv=(ListView)findViewById(R.id.search_result_list);
        searchResultList=new ArrayList<>();

        Map<String,Object> item=new HashMap<>();
        item.put("name","暂无搜索结果");
        item.put("id","暂无id");
        searchResultList.add(item);
        String[] from={"name","id"};
        int[] to={R.id.search_item_name,R.id.search_item_id};
        adapter=new SimpleAdapter(this,searchResultList,R.layout.item_search,from,to);
        lv.setAdapter(adapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView tv=(TextView)view.findViewById(R.id.search_item_id);
                AlertDialog.Builder ab=new AlertDialog.Builder(home_page.this);
                ab.setTitle("确定添加为好友？");
                ab.setNegativeButton("取消",null);
                ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataShare ds=((DataShare)getApplicationContext());
                        addFriendToCloud(ds.getUserid(),tv.getText().toString());
                        lv.setVisibility(View.GONE);
                    }
                });
                ab.show();
                return true;
            }
        });
    }
    public void queryFromCloud(String s){
        BmobQuery<login.user> cond1=new BmobQuery<>();
        BmobQuery<login.user> cond2=new BmobQuery<>();
        cond1.addWhereEqualTo("objectId",s);
        cond2.addWhereEqualTo("name",s);
        List<BmobQuery<login.user>> queries=new ArrayList<BmobQuery<login.user>>();
        queries.add(cond1);
        queries.add(cond2);
        BmobQuery<login.user> query=new BmobQuery<>();
        query.or(queries);//或查找
        Toast.makeText(home_page.this,"正在搜索...",Toast.LENGTH_SHORT).show();
        query.findObjects(new FindListener<login.user>() {
            @Override
            public void done(List<login.user> list, BmobException e) {
                if(e==null){;
                    if(list!=null/*&&list.size()>0*/){
                        lv.setVisibility(View.VISIBLE);
                        setSearchList(list);
                    }
                }
                else Toast.makeText(home_page.this,"云端查询失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setSearchList(List<login.user> list){
        //ListView lv=(ListView)findViewById(R.id.search_result_list);
        searchResultList.clear();
        for(int i=0;i<list.size();i++){
            Map<String,Object> item=new HashMap<>();
            item.put("name",list.get(i).getName());
            item.put("id",list.get(i).getObjectId());
            searchResultList.add(item);
        }
        adapter.notifyDataSetChanged();
        //Toast.makeText(this,"匹配数："+list.size(),Toast.LENGTH_SHORT).show();
    }
    public void addFriendToCloud(String userid,String fid){
        friends f=new friends();
        f.setId1(userid);
        f.setId2(fid);
        f.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null) Toast.makeText(home_page.this,"添加成功",Toast.LENGTH_SHORT).show();
                else Toast.makeText(home_page.this,"添加失败",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public class friends extends BmobObject {
        private String id1;
        private String id2;
        private String lastword;

        public void setId1(String id1) {
            this.id1 = id1;
        }
        public void setId2(String id2) {
            this.id2 = id2;
        }
        public void setLastword(String lastword) {
            this.lastword = lastword;
        }

        public String getId1() {
            return id1;
        }
        public String getId2() {
            return id2;
        }
        public String getLastword() {
            return lastword;
        }
    }

}


