package com.mzywx.liao.android.model;

import org.litepal.crud.DataSupport;


public class ChatMessage extends DataSupport {

	public class MessageType {
		public static final int FROM = 0;// 收到消息
		public static final int TO = 1;// 发出消息
	}

	public class MessageContentType {
		public static final int DEFAULT = -1;
		public static final int TXT = 0;// 文本
		public static final int IMG = 1;// 图片
		public static final int VOICE = 2;// 语音
	}

	private int messageId;// 消息ID
	/**
	 * 消息类型
	 */
	private int messageType;
	/**
	 * 内容类型
	 */
	private int contentType;// text ,voice ,image
	/**
	 * 消息内容
	 */
	private String contentText;// 文本内容

	private String contentImage;// 图片地址

	private float contentRecordDuration;// 语音时长
	private String contentRecordPath;// 语音路径

	protected Recorder recorder;

	/**
	 * 发送文字
	 * 
	 * @param messageType
	 *            消息类型
	 * @param content
	 *            文本内容
	 * @param contentType
	 *            内容类型
	 */
	public ChatMessage(int messageType, String contentText, int contentType) {
		super();
		this.messageType = messageType;
		this.contentText = contentText;
		this.contentType = contentType;
	}

	/**
	 * 发送图片 推送时，发送图片URL 本地图片发送图片URI
	 * 
	 * @param messageType
	 *            消息类型
	 * @param contentImageUrl
	 *            图片链接
	 * @param contentType
	 *            内容类型
	 */
	public ChatMessage(int messageType, String contentImage, int contentType,
			int flag) {
		super();
		this.messageType = messageType;
		this.contentImage = contentImage;
		this.contentType = contentType;
	}

	/**
	 * 发送语音
	 * 
	 * @param type
	 * @param recorder
	 * @param contentType
	 */
	public ChatMessage(int type, Recorder recorder, int contentType) {
		super();
		this.messageType = type;
		this.recorder = recorder;
		this.contentRecordDuration = recorder.getSeconds();
		this.contentRecordPath = recorder.getFilePath();
		this.contentType = contentType;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getContentText() {
		return contentText;
	}

	public void setContentText(String contentText) {
		this.contentText = contentText;
	}

	public String getContentImage() {
		return contentImage;
	}

	public void setContentImage(String contentImage) {
		this.contentImage = contentImage;
	}

	public float getContentRecordDuration() {
		return contentRecordDuration;
	}

	public void setContentRecordDuration(float contentRecordDuration) {
		this.contentRecordDuration = contentRecordDuration;
	}

	public String getContentRecordPath() {
		return contentRecordPath;
	}

	public void setContentRecordPath(String contentRecordPath) {
		this.contentRecordPath = contentRecordPath;
	}

	public Recorder getRecorder() {
		return recorder;
	}

	public void setRecorder(Recorder recorder) {
		this.recorder = recorder;
	}

}
