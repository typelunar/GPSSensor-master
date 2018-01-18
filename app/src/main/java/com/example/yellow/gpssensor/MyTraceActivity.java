package com.example.yellow.gpssensor;

//service-id:153058

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.trace.Trace;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.entity.AddEntityResponse;
import com.baidu.trace.api.entity.AroundSearchResponse;
import com.baidu.trace.api.entity.BoundSearchResponse;
import com.baidu.trace.api.entity.DeleteEntityResponse;
import com.baidu.trace.api.entity.DistrictSearchResponse;
import com.baidu.trace.api.entity.EntityListResponse;
import com.baidu.trace.api.entity.PolygonSearchResponse;
import com.baidu.trace.api.entity.SearchResponse;
import com.baidu.trace.api.entity.UpdateEntityResponse;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.fence.OnFenceListener;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.analysis.OnAnalysisListener;
import com.baidu.trace.api.bos.OnBosListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.TraceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.StreamHandler;

/**
 * Created by Yellow on 2017-11-9.
 */

public class MyTraceActivity extends AppCompatActivity{
    private long serviceId;// 轨迹服务ID
    private String entityName;// 设备标识
    private boolean isNeedObjectStorage;// 是否需要对象存储服务
    private int gatherInterval;// 定位周期(单位:秒)
    private int packInterval;// 打包回传周期(单位:秒)
    private int tag=1;// 请求标识
    private boolean isTracing=false;

    public Trace mTrace;
    public LBSTraceClient mTraceClient;
    public OnTrackListener trackListener;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        initTrace();

        startTrace();
    }
    public void initTrace(){
        serviceId=153058;
        entityName="myTrace";
        // 是否需要对象存储服务，默认为：false，关闭对象存储服务。
        // 注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，
        // 若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
        isNeedObjectStorage=false;
        // 初始化轨迹服务
        mTrace=new Trace(serviceId,entityName,isNeedObjectStorage);
        // 初始化轨迹服务客户端
        mTraceClient=new LBSTraceClient(getApplicationContext());

        gatherInterval=1;// 定位周期(单位:秒)
        packInterval=3;// 打包回传周期(单位:秒)
        // 设置定位和打包周期
        mTraceClient.setInterval(gatherInterval,packInterval);
        mTraceClient.setLocationMode(LocationMode.High_Accuracy);
        mTraceClient.setOnTraceListener(mTraceListener);

        final ImageButton ibtn=(ImageButton) findViewById(R.id.trace_button);
        ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv=(TextView)findViewById(R.id.trace_text);
                if(isTracing){
                    isTracing=false;
                    stopTrace();
                    tv.setText("录制轨迹");
                    ibtn.setBackgroundResource(R.drawable.trace_start);
                }
                else{
                    isTracing=true;
                    startTrace();
                    tv.setText("停止录制");
                    ibtn.setBackgroundResource(R.drawable.trace_stop);
                }
            }
        });
    }

    OnTraceListener mTraceListener=new OnTraceListener() {
        @Override
        public void onBindServiceCallback(int i, String s) {

        }

        @Override
        public void onStartTraceCallback(int i, String s) {

        }

        @Override
        public void onStopTraceCallback(int i, String s) {

        }

        @Override
        public void onStartGatherCallback(int i, String s) {

        }

        @Override
        public void onStopGatherCallback(int i, String s) {

        }

        @Override
        public void onPushCallback(byte b, PushMessage pushMessage) {

        }

        @Override
        public void onInitBOSCallback(int i, String s) {

        }
    };
    OnCustomAttributeListener mCustomAttributeListener=new OnCustomAttributeListener() {
        @Override
        public Map<String, String> onTrackAttributeCallback() {
            Map<String,String> trackAttrs=new HashMap<String,String>();

            String energy="67%";//伪电量
            trackAttrs.put("energy",energy);
            return trackAttrs;
        }

        @Override
        public Map<String, String> onTrackAttributeCallback(long l) {
            return null;
        }
    };
    public void startTrace(){
        mTraceClient.startTrace(mTrace,mTraceListener);// 开启服务
        mTraceClient.startGather(mTraceListener);// 开启采集
    }
    public void stopTrace(){
        mTraceClient.stopTrace(mTrace,mTraceListener);// 停止服务
        mTraceClient.stopGather(mTraceListener);// 停止采集
    }
    //以上四个listener必须相同,或者在一开始mTraceClient.setOnTraceListener(mTraceListener)设置在此不用传入


    public void queryHistoryTrace(){
        HistoryTrackRequest historyTrackRequest=new HistoryTrackRequest(tag,serviceId,entityName);
        long startTime=System.currentTimeMillis()/1000-12*60*60;// 开始时间(单位：秒)
        long endTime=System.currentTimeMillis()/1000;// 结束时间(单位：秒)
        historyTrackRequest.setStartTime(startTime);
        historyTrackRequest.setEndTime(endTime);
        int pageSize=1000;
        int pageIndex=1;


        OnTrackListener mTrackListener=new OnTrackListener() {
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse response){

            }
        };
    }
    public void queryCurrentTrace(){

        String entityNames="myTrace";//entity标识列表（多个entityName，以英文逗号"," 分割）
        String columnKey="car_team=1";//检索条件（格式为 : "key1=value1,key2=value2,....."）
        int returnType=0;//返回结果的类型（0 : 返回全部结果，1 : 只返回entityName的列表）
        int activeTime=(int)(System.currentTimeMillis()/1000-12*60*60);
        int pageSize=100;//分页大小
        int pageIndex=1;//分页索引
        OnEntityListener entityListener=new OnEntityListener() {

        };

    }
    private void initOnTrackListener(){
        trackListener=new OnTrackListener() {
        };
    }
}
