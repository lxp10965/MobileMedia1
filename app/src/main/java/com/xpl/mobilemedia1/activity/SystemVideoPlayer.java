package com.xpl.mobilemedia1.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
     更新进度
     */
    private static final int PROGRESS = 1;
    /**
     * 隐藏控制面板
     */
    private static final int HIDE_MEDIACONTROLLER = 2;
    /**
     * 显示网速
     */
    private static final int SHOW_SPEED = 3;
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
    /**
     * 最大亮度
     */
    private int maxBrightness;

    //判断是不是网络Uri
    private boolean isNetUri;
    /**
     * 缓冲进度
     */
    private LinearLayout ll_buffer;
    /**
     * 缓冲进度 网速显示
     */
    private TextView tv_buffer_netspeed;
    /**
     * 进入播放加载框
     */
    private LinearLayout ll_loading;
    /**
     * 进入播放加载框 网速显示
     */
    private TextView tv_laoding_netspeed;
    private boolean isUseSystem =true;
    /**
     * 上一次的进度
     */
    private int precurrentPosition;


    private void initData() {

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

        //得到系统最大亮度
        maxBrightness = getScreenBrightness(this);
        LogUtil.d("maxBrightness : " + maxBrightness);

    }

    /**
     * 1.获取系统默认屏幕亮度值 屏幕亮度值范围（0-255）
     **/
    private int getScreenBrightness(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        int defVal = 125;
        return Settings.System.getInt(contentResolver,
                Settings.System.SCREEN_BRIGHTNESS, defVal);
    }

    /**
     * 2.设置 APP界面屏幕亮度值方法
     **/
    private void setAppScreenBrightness(int birghtessValue) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = birghtessValue / 255.0f;
        window.setAttributes(lp);
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
     * 设置全屏或者默认
     *
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
            /**
             * 更新电量
             */
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
        utils = new Utils();
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
            isNetUri = utils.isNetUri(mediaItem.getData());
            videoview.setVideoPath(mediaItem.getData());
        } else if (null != uri) {
            videoview.setVideoURI(uri);
            isNetUri = utils.isNetUri(uri.toString());
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
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_laoding_netspeed = (TextView) findViewById(R.id.tv_laoding_netspeed);
        tv_buffer_netspeed = (TextView) findViewById(R.id.tv_buffer_netspeed);

        //更新网速
        handler.sendEmptyMessage(SHOW_SPEED);

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
                //切换Vitamio播放器
                showSwichPlayerDialog();
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

    /**
     * 切换Vitamio播放器
     */
    private void showSwichPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("系统播放器提醒您！");
        builder.setMessage("当您播放视频，有声音没有画面的时候，请切换万能播放器播放");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startVitamioPlayer();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }



    /**
     * 下一曲
     */
    private void playPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position--;
            ll_loading.setVisibility(View.VISIBLE);
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
            ll_loading.setVisibility(View.VISIBLE);
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
        ll_loading.setVisibility(View.GONE);
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

    //开始触碰的 X
    private float startX;
    //开始触碰的 Y
    private float startY;
    /**
     * 屏幕的高
     */
    private float touchRang;

    /**
     * 当一按下的音量
     */
    private int mVol;
    /**
     * 当前屏幕亮度
     */
    private int mBri;

    //当一按下的播放进度
    private int mPro;

    //当前视频的最大进度
    private int maxPro;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://手指按下
                startX = event.getX();
                startY = event.getY();
                touchRang = Math.min(screenWidth, screenHeight);
                if (event.getX() > screenWidth / 2) {
                    mVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                } else if (event.getX() < screenWidth / 2) {
                    mBri = getScreenBrightness(this);
                }
                mPro = videoview.getCurrentPosition();
                maxPro = videoview.getDuration();
