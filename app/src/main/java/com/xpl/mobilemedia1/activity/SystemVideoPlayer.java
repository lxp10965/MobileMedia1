package com.xpl.mobilemedia1.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.xpl.mobilemedia.R;
import com.xpl.mobilemedia1.utils.LogUtil;
import com.xpl.mobilemedia1.utils.Utils;

public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    private Uri uri;
    private VideoView videoview;

    private static final int PROGRESS = 1;

    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private SeekBar seekbarVoice;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btn_video_start_pause;
    private Button btnvideopre;
    private Button btn_video_next;
    private Button btn_video_siwch_screen;

    private Utils utils;

    private MyReceiver receiver;

    private void initData() {
        utils = new Utils();
        //动态注册广播  电量            有注册就要有释放
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //当电量变化的时候发这个广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);
    }


    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0); // 拿到电量 0 ~ 100
            setBattery(level);
        }
    }

    /**
     * 设置电量图片
     *
     * @param level
     */
    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        videoview = findViewById(R.id.videoview);
        findViews();
        setListenter();

        uri = getIntent().getData();
        if (null != uri) {
            videoview.setVideoURI(uri);
        }

        //调起系统面板
//        videoview.setMediaController(new MediaController(this));

        initData();
    }


    private void findViews() {
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        findViewById(R.id.btn_voice).setOnClickListener(this);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        findViewById(R.id.btn_swich_player).setOnClickListener(this);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        btnvideopre = findViewById(R.id.btn_video_pre);
        btnvideopre.setOnClickListener(this);
        btn_video_start_pause = findViewById(R.id.btn_video_start_pause);
        btn_video_start_pause.setOnClickListener(this);
        btn_video_next = findViewById(R.id.btn_video_next);
        btn_video_next.setOnClickListener(this);
        btn_video_siwch_screen = findViewById(R.id.btn_video_siwch_screen);
        btn_video_siwch_screen.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_voice:
                //TODO implement
                break;
            case R.id.btn_swich_player:
                //TODO implement
                break;
            case R.id.btn_exit:
                //TODO implement
                break;
            case R.id.btn_video_pre:
                //TODO implement
                break;
            case R.id.btn_video_start_pause:
                //播放暂停按键
                if (videoview.isPlaying()) {
                    //视频在播放==设置暂停
                    videoview.pause();
                    //设置按钮播放状态
                    btn_video_start_pause.setBackgroundResource(R.drawable.btn_video_play_selector);
                } else {
                    //视频在暂停==设置播放
                    videoview.start();
                    //设置按钮暂停状态
                    btn_video_start_pause.setBackgroundResource(R.drawable.btn_video_pause_selector);
                }

                break;
            case R.id.btn_video_next:
                //TODO implement
                break;
            case R.id.btn_video_siwch_screen:
                //TODO implement
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    //1.获取当前进度
                    int currentPosition = videoview.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };

    private void setListenter() {
        //准备好的监听
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //进入视频就播
                videoview.start();
//                Toast.makeText(SystemVideoPlayer.this, "准备好了，开始播放！", Toast.LENGTH_SHORT).show();
                //1.获取视频总时长
                int duration = videoview.getDuration();
                tvDuration.setText(utils.stringForTime(duration));
                seekbarVideo.setMax(duration);
                //2.发消息

                handler.sendEmptyMessage(PROGRESS);
            }
        });


        //播放异常的监听
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(SystemVideoPlayer.this, "播放错误！", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //播放完成的监听
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Toast.makeText(SystemVideoPlayer.this, "播放结束！", Toast.LENGTH_SHORT).show();
            }
        });

        //监听seekbar
        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 当手指滑动的时候，会引起SeekBar进度变化，会回调这个方法
             * @param seekBar
             * @param progress
             * @param fromUser 如果是用户引起的true,不是用户引起的false
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoview.seekTo(progress);
                }
            }

            /**
             * 当手指触碰时回调
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            /**
             * 当手指离开的时候回调
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("onRestart--");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart--");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume--");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause--");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop--");
    }

    @Override
    protected void onDestroy() {

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        super.onDestroy();
        LogUtil.e("onDestroy--");
    }
}
