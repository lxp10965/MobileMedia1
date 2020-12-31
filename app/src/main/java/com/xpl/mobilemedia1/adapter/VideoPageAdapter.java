package com.xpl.mobilemedia1.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xpl.mobilemedia.R;
import com.xpl.mobilemedia1.domain.MediaItem;
import com.xpl.mobilemedia1.utils.Utils;

import java.util.ArrayList;

/**
 *
 */
public class VideoPageAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<MediaItem> mediaItems;
    private Utils utils;

    public VideoPageAdapter(Context context, ArrayList<MediaItem> mediaItems) {
        this.mContext = context;
        this.mediaItems = mediaItems;
        utils=new Utils();

    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("TAG", "getView: " +position);
        ViewHolder viewHolder;
        if (convertView==null) {
            convertView = View.inflate(mContext, R.layout.item_video_pager, null);
            viewHolder=new ViewHolder();
            viewHolder.iv_icon=convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name=convertView.findViewById(R.id.tv_name);
            viewHolder.tv_time=convertView.findViewById(R.id.tv_time);
            viewHolder.tv_size=convertView.findViewById(R.id.tv_size);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MediaItem mediaItem=mediaItems.get(position);
        viewHolder.tv_name.setText(mediaItem.getName());
        viewHolder.tv_time.setText(Formatter.formatFileSize(mContext, mediaItem.getSize()));
        viewHolder.tv_size.setText(utils.stringForTime((int) mediaItem.getDuration()));

        return convertView;
    }

    class ViewHolder {

        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;

    }
}
