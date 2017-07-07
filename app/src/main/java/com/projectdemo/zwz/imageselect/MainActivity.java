package com.projectdemo.zwz.imageselect;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yanzhenjie.permission.AndPermission;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ArrayList<ImageEntity> mImageList;
    private final int SELECT_IMAGE_REQUEST=0x0011;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndPermission.with(this)
                .requestCode(101)
                .permission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.CAMERA, Manifest.permission.LOCATION_HARDWARE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.READ_PHONE_STATE)
                .send();
    }
    //选择图片
    public void setimage(View view){


        //调用设置参数
//        Intent intent = new Intent(this, SelectImageActivity.class);
//        intent.putExtra(SelectImageActivity.EXTRA_SELECT_COUNT,9);
//        intent.putExtra(SelectImageActivity.EXTRA_SELECT_MODE,SelectImageActivity.MODE_MULTI);
//        intent.putExtra(SelectImageActivity.EXTRA_DEFAULT_SELECTED_LIST, mImageList);
//        intent.putExtra(SelectImageActivity.EXTRA_SHOW_CAMERA,true);
//        startActivityForResult(intent,SELECT_IMAGE_REQUEST);
        ImageSelector.create().count(10).multi().origin(mImageList).showCamera(true).start(this, SELECT_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
                  if (requestCode==SELECT_IMAGE_REQUEST && data != null){

                      mImageList = (ArrayList<ImageEntity>) data.getSerializableExtra(SelectImageActivity.EXTRA_RESULT);

                      //做一下显示
                      Log.e("TAG","===="+mImageList.size());
                  }
        }

    }
}
