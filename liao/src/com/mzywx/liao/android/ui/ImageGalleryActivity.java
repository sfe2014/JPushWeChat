package com.mzywx.liao.android.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.mzywx.liao.android.R;
import com.mzywx.liao.android.bean.ChatMessage;
import com.mzywx.liao.android.db.DbQueryHelper;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

public class ImageGalleryActivity extends Activity {
    private ImageView mImageView;
    private View rootView;
    private ViewPager mViewPager;
    private GalleryPagerAdapter mAdapter;
    List<View> imageList = new ArrayList<View>();
    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_image_gallery_layout);
        init();
        initDatas();
    }

    private void init() {
        mInflater = LayoutInflater.from(this);

        mImageView = (ImageView) findViewById(R.id.id_chat_image_gallery_image);
        rootView = findViewById(R.id.id_chat_image_gallery_rootview);

        mViewPager = (ViewPager) findViewById(R.id.id_chat_image_gallery_pager);
        mAdapter = new GalleryPagerAdapter();
        mViewPager.setAdapter(mAdapter);
    }

    private void initDatas() {
        List<ChatMessage> messageList = DataSupport.where("contentType = ?",
                String.valueOf(1)).find(ChatMessage.class);
        for (int i = 0; i < messageList.size(); i++) {
            View view = mInflater.inflate(R.layout.chat_image_gallery_view,
                    null);
            ImageView image = (ImageView) view
                    .findViewById(R.id.id_chat_image_gallery_image);
            image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    ImageGalleryActivity.this.finish();
                }
            });
            String imagePath = messageList.get(i).getContentImage();
            Log.d("mikes", "path=" + imagePath);
            if (!TextUtils.isEmpty(imagePath)) {
                if (imagePath.startsWith("http")
                        || imagePath.startsWith("https")) {
                    Picasso.with(this).load(imagePath)
                            .placeholder(R.drawable.default_background).into(image);
                } else {
                    Picasso.with(this).load(new File(imagePath))
                            .placeholder(R.drawable.default_background).into(image);
                }
            }
            imageList.add(view);
        }

        mAdapter.notifyDataSetChanged();
    }

    class GalleryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageList.get(position));
            return imageList.get(position);
        }
    }
}
