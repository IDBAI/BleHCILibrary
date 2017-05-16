package com.revenco.library.command;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/3/1 11:11.</p>
 * <p>CLASS DESCRIBE :</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class HCIVendorEcode {
    /**
     *
     */
    public static final byte[] EVT_BLUE_INITIALIZED_Ecode = {0x01,0x00};
    /**
     * 属性被改变
     */
    public static final byte[] EVT_BLUE_GATT_ATTRIBUTE_MODIFIED_Ecode = {0x01,0x0C};
    /**
     *
     */
    public static final byte[] EVT_BLUE_GATT_ERROR_RESPONSE_Ecode = {0x11,0x0C};
    /**
     *
     */
    public static final byte[] EVT_BLUE_GATT_PROCEDURE_COMPLETE_Ecode = {0x10,0x0C};

    public static final byte[] EVT_BLUE_GATT_PROCEDURE_TIMEOUT_Ecode = {0x02,0x0C};

    //
    public static final byte[] EVT_BLUE_L2CAP_CONN_UPDATE_RESP_Ecode = {0x00,0x08};


//    0x00: BLE_STATUS_SUCCESS
//    0x41: BLE_STATUS_FAILED
//    0x42: Invalid parameters
//    0x05: ERR_AUTH_FAILURE, Procedure failed due to
//    authentication requirements

    public static final byte BLE_STATUS_SUCCESS = 0x00;
    public static final byte BLE_STATUS_FAILED = 0x41;
    public static final byte INVALID_PARAMETERS = 0x42;
    public static final byte ERR_AUTH_FAILURE = 0x05;

}
