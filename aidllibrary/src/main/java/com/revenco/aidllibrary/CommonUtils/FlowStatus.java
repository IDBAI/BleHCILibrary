package com.revenco.aidllibrary.CommonUtils;

import java.io.Serializable;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/3/17 17:27.</p>
 * <p>CLASS DESCRIBE :流程控制状态枚举</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public enum FlowStatus implements Serializable {
    STATUS_NONE,
    STATUS_HWRESET_SUCCESS,
    STATUS_CONFIG_MODE_SUCCESS,
    STATUS_CONFIG_PUBADDR_SUCCESS,
    STATUS_SET_TX_POWER_LEVEL_SUCCESS,
    STATUS_GATT_INIT_SUCCESS,
    STATUS_GAP_INIT_SUCCESS,
    STATUS_GATT_UPDATE_CHAR_VAL_SUCCESS,
    STATUS_GATT_ADD_SERVICE_SUCCESS,
    STATUS_GATT_ADD_CHAR_SUCCESS,
    STATUS_ACI_GATT_ADD_CHAR_DESC_SUCCESS,
    STATUS_SET_SCAN_RESPONSE_DATA_SUCCESS,
    STATUS_ACI_GAP_SET_DISCOVERABLE_SUCCESS,//标准BLE到此结束
    STATUS_L2CAP_CONN_UPDATE_RESP,//更新连接参数成功反馈
    ACI_GAP_DELETE_AD_TYPE_SUCCESS,//用于iBeacon模式
    STATUS_ACI_GAP_UPDATE_ADV_DATA_SUCCESS,//用于iBeacon模式
    STATUS_HCI_READY_DISCONNECT,
    STATUS_ENABLE_ADVERTISING,
    STATUS_RESETHW_INIT,
}
