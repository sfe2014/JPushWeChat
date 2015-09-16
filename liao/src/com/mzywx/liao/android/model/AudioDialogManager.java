package com.mzywx.liao.android.model;

import com.mzywx.liao.android.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AudioDialogManager {
    private ImageView mIcon;
    private ImageView mVoice;
    private TextView mLable;

    private Context mContext;

    private AlertDialog dialog;

    public AudioDialogManager(Context context) {
        this.mContext = context;
    }

    // 显示录音的对话框
    public void showRecordingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.Theme_audioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recorder, null);

        mIcon = (ImageView) view.findViewById(R.id.id_recorder_dialog_icon);
        mVoice = (ImageView) view.findViewById(R.id.id_recorder_dialog_voice);
        mLable = (TextView) view.findViewById(R.id.id_recorder_dialog_label);
        
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    public void recording() {
        if (dialog != null && dialog.isShowing()) { // 显示状态
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.ic_chat_recoder);
            mLable.setText(mContext.getString(R.string.voice_recording));
        }
    }

    // 显示想取消的对话框
    public void wantToCancel() {
        if (dialog != null && dialog.isShowing()) { // 显示状态
            mVoice.setVisibility(View.GONE);
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.ic_chat_cancel);
            mLable.setText(mContext.getString(R.string.voice_record_cancel));
        }
    }

    // 显示时间过短的对话框
    public void tooShort() {
        if (dialog != null && dialog.isShowing()) { // 显示状态
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.ic_chat_warning);
            mLable.setText(mContext.getString(R.string.voice_record_too_short));
        }
    }

    // 显示取消的对话框
    public void dimissDialog() {
        if (dialog != null && dialog.isShowing()) { // 显示状态
            dialog.dismiss();
            dialog = null;
        }
    }

    // 显示更新音量级别的对话框
    public void updateVoiceLevel(int level) {
        if (dialog != null && dialog.isShowing()) { // 显示状态
//            mIcon.setVisibility(View.VISIBLE);
//            mVoice.setVisibility(View.VISIBLE);
//            mLable.setVisibility(View.VISIBLE);

            // 设置图片的id
            int resId = mContext.getResources().getIdentifier("ic_volume_v" + level,
                    "drawable", mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }

}
