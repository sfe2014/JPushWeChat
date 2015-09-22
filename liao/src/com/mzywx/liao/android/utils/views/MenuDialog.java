package com.mzywx.liao.android.utils.views;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import com.mzywx.liao.android.R;

public class MenuDialog extends android.app.Dialog {

	Context context;
	private ListView mListView;
	private View view;
	private View backView;

	public MenuDialog(Context context) {
		super(context, android.R.style.Theme_Translucent);
		this.context = context;// init Context
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_menu_dialog);

		view = (RelativeLayout) findViewById(R.id.dialog_contentview);
		backView = (RelativeLayout) findViewById(R.id.dialog_rootView);
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
		mListView = (ListView) findViewById(R.id.id_item_menu_list);
	}
	
	public void setSimpleAdapter(SimpleAdapter adapter, OnItemClickListener listener){
		mListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		mListView.setOnItemClickListener(listener);
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
						MenuDialog.super.dismiss();
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
