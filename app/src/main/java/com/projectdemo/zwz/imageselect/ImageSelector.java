package com.projectdemo.zwz.imageselect;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 *  图片选择的链式调用
 */

public class ImageSelector {
    // 最多可以选择多少张图片 - 默认8张
    private int mMaxCount = 8;
    // 选择图片的模式 - 默认多选
    private int mMode = SelectImageActivity.MODE_MULTI;
    // 是否显示拍照的相机
    private boolean mShowCamera = true;
    // 原始的图片
    private ArrayList<ImageEntity> mOriginData;

    private ImageSelector() {
    }

    public static ImageSelector create() {
        return new ImageSelector();
    }

    /**
     * 单选模式
     */
    public ImageSelector single() {
        mMode = SelectImageActivity.MODE_SINGLE;
        return this;
    }

    /**
     * 多选模式
     */
    public ImageSelector multi() {
        mMode = SelectImageActivity.MODE_MULTI;
        return this;
    }

    /**
     * 设置可以选多少张图片
     */
    public ImageSelector count(int count) {
        mMaxCount = count;
        return this;
    }

    /**
     * 是否显示相机
     */
    public ImageSelector showCamera(boolean showCamera) {
        mShowCamera = showCamera;
        return this;
    }

    /**
     * 原来选择好的图片
     */
    public ImageSelector origin(ArrayList<ImageEntity> originList) {
        this.mOriginData = originList;
        return this;
    }

    /**
     * 启动执行 权限6.0自己需要去申请，也可以用我的权限申请框架
     */
    public void start(Activity activity, int requestCode) {

        Intent intent = new Intent(activity, SelectImageActivity.class);
        addParamsByIntent(intent);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 启动执行 权限6.0自己需要去申请，也可以用我的权限申请框架
     */
    public void start(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), SelectImageActivity.class);
        addParamsByIntent(intent);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 给Intent添加参数
     *
     * @param intent
     */
    private void addParamsByIntent(Intent intent) {
        intent.putExtra(SelectImageActivity.EXTRA_SHOW_CAMERA, mShowCamera);
        intent.putExtra(SelectImageActivity.EXTRA_SELECT_COUNT, mMaxCount);
        if (mOriginData != null && mMode == SelectImageActivity.MODE_MULTI) {
            intent.putExtra(SelectImageActivity.EXTRA_DEFAULT_SELECTED_LIST, mOriginData);
        }
        intent.putExtra(SelectImageActivity.EXTRA_SELECT_MODE, mMode);
    }

}
