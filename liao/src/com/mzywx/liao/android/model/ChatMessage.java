package com.mzywx.liao.android.model;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.os.Bundle;

public class ChatMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8841720805424349208L;

    public class MessageType {
        public static final int FROM = 0;// 收到消息
        public static final int TO = 1;// 发出消息
    }

    public class MessageContentType {
        public static final int DEFAULT = -1;
        public static final int TXT = 0;// 文本
        public static final int IMG = 1;// 图片
        public static final int IMG_TXT = 3;// 图片+文本
        public static final int VOICE = 2;// 语音
    }

    /**
     * 消息类型
     */
    private int type;
    /**
     * 消息内容
     */
    private String content;

    private String contentImageUrl;
    
    private String coontentVoiceUrl;
    /**
     * 内容类型
     */
    private int contentType;

    private Bundle contentImage;

    /**
     * 发送文字 
     * 
     * @param messageType
     *            消息类型
     * @param content
     *            内容
     * @param contentType
     *            内容类型
     */
    public ChatMessage(int messageType, String content, int contentType) {
        super();
        this.type = messageType;
        this.content = content;
        this.contentType = contentType;
    }

    /**
     * 推送 图片和文字
     * @param type
     *            消息类型
     * @param content
     *            内容
     * @param contentImageUrl
     *            图片链接
     * @param contentType
     *            内容类型
     */
    public ChatMessage(int messageType, String content, String contentImageUrl,
            int contentType) {
        super();
        this.type = messageType;
        this.content = content;
        this.contentImageUrl = contentImageUrl;
        this.contentType = contentType;
    }
    
    
    /**
     * 推送语音
     * @param type
     * @param coontentVoiceUrl
     * @param contentType
     */
    public ChatMessage(int type, String coontentVoiceUrl, int contentType, int flag) {
        super();
        this.type = type;
        this.coontentVoiceUrl = coontentVoiceUrl;
        this.contentType = contentType;
    }

    /**
     * 发送图片 本地
     * 
     * @param messageType
     * @param bitmap
     * @param contentType
     */
    public ChatMessage(int messageType, Bundle contentImage, int contentType) {
        super();
        this.type = messageType;
        this.contentImage = contentImage;
        this.contentType = contentType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public Bundle getContentImage() {
        return contentImage;
    }

    public void setContentImage(Bundle contentImage) {
        this.contentImage = contentImage;
    }

    public String getContentImageUrl() {
        return contentImageUrl;
    }

    public void setContentImageUrl(String contentImageUrl) {
        this.contentImageUrl = contentImageUrl;
    }

    public String getCoontentVoiceUrl() {
        return coontentVoiceUrl;
    }

    public void setCoontentVoiceUrl(String coontentVoiceUrl) {
        this.coontentVoiceUrl = coontentVoiceUrl;
    }

    @Override
    public String toString() {
        return "ChatMessage [type=" + type + ", content=" + content
                + ", contentImageUrl=" + contentImageUrl + ", contentType="
                + contentType + ", contentImage=" + contentImage + "]";
    }

}
