package com.xpl.mobilemedia1.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xpl.mobilemedia.R;
import com.xpl.mobilemedia1.domain.MediaItem;
import com.xpl.mobilemedia1.utils.LogUtil;
import com.xpl.mobilemedia1.utils.Utils;
import com.xpl.mobilemedia1.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    private Uri uri;
    private VideoView videoview;
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
    private Button btn_video_pre;
    private Button btn_video_next;
    private Button btn_video_siwch_screen;
    private Utils utils;
    /*
    检测电量
     */
    private static final int PROGRESS = 1;
    /**
     * 隐藏控制面板
     */
    private static final int HIDE_MEDIACONTROLLER = 2;
    /**
     * 默认视频宽高
     */
    private static final int DEFAULT_SCREEN = 1;
    /**
     * 全屏视频宽高
     */
    private static final int FULL_SCREEN = 2;

    /**
     * 当前视频播放的进度
     */
    public static int mProgress = 0;

    /**
     * 广播接收者
     */
    private MyReceiver receiver;

    /**
     * 传递视频列表进来 mediaItems
     */
    private ArrayList<MediaItem> mediaItems;

    /**
     * 得到播放列表的具体位置
     */
    private int position;
    /**
     * 控制面板
     */
    private RelativeLayout media_controller;

    /**
     * 手势识别器，并且重写双击，点击，长按
     */
    private GestureDetector detector;

    /**
     * 是否显示控制面板
     */
    private boolean isShowMediaController = false;
    /**
     * 是否全屏
     */
    private boolean isFullScreen = false;
    /**
     * 屏幕的宽
     */
    private int screenWidth = 0;
    /**
     * 屏幕的高
     */
    private int screenHeight = 0;
    /**
     * 视频默认的宽
     */
    int videoWidth = 0;
    /**
     * 视频默认的高
     */
    int videoHeight = 0;
    /**
     * 声音状态,是否静音
     */
    private boolean isMute;

    /**
     * 音频管理对象
     */
    private AudioManager audioManager;

    /**
     * 当前声音
     */
    private int currentVolume;

    /**
     * 最大声音
     */
    private int maxVolume;

    private void initData() {
        utils = new Utils();
        //动态注册广播  电量            有注册就要有释放
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //当电量变化的时候发这个广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);

        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this, "我被长按了", Toast.LENGTH_SHORT).show();
                startAndPause();
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this, "我被双击了", Toast.LENGTH_SHORT).show();
                //全屏或默认
                setFullScreenAndDefault();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this, "我被单击了", Toast.LENGTH_SHORT).show();
                if (isShowMediaController) {
                    //隐藏
                    hideMediaController();
                    handler.removeMessages(HIDE_MEDIACONTROLLER);
                } else {
                    //显示
                    showMediaController();
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);

                }
                return super.onSingleTapConfirmed(e);
            }
        });

        //得到屏幕的宽和高
        //过时的方式
