package com.xpl.mobilemedia1.page;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.xpl.mobilemedia1.base.BasePager;
import com.xpl.mobilemedia1.utils.LogUtil;

public class AudioPage extends BasePager {
    private TextView textView;
    public Context mContext = null;

    public AudioPage(Context context) {
        super(context);
        this.mContext = context;
        Log.e("TAG", "AudioPage: ======context=="+mContext+"====textView==" +textView);
    }

    @Override
    public View initView(Context context1) {
        Log.e("TAG", "initView: ======context=="+context1+"====textView==" +textView);
        textView = new TextView(context1);
        textView.setTextSize(30);
        textView.setTextColor(Color.RED);
//        textView = findViewById(R.id.audio_page);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地音频的数据被初始化了。。。");
        //联网
        //音频内容
        textView.setText("AudioPage");
        Log.i("mContext", "AudioPage: "+mContext.getClass());
    }
}
