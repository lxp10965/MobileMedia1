package com.xpl.mobilemedia1.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RadioGroup;

import com.xpl.mobilemedia.R;
import com.xpl.mobilemedia1.fragment.MyFragment;


public class MainActivity extends FragmentActivity {

    private RadioGroup rg_bottom_tag;

    private int p;

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);
        //设置RadioGroup监听
        rg_bottom_tag.setOnCheckedChangeListener(new MysetOnCheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_video);
        requestMyPermissions();
    }

    private void requestMyPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d("TAG", "requestMyPermissions: 有写SD权限");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d("TAG", "requestMyPermissions: 有读SD权限");
        }
    }


    private class MysetOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId) {
                default:
                case R.id.rb_video:
                    p = 0;
                    break;
                case R.id.rb_music:
                    p = 1;
                    break;
                case R.id.rb_net_video:
                    p = 2;
                    break;
                case R.id.rb_net_music:
                    p = 3;
                    break;

            }
            setFragment();
        }
    }

    private void setFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //3.替换
        ft.replace(R.id.fl_main_content, new MyFragment(this, p));
        //4.提交事务
        ft.commit();
    }


}