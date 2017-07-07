package com.projectdemo.zwz.imageselect;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by ylzx on 2017/7/6.
 *
 * 正方形 图片
 */
public class SquareImageView  extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //自定义view 正方形
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=width;
        //设置宽高一样
        setMeasuredDimension(width, height);
    }
}
