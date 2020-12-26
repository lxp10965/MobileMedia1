package com.xpl.mobilemedia1.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * * 作用：基类，公共类，
 *  * VideoPager
 *  * <p/>
 *  * AudioPager
 *  * <p/>
 *  * NetVideoPager
 *  * <p/>
 *  * NetAudioPager
 *  * 都继承该类
 */
public abstract class BasePager {

    public final Context mContext;//


    public View rootView; //接收各个页面的实例

    public boolean isInitData; // 只初始化一次数据

    public BasePager(Context context) {
        mContext = context;
        rootView = initView(mContext);
    }

    /**
     * 强制子页面实现该方法，实现想要的特定的效果
     * @return
     */
    public abstract View initView(Context mContext);

    /**
     * 当子页面，需要绑定数据，或者联网请求数据并且绑定的时候，重写该方法
     */
    public void initData(){

    }
}

