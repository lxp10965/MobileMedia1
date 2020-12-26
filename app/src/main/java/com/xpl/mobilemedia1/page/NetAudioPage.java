package com.xpl.mobilemedia1.page;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xpl.mobilemedia1.base.BasePager;
import com.xpl.mobilemedia1.utils.LogUtil;


public class NetAudioPage extends BasePager {

    private TextView textView;

    public NetAudioPage(Context context) {
        super(context);
    }

    @Override
    public View initView(Context context1) {
        textView=new TextView(context1);
        textView.setTextSize(30);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地音频的数据被初始化了。。。");
        //联网
        //音频内容
        textView.setText("本地音频的内容");
        Log.i("mContext", "NetAudioPage: "+mContext.getClass());
    }

}
