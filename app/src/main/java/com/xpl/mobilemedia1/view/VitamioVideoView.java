package com.xpl.mobilemedia1.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;


public class VitamioVideoView extends io.vov.vitamio.widget.VideoView {
    /**
     * 在代码中创建实例
     * @param context
     */
    public VitamioVideoView(Context context) {
        this(context,null);
    }

    /**
     * 当这个类在布局文件时候调用，系统通过该构造方法实例化该类
     * @param context
     * @param attrs
     */
    public VitamioVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 当需要设置样式的时候调用
     */
    public VitamioVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);

    }

    /**
     * 设置视频的宽和高 \n
     * @param videoWidth
     * @param videoHeight
     */
    public void setVideoSize(int videoWidth,int videoHeight){
        ViewGroup.LayoutParams params=getLayoutParams();
        params.width=videoWidth;
        params.height=videoHeight;
        setLayoutParams(params);
    }



}
