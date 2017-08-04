package com.revenco.aidllibrary.CommonUtils;

import android.content.Context;
import android.os.PowerManager;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/3/1 15:07.</p>
 * <p>CLASS DESCRIBE :实用小工具</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class Tools {
    private static final String TAG = "Tools";
    /**
     * 当前连接的app mac地址，会每次覆盖更新
     */
    private static final String CURRENT_CON_APP_BLE_MAC_ADDR = "CURRENT_CON_APP_BLE_MAC_ADDR";
    private static PowerManager.WakeLock mWakeLock;

    /**
     * @param context
     * @return
     */
    public static String getConnectAppBleMacAddr(Context context) {
        return MySharedPreferences.getStringPreference(context, CURRENT_CON_APP_BLE_MAC_ADDR);
    }

    public static void setConnectAppBleMacAddr(Context context, String addr) {
        MySharedPreferences.setStringPreference(context, CURRENT_CON_APP_BLE_MAC_ADDR, addr);
    }

    /**
     * 保持持久唤醒状态
     * 一共有如下几个flag来进行不一样的唤醒方式.可以根据需要设置
     * Flag Value                   CPU     Screen      Keyboard
     * PARTIAL_WAKE_LOCK            On*      can-off      Off
     * SCREEN_DIM_WAKE_LOCK         On       Dim          Off
     * PROXIMITY_SCREEN_OFF_WAKE_LOCK on      距离传感器时关闭  off
     * SCREEN_BRIGHT_WAKE_LOCK      On       Bright       Off
     * FULL_WAKE_LOCK               On       Bright       Bright
     *
     * @param context
     * @param wakeLeavel
     */
    public static void acquireWakeLock(Context context, int wakeLeavel) {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(wakeLeavel, context.getClass().getCanonicalName());
            mWakeLock.acquire();
        }
    }

    /**
     * 释放持久唤醒锁
     */
    public static void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}
