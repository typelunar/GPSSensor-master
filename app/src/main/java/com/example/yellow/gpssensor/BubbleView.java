package com.example.yellow.gpssensor;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;



import java.util.ArrayList;
import java.util.List;

import static com.example.yellow.gpssensor.label.labelhight;
import static com.example.yellow.gpssensor.label.labelwidth;

/**
 * Created by allen on 2016/12/9.
 * <p>
 * 气泡动画
 */

public class BubbleView extends View {


    private static final String TAG = "allen";

    private int colors[] = new int[2];
    private float positions[] = new float[2];
    private int amplitude = 10;//振幅
    private int height;
    private int width;
    private Paint paint;
    private List<CircleBean> mCircleBeen = new ArrayList<>();
    private List<PathMeasure> pathMeasures = new ArrayList<>();
    private AnimatorSet animatorSet;

    public Button getlabel(int i) {
        return mCircleBeen.get(i).getLabel();
    }

    public List<CircleBean> getCircleBeen() {
        return mCircleBeen;
    }

    public void setCircleBeen(List<CircleBean> circleBeen) {
        this.mCircleBeen = circleBeen;
    }

    public OnBubbleAnimationListener onBubbleAnimationListener;

    public void setOnBubbleAnimationListener(OnBubbleAnimationListener onBubbleAnimationListener) {
        this.onBubbleAnimationListener = onBubbleAnimationListener;
    }

    public abstract static class OnBubbleAnimationListener {

        public abstract void onCompletedAnimationListener();

    }

    public BubbleView(Context context) {
        super(context);
        init();
    }

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        animatorSet = new AnimatorSet();
        initPaint();
        initShader();
    }

    /**
     * 初始化圆渐变色的LinearGradient的colors和positions
     */
    private void initShader() {
        colors[0] = getResources().getColor(R.color.circle_start);
        colors[1] = getResources().getColor(R.color.circle_end);
        positions[0] = 0;
        positions[1] = 1;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(30);
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.circle_start));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (animatorSet.isStarted()) {
            if (mCircleBeen != null) {
                for (CircleBean circleBean : getCircleBeen()) {
                    //paint.setShader(getColorShader(circleBean));
                    //paint.setAlpha(circleBean.getAlpha());
                    canvas.drawCircle(circleBean.getP().x, circleBean.getP().y, circleBean.getRadius(), paint);
                }
            }
        }
    }

    /**
     * 获取圆的渐变色
     *
     * @param circleBean 传入每个圆的bean对象
     * @return 返回Shader
     */
    private Shader getColorShader(CircleBean circleBean) {

        float x0 = circleBean.getP().x - circleBean.getRadius();
        float y0 = circleBean.getP().y;

        float x1 = circleBean.getP().x + circleBean.getRadius();
        float y1 = circleBean.getP().y;


        Shader shader = new LinearGradient(x0, y0, x1, y1, colors, positions, Shader.TileMode.MIRROR);
        return shader;
    }


    /**
     * 开启动画
     */
    public void openAnimation() {
        if (!animatorSet.isRunning()) {
            animatorSet.play(floatAnimation()).after(inAnimation());
            animatorSet.start();
        }
    }

    /**
     * 关闭动画
     */
    public void stopAnimation() {
        if (animatorSet.isRunning()) {
            animatorSet.play(outAnimation());
            animatorSet.start();
        }
    }

    /**
     * 启动动画
     *
     * @return 返回ValueAnimator对象
     */
    public ValueAnimator inAnimation() {

        ValueAnimator startAnimator = ValueAnimator.ofFloat(0, 1);
        startAnimator.setDuration(800);
        startAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        startAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = (Float) valueAnimator.getAnimatedValue();
                for (int i = 0; i < mCircleBeen.size(); i++) {
                    CircleBean c = mCircleBeen.get(i);
                    PointF pointF = BezierUtil.CalculateBezierPointForQuadratic(t, c.getP0(), c.getP1(), c.getP2());
                    mCircleBeen.get(i).setP(pointF);
                    mCircleBeen.get(i).getLabel().setX(pointF.x-labelwidth[i]/2);
                    mCircleBeen.get(i).getLabel().setY(pointF.y-labelhight[i]/2);
                    c.setAlpha((int) (t * 100));
                    if (t > 0.5) {
                        setCenterViewAlpha(t,i);
                    }
                    else {
                        setCenterViewAlpha(0,i);
                    }
                }

                invalidate();
            }
        });
        return startAnimator;
    }


    /**
     * 上下左右浮动效果动画
     *
     * @return 返回ValueAnimator
     */
    private ValueAnimator floatAnimation() {

        final float[] pos = new float[2];
        final float[] tan = new float[2];

        for (int i = 0; i < mCircleBeen.size(); i++) {
            Path path = new Path();
            if (i % 2 == 0) {
                path.addCircle(mCircleBeen.get(i).getP2().x - amplitude, mCircleBeen.get(i).getP2().y, amplitude, Path.Direction.CCW);
            } else {
                path.addCircle(mCircleBeen.get(i).getP2().x - amplitude, mCircleBeen.get(i).getP2().y, amplitude, Path.Direction.CW);
            }
            PathMeasure pathMeasure = new PathMeasure();
            pathMeasure.setPath(path, true);
            pathMeasures.add(pathMeasure);
        }

        ValueAnimator floatAnimator = ValueAnimator.ofFloat(0, 1);
        floatAnimator.setDuration(3000);
        floatAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = (float) valueAnimator.getAnimatedValue();

                for (int i = 0; i < mCircleBeen.size(); i++) {
                    pathMeasures.get(i).getPosTan(pathMeasures.get(i).getLength() * t, pos, tan);
                    mCircleBeen.get(i).setP(new PointF(pos[0], pos[1]));
                    mCircleBeen.get(i).getLabel().setX(pos[0]-labelwidth[i]/2);
                    mCircleBeen.get(i).getLabel().setY(pos[1]-labelhight[i]/2);
                    setCenterViewAlpha(5,i);
                }
                invalidate();
            }
        });
        floatAnimator.setRepeatCount(ValueAnimator.INFINITE);
        return floatAnimator;
    }

    /**
     * 结束动画
     *
     * @return 返回ValueAnimator对象
     */
    private ValueAnimator outAnimation() {
        ValueAnimator outAnimator = ValueAnimator.ofFloat(0, 1);
        outAnimator.setDuration(600);
        outAnimator.setInterpolator(new DecelerateInterpolator());
        outAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = (Float) valueAnimator.getAnimatedValue();
                for (int i = 0; i < mCircleBeen.size(); i++) {
                    CircleBean c = mCircleBeen.get(i);
                    PointF pointF = BezierUtil.CalculateBezierPointForQuadratic(t, c.getP2(), c.getP3(), c.getP4());
                    mCircleBeen.get(i).setP(pointF);
                    c.setAlpha((int) ((1 - t) * 100));
                    setCenterViewAlpha(0,i);
                }
                invalidate();
                if (1==t){
                    if (onBubbleAnimationListener!=null){
                        onBubbleAnimationListener.onCompletedAnimationListener();
                    }
                }
            }
        });
        return outAnimator;
    }

    private void setCenterViewAlpha(float alpha, int i) {
        if (getlabel(i)!=null){
            getlabel(i).setAlpha(alpha);
        }
    }


    /**
     * 设置上下左右浮动的振幅
     *
     * @param amplitude 振幅值
     * @return 返回
     */
    private BubbleView setAmplitude(int amplitude) {
        this.amplitude = amplitude;
        return this;
    }

}
