package com.mzywx.liao.android.utils.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import com.mzywx.liao.android.R;
import com.mzywx.liao.android.ui.LiaoChatActivity;

public class ExpressionDialog extends android.app.Dialog {

    public interface ChooseExpressionClickListener {
        void expressionClick(SpannableString spannableString);
    }

    Context context;
    private GridView mGridView;
    private View view;
    private View backView;

    private int[] imageIds = new int[107];

    public ExpressionDialog(Context context) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;// init Context
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_expression_dialog);

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
        p.height = (int) (dm.heightPixels * 0.6);
        dialogWindow.setAttributes(p);

        view = findViewById(R.id.expression_dialog_contentview);
        backView = findViewById(R.id.expression_dialog_rootView);
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

        mGridView = (GridView) findViewById(R.id.id_dialog_expression_gv);
    }

    public void showExpressionDialog(
            final ChooseExpressionClickListener listener) {
        this.show();
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < imageIds.length; i++) {
            try {
                if (i < 10) {
                    int resId = context.getResources().getIdentifier("f00" + i,
                            "drawable", context.getPackageName());
                    imageIds[i] = resId;
                } else if (i < 100) {
                    int resId = context.getResources().getIdentifier("f0" + i,
                            "drawable", context.getPackageName());
                    imageIds[i] = resId;
                } else {
                    int resId = context.getResources().getIdentifier("f" + i,
                            "drawable", context.getPackageName());
                    imageIds[i] = resId;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("image", imageIds[i]);
            listItems.add(listItem);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems,
                R.layout.layout_single_expression_cell,
                new String[] { "image" }, new int[] { R.id.id_expression_cell_image });
        mGridView.setAdapter(simpleAdapter);

        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Bitmap bitmap = BitmapFactory.decodeResource(
                        context.getResources(),
                        imageIds[arg2 % imageIds.length]);
                ImageSpan imageSpan = new ImageSpan(context, bitmap);
                String str = null;
                if (arg2 < 10) {
                    str = "f00" + arg2;
                } else if (arg2 < 100) {
                    str = "f0" + arg2;
                } else {
                    str = "f" + arg2;
                }
                SpannableString spannableString = new SpannableString(str);
                spannableString.setSpan(imageSpan, 0, 4,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                listener.expressionClick(spannableString);
                dismiss();
            }
        });
        
        
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
                        ExpressionDialog.super.dismiss();
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
