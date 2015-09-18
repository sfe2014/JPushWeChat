package com.mzywx.liao.android.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;
import cn.jpush.android.api.JPushInterface;

import com.mzywx.liao.android.R;
import com.mzywx.liao.android.adapter.ChatAdapter;
import com.mzywx.liao.android.adapter.ChatAdapter.VoiceClickListener;
import com.mzywx.liao.android.db.DbQueryHelper;
import com.mzywx.liao.android.model.ChatMessage;
import com.mzywx.liao.android.model.ChatMessage.MessageContentType;
import com.mzywx.liao.android.model.ChatMessage.MessageType;
import com.mzywx.liao.android.model.MediaManager;
import com.mzywx.liao.android.model.MediaManager.GetDurationCallBack;
import com.mzywx.liao.android.model.Recorder;
import com.mzywx.liao.android.utils.AudioRecorderButton;
import com.mzywx.liao.android.utils.AudioRecorderButton.AudioFinishRecorderListener;
import com.mzywx.liao.android.utils.CameraUtils;
import com.mzywx.liao.android.utils.CustomTopBarNew;
import com.mzywx.liao.android.utils.CustomTopBarNew.OnTopbarNewLeftLayoutListener;
import com.mzywx.liao.android.utils.ImageUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class LiaoChatActivity extends Activity implements
		OnLayoutChangeListener, VoiceClickListener,
		OnTopbarNewLeftLayoutListener {

	private static final int PICK_IMAGE = 0x10;
	private static final int PICK_CAMERA = 0x11;
	private static final int PICK_PICTURE = 0x12;
	private static final int OPEN_FULLSCREEN = 0x13;

	private static final int ICON_WIDTH_AND_HEIGHT = 200;

	private String mCameraPhotoPath = "";//当前图片路径

	private View mRootView;
	private View mBottomView;

	// 屏幕高度
	private int screenHeight = 0;
	// 软件盘弹起后所占高度阀值
	private int keyHeight = 0;

	private ListView mChatListView;
	private ChatAdapter mChatAdapter;
	private List<ChatMessage> mDatas = new ArrayList<ChatMessage>();

	private EditText mContentEdit;
	private Button mSendButton;
	private ImageView mAddPictureButton;
	private ImageView mVoiceToggleButton;
	private AudioRecorderButton mVoiceButton;

	private String mContentString;

	private int mContentType = MessageContentType.DEFAULT;
	// listview
	private boolean scrollFlag = false;// 标记是否滑动
	private Bitmap mBitmap;
	public static boolean isForeground = false;

	// for receive customer msg from jpush server
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";//自定义消息中的文本
	public static final String KEY_EXTRAS = "extras";
	public static final String KEY_IMG = "img";// 自定义消息中的图片
	public static final String KEY_VOICE = "voice";// 自定义消息中的语音

	private DbQueryHelper db = DbQueryHelper.getInstance();//打开数据库 若没有就创建

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_layout);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		registerMessageReceiver(); // used for receive msg

		init();
		initDatas();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MediaManager.resume();
		JPushInterface.onResume(this);
		isForeground = true;
		mRootView.addOnLayoutChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MediaManager.pause();
		isForeground = false;
		JPushInterface.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MediaManager.release();
		unregisterReceiver(mMessageReceiver);
	}

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	private void init() {
		initTopBar();

		mRootView = findViewById(R.id.id_chat_main_rootview);
		// 获取屏幕高度
		screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
		// 阀值设置为屏幕高度的1/3
		keyHeight = screenHeight / 3;

		mBottomView = findViewById(R.id.id_chat_main_btn_bottom);
		mChatListView = (ListView) findViewById(R.id.id_chat_main_list);
		mContentEdit = (EditText) findViewById(R.id.id_chat_main_edit);
		mContentEdit.addTextChangedListener(new ContentWatcher());
		mSendButton = (Button) findViewById(R.id.id_chat_main_send);
		mSendButton.setOnClickListener(mOnClickListener);
		mAddPictureButton = (ImageView) findViewById(R.id.id_chat_main_add);
		mAddPictureButton.setOnClickListener(mOnClickListener);
		mVoiceToggleButton = (ImageView) findViewById(R.id.id_chat_main_voice);
		mVoiceToggleButton.setOnClickListener(mOnClickListener);
		mVoiceButton = (AudioRecorderButton) findViewById(R.id.id_chat_main_record);
		mVoiceButton
				.setAudioFinishRecorderListener(new AudioFinishRecorderListener() {
					@Override
					public void onFinish(float seconds, String filePath) {// 录音完成后的回调
						Recorder recorder = new Recorder(seconds, filePath);
						recorder.save();
						ChatMessage recordMessage = new ChatMessage(MessageType.TO, recorder,
								MessageContentType.VOICE);
						recordMessage.save();
						
						mDatas.add(recordMessage);
						mChatAdapter.notifyDataSetChanged();
						setListViewPos(mChatAdapter.getCount());
						Log.d("mikes", "add Voice: message id="+recordMessage.getId()
								+",record id="+recordMessage.getRecorder().getId());
					}
				});
	}

	private void initTopBar() {
		CustomTopBarNew topbar = (CustomTopBarNew) findViewById(R.id.topbar);
		topbar.setTopbarTitle("LiaoChat");
		topbar.setonTopbarNewLeftLayoutListener(this);
	}

	private void initDatas() {
		mChatAdapter = new ChatAdapter(this, mDatas, this);
		mChatListView.setAdapter(mChatAdapter);
		mChatListView.setOnScrollListener(mListViewScrollListener);

		List<ChatMessage> messagesList = DataSupport.findAll(ChatMessage.class);
		for (ChatMessage chatMessage : messagesList) {
			List<Recorder> recorders = chatMessage.getRecorders();
			if (recorders.size() > 0) {
				System.out.println(recorders.toString());
				chatMessage.setRecorder(recorders.get(0));
			}
		}
		mDatas.addAll(messagesList);
		mChatAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && data != null) {
			switch (requestCode) {
			case PICK_IMAGE:
				mContentType = MessageContentType.IMG;
				String result = data.toString();
				if (result.indexOf("flg") != -1) { // picture
					String path = CameraUtils
							.getPhotoPathByLocalUriTargetKitkat(this, data);
					Log.d("mikes", "path=" + path);
					if (path != null
							&& (path.endsWith(".jpg") || path.endsWith(".png")
									|| path.endsWith(".PNG") || path
										.endsWith(".JPG"))) {
						BitmapFactory.Options option = new BitmapFactory.Options();
						option.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(path, option);
						option.inSampleSize = ImageUtils.calculateInSampleSize(
								option, ICON_WIDTH_AND_HEIGHT,
								ICON_WIDTH_AND_HEIGHT);
						option.inJustDecodeBounds = false;
						mBitmap = BitmapFactory.decodeFile(path, option);
					}
				} else { // camera
					mBitmap = (Bitmap) data.getExtras().get("data");
				}

				break;
			case PICK_CAMERA:
				Log.d("mikes",
						"result data:" + data.getData().toString() + ",uri="
								+ Uri.fromFile(new File(mCameraPhotoPath)));
				// mBitmap = (Bitmap) data.getExtras().get("data");
				break;
			case PICK_PICTURE:
				Log.d("mikes", "result data:" + data.toString());
				mCameraPhotoPath = CameraUtils.getPhotoPathByLocalUri(this,
						data);

				Log.d("mikes",
						"path=" + mCameraPhotoPath + ",uri="
								+ Uri.fromFile(new File(mCameraPhotoPath)));
				// if (path != null
				// && (path.endsWith(".jpg") || path.endsWith(".png")
				// || path.endsWith(".PNG") || path
				// .endsWith(".JPG"))) {
				// BitmapFactory.Options option = new BitmapFactory.Options();
				// option.inJustDecodeBounds = true;
				// BitmapFactory.decodeFile(path, option);
				// option.inSampleSize = ImageUtils.calculateInSampleSize(
				// option, ICON_WIDTH_AND_HEIGHT,
				// ICON_WIDTH_AND_HEIGHT);
				// option.inJustDecodeBounds = false;
				// mBitmap = BitmapFactory.decodeFile(path, option);
				// }
				break;
			default:
				break;
			}
			if (!TextUtils.isEmpty(mCameraPhotoPath)) {
				Intent intent = new Intent(this, ImageFullScreenActivity.class);
				intent.putExtra("imageUri", mCameraPhotoPath);
				startActivityForResult(intent, OPEN_FULLSCREEN);
			}
		} else {
			switch (requestCode) {
			case PICK_CAMERA:
			case PICK_PICTURE:
				mCameraPhotoPath = "";
				break;
			default:
				break;
			}
		}

		if (resultCode == RESULT_OK && requestCode == OPEN_FULLSCREEN) {
			addImg(MessageType.TO, mCameraPhotoPath);
		}
	}

	/**
	 * 添加文本
	 */
	private void addTxt(int messageType, String content) {
		ChatMessage message = new ChatMessage(messageType, content,
				MessageContentType.TXT);
		message.setMessageDate(new Date());
		mDatas.add(message);
		message.save();
		mChatAdapter.notifyDataSetChanged();
		setListViewPos(mChatAdapter.getCount());
		Log.d("mikes", "add Txt: id="+message.getId());
	}

	/**
	 * 添加图片
	 */
	private void addImg(int messageType, String img) {
		ChatMessage imgMessage = new ChatMessage(messageType, img,
				MessageContentType.IMG, 0);
		imgMessage.setMessageDate(new Date());
		imgMessage.save();
		mDatas.add(imgMessage);
		mChatAdapter.notifyDataSetChanged();
		setListViewPos(mChatAdapter.getCount());
		Log.d("mikes", "add Img: id="+imgMessage.getId());
	}

	/**
	 * 推送 语音
	 */
	private void addVoice(final String voice) {
		MediaManager.getMediaDuration(voice, new GetDurationCallBack() {
			@Override
			public void getDurationCallback(int duration) {
				MediaManager.release();
				Recorder recorder = new Recorder(duration,voice);
				recorder.save();
				ChatMessage recordMessage = new ChatMessage(MessageType.FROM,
						recorder, MessageContentType.VOICE);
				recordMessage.setMessageDate(new Date());
				recordMessage.save();
				mDatas.add(recordMessage);
				mChatAdapter.notifyDataSetChanged();
				setListViewPos(mChatAdapter.getCount());
				Log.d("mikes", " push add Voice: message id="+recordMessage.getId()
						+", record id="+recordMessage.getRecorder().getId());
			}
		});
	}

	/**
	 * 滚动ListView到指定位置
	 * 
	 * @param pos
	 */
	private void setListViewPos(int pos) {
		if (android.os.Build.VERSION.SDK_INT >= 8) {
			mChatListView.smoothScrollToPosition(pos);
		} else {
			mChatListView.setSelection(pos);
		}
	}

	private void hideImm() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isShown = imm.isActive();
		if (isShown) {
			imm.hideSoftInputFromWindow(mContentEdit.getWindowToken(), 0);
		}
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.id_chat_main_send:
				if (mContentType == MessageContentType.DEFAULT)
					return;
				addTxt(MessageType.TO, mContentString);
				mContentEdit.setText("");
				break;
			case R.id.id_chat_main_add:
				if (mVoiceButton.getVisibility() == View.VISIBLE) {
					mVoiceButton.setVisibility(View.GONE);
					mBottomView.setVisibility(View.VISIBLE);
				} else {
					// if (android.os.Build.VERSION.SDK_INT >=
					// android.os.Build.VERSION_CODES.KITKAT) {
					// CameraUtils.openCameraOrPicture(LiaoChatActivity.this,
					// PICK_IMAGE);
					// } else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							LiaoChatActivity.this);
					builder.setTitle(R.string.modify_icon_dialog_title)
							.setItems(R.array.modify_icon_dialog_choices,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface arg0, int which) {
											if (which == 0) {
//												mCameraPhotoPath = CameraUtils
//														.openCamera(
//																LiaoChatActivity.this,
//																PICK_CAMERA,
//																AppContext.CAMERA_PATH);
												CameraUtils
														.openCamera(
																LiaoChatActivity.this,
																PICK_CAMERA);
												Log.d("mikes", "camera path="
														+ mCameraPhotoPath);
											} else if (which == 1) {
												CameraUtils.openPhotos(
														LiaoChatActivity.this,
														PICK_PICTURE);
											}
										}
									}).create().show();
				}
				// }
				break;
			case R.id.id_chat_main_voice:
				mContentType = MessageContentType.VOICE;
				if (mBottomView.getVisibility() == View.VISIBLE) {
					mVoiceToggleButton
							.setImageResource(R.drawable.ic_chat_keyboard);
					hideImm();
					mVoiceButton.setVisibility(View.VISIBLE);
					mBottomView.setVisibility(View.GONE);
				} else {
					mVoiceToggleButton
							.setImageResource(R.drawable.ic_chat_voice_button);
					mVoiceButton.setVisibility(View.GONE);
					mBottomView.setVisibility(View.VISIBLE);
				}
				break;
			default:
				break;
			}
		}
	};

	private OnScrollListener mListViewScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView arg0, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:// 当不滚动时
				scrollFlag = false;
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
				scrollFlag = true;
				break;
			case OnScrollListener.SCROLL_STATE_FLING:// 惯性滑动时
				scrollFlag = false;
				break;
			}
		}

		@Override
		public void onScroll(AbsListView arg0, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (scrollFlag) {
				hideImm();
			}
		}
	};

	@Override
	public void onLayoutChange(View v, int left, int top, int right,
			int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
		if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {// 软键盘弹起
			setListViewPos(mChatAdapter.getCount());
		} else if (oldBottom != 0 && bottom != 0
				&& (bottom - oldBottom > keyHeight)) {// 软键盘关闭
		}
	}

	View animView;
	private int previouceMessageType = -1;

	@Override
	public void onVoiceClick(View view, int position) {
		final int messageType = mDatas.get(position).getMessageType();
		if (animView != null && previouceMessageType == messageType) {
			if (messageType == MessageType.FROM) {
				animView.setBackgroundResource(R.drawable.ic_chat_voice_from);
			} else if (messageType == MessageType.TO) {
				animView.setBackgroundResource(R.drawable.ic_chat_voice_to);
			}
			animView = null;
		}
		previouceMessageType = messageType;
		if (messageType == MessageType.FROM) {
			animView = view
					.findViewById(R.id.id_chat_item_from_content_voice_anim);
			animView.setBackgroundResource(R.drawable.recoder_from_play);
		} else if (messageType == MessageType.TO) {
			animView = view
					.findViewById(R.id.id_chat_item_to_content_voice_anim);
			animView.setBackgroundResource(R.drawable.recoder_to_play);
		}
		final AnimationDrawable drawable = (AnimationDrawable) animView
				.getBackground();
		drawable.start();

		String filePath = mDatas.get(position).getRecorder().getFilePath();
		try {
			MediaManager.playSound(filePath, new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer arg0) {
					drawable.stop();
					if (messageType == MessageType.FROM) {
						animView.setBackgroundResource(R.drawable.ic_chat_voice_from);
					} else if (messageType == MessageType.TO) {
						animView.setBackgroundResource(R.drawable.ic_chat_voice_to);
					}
				}
			});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "Sorry,未找到语音文件", Toast.LENGTH_SHORT).show();
			drawable.stop();
			if (messageType == MessageType.FROM) {
				animView.setBackgroundResource(R.drawable.ic_chat_voice_from);
			} else if (messageType == MessageType.TO) {
				animView.setBackgroundResource(R.drawable.ic_chat_voice_to);
			}
		}
	}

	public class MessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String messge = intent.getStringExtra(KEY_MESSAGE);
				String img = intent.getStringExtra(KEY_IMG);
				String voice = intent.getStringExtra(KEY_VOICE);
				if (!TextUtils.isEmpty(img)) {
					addImg(MessageType.FROM, img);
				} else if (!TextUtils.isEmpty(voice)) {
					addVoice(voice);
				} else {
					addTxt(MessageType.FROM, messge);
				}
			}
		}
	}

	class ContentWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			if (s.length() > 0) {
				mContentType = MessageContentType.TXT;
				mSendButton.setVisibility(View.VISIBLE);
				mAddPictureButton.setVisibility(View.GONE);
				mContentString = s.toString();
			} else {
				mContentType = MessageContentType.DEFAULT;
				mSendButton.setVisibility(View.GONE);
				mAddPictureButton.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onTopbarLeftLayoutSelected() {
		this.finish();
	}

}
