package com.mzywx.liao.android.bean;

import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

public class ChatMessage extends DataSupport {

	public class MessageType {
		public static final int FROM = 0;// 收到消息
		public static final int TO = 1;// 发出消息
	}

	public class MessageState {
		public static final int SUCCESS = 0;// 成功
		public static final int RUNNING = 1;// 发送中
		public static final int FAILURE = 2;// 失败
	}

	public class MessageContentType {
		public static final int DEFAULT = -1;
		public static final int TXT = 0;// 文本
		public static final int IMG = 1;// 图片
		public static final int VOICE = 2;// 语音
	}

	private int id;// 消息ID
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

	private Date messageDate;// 消息时间

	private int messageState = MessageState.SUCCESS;// 消息状态

	private Recorder recorder;

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
	 * @param flag
	 *            重载
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
		this.contentType = contentType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Recorder getRecorder() {
		return recorder;
	}

	public void setRecorder(Recorder recorder) {
		this.recorder = recorder;
	}

	public List<Recorder> getRecorders() {
		return DataSupport.where("chatmessage_id = ?", String.valueOf(id))
				.find(Recorder.class);
	}

	public Date getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}

	public int getMessageState() {
		return messageState;
	}

	public void setMessageState(int messageState) {
		this.messageState = messageState;
	}

	@Override
	public String toString() {
		return "ChatMessage [id=" + id + ", messageType=" + messageType
				+ ", contentType=" + contentType + ", contentText="
				+ contentText + ", contentImage=" + contentImage
				+ ", messageDate=" + messageDate + ", messageState="
				+ messageState + ", recorder=" + recorder + "]";
	}

}
