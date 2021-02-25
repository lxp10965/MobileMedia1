package com.xpl.startallplayer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_start_net;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start_net = findViewById(R.id.btn_start_net);
    }

    public void startAllPlayer(View view){
        Intent intent = new Intent();
//        intent.setDataAndType(Uri.parse("http://192.168.110.74/wodemeng.mp4"),"video/*");
        intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2016/07/19/mp4/160719095812990469.mp4"),"video/*");
        startActivity(intent);
    }
}
