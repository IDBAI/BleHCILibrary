package com.revenco.library.utils;

import android.content.Context;

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
}
