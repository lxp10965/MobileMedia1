package com.xpl.mobilemedia1.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.MediaController;
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

        uri = getIntent().getData();
        if (null!=uri){
            videoview.setVideoURI(uri);
        }

        videoview.setMediaController(new MediaController(this));


        
    }
}
