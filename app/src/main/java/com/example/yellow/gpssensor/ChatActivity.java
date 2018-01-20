package com.example.yellow.gpssensor;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by asus2 on 2018/1/2.
 */

public class ChatActivity extends AppCompatActivity {
    private ChatViewAdapter listView_adapter;
    private MYSQL sql;
    private List<ChatMsg> mychatMsg = new ArrayList<>();

    private BmobRealTimeData rtd;
    private Thread msgThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sql=new MYSQL(this);
        initCloudChatDB();
        init_chat();
    }
    class iitem{
        String I_word;
        String Other_word;
        String I_c;
        String Other_c;
        String I_icon;
        String Other_icon;
    }



    public void initCloudChatDB(){
        //msgThread=new Thread(new Runnable() {
            //@Override
           // public void run() {
                BmobRealTimeData rtd=new BmobRealTimeData();
                rtd.start(new ValueEventListener() {
                    @Override
                    public void onConnectCompleted(Exception e) {
                        //Toast.makeText(ChatActivity.this,"连接数据库成功",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onDataChange(JSONObject jsonObject) {
                        try {
                            ChatMsg chatMsg=new ChatMsg();
                            chatMsg.setMsg(jsonObject.getString("msg"));
                            chatMsg.setS_id(jsonObject.getString("s_id"));
                            chatMsg.setR_id(jsonObject.getString("r_id"));
                            listView_adapter.mList.add(chatMsg);
                            listView_adapter.notifyDataSetChanged();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
               });
                if(rtd.isConnected()) rtd.subTableUpdate("ChatMsg");
            //}
       // });
        //msgThread.start();
    }
    public void init_chat()
    {
        listView_adapter = new ChatViewAdapter(ChatActivity.this,mychatMsg);
        final Intent intent =getIntent();
        final RecyclerView listView=(RecyclerView)findViewById(R.id.list_view);
        listView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        listView.setAdapter(listView_adapter);
        queryFromCloud(intent.getStringExtra("user_id"),intent.getStringExtra("friend_id"));
        final EditText chatedit = (EditText)findViewById(R.id.edit_message);
        final Button send =(Button)findViewById(R.id.send_btn);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sql.new_chat(intent.getStringExtra("user_id"),intent.getStringExtra("friend_id"),chatedit.getText().toString());
                addChatMsgToCloud(intent.getStringExtra("user_id"),intent.getStringExtra("friend_id"),chatedit.getText().toString());
                chatedit.setText("");
            }
        });
    }
    public class List_ViewHolder extends RecyclerView.ViewHolder {

        private TextView I_word;
        private TextView Other_word;
        private CardView I_c;
        private CardView Other_c;
        private ImageView I_icon;
        private ImageView Other_icon;
        public List_ViewHolder(View view) {
            super(view);
            I_word = (TextView)view.findViewById(R.id.send_message);
            Other_word =(TextView)view.findViewById(R.id.get_message);
            I_c = (CardView)view.findViewById(R.id.my_word);
            Other_c =(CardView)view.findViewById(R.id.others_word);
            I_icon =(ImageView) view.findViewById(R.id.avatar_my);
            Other_icon =(ImageView) view.findViewById(R.id.avatar_other);
        }
        public void bindData(ChatMsg position) {
            if(getIntent().getStringExtra("user_id").equals(position.getS_id()))
            {
                this.Other_icon.setVisibility(View.INVISIBLE);
                this.Other_c.setVisibility(View.INVISIBLE);
                this.I_icon.setVisibility(View.VISIBLE);
                this.I_c.setVisibility(View.VISIBLE);
                this.I_word.setText(position.getMsg());
                Uri ui=Uri.parse(sql.get_user_icon(position.getS_id()));
                this.I_icon.setImageURI(ui);
            }
            else
            {
                Uri ui=Uri.parse(sql.get_user_icon(position.getR_id()));
                this.Other_icon.setVisibility(View.VISIBLE);
                this.Other_c.setVisibility(View.VISIBLE);
                this.I_icon.setVisibility(View.INVISIBLE);
                this.I_c.setVisibility(View.INVISIBLE);
                this.Other_icon.setImageURI(ui);
                this.Other_word.setText(position.getMsg());
            }
        }
    }
    public interface OnItemClickListener {
        void onClick(int position);
        void onLongClick(int position);
    }
    public class ChatViewAdapter extends RecyclerView.Adapter<List_ViewHolder>  {
        public List<ChatMsg> mList;
        private Context mContext;
        private OnItemClickListener myonItemClickListener = null;
        public ChatViewAdapter(Context context, List<ChatMsg> list) {
            this.mContext = context;
            this.mList = list;
        }
        @Override
        public List_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
            return new List_ViewHolder(itemView);
        }
        public ChatMsg getItem(int position) {
            return mList.get(position);
        }
        @Override
        public void onBindViewHolder(final List_ViewHolder holder, int position) {
            holder.bindData(getItem(position));
            if (myonItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myonItemClickListener.onClick(holder.getAdapterPosition());
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        myonItemClickListener.onLongClick(holder.getAdapterPosition());
                        return false;
                    }
                });
            }
        }
        @Override
        public int getItemCount() {
            return mList.size();
        }
        public void setOnItemClickListener(OnItemClickListener OnItemClickListener) {
            this.myonItemClickListener = OnItemClickListener;
        }
    }
    public void backToGroup(View view){
        Intent intent=new Intent(this,GroupActivity.class);
        startActivity(intent);
        //this.finish();
    }


    public void addChatMsgToCloud(String sendId,String receiveId,String msg){
        ChatMsg chatMsg=new ChatMsg();
        chatMsg.setMsg(msg);
        chatMsg.setR_id(receiveId);
        chatMsg.setS_id(sendId);
        chatMsg.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null) Toast.makeText(ChatActivity.this,"成功发布到云端",Toast.LENGTH_SHORT).show();
                else Toast.makeText(ChatActivity.this,"同步到云端失败",Toast.LENGTH_SHORT).show();

                queryFromCloud(getIntent().getStringExtra("user_id"),getIntent().getStringExtra("friend_id"));
            }
        });
    }
    public void queryFromCloud(String s_id,String r_id){
        //BmobQuery<ChatMsg> query=new BmobQuery<ChatMsg>();
        //String bql="select * from ChatMsg where s_id = 1";//+s_id+" and r_id = "+r_id;// +" or  s_id = "+r_id+" and r_id = "+s_id;
        //Toast.makeText(ChatActivity.this,"123",Toast.LENGTH_SHORT).show();
        //query.setSQL(bql);
        BmobQuery<ChatMsg> cond1=new BmobQuery<>();
        cond1.addWhereEqualTo("s_id",s_id);
        cond1.addWhereEqualTo("r_id",r_id);
        cond1.setLimit(50);//返回条目数量，不设置默认10
        cond1.findObjects(new FindListener<ChatMsg>() {
            @Override
            public void done(List<ChatMsg> list, BmobException e) {
                if(e==null){
                    //List<ChatMsg> list=bmobQueryResult.getResults();
                    //Toast.makeText(ChatActivity.this,"expt is null",Toast.LENGTH_SHORT).show();
                    if(list!=null/*&&list.size()>0*/){
                        //resultOut(list);
                        listView_adapter.mList=list;
                        listView_adapter.notifyDataSetChanged();
                        Toast.makeText(ChatActivity.this,list.get(0).getMsg(),Toast.LENGTH_SHORT).show();
                    }
                }
                else Toast.makeText(ChatActivity.this,"云端查询失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

/*        query.doSQLQuery(new SQLQueryListener<ChatMsg>() {
            @Override
            public void done(BmobQueryResult<ChatMsg> bmobQueryResult, BmobException e) {
                if(e==null){
                    List<ChatMsg> list=bmobQueryResult.getResults();
                    Toast.makeText(ChatActivity.this,mychatMsg.get(0).getMsg(),Toast.LENGTH_SHORT).show();
                    if(list!=null*//*&&list.size()>0*//*){
                        //resultOut(list);
                        mychatMsg=list;
                    }
                }
                else Toast.makeText(ChatActivity.this,"云端查询失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
                listView_adapter.notifyDataSetChanged();
            }
        });*/
    }
    public void resultOut(List<ChatMsg> l){
        mychatMsg= l;
        Toast.makeText(ChatActivity.this,mychatMsg.get(0).getMsg(),Toast.LENGTH_SHORT).show();
    }


    public class ChatMsg extends BmobObject {
        private String s_id;//id
        private String r_id;//id
        private String msg;

        public String getMsg() {
            return msg;
        }

        public String getR_id() {
            return r_id;
        }

        public String getS_id() {
            return s_id;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void setR_id(String receive) {
            this.r_id = receive;
        }
        public void setS_id(String send) {
            this.s_id=send;
        }
    }
}