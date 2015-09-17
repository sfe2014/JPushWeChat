package com.mzywx.liao.android.adapter;

import java.io.File;
import java.util.List;

import com.mzywx.liao.android.R;
import com.mzywx.liao.android.model.ChatMessage;
import com.mzywx.liao.android.model.TimeModule;
import com.mzywx.liao.android.model.ChatMessage.MessageContentType;
import com.mzywx.liao.android.model.ChatMessage.MessageType;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {
    
    public interface VoiceClickListener{
        void onVoiceClick(View view, int position);
    }
    
    private static final int CHAT_TYPE_COUNT = 2;

    private Context mContext;
    private List<ChatMessage> mDatas;
    private LayoutInflater mInflater;
    private VoiceClickListener mListener;
    
    private int mMinItemWidth;
    private int mMaxItemWidth;

    public ChatAdapter(Context context, List<ChatMessage> data,VoiceClickListener listener) {
        mContext = context;
        mDatas = data;
        mInflater = LayoutInflater.from(context);
        mListener = listener;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth= (int) (outMetrics.widthPixels * 0.15f);
    }

    @Override
    public int getViewTypeCount() {
        return CHAT_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage msg = mDatas.get(position);
        if (msg.getMessageType() == MessageType.FROM) {
            return MessageType.FROM;
        } else {
            return MessageType.TO;
        }
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mDatas.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        final ChatMessage msg = mDatas.get(position);
        int type = msg.getMessageType();
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
            case MessageType.FROM:
                convertView = mInflater.inflate(R.layout.chat_from, null);
                holder.itemVoiceView = convertView
                        .findViewById(R.id.id_chat_item_from_content_voice_layout);
                holder.itemVoiceAnim = convertView
                        .findViewById(R.id.id_chat_item_from_content_voice_anim);
                holder.itemVoiceSeconds = (TextView) convertView
                        .findViewById(R.id.id_chat_item_from_content_voice_seconds);
                
                holder.itemTime = (TextView) convertView
                        .findViewById(R.id.id_chat_item_from_time);
                holder.itemIcon = (ImageView) convertView
                        .findViewById(R.id.id_chat_item_from_user_icon);
                holder.itemContentTxt = (TextView) convertView
                        .findViewById(R.id.id_chat_item_from_content_txt);
                holder.itemContentImg = (ImageView) convertView
                        .findViewById(R.id.id_chat_item_from_content_img);
                convertView.setTag(holder);
                break;
            case MessageType.TO:
                convertView = mInflater.inflate(R.layout.chat_to, null);
                holder.itemVoiceView = convertView
                        .findViewById(R.id.id_chat_item_to_content_voice_layout);
                holder.itemVoiceAnim = convertView
                        .findViewById(R.id.id_chat_item_to_content_voice_anim);
                holder.itemVoiceSeconds = (TextView) convertView
                        .findViewById(R.id.id_chat_item_to_content_voice_seconds);
                
                holder.itemTime = (TextView) convertView
                        .findViewById(R.id.id_chat_item_to_time);
                holder.itemIcon = (ImageView) convertView
                        .findViewById(R.id.id_chat_item_to_user_icon);
                holder.itemContentTxt = (TextView) convertView
                        .findViewById(R.id.id_chat_item_to_content_txt);
                holder.itemContentImg = (ImageView) convertView
                        .findViewById(R.id.id_chat_item_to_content_img);
                convertView.setTag(holder);
                break;
            default:
                break;
            }
        } else {
            switch (type) {
            case MessageType.FROM:
                holder = (ViewHolder) convertView.getTag();
                break;
            case MessageType.TO:
                holder = (ViewHolder) convertView.getTag();
                break;
            default:
                break;
            }
        }

        holder.itemTime.setText(TimeModule.getTimeFormat());
        holder.itemContentTxt.setMaxWidth(mMaxItemWidth);
        holder.itemContentImg.setMaxWidth(mMaxItemWidth);

        int contentType = mDatas.get(position).getContentType();
        switch (contentType) {
        case MessageContentType.TXT:
            holder.itemVoiceView.setVisibility(View.GONE);
            holder.itemContentImg.setVisibility(View.GONE);
            holder.itemContentTxt.setVisibility(View.VISIBLE);
            
            holder.itemContentTxt.setText(mDatas.get(position).getContentText());
            break;
        case MessageContentType.IMG:
            holder.itemVoiceView.setVisibility(View.GONE);
            holder.itemContentTxt.setVisibility(View.GONE);
            holder.itemContentImg.setVisibility(View.VISIBLE);
            
            String imagePath = mDatas.get(position).getContentImage();
            if (!TextUtils.isEmpty(imagePath)) {
                Picasso.with(mContext).load(new File(imagePath)).into(holder.itemContentImg);
            }
            break;
        case MessageContentType.VOICE:
            holder.itemVoiceView.setVisibility(View.VISIBLE);
            holder.itemContentTxt.setVisibility(View.GONE);
            holder.itemContentImg.setVisibility(View.GONE);
            
            holder.itemVoiceSeconds.setText(Math.round(mDatas.get(position).getRecorder().getSeconds())+"\"");
            ViewGroup.LayoutParams lp1 = holder.itemVoiceView.getLayoutParams();
            lp1.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f)* mDatas.get(position).getRecorder().getSeconds());
            holder.itemVoiceView.setLayoutParams(lp1);
            
            holder.itemVoiceView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onVoiceClick(view,position);
                }
            });
            break;
        default:
            break;
        }

        return convertView;
    }

    static class ViewHolder {
        View itemVoiceView;
        View itemVoiceAnim;
        TextView itemVoiceSeconds;
        
        TextView itemTime;
        ImageView itemIcon;
        TextView itemContentTxt;
        ImageView itemContentImg;
    }

}
