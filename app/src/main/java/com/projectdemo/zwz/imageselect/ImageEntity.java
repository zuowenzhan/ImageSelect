package com.projectdemo.zwz.imageselect;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by ylzx on 2017/7/6.
 * 图片对象
 */
public class ImageEntity implements Serializable{
    public String path;
    public String name;
    public long time;
    public ImageEntity(String path,String name,long time){
        this.path=path;
        this.name=name;
        this.time=time;
    }
    public boolean equals(Object o){
        if (o instanceof  ImageEntity){
            ImageEntity compare= (ImageEntity) o;
            return TextUtils.equals(this.path,compare.path);
        }
        return false;
    }

}
