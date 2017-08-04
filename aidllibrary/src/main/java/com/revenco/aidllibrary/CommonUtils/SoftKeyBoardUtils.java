package com.revenco.aidllibrary.CommonUtils;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * <p>PROJECT : FactoryTest</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017-07-14 16:18.</p>
 * <p>CLASS DESCRIBE :</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class SoftKeyBoardUtils {
    /**
     * ******************************************
     *
     * @param context *******************************************
     * @fun 隐藏软键盘
     */
    public static void HideSoftInput(Context context) {
        if (context == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
        if (isOpen) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void HideSoftInput(EditText editText) {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 隐藏键盘
     *
     * @param activity
     */
    public static void HideSoftInput(Activity activity) {
        if (activity == null)
            return;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 隐藏键盘,以SOFT_INPUT_STATE_ALWAYS_HIDDEN模式,可以针对顽固不隐藏键盘的情况使用必然有效
     *
     * @param activity
     */
    public static void HideSoftInputAlWays(Activity activity) {
        if (activity == null)
            return;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * 隐藏键盘
     *
     * @param context
     * @param view
     */
    public static void HideSoftInput(Context context, EditText view) {
        if (context == null || view == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * ******************************************
     *
     * @param context *******************************************
     * @fun 显示软键盘
     */
    public static void ShowSoftInput(Context context) {
        if (context == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
        if (!isOpen) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * ******************************************
     *
     * @param editText *******************************************
     * @fun 显示软键盘
     */
    public static void ShowSoftInput(EditText editText) {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
