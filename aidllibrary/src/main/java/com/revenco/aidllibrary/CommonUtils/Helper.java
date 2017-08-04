package com.revenco.aidllibrary.CommonUtils;

import android.util.SparseArray;

import com.revenco.aidllibrary.CharBean;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017-04-27 10:43.</p>
 * <p>CLASS DESCRIBE : 为了解耦 PeripheralService </p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class Helper {
    /**
     * ble固件HCI控制的流程状态
     */
    public static final String ACTON_FLOWCONTROL_STATUS = "com.revenco.library.core.ACTON_FLOWCONTROL_STATUS";
    /**
     * 接收到特征值
     */
    public static final String ACTION_REVEIVE_ATTRIBUTE_VALUES = "com.revenco.library.core.ACTION_REVEIVE_ATTRIBUTE_VALUES";
    /**
     * app连接
     */
    public static final String ACTION_APP_CONNECT_STATUS = "com.revenco.library.core.ACTION_APP_CONNECT_STATUS";
    public static final String EXTRA_APPBEAN = "EXTRA_APPBEAN";
    public static final String EXTRA_APPMAC = "EXTRA_APPMAC";
    public static final String EXTRA_CHAR_UUID = "EXTRA_CHAR_UUID";
    public static final String EXTRA_CHAR_VALUES = "EXTRA_CHAR_VALUES";
    public static final int MSG_REMOVE_WAITING_TIMER = 1001;
    public static final int MSG_TEST_SEND_NOTIFY = 1002;
    /**
     * 特征值集合
     */
    public static final int CHAR_SET_SIZE = 8;
    /**
     * 当前已经配置的进度
     */
    public static volatile ConfigProcess currentHasConfig = ConfigProcess.config_none;
    /**
     * 特征值实体集合
     */
    public static SparseArray<CharBean> charBeanSparseArray = new SparseArray<>(CHAR_SET_SIZE);
}
