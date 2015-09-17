package com.mzywx.liao.android.ui;

import java.io.File;

import com.mzywx.liao.android.R;
import com.mzywx.liao.android.utils.CustomTopBarNew;
import com.mzywx.liao.android.utils.CustomTopBarNew.OnTopbarNewLeftLayoutListener;
import com.mzywx.liao.android.utils.CustomTopBarNew.OnTopbarNewRightButtonListener;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ImageFullScreenActivity extends Activity implements OnTopbarNewLeftLayoutListener,OnTopbarNewRightButtonListener{
    private ImageView mImageView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_image_fullscreen_layout);
        init();
    }
    
    private void init(){
        initTopBar();
        mImageView = (ImageView) findViewById(R.id.id_chat_fullscreen_image);
        try {
            Intent intent=getIntent();
            if (intent != null) {
            	String filePath = intent.getStringExtra("imageUri");
            	Picasso.with(this).load(new File(filePath)).into(mImageView);
            }
        } catch (ClassCastException e) {
            Log.e("mikes", "e:",e);
        }
    }
    
    private void initTopBar() {
        CustomTopBarNew topbar = (CustomTopBarNew) findViewById(R.id.id_chat_fullscreen_topbar);
        topbar.setTopbarTitle(R.string.image_fullscreen_title);
        topbar.setonTopbarNewLeftLayoutListener(this);
        topbar.setRightText("发送");
        topbar.setRightTextColor(getResources().getColor(R.color.color_white));
        topbar.setOnTopbarNewRightButtonListener(this);
    }

    @Override
    public void onTopbarLeftLayoutSelected() {
        setResult(RESULT_CANCELED);
        this.finish();
    }

    @Override
    public void onTopbarRightButtonSelected() {
        setResult(RESULT_OK);
        this.finish();
    }
}
