package com.revenco.library.command;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/2/27 15:27.</p>
 * <p>CLASS DESCRIBE : opCode</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class OpCode {
    //open
//    public static final byte[] open_command_opCode = {0x01,0x10};
    public static final byte[] HCI_Disconnect_opCode = {0x06, 0x04};
    //HW reset
    public static final byte[] hwReset_command_opCode = {0x03, 0x0C};
    //写配置
    public static final byte[] ACI_HAL_WRITE_CONFIG_DATA_opCode = {0x0C, (byte) 0xFC};
    //设置tx power
    public static final byte[] ACI_HAL_SET_TX_POWER_LEVEL_opCode = {0x0F, (byte) 0xFC};
    //gatt init
    public static final byte[] ACI_GATT_INIT_opCode = {0x01, (byte) 0xFD};
    //gap init
    public static final byte[] ACI_GAP_INIT_opCode = {(byte) 0x8A, (byte) 0xFC};
    //gatt update char val
    public static final byte[] ACI_GATT_UPD_CHAR_VAL_opCode = {(byte) 0x06, (byte) 0xFD};
    //gatt 添加服务
    public static final byte[] ACI_GATT_ADD_SERVICE_opCode = {(byte) 0x02, (byte) 0xFD};
    //gatt 在服务之下，添加特征值
    public static final byte[] ACI_GATT_ADD_CHAR_opCode = {(byte) 0x04, (byte) 0xFD};
    //为特征值添加描述符
    public static final byte[] ACI_GATT_ADD_CHAR_DESC_opCode = {0x05, (byte) 0xFD};
    //写特征值
    public static final byte[] ACI_GATT_WRITE_CHARAC_VAL_opCode = {0x1C, (byte) 0xFD};
    //HCI BLE 扫描应答数据
    public static final byte[] HCI_LE_SET_SCAN_RESPONSE_DATA_opCode = {(byte) 0x09, (byte) 0x20};
    //开启广播
    public static final byte[] ACI_GAP_SET_DISCOVERABLE_opCode = {(byte) 0x83, (byte) 0xFC};
    //更新连接参数请求
    public static final byte[] Aci_L2CAP_Connection_Parameter_Update_Request_opCode = {(byte) 0x81, (byte) 0xFD};
    //更新连接参数请求回应
    public static final byte[] Aci_L2CAP_Connection_Parameter_Update_Response_opCode = {(byte) 0x82, (byte) 0xFD};
    /**
     * 更新广播数据,用于iBeacon 基站开发模式
     */
    public static final byte[] ACI_GAP_UPDATE_ADV_DATA_opCode = {(byte) 0x8E, (byte) 0xFC};
    /**
     * 用于iBeacon 基站开发模式
     */
    public static final byte[] ACI_GAP_DELETE_AD_TYPE_opCode = {(byte) 0x8F, (byte) 0xFC};

}
