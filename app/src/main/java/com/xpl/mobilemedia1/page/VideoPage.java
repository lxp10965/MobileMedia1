package com.xpl.mobilemedia1.page;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xpl.mobilemedia.R;
import com.xpl.mobilemedia1.activity.SystemVideoPlayer;
import com.xpl.mobilemedia1.adapter.VideoPageAdapter;
import com.xpl.mobilemedia1.base.BasePager;
import com.xpl.mobilemedia1.domain.MediaItem;

import java.util.ArrayList;

public class VideoPage extends BasePager {

    public Context mContext = null;
    private ListView list_view;
    private ProgressBar sv_load;
    private TextView tv_no_video;

    private VideoPageAdapter videoPageAdapter;

    /**
     * 装数据集合
     */
    private ArrayList<MediaItem> mediaItems;

    public VideoPage(Context context) {
        super(context);
        this.mContext = context;

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                Log.d("TAG", "handleMessage: " + mediaItems.toString());
                videoPageAdapter = new VideoPageAdapter(mContext, mediaItems);
                list_view.setAdapter(videoPageAdapter);
                tv_no_video.setVisibility(View.INVISIBLE);
            } else {
                tv_no_video.setVisibility(View.VISIBLE);
            }
            sv_load.setVisibility(View.INVISIBLE);

        }
    };

    @Override
    public View initView(Context context) {
        this.mContext = context;
        View view = View.inflate(mContext, R.layout.video_page, null);
        list_view = view.findViewById(R.id.list_view);
        sv_load = view.findViewById(R.id.sv_load);
        tv_no_video = view.findViewById(R.id.tv_no_video);
        list_view.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaItem = mediaItems.get(position);
//            Toast.makeText(mContext,""+position,Toast.LENGTH_SHORT).show();

            //1.调起系统所有的播放-隐式意图
//            Intent intent=new Intent();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");
//            mContext.startActivity(intent);

            //调起自己的播放-显式意图
//            Intent intent = new Intent(mContext, SystemVideoPlayer.class);
//            intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");
//            mContext.startActivity(intent);

            //3.传递列表数据-对象-序列化
            Intent intent = new Intent(mContext,SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();

        //加载本地视频数据
        getDataFromLocal();

    }

    /**
     * 从本地的sdcard得到数据
     * * //1.遍历sdcard,后缀名   x掉
     * * //2.从内容提供者里面获取视频
     * * //3.如果是6.0的系统，动态获取读取sdcard的权限
     */
    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
//                SystemClock.sleep(2000);
                Log.d("VideoPage", "mContext: " + mContext);
                mediaItems = new ArrayList<>();
                ContentResolver resolver = mContext.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件在SD卡的名称
                        MediaStore.Video.Media.DURATION,//视频文件的时长
                        MediaStore.Video.Media.SIZE,//视频文件大小
                        MediaStore.Video.Media.DATA,//视频文件的绝对地址
                        MediaStore.Video.Media.ARTIST,//艺术家
                };
                Log.d("TAG", "MediaStore.Video.Media.DATA: " + objs[3]);
                Cursor cursor = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    cursor = resolver.query(uri, objs, null, null);
//                }else {
                    cursor = resolver.query(uri, objs, null, null,null);
//                }
//                Log.d("TAG", "cursor.getCount(): " + cursor.getCount());
                if (cursor != null) {
                    while (cursor.moveToNext()) {
//                        Log.e("TAG", "mediaItems: " + mediaItems.toString());

                        MediaItem mediaItem = new MediaItem();
                        mediaItems.add(mediaItem);

                        String name = cursor.getString(0);
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);
                        Log.d("TAG", "duration: " + duration);

                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);
                        mediaItem.setData(data);
//                        Log.e("TAG", "data : " + data);

                        String artist = cursor.getString(4);
                        mediaItem.setArtast(artist);

                    }
                    cursor.close();
                }
                handler.sendEmptyMessage(10);
            }
        }.start();
    }


}
