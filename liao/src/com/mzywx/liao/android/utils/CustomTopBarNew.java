package com.mzywx.liao.android.utils;

import com.mzywx.liao.android.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomTopBarNew extends RelativeLayout {

    private OnTopbarNewLeftLayoutListener onTopbarNewLeftLayoutListener = null;
    private OnTopbarNewRightButtonListener onTopbarNewRightButtonListener = null;
    private OnTopbarNewCenterListener onTopbarNewCenterListener = null;

    private RelativeLayout mainItemLayout = null;
    private LinearLayout leftLayout = null;
    private ImageButton rightButton = null;
    private TextView rightTextView = null;
    private TextView topTitleTextView = null;

    public CustomTopBarNew(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.custom_topbar, this,
                true);
        init();
    }

    public CustomTopBarNew(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.custom_topbar, this,
                true);
        init();
    }

    private void init() {

        mainItemLayout = (RelativeLayout) findViewById(R.id.mainItemLayout);
        leftLayout = (LinearLayout) findViewById(R.id.topbarLeftLinearLayout);
        rightButton = (ImageButton) findViewById(R.id.topbarRightImageButton);
        rightTextView = (TextView) findViewById(R.id.topbarRightTextView);

        leftLayout.setOnClickListener(new MyViewOnClickListener());
        rightButton.setOnClickListener(new MyViewOnClickListener());
        rightTextView.setOnClickListener(new MyViewOnClickListener());

        topTitleTextView = (TextView) findViewById(R.id.topbarTitle);
        topTitleTextView.setOnClickListener(new MyViewOnClickListener());
    }

    public void setTopbarBackground(int drawable) {
        if (mainItemLayout != null) {
            mainItemLayout.setBackgroundResource(drawable);
        }
    }

    public void setTopbarLeftLayoutHide() {
        if (leftLayout != null) {
            leftLayout.setVisibility(View.GONE);
        }
    }

    public void setTopbarLeftLayout(int backImageResId, int backTitleResId,
                                    int backTitleColorResId) {
        if (leftLayout != null) {
            if (backImageResId > 0) {
                ImageView backImage = (ImageView) findViewById(R.id.topbarLeftBackImageView);
                backImage.setImageResource(backImageResId);
            }

            TextView backTitle = (TextView) findViewById(R.id.topbarLeftBackTitle);
            if (backTitleResId > 0) {
                backTitle.setText(backTitleResId);
            }

            if (backTitleColorResId > 0) {
                backTitle.setBackgroundResource(backTitleColorResId);
            }
        }
    }

    public void setRightButton(int resBackgroundId) {
        if (rightButton != null) {
            if (resBackgroundId > 0) {
                rightButton.setVisibility(View.VISIBLE);
                rightButton.setImageResource(resBackgroundId);
            } else {
                rightButton.setVisibility(View.GONE);
            }
        }
    }

    public void setRightText(Object object) {
        if (rightTextView != null) {

            if (object != null) {
                if (object instanceof Integer) {
                    rightTextView.setText(getResources().getString(
                            (Integer) object));
                } else if (object instanceof String) {
                    rightTextView.setText((String) object);
                }
                rightTextView.setVisibility(View.VISIBLE);
            } else {
                rightTextView.setVisibility(View.GONE);
            }
        }
    }

    public void setRightTextColor(int color) {
        if (rightTextView != null) {
            rightTextView.setTextColor(color);
        }
    }

    public void setRightBtnDrable(int color) {
        if (rightTextView != null) {
            rightTextView.setBackgroundResource(color);
        }
    }

    public void setTopbarTitle(int titleResId) {
        if (topTitleTextView != null) {
            topTitleTextView.setText(titleResId);
        }
    }

    public void setTopbarTitle(String title) {
        if (topTitleTextView != null) {
            topTitleTextView.setText(title);
        }
    }

    public void setTopbarTitleRightDrawable(int drawableId) {
        if (topTitleTextView != null) {
            Drawable drawable = getResources().getDrawable(drawableId);
            topTitleTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                    null, drawable, null);
        }
    }

    public void setTopbarTitleDrawable(int drawableId) {
        if (topTitleTextView != null) {
            topTitleTextView.setBackgroundResource(drawableId);
        }
    }

    public void setOnTopbarNewRightButtonListener(
            OnTopbarNewRightButtonListener listener) {
        onTopbarNewRightButtonListener = listener;
    }

    public void setonTopbarNewLeftLayoutListener(
            OnTopbarNewLeftLayoutListener listener) {
        onTopbarNewLeftLayoutListener = listener;
    }

    public void setonTopbarNewCenterListener(OnTopbarNewCenterListener listener) {
        onTopbarNewCenterListener = listener;
    }

    public interface OnTopbarNewRightButtonListener {
        public void onTopbarRightButtonSelected();
    }

    public interface OnTopbarNewLeftLayoutListener {
        public void onTopbarLeftLayoutSelected();
    }

    public interface OnTopbarNewCenterListener {
        public void onTopbarCenterSelected();
    }

    class MyViewOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            int rid = v.getId();
            if (rid == R.id.topbarLeftLinearLayout) {
                if (onTopbarNewLeftLayoutListener != null) {
                    onTopbarNewLeftLayoutListener.onTopbarLeftLayoutSelected();
                }
            } else if (rid == R.id.topbarRightImageButton) {
                if (onTopbarNewRightButtonListener != null) {
                    onTopbarNewRightButtonListener
                            .onTopbarRightButtonSelected();
                }
            } else if (rid == R.id.topbarTitle) {
                if (onTopbarNewCenterListener != null) {
                    onTopbarNewCenterListener.onTopbarCenterSelected();
                }
            } else if (rid == R.id.topbarRightTextView) {
                if (onTopbarNewRightButtonListener != null) {
                    onTopbarNewRightButtonListener
                            .onTopbarRightButtonSelected();
                }
            }
        }
    }

}