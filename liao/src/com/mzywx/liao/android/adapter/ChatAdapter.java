package com.mzywx.liao.android.adapter;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {
    private static final int CHAT_TYPE = 2;

    private static final int MSG_FROM = 0;
    private static final int MSG_TO = 1;

    private Context mContext;
    private List<ChatMessage> mDatas;
    private LayoutInflater mInflater;

    public ChatAdapter(Context context, List<ChatMessage> data) {
        mContext = context;
        mDatas = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return CHAT_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage msg = mDatas.get(position);
        if (msg.getType() == MessageType.FROM) {
            return MSG_FROM;
        } else {
            return MSG_TO;
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
    public View getView(int position, View convertView, ViewGroup arg2) {
        final ChatMessage msg = mDatas.get(position);
        int type = msg.getType();
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
            case MessageType.FROM:
                convertView = mInflater.inflate(R.layout.chat_from, null);
                holder.itemTime = (TextView) convertView
                        .findViewById(R.id.id_chat_item_from_time);
                holder.itemIcon = (ImageView) convertView
                        .findViewById(R.id.id_chat_item_from_icon);
                holder.itemContentTxt = (TextView) convertView
                        .findViewById(R.id.id_chat_item_from_content_txt);
                holder.itemContentImg = (ImageView) convertView
                        .findViewById(R.id.id_chat_item_from_content_img);
                holder.itemMsgType = MessageType.FROM;
                break;
            case MessageType.TO:
                convertView = mInflater.inflate(R.layout.chat_to, null);
                holder.itemTime = (TextView) convertView
                        .findViewById(R.id.id_chat_item_to_time);
                holder.itemIcon = (ImageView) convertView
                        .findViewById(R.id.id_chat_item_to_icon);
                holder.itemContentTxt = (TextView) convertView
                        .findViewById(R.id.id_chat_item_to_content_txt);
                holder.itemContentImg = (ImageView) convertView
                        .findViewById(R.id.id_chat_item_to_content_img);
                holder.itemMsgType = MessageType.TO;
                break;
            default:
                break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.itemTime.setText(TimeModule.getTimeFormat());
        int contentType = mDatas.get(position).getContentType();
        switch (contentType) {
        case MessageContentType.TXT:
            holder.itemContentTxt.setText(mDatas.get(position).getContent());
            break;
        case MessageContentType.IMG:
            if (type == MessageType.FROM) {
                holder.itemContentTxt.setText(mDatas.get(position).getContent());
                holder.itemContentImg.setVisibility(View.VISIBLE);
                String icon = mDatas.get(position).getContentImageUrl();
                if (!TextUtils.isEmpty(icon)) {
                    Picasso.with(mContext).load(icon).into(holder.itemContentImg);
                }
            } else {
                holder.itemContentImg.setVisibility(View.VISIBLE);
                Bitmap bitmap = (Bitmap) mDatas.get(position).getContentImage()
                        .getParcelable("bitmap");
                holder.itemContentImg.setImageBitmap(bitmap);
            }
            
            break;
        case MessageContentType.VOICE:

            break;

        default:
            break;
        }

        return convertView;
    }

    static class ViewHolder {
        TextView itemTime;
        ImageView itemIcon;
        TextView itemContentTxt;
        ImageView itemContentImg;
        int itemMsgType;
    }

}
