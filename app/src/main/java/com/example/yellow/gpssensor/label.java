package com.example.yellow.gpssensor;

import android.content.Intent;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;



public class label extends AppCompatActivity {
    private static final String TAG = "allen";
    private Button end_button;
    private Button start_button;
    private BubbleView bezierView;
    private int[] tag = new int[6];
    public static int[] labelhight = new int[9];
    public static int[] labelwidth = new int[9];
    private Button label1;
    private Button label2;
    private Button label3;
    private Button label4;
    private Button label5;
    private Button label6;
    private Button label7;
    private Button label8;
    private Button label9;
    private List<CircleBean> circleBeanList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);
        label1 = (Button) findViewById(R.id.label1);
        label2 = (Button) findViewById(R.id.label2);
        label3 = (Button) findViewById(R.id.label3);
        label4 = (Button) findViewById(R.id.label4);
        label5 = (Button) findViewById(R.id.label5);
        label6 = (Button) findViewById(R.id.label6);
        label7 = (Button) findViewById(R.id.label7);
        label8 = (Button) findViewById(R.id.label8);
        label9 = (Button) findViewById(R.id.label9);

        bezierView = (BubbleView) findViewById(R.id.circle_view);
        end_button = (Button) findViewById(R.id.end_btn);
        start_button = (Button) findViewById(R.id.start_btn);
        bezierView.setCircleBeen(circleBeanList);
        initPoint();
        for(int i = 0; i < 6; i++){
            tag[i] = 0;
        }
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bezierView.openAnimation();
                findViewById(R.id.start_btn).setVisibility(View.GONE);
                end_button.setText("我选好了");
            }
        });
        end_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bezierView.stopAnimation();
                Intent intent = new Intent(label.this,home_page.class);
                intent.putExtra("user",getIntent().getStringExtra("user"));
                startActivity(intent);
                label.this.finish();
            }
        });
        label1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag[0] == 0){
                    tag[0] = 1;
                    label1.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                else{
                    tag[0] = 0;
                    label1.setTextColor(getResources().getColor(R.color.colorWhite));
                }
            }
        });
        label2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag[1] == 0){
                    tag[1] = 1;
                    label2.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                else{
                    tag[1] = 0;
                    label2.setTextColor(getResources().getColor(R.color.colorWhite));
                }
            }
        });
        label3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag[2] == 0){
                    tag[2] = 1;
                    label3.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                else{
                    tag[2] = 0;
                    label3.setTextColor(getResources().getColor(R.color.colorWhite));
                }
            }
        });
        label4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag[3] == 0){
                    tag[3] = 1;
                    label4.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                else{
                    tag[3] = 0;
                    label4.setTextColor(getResources().getColor(R.color.colorWhite));
                }
            }
        });
        label6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag[4] == 0){
                    tag[4] = 1;
                    label6.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                else{
                    tag[4] = 0;
                    label6.setTextColor(getResources().getColor(R.color.colorWhite));
                }
            }
        });
        label7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag[5] == 0){
                    tag[5] = 1;
                    label7.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                else{
                    tag[5] = 0;
                    label7.setTextColor(getResources().getColor(R.color.colorWhite));
                }

            }
        });


    }
    public int getHight(View view){
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredHeight());
    }

    public int getWidth(View view){
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredWidth());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }
    private void initPoint() {

        int height = DisplayUtils.getDisplayHight(this);
        int width = DisplayUtils.getDisplayWidth(this);

        int centerX = width / 2;
        int centerY = height / 2;


        Log.d(TAG, "initPoint: " + centerX + "----" + centerY);

        CircleBean circleBean = new CircleBean(
                new PointF((float) (-width / 5.1), (float) (height / 1.5)),
                new PointF((float) (centerX * 1.2), (float) (height * 0.6)),
                new PointF((float) (centerX * 1.06), (float) (centerY * 0.67)),
                new PointF(width / 6, centerY - 120),
                new PointF((float) (width / 7.2), -height / 128),
                (float) (width / 10.9), 60, label1);
        CircleBean circleBean2 = new CircleBean(
                new PointF(-width / 4, (float) (height / 1.3)),
                new PointF(centerX - 20, height * 3 / 5),
                new PointF((float) (centerX * 0.4), (float) (centerY * 0.8)),
                new PointF(width / 3, centerY - 10),
                new PointF(width / 4, (float) (-height / 5.3)),
                width / 10, 60, label2);
        CircleBean circleBean3 = new CircleBean(
                new PointF(-width / 12, (float) (height / 1.1)),
                new PointF(centerX - 100, height * 2 / 3),
                new PointF((float) (centerX * 0.7), (float) (centerY * 1.26)),
                new PointF(0, centerY + 100),
                new PointF(0, 0),
                width / 9, 60, label3);
        CircleBean circleBean4 = new CircleBean(
                new PointF(-width / 9, (float) (height / 0.9)),
                new PointF(centerX, height * 3 / 4),
                new PointF((float) (centerX * 1.4), (float) (centerY * 1.05)),
                new PointF(width / 2, centerY),
                new PointF((float) (width / 1.5), (float) (-height / 5.6)),
                (float) (width / 6.8), 60, label4);
        CircleBean circleBean5 = new CircleBean(
                new PointF((float) (width / 1.4), (float) (height / 0.9)),
                new PointF(centerX, height * 3 / 4),
                new PointF((float) (centerX * 0.93), (float) (centerY * 0.9)),
                new PointF(width * 10 / 13, centerY - 20),
                new PointF(width / 2, (float) (-height / 7.1)),
                (float) (width / 18), 60, label5);
        CircleBean circleBean6 = new CircleBean(
                new PointF((float) (width / 0.8), height),
                new PointF(centerX + 20, height * 2 / 3),
                new PointF((float) (centerX * 0.75), (float) (centerY * 0.43)),
                new PointF(width * 11 / 14, centerY + 10),
                new PointF((float) (width / 1.1), (float) (-height / 6.4)),
                (float) (width / 7.2), 60, label6);
        CircleBean circleBean7 = new CircleBean(
                new PointF((float) (width / 0.9), (float) (height / 1.2)),
                new PointF(centerX + 20, height * 4 / 7),
                new PointF((float) (centerX * 1.5), (float) (centerY * 0.56)),
                new PointF(width, centerY + 10),
                new PointF(width, 0),
                (float) (width / 10.5), 60, label7);
        CircleBean circleBean8 = new CircleBean(
                new PointF((float) (width / 1.4), (float) (height / 0.9)),
                new PointF(centerX, height * 3 / 4),
                new PointF((float) (centerX * 1.7), (float) (centerY * 0.8)),
                new PointF(width * 10 / 13, centerY - 20),
                new PointF(width / 2, (float) (-height / 7.1)),
                (float) (width / 20), 60, label8);
        CircleBean circleBean9 = new CircleBean(
                new PointF((float) (width / 1.4), (float) (height / 0.9)),
                new PointF(centerX, height * 3 / 4),
                new PointF((float) (centerX * 1.1), (float) (centerY * 1.34)),
                new PointF(width * 10 / 13, centerY - 20),
                new PointF(width / 2, (float) (-height / 7.1)),
                (float) (width / 25), 60, label9);

        circleBeanList.add(circleBean);
        circleBeanList.add(circleBean2);
        circleBeanList.add(circleBean3);
        circleBeanList.add(circleBean4);
        circleBeanList.add(circleBean5);
        circleBeanList.add(circleBean6);
        circleBeanList.add(circleBean7);
        circleBeanList.add(circleBean8);
        circleBeanList.add(circleBean9);

        labelhight[0] = getHight(label1);
        labelwidth[0] = getWidth(label1);
        labelhight[1] = getHight(label2);
        labelwidth[1] = getWidth(label2);
        labelhight[2] = getHight(label3);
        labelwidth[2] = getWidth(label3);
        labelhight[3] = getHight(label4);
        labelwidth[3] = getWidth(label4);
        labelhight[4] = getHight(label5);
        labelwidth[4] = getWidth(label5);
        labelhight[5] = getHight(label6);
        labelwidth[5] = getWidth(label6);
        labelhight[6] = getHight(label7);
        labelwidth[6] = getWidth(label7);
        labelhight[7] = getHight(label8);
        labelwidth[7] = getWidth(label8);
        labelhight[8] = getHight(label9);
        labelwidth[8] = getWidth(label9);

    }

}
