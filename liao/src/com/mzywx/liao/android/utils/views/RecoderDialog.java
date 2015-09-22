package com.mzywx.liao.android.utils.views;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mzywx.liao.android.R;

public class RecoderDialog extends android.app.Dialog {

	Context context;
	private ImageView mIcon;
	private ImageView mVoice;
	private TextView mLable;
	private ProgressBar mProgressBar;
	private View view;
	private View backView;

	public RecoderDialog(Context context) {
		super(context, android.R.style.Theme_Translucent);
		this.context = context;// init Context
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_recorder);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.x = 25;
        lp.y = 150;
        dialogWindow.setAttributes(lp);
		/*
		 * 将对话框的大小按屏幕大小的百分比设置
		 */
        DisplayMetrics dm = new DisplayMetrics();
        dialogWindow.getWindowManager().getDefaultDisplay().getMetrics(dm);
		WindowManager.LayoutParams p = dialogWindow.getAttributes();
		p.height = (int) (dm.heightPixels * 0.8);
		p.width = (int) (dm.widthPixels * 0.8);
		dialogWindow.setAttributes(p);

		view = (RelativeLayout) findViewById(R.id.id_recoder_contentview);
		backView = (RelativeLayout) findViewById(R.id.id_recoder_rootview);
		backView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getX() < view.getLeft()
						|| event.getX() > view.getRight()
						|| event.getY() > view.getBottom()
						|| event.getY() < view.getTop()) {
					dismiss();
				}
				return false;
			}
		});
		mIcon = (ImageView) findViewById(R.id.id_recorder_dialog_icon);
		mVoice = (ImageView) findViewById(R.id.id_recorder_dialog_voice);
		mLable = (TextView) findViewById(R.id.id_recorder_dialog_label);
		mProgressBar = (ProgressBar) findViewById(R.id.id_recorder_dialog_progress);
		mProgressBar.incrementProgressBy(-1);
	}

	@Override
	public void show() {
		super.show();
		// set dialog enter animations
		view.startAnimation(AnimationUtils.loadAnimation(context,
				R.anim.dialog_main_show_amination));
		backView.startAnimation(AnimationUtils.loadAnimation(context,
				R.anim.dialog_root_show_amin));
	}

	// change visibility of views
	public void setIconVisibility(int visibility) {
		mIcon.setVisibility(visibility);
	}

	public void setVoiceVisibility(int visibility) {
		mVoice.setVisibility(visibility);
	}

	public void setLabelVisibility(int visibility) {
		mLable.setVisibility(visibility);
	}

	// change background of views
	public void setVoiceImageResource(int resId) {
		mVoice.setImageResource(resId);
	}

	public void setIconImageResource(int resId) {
		mIcon.setImageResource(resId);
	}

	public void setLabelText(int resId) {
		mLable.setText(resId);
	}
	
	public void setLabelTextBackground(int resId) {
		mLable.setBackgroundResource(resId);
	}
	
	public void setProgress(int value) {
		Log.d("lyl", "value"+value);
		mProgressBar.setProgress(value);
	}

	@Override
	public void dismiss() {
		Animation anim = AnimationUtils.loadAnimation(context,
				R.anim.dialog_main_hide_amination);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view.post(new Runnable() {
					@Override
					public void run() {
						RecoderDialog.super.dismiss();
					}
				});
			}
		});
		Animation backAnim = AnimationUtils.loadAnimation(context,
				R.anim.dialog_root_hide_amin);

		view.startAnimation(anim);
		backView.startAnimation(backAnim);
	}

}
