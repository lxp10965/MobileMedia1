package com.xpl.mobilemedia1.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xpl.mobilemedia1.base.BasePager;
import com.xpl.mobilemedia1.page.AudioPage;
import com.xpl.mobilemedia1.page.NetAudioPage;
import com.xpl.mobilemedia1.page.NetVideoPage;
import com.xpl.mobilemedia1.page.VideoPage;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class MyFragment extends Fragment {

    private ArrayList<BasePager> basePages;

    private int position;

    public Context mContext = null;

    public MyFragment(Context context, int position) {
        super();
        this.mContext = context;
        this.position = position;
        Log.e("TAG", "MyFragment: ======context=="+mContext+"====textView==");

        addPage();

        Log.i("mContext", "MyFragment: " + context.getClass());
    }

    private void addPage() {
        basePages = new ArrayList<>();
        basePages.add(new VideoPage(mContext));//添加本地视频页面 - 0
        basePages.add(new AudioPage(mContext));//添加本地视频页面 - 1
        basePages.add(new NetAudioPage(mContext));//添加本地视频页面 - 2
        basePages.add(new NetVideoPage(mContext));//添加本地视频页面 - 3
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private BasePager getBasePager() {
        BasePager basePager = basePages.get(getPosition());
        if (basePager != null && !basePager.isInitData) {
            basePager.initData();//联网请求或者绑定数据
            basePager.isInitData = true;
        }
        return basePager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BasePager basePager = getBasePager();
        if (basePager != null) {
            //各个页面的视图
            return basePager.rootView;
        }
        return null;
    }
}
