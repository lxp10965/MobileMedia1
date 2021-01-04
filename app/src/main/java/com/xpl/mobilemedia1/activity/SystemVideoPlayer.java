package com.xpl.mobilemedia1.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.xpl.mobilemedia.R;

public class SystemVideoPlayer extends Activity {
    private Uri uri;
    private VideoView videoview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);

        videoview=findViewById(R.id.videoview);

        setListenter();

        uri = getIntent().getData();
        if (null!=uri){
            videoview.setVideoURI(uri);
        }


        videoview.setMediaController(new MediaController(this));



    }
    private void setListenter(){
        //准备好的监听
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                videoview.start();
                Toast.makeText(SystemVideoPlayer.this,"准备好了，开始播放！",Toast.LENGTH_SHORT).show();
            }
        });

        //播放异常的监听
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(SystemVideoPlayer.this,"播放错误！",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //播放完成的监听
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Toast.makeText(SystemVideoPlayer.this,"播放结束！",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
