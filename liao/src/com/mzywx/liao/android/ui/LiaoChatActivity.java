package com.mzywx.liao.android.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import com.mzywx.liao.android.R;
import com.mzywx.liao.android.adapter.ChatAdapter;
import com.mzywx.liao.android.adapter.ChatAdapter.VoiceClickListener;
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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class LiaoChatActivity extends Activity implements
        OnLayoutChangeListener, VoiceClickListener {

    private static final int PICK_IMAGE = 0x10;
    private static final int PICK_CAMERA = 0x11;
    private static final int PICK_PICTURE = 0x12;
    private static final int OPEN_FULLSCREEN = 0x13;

    private static final int ICON_WIDTH_AND_HEIGHT = 200;

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
    private ImageButton mAddPictureButton;
    private ImageButton mVoiceToggleButton;
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
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static final String KEY_IMG = "img";// 自定义消息中的图片
    public static final String KEY_VOICE = "voice";// 自定义消息中的语音

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
        JPushInterface.onResume(this);
        isForeground = true;
        mRootView.addOnLayoutChangeListener(this);
        MediaManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
        JPushInterface.onPause(this);
        MediaManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
        MediaManager.release();
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
        mAddPictureButton = (ImageButton) findViewById(R.id.id_chat_main_add);
        mAddPictureButton.setOnClickListener(mOnClickListener);
        mVoiceToggleButton = (ImageButton) findViewById(R.id.id_chat_main_voice);
        mVoiceToggleButton.setOnClickListener(mOnClickListener);
        mVoiceButton = (AudioRecorderButton) findViewById(R.id.id_chat_main_record);
        mVoiceButton
                .setAudioFinishRecorderListener(new AudioFinishRecorderListener() {
                    @Override
                    public void onFinish(float seconds, String filePath) {// 录音完成后的回调
                        Recorder recorder = new Recorder(seconds, filePath);
                        mDatas.add(new ChatMessage(MessageType.TO, recorder,
                                MessageContentType.VOICE));
                        mChatAdapter.notifyDataSetChanged();
                        setListViewPos(mChatAdapter.getCount());
                    }
                });
    }

    private void initTopBar() {
        CustomTopBarNew topbar = (CustomTopBarNew) findViewById(R.id.topbar);
        topbar.setTopbarTitle("LiaoChat");
        topbar.setTopbarLeftLayoutHide();
    }

    private void initDatas() {
        mChatAdapter = new ChatAdapter(this, mDatas, this);
        mChatListView.setAdapter(mChatAdapter);
        mChatListView.setOnScrollListener(mListViewScrollListener);

        mDatas.add(new ChatMessage(MessageType.FROM, "牛市",
                MessageContentType.TXT));
        mDatas.add(new ChatMessage(MessageType.TO, "熊市", MessageContentType.TXT));
        mDatas.add(new ChatMessage(MessageType.FROM, "牛市",
                MessageContentType.TXT));
        mDatas.add(new ChatMessage(MessageType.TO, "熊市", MessageContentType.TXT));
        mDatas.add(new ChatMessage(MessageType.TO, "奥巴马",
                MessageContentType.TXT));
        mDatas.add(new ChatMessage(MessageType.TO, "维多利亚",
                MessageContentType.TXT));
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
                mBitmap = (Bitmap) data.getExtras().get("data");
                break;
            case PICK_PICTURE:
                String path = CameraUtils.getPhotoPathByLocalUri(this, data);
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
                break;
            default:
                break;
            }
            if (mBitmap != null) {
                Intent intent = new Intent(this, ImageFullScreenActivity.class);
                intent.putExtra("bitmap", mBitmap);
                startActivityForResult(intent, OPEN_FULLSCREEN);
            }
        }

        if (resultCode == RESULT_OK && requestCode == OPEN_FULLSCREEN) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("bitmap", mBitmap);
            addImgLocal(bundle);
        }
    }

    /**
     * 添加文本
     */
    private void addTxt(int messageType, String content) {
        mDatas.add(new ChatMessage(messageType, content, MessageContentType.TXT));
        mChatAdapter.notifyDataSetChanged();
        setListViewPos(mChatAdapter.getCount());
    }

    /**
     * 添加图片-本地
     */
    private void addImgLocal(Bundle bundle) {
        mDatas.add(new ChatMessage(MessageType.TO, bundle,
                MessageContentType.IMG));
        mChatAdapter.notifyDataSetChanged();
        setListViewPos(mChatAdapter.getCount());
    }

    /**
     * 添加图片-推送
     */
    private void addImgAndTxtJPush(String txt, String img) {
        mDatas.add(new ChatMessage(MessageType.FROM, txt, img,
                MessageContentType.IMG_TXT));
        mChatAdapter.notifyDataSetChanged();
        setListViewPos(mChatAdapter.getCount());
    }

    /**
     * 推送 语音
     */
    private void addVoice(final String voice) {
        MediaManager.getMediaDuration(voice, new GetDurationCallBack() {
            @Override
            public void getDurationCallback(int duration) {
                MediaManager.release();
                Recorder recorder = new Recorder(duration, voice);
                
                mDatas.add(new ChatMessage(MessageType.FROM, recorder,
                        MessageContentType.VOICE));
                mChatAdapter.notifyDataSetChanged();
                setListViewPos(mChatAdapter.getCount());
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
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        CameraUtils.openCameraOrPicture(LiaoChatActivity.this,
                                PICK_IMAGE);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                LiaoChatActivity.this);
                        builder.setTitle(R.string.modify_icon_dialog_title)
                                .setItems(R.array.modify_icon_dialog_choices,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface arg0,
                                                    int which) {
                                                if (which == 0) {
                                                    CameraUtils
                                                            .openCamera(
                                                                    LiaoChatActivity.this,
                                                                    PICK_CAMERA);
                                                } else if (which == 1) {
                                                    CameraUtils
                                                            .openPhotos(
                                                                    LiaoChatActivity.this,
                                                                    PICK_PICTURE);
                                                }
                                            }
                                        }).create().show();
                    }
                }
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

    @Override
    public void onVoiceClick(View view, int position) {
        final int messageType = mDatas.get(position).getType();
        if (animView != null) {
            if (messageType == MessageType.FROM) {
                animView.setBackgroundResource(R.drawable.ic_chat_voice_from);
                animView = view.findViewById(R.id.id_chat_item_from_content_voice_anim);
            } else if (messageType == MessageType.TO) {
                animView.setBackgroundResource(R.drawable.ic_chat_voice_to);
                animView = view.findViewById(R.id.id_chat_item_to_content_voice_anim);
            }
        }
        if (messageType == MessageType.FROM) {
            animView = view.findViewById(R.id.id_chat_item_from_content_voice_anim);
        } else if (messageType == MessageType.TO) {
            animView = view.findViewById(R.id.id_chat_item_to_content_voice_anim);
        }
        animView.setBackgroundResource(R.drawable.recoder_play);
        final AnimationDrawable drawable = (AnimationDrawable) animView
                .getBackground();
        drawable.setOneShot(false);
        drawable.start();

        String filePath = mDatas.get(position).getRecorder().getFilePath();
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
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String img = intent.getStringExtra(KEY_IMG);
                String voice = intent.getStringExtra(KEY_VOICE);
                if (!TextUtils.isEmpty(img)) {
                    addImgAndTxtJPush(messge, img);
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

}
