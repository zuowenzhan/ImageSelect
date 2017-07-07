package com.projectdemo.zwz.imageselect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ylzx on 2017/7/6.
 */
public class SelectImageListAdapter extends RecyclerView.Adapter<SelectImageListAdapter.ViewHolder> {
    private List<ImageEntity> mImageList;
    private ArrayList<ImageEntity> mSelectImages;
    private Context mContext;
    private int mMaxCount;
    public SelectImageListAdapter(Context context, List<ImageEntity> datas,ArrayList<ImageEntity> imagelist,int maxCount) {
        this.mImageList = datas;
        this.mContext = context;
        this.mSelectImages=imagelist;
        this.mMaxCount=maxCount;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_chooser_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
            if (TextUtils.isEmpty(mImageList.get(position).path)){
                //显示拍照
                holder.cameraLl.setVisibility(View.VISIBLE);
                holder.mediaSelectedIndicator.setVisibility(View.INVISIBLE);
                holder.image.setVisibility(View.INVISIBLE);

                //点击照相机  注意权限
                holder.ll_item.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {
                        //不能大于最大张数
                        if (mSelectImages.size()>=mMaxCount){

                            Toast.makeText(mContext, "不能再选了哦!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        openCamera();
                    }
                });

            }else {
                //显示图片
                holder.cameraLl.setVisibility(View.INVISIBLE);
                holder.mediaSelectedIndicator.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.VISIBLE);

                //glide显示图片
                Glide
                        .with(mContext)
                        .load(mImageList.get(position).path)
                        .centerCrop()
                        .into(holder.image);
                if (mSelectImages.contains(mImageList.get(position))){
                        //包含选中
                    holder.mediaSelectedIndicator.setSelected(true);
                }else {
                     //不选中
                    holder.mediaSelectedIndicator.setSelected(false);
                }

                //给条目添加点击事件
                holder.ll_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        //有就移除集合，没有就添加
                        if (mSelectImages.contains(mImageList.get(position))) {
                            mSelectImages.remove(mImageList.get(position));
                        } else {

                            //不能大于最大张数
                            if (mSelectImages.size()>=mMaxCount){

                                Toast.makeText(mContext, "不能再选了哦!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            mSelectImages.add(mImageList.get(position));
                        }

                        notifyDataSetChanged();

                        //通知显示布局
                        if (mListener!=null){
                                 mListener.setSelect();
                        }
                    }
                });

            }
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }


    /**
     * 打开相机拍照
     */
    private void openCamera() {
        try {
            File tmpFile = FileUtils.createTmpFile(mContext);
            if (mListener != null) {
                mListener.openCamera(tmpFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "相机打开失败", Toast.LENGTH_LONG).show();
        }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        SquareFrameLayout ll_item;
        ImageView image;
        View mask;
        ImageView mediaSelectedIndicator;
        LinearLayout cameraLl;
        public ViewHolder(View itemView) {
            super(itemView);
            ll_item = (SquareFrameLayout) itemView.findViewById(R.id.ll_item);
            image = (ImageView) itemView.findViewById(R.id.image);
            mask = (View) itemView.findViewById(R.id.mask);
            mediaSelectedIndicator = (ImageView) itemView.findViewById(R.id.media_selected_indicator);
           cameraLl = (LinearLayout) itemView.findViewById(R.id.camera_ll);
        }
    }
    //设置选择图片监听
    private SelectImageListener mListener;
    public void setOnSelectImageListener(SelectImageListener listener){
        this.mListener=listener;
    }
}
