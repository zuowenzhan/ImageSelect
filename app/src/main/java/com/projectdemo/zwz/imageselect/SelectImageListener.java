package com.projectdemo.zwz.imageselect;

import java.io.File;

/**
 * Created by ylzx on 2017/7/6.
 */
public interface SelectImageListener {
    //选则回调
    void setSelect();
   void openCamera(File file);
}