//        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
//        screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        //得到屏幕宽高的新方式
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //得到系统音量
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //最大音量与进度条关联
        seekbarVoice.setMax(maxVolume);
        //把当前音量设到进度条
        seekbarVoice.setProgress(currentVolume);

    }

    /**
     * 设置全屏或者默认
     */
    private void setFullScreenAndDefault() {
        if (isFullScreen) {
            //如果是全屏，就设为默认
            setVideoType(DEFAULT_SCREEN);
        } else {
            //如果是默认就设为全屏
            setVideoType(FULL_SCREEN);
        }
    }

    /**
     * @param defaultScreen
     */
    private void setVideoType(int defaultScreen) {
        switch (defaultScreen) {
            case FULL_SCREEN:
                //设置全屏
                videoview.setVideoSize(screenWidth, screenHeight);
                //设置按钮状态
                btn_video_siwch_screen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN:
                //设置默认视频大小

                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                int width = screenWidth;
                int height = screenHeight;

                // 为了兼容，我们根据长宽比调整大小
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoview.setVideoSize(width, height);
                btn_video_siwch_screen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
                isFullScreen = false;
                break;
        }
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
        getData();
        setData();

        //调起系统面板
//        videoview.setMediaController(new MediaController(this));

        initData();
    }

    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            videoview.setVideoPath(mediaItem.getData());
        } else if (null != uri) {
            videoview.setVideoURI(uri);
            tvName.setText(uri.toString());
        } else {
            Toast.makeText(SystemVideoPlayer.this, "没有视频", Toast.LENGTH_SHORT).show();
        }
        setButtonState();
        hideMediaController();
    }

    private void getData() {
        //得到播放地址
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
    }


    private void findViews() {
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btn_video_pre = findViewById(R.id.btn_video_pre);
        btn_video_start_pause = findViewById(R.id.btn_video_start_pause);
        btn_video_next = findViewById(R.id.btn_video_next);
        btn_video_siwch_screen = findViewById(R.id.btn_video_siwch_screen);
        media_controller = findViewById(R.id.media_controller);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_voice:
                //静音键
                isMute = !isMute;
                updateVolume(currentVolume, isMute);
                break;
            case R.id.btn_swich_player:
                //TODO implement
                break;
            case R.id.btn_exit:
                //TODO implement
                finish();
                break;
            case R.id.btn_video_pre:
                //上一首
                playPreVideo();
                break;
            case R.id.btn_video_start_pause:
                //暂停播放
                startAndPause();
                break;
            case R.id.btn_video_next:
                //下一首
                playNextVideo();
                break;
            case R.id.btn_video_siwch_screen:
                //切换全屏或默认
                setFullScreenAndDefault();
                break;
        }
        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
    }

    private void playPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position--;
            if (position < mediaItems.size()) {
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        } else if (uri != null) {
            ////设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
        }
    }

    private void playNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position++;
            if (position < mediaItems.size()) {
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        } else if (uri != null) {
            ////设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
        }
    }

    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (mediaItems.size() == 1) {
                //上下曲按钮不可点
                setEnable(false);
            } else if (mediaItems.size() == 2) {
                if (position == 0) {
                    //上一曲不可点
                    btn_video_pre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btn_video_pre.setEnabled(false);
                    btn_video_next.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btn_video_next.setEnabled(true);//可点
                } else if (position == mediaItems.size() - 1) {
                    //下一曲不可点
                    btn_video_pre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btn_video_pre.setEnabled(true);
                    btn_video_next.setBackgroundResource(R.drawable.btn_next_gray);
                    btn_video_next.setEnabled(false);//不可点
                }
            } else {
                if (position == 0) {
                    //上一曲不可点
                    btn_video_pre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btn_video_pre.setEnabled(false);
                } else if (position == mediaItems.size() - 1) {
                    //下一曲不可点
                    btn_video_next.setBackgroundResource(R.drawable.btn_next_gray);
                    btn_video_next.setEnabled(false);//不可点
                } else {
                    setEnable(true);
                }
            }
        } else {
            if (uri != null) {
                setEnable(false);
            }
        }
    }

    private void setEnable(Boolean enable) {
        if (enable) {
            btn_video_pre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btn_video_pre.setEnabled(true);
            btn_video_next.setBackgroundResource(R.drawable.btn_video_next_selector);
            btn_video_next.setEnabled(true);
        } else {
            //上下曲按钮不可点
            btn_video_pre.setBackgroundResource(R.drawable.btn_pre_gray);
            btn_video_pre.setEnabled(false);
            btn_video_next.setBackgroundResource(R.drawable.btn_next_gray);
            btn_video_next.setEnabled(false);
        }
    }

    private void startAndPause() {
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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
//        showMediaController();

        return super.onTouchEvent(event);
    }

    /**
     * 隐藏控制面板
     */
    private void showMediaController() {
        media_controller.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }

    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        media_controller.setVisibility(View.GONE);
        isShowMediaController = false;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDE_MEDIACONTROLLER:
                    hideMediaController();
                    break;
                case PROGRESS:
                    //1.获取当前进度
                    int currentPosition = videoview.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    //设置系统时间
                    tvSystemTime.setText(getSystemTime());

                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };


    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        return format.format(new Date());
    }

    private void setListenter() {

        findViewById(R.id.btn_exit).setOnClickListener(this);
        findViewById(R.id.btn_voice).setOnClickListener(this);
        findViewById(R.id.btn_swich_player).setOnClickListener(this);
        btn_video_pre.setOnClickListener(this);
        btn_video_start_pause.setOnClickListener(this);
        btn_video_next.setOnClickListener(this);
        btn_video_siwch_screen.setOnClickListener(this);

        //准备好的监听
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();
                //进入视频就播
//                videoview.start();
//                Toast.makeText(SystemVideoPlayer.this, "准备好了，开始播放！", Toast.LENGTH_SHORT).show();
                startAndPause();
                //1.获取视频总时长
                int duration = videoview.getDuration();
                tvDuration.setText(utils.stringForTime(duration));
                seekbarVideo.setMax(duration);

                //2.发消息
                handler.sendEmptyMessage(PROGRESS);

                //设置视频宽高
//                videoview.setVideoSize(500,500);
                //设置视频的真实宽高
//                videoview.setVideoSize(mp.getVideoWidth(),mp.getVideoHeight());

                setVideoType(DEFAULT_SCREEN);
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
                playNextVideo();
                if (position == mediaItems.size() - 1) {
                    Toast.makeText(SystemVideoPlayer.this, "播放结束！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //监听视屏进度条seekbar
        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 当手指滑动的时候，会引起SeekBar进度变化，会回调这个方法
             * @param seekBar
             * @param progress
             * @param fromUser 如果是用户引起的true,不是用户引起的false
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                mProgress = progress;
                LogUtil.d("mProgress" + mProgress);
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
                handler.removeMessages(HIDE_MEDIACONTROLLER);
            }

            /**
             * 当手指离开的时候回调
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            }
        });
        /**
         * 监听音量条
         */
        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress > 0) {
                        isMute = false;
                    } else {
                        isMute = true;
                    }
                    updateVolume(progress, isMute);

                }

            }

            //当手指触碰时回调
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDE_MEDIACONTROLLER);
            }

            //当手指离开的时候回调
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            }
        });

    }

    /**
     * 设置音量大小
     *
     * @param progress
     * @param isMute
     */
    private void updateVolume(int progress, boolean isMute) {
        if (isMute) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);//flags: 0不显示系统音量条 、 1显示系统音量条
            seekbarVoice.setProgress(0);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVoice.setProgress(progress);
            currentVolume = progress;

        }
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
//        videoview.seekTo(mProgress);
//        LogUtil.e("onResume--mProgress : " + mProgress);
        LogUtil.e("onResume--");
    }

    @Override
    protected void onPause() {
//        mProgress=seekbarVideo.getProgress();
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