//                LogUtil.d("mPro : " + mPro);
//                LogUtil.d("maxPro : " + maxPro);

                handler.removeMessages(HIDE_MEDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                float endX = event.getX();
                float endY = event.getY();
                float distanceX = startX - endX;
                float distanceY = startY - endY;//大于0向上滑
                if (Math.abs(startY - endY) > 5) {
                    if (event.getX() > screenWidth * 3 / 4) {
                        //改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
                        float delte = (distanceY / touchRang) * maxVolume;
//                      int voice = (int) (mVol+delte);
                        int voice = (int) Math.min(Math.max(delte + mVol, 0), maxVolume);
                        if (delte != 0) {
                            isMute = false;
                            updateVolume(voice, isMute);
                        }
                    } else if (event.getX() < screenWidth * 1 / 4) {
//                    滑动屏幕的距离： 总距离 = 改变亮度：亮度最大值
//                    改变亮度 = （滑动屏幕的距离： 总距离）*亮度最大值
                        float brightness = (distanceY / touchRang) * 255;
//                    LogUtil.d("brightness : "+brightness);
//                            最终亮度 = 原来的 + 改变亮度；
                        int mbrightness = (int) Math.min(Math.max(mBri + brightness, 25), 255);
//                    LogUtil.d("mbrightness : "+mbrightness);
//                    LogUtil.d("mBri : "+mBri);
                        if (brightness != 0) {
                            setAppScreenBrightness(mbrightness);
                        }
                    }
                } else if (Math.abs(startX - endX) > 5) {
                    //改变进度 = （滑动屏幕的距离： 总距离）*进度最大值

                    float alterProgress = (distanceX / screenWidth) * maxPro;
                    int mAlterProgress = (int) Math.min(Math.max(mPro - alterProgress, 0), maxPro);
//                    LogUtil.d("mAlterProgress : " + mAlterProgress);
                    if (alterProgress != 0) {
                        videoview.seekTo(mAlterProgress);
                    }
                }

                break;
            case MotionEvent.ACTION_UP://手指松开

                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 监听物理健，实现声音的调节大小
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Toast.makeText(this, "音量减", Toast.LENGTH_SHORT).show();
            currentVolume--;
            showMediaController();
            updateVolume(currentVolume, false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Toast.makeText(this, "音量加", Toast.LENGTH_SHORT).show();
            currentVolume++;
            showMediaController();
            updateVolume(currentVolume, false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                case SHOW_SPEED://显示网速
                    //获取网速
                    String netSpeed = utils.getNetSpeed(SystemVideoPlayer.this);

                    tv_buffer_netspeed.setText("缓存中..."+netSpeed);
                    tv_laoding_netspeed.setText("加载中..."+netSpeed);
                    //每两秒更新一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED,2000);

                    break;
                case HIDE_MEDIACONTROLLER:  //隐藏控制面板
                    hideMediaController();
                    break;
                case PROGRESS:
                    //1.获取当前进度
                    int currentPosition = videoview.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    //缓存进度的更新
                    if (isNetUri) {
                        //只有网络资源才有缓存效果
                        int buffer = videoview.getBufferPercentage();
                        int totalBuffer = buffer * seekbarVideo.getMax();
                        int secondaryprogress = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryprogress);
                    } else {
                        //本地视频没有缓冲效果
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    //设置系统时间
                    tvSystemTime.setText(getSystemTime());

                    //监听卡
                    if (!isUseSystem) {

                        if(videoview.isPlaying()){
                            int buffer = currentPosition - precurrentPosition;
                            if (buffer < 500) {
                                //视频卡了
                                ll_buffer.setVisibility(View.VISIBLE);
                            } else {
                                //视频不卡了
                                ll_buffer.setVisibility(View.GONE);
                            }
                        }else{
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }
                    precurrentPosition = currentPosition;

                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 100);
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

        //监听卡
        videoview.setOnInfoListener(new MyOnInfoListener());

        //准备好的监听
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();
                //进入视频就播
//                videoview.start();
                Toast.makeText(SystemVideoPlayer.this, "准备好了，开始播放！", Toast.LENGTH_SHORT).show();
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
                startVitamioPlayer();
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
                mProgress = progress;
//                LogUtil.d("progress" + progress);

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
     * a,把数据按照原样传入VtaimoVideoPlayer播放器
     b,关闭系统播放器
     */
    private void startVitamioPlayer(){

        if(videoview != null){
            videoview.stopPlayback();
        }


        Intent intent = new Intent(this,VitamioVideoPlayer.class);
        if(mediaItems != null && mediaItems.size() > 0){

            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position);

        }else if(uri != null){
            intent.setData(uri);
        }
        startActivity(intent);

        finish();//关闭页面
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

    /**
     * 监听卡，
     */
    private class MyOnInfoListener implements MediaPlayer.OnInfoListener {
        /**
         * 指示信息或警告
         *
         * @param mp    信息所属的媒体播放器。
         * @param what  什么类型的信息或警告
         * @param extra 一个额外的代码，具体到信息。通常依赖于实现的。
         * @return 如果该方法处理了该信息，则返回True，否则返回false。返回false或not将导致信息被丢弃。
         */
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START: //开始卡了
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END: //卡结束了
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }
}
