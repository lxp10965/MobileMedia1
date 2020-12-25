package com.xpl.mobilemedia1.page;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xpl.mobilemedia1.base.BasePager;

public class VideoPage extends BasePager {

    private TextView textView;

    public Context mContext=null;

    public VideoPage(Context context) {
        super(context);
        this.mContext=context;
        Log.i("mContext", "VideoPage: "+mContext);
    }

    @Override
    public View initView() {
        Log.i("mContext", "VideoPage1: "+mContext);
        textView=new TextView(mContext);
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return null;
    }

    @Override
    public void initData() {
        super.initData();
//        textView.setText("VideoPage");
        Log.i("mContext", "VideoPage: "+mContext.getClass());
    }
}
