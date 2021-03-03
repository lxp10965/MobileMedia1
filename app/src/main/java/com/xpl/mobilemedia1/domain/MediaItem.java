package com.xpl.mobilemedia1.domain;

import java.io.Serializable;

public class MediaItem implements Serializable {
    private String name;
    private long duration;  //媒体时长
    private long size;      //媒体大小
    private String data;    //媒体路劲
    private String artast;  //媒体艺术家

    private String imageUrl;  //网络图片路径
    private String desc;  //媒体简介

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artast='" + artast + '\'' +
                ", desc='" + desc + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtast() {
        return artast;
    }

    public void setArtast(String artast) {
        this.artast = artast;
    }


}
