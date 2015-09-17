package com.mzywx.liao.android.utils;

import java.io.File;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

public class CameraUtils {
    private static final String IMAGE_TYPE = "image/*";

    private static Context mContext;

    public CameraUtils(Context context) {
        mContext = context;
    }

    /**
     * 
     */
    public static void openCameraOrPicture(Activity activity, int requestCode) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_TYPE);

        Intent chooseIntent = Intent.createChooser(takePhotoIntent,
                "选择一种应用");
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                new Intent[] { pickIntent });
        activity.startActivityForResult(chooseIntent, requestCode);
    }

    /**
     * 打开照相机，没有照片存储路径
     * 
     * @param activity
     * @param requestCode
     */
    public static void openCamera(Activity activity, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开照相机
     * 
     * @param activity
     *            当前的activity
     * @param requestCode
     *            拍照成功时activity forResult 的时候的requestCode
     * @param photoFile
     *            拍照完毕时,图片保存的位置
     */
    public static String openCamera(Activity activity, int requestCode,
            String  photoFilePath) {
    	String filePath = photoFilePath + generateFileName();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePath)));
        activity.startActivityForResult(intent, requestCode);
        return filePath;
    }
    
    /**
     * 随机生成文件的名称
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString() + ".jpg";
    }

    /**
     * 本地照片调用
     * 
     * @param activity
     * @param requestCode
     */
    public static void openPhotos(Activity activity, int requestCode) {
        if (openPhotosNormal(activity, requestCode)
                && openPhotosBrowser(activity, requestCode)
                && openPhotosFinally())
            ;
    }

    /**
     * PopupMenu打开本地相册.
     */
    private static boolean openPhotosNormal(Activity activity, int actResultCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_TYPE);
        try {
            activity.startActivityForResult(intent, actResultCode);
        } catch (android.content.ActivityNotFoundException e) {
            return true;
        }
        return false;
    }

    /**
     * 打开其他的一文件浏览器,如果没有本地相册的话
     */
    private static boolean openPhotosBrowser(Activity activity, int requestCode) {
        Toast.makeText(mContext, "没有相册软件，运行文件浏览器", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
        intent.setType(IMAGE_TYPE);
        Intent wrapperIntent = Intent.createChooser(intent, null);
        try {
            activity.startActivityForResult(wrapperIntent, requestCode);
        } catch (android.content.ActivityNotFoundException e1) {
            return true;
        }
        return false;
    }

    /**
     * 这个是找不到相关的图片浏览器,或者相册
     */
    private static boolean openPhotosFinally() {
        Toast.makeText(mContext, "您的系统没有文件浏览器或则相册支持,请安装！", Toast.LENGTH_LONG)
                .show();
        return false;
    }

    /**
     * 获取从本地图库返回来的时候的URI解析出来的文件路径
     * 
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPhotoPathByLocalUriTargetKitkat(Context context,
            Intent data) {
        String picturePath = "";
        Uri selectedImage = data.getData();
        if (DocumentsContract.isDocumentUri(context, selectedImage)) {
            String wholeID = DocumentsContract.getDocumentId(selectedImage);
            String id = wholeID.split(":")[1];
            String[] column = { MediaStore.Images.Media.DATA };
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,
                    new String[] { id }, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                picturePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return picturePath;
    }

    public static String getPhotoPathByLocalUri(Context context, Intent data) {
        String picturePath = "";
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        picturePath = cursor.getString(columnIndex);
        cursor.close();

        return picturePath;
    }
}
