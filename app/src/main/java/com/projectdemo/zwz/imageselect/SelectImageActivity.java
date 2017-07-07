package com.projectdemo.zwz.imageselect;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class SelectImageActivity extends Activity implements View.OnClickListener, SelectImageListener {

    private RelativeLayout llTitle;
    private LinearLayout llBack;
    private TextView tvTitleText;
    private RecyclerView imageListRv;
    private RelativeLayout opBar;
    private TextView selectPreview;
    private TextView selectNum;
    private TextView selectFinish;
    private ProgressBar loadingProgress;

     //是否显示相机的 EXTRA_KEY
    public static final String EXTRA_SHOW_CAMERA="EXTRA_SHOW_CAMERA";
    //总共可以选择多少张图片的EXTRA_KEY
    public static final String EXTRA_SELECT_COUNT="EXTRA_SELECT_COUNT";
    //原始的图片路径的EXTRA_KEY
    public static final String EXTRA_DEFAULT_SELECTED_LIST="EXTRA_DEFAULT_SELECTED_LIST";
    //选择模式EXTRA_KEY
    public static final String EXTRA_SELECT_MODE="EXTRA_SELECT_MODE";
    //返回选择图片列表的EXTRA_KEY
    public static final String EXTRA_RESULT="EXTRA_RESULT";
    //加载所有的数据
    private static final int LOADER_TYPE=0x0021;
    // 拍照临时存放的文件
    private File mTempFile;

    public final static int REQUEST_CAMERA = 0x0045;
    /*
    获取传递过来的参数
     */
    //选择图片的模式--多选
    public static final int MODE_MULTI=0x0011;
    //选择图片的模式--单选
    public static final int MODE_SINGLE=0x0012;
    //单选或者多选 int类型的type
    private int mMode=MODE_MULTI;
    // int 类型的图片张数
    private int mMaxCount=8;
    //boolean 类型的是否显示拍照按钮
    private boolean mShowCamera=true;
    //ArrayList<String> 已经选好的图片
    private ArrayList<ImageEntity> mResultList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector);

        llTitle = (RelativeLayout) findViewById(R.id.ll_title);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        tvTitleText = (TextView) findViewById(R.id.tv_title_text);
        imageListRv = (RecyclerView) findViewById(R.id.image_list_rv);
        opBar = (RelativeLayout) findViewById(R.id.op_bar);
        selectPreview = (TextView) findViewById(R.id.select_preview);
        selectNum = (TextView) findViewById(R.id.select_num);
        selectFinish = (TextView) findViewById(R.id.select_finish);
        loadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
        initData();
    }

    private void initData() {
        //获取上一个页面传递过来的参数
        Intent intent = getIntent();
        mMode = intent.getIntExtra(EXTRA_SELECT_MODE, mMode);
        mMaxCount=intent.getIntExtra(EXTRA_SELECT_COUNT, mMaxCount);
        mShowCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, mShowCamera);
        mResultList= (ArrayList<ImageEntity>) intent.getSerializableExtra(EXTRA_DEFAULT_SELECTED_LIST);
        if (mResultList==null){
              mResultList=new ArrayList<>();
        }
        //初始化本地图片数据
        initImageList();

        //改变显示
        showExchangView();

        selectFinish.setOnClickListener(this);
        llBack.setOnClickListener(this);
    }

    /**
     * 底部栏的显示 预览 图片数 需要及时更新
     *
     */
   private void showExchangView(){

       //预览是否可以点击
       if (mResultList.size()>0){
              //至少为一张
           selectPreview.setEnabled(true);
           selectPreview.setOnClickListener(this);
       }else {
           //一张都没有
           selectPreview.setEnabled(false);
           selectPreview.setOnClickListener(null);
       }

       //中间图片张数显示
       selectNum.setText(mResultList.size() + "/" + mMaxCount);
   }
    /**
     * 获取内存卡中所有图片
     */
    private void initImageList() {
           //耗时操作
        getLoaderManager().initLoader(LOADER_TYPE, null, mLoaderCallback);
    }
    /**
     * 加载图片的回调
     */
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback=new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION={
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID,
        };
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //查询数据库语句
            CursorLoader cursorLoader=new CursorLoader(SelectImageActivity.this,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_PROJECTION,IMAGE_PROJECTION[4]+">0 AND " +IMAGE_PROJECTION[3]+"=? OR "+IMAGE_PROJECTION[3]+"=? ",new String[]{"image/jpeg","image/png"},IMAGE_PROJECTION[2]+" DESC");
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //解析，封装到集合
            if (data !=null && data.getCount()>0){

                ArrayList<ImageEntity> images=new ArrayList<>();

                //如果要显示拍照。就在第一个位置上加空的对象
                if (mShowCamera){
                    images.add(new ImageEntity("","",1));
                }
                //不断的遍历循环
                while (data.moveToNext()){
                  String path= data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                   String name= data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                   long dataTime= data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));

                    Log.e("TAG","path=="+path+";name=="+name+";dataTime=="+dataTime);

                    //判断文件是不是存在
                    if (!pathExist(path)){
                        continue;
                    }

                    Log.e("TAG","path=="+path+";name=="+name+";dataTime=="+dataTime);
                    //封装数据对象
                    ImageEntity image=new ImageEntity(path,name,dataTime);
                    images.add(image);


                }
                //显示列表数据
                showImageList(images);

            }

        }

        /**
         *判断该路径文件是不是存在
         */
        private boolean pathExist(String path){
            if (!TextUtils.isEmpty(path)){
                return new File(path).exists();
            }
            return false;
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    /**
     * 显示图片列表
     * @param images
     */

    private void showImageList(ArrayList<ImageEntity> images) {
        SelectImageListAdapter selectImageListAdapter = new SelectImageListAdapter(this, images,mResultList,mMaxCount);
        imageListRv.setLayoutManager(new GridLayoutManager(this,4));
        imageListRv.setAdapter(selectImageListAdapter);
        selectImageListAdapter.setOnSelectImageListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_preview:
                //图片预览 传入选择集合

                break;
            case R.id.select_finish:
                //确定按钮     传递选择好的图片
                Intent intent=new Intent();
                intent.putExtra(EXTRA_RESULT,mResultList);
                setResult(RESULT_OK,intent);
                //关闭当前按钮
                finish();
                break;
            case R.id.ll_back:
                finish();
                break;
        }
    }

    /**
     * 监听回调
     */
    @Override
    public void setSelect() {
        showExchangView();
    }

    /**
     * 打开照相机
     * @param file
     */
    @Override
    public void openCamera(File file) {

        mTempFile = file;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照完成，将图片加载到集合
        //调用setSelect（）
        //通知本地图片有更新
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                // notify system the image has change
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTempFile)));

                mResultList.add(new ImageEntity(mTempFile.getAbsolutePath(), getTime(), 1));

                showImageList(mResultList);
                setSelect();

            }
        }
    }

    private String getTime(){
        Calendar c = Calendar.getInstance();
       // 取得系统日期:
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        //取得系统时间
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return year+month+day+hour+minute+"";
    }
}
