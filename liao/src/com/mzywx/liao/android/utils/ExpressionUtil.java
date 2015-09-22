package com.mzywx.liao.android.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

public class ExpressionUtil {
    /**
     * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
     * 
     * @param context
     * @param spannableString
     * @param patten
     * @param start
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws NumberFormatException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void dealExpression(Context context,
            SpannableString spannableString, Pattern patten, int start)
            throws SecurityException, NoSuchFieldException,
            NumberFormatException, IllegalArgumentException,
            IllegalAccessException {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            int resId = context.getResources().getIdentifier(key,
                    "drawable", context.getPackageName());
            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(
                        context.getResources(), resId);
                ImageSpan imageSpan = new ImageSpan(context,bitmap);
                int end = matcher.end();
                spannableString.setSpan(imageSpan, matcher.start(), end,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                if (end < spannableString.length()) {
                    dealExpression(context, spannableString, patten, end);
                }
                break;
            }
        }
    }

    /**
     * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
     * 
     * @param context
     * @param str
     * @return
     */
    public static SpannableString getExpressionString(Context context,
            String str, String pattern) {
        SpannableString spannableString = new SpannableString(str);
        Pattern sinaPatten = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        try {
            dealExpression(context, spannableString, sinaPatten, 0);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;
    }

}