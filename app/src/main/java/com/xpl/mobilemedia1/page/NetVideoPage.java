package com.xpl.mobilemedia1.page;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xpl.mobilemedia1.base.BasePager;


public class NetVideoPage extends BasePager {

    private TextView textView;
    public Context mContext;
    public NetVideoPage(Context context) {
        super(context);
        this.mContext=context;

    }

    @Override
    public View initView() {
        textView=new TextView(mContext);
        textView.setTextSize(30);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);

        return null;
    }

    @Override
    public void initData() {
        super.initData();
        textView.setText("NetVideoPage");
        Log.i("mContext", "NetVideoPage: "+mContext.getClass());
    }
}
