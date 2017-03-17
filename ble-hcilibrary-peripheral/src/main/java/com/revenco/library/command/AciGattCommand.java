package com.revenco.library.command;

import com.revenco.library.others.Config;
import com.revenco.library.core.PeripharalManager;
import com.revenco.library.core.SerialPortListenTask;
import com.revenco.library.utils.XLog;

/**
 *
 */
public class AciGattCommand {
    private static final String TAG = "AciGattCommand";

    /**
     * [0x01,0x01,0xFD,0x00]
     *
     * @param portListenTask
     */
    public static void bleGattInit(SerialPortListenTask portListenTask) {
        XLog.d(TAG, "bleGattInit() called with: portListenTask = [" + portListenTask + "]");
        byte[] buffer = new byte[4];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.ACI_GATT_INIT_opCode, 0, buffer, 1, 2);
        buffer[3] = 0x00;//Parameter Total Length
        portListenTask.sendData(OpCode.ACI_GATT_INIT_opCode, buffer);
    }

    /**
     * 01      06 FD       0F      05 00       06 00       00      05                  57 5A 42 4C 45
     * <p>
     * [0x01, 0x06,0xFD,    0x0D,  0x05,0x00, 0x06,0x00,  0x00    ,0x07,              0x42,0x6C,0x75,0x65,0x4E,0x52,0x47]
     *
     * @param Service_Handle
     * @param Dev_Name_Char_Handle
     * @param portListenTask
     */
    public static void updateCharValues(byte[] Service_Handle, byte[] Dev_Name_Char_Handle, SerialPortListenTask portListenTask) {
        XLog.d(TAG, "updateCharValues() called with:  Service_Handle = [" + Service_Handle + "], Dev_Name_Char_Handle = [" + Dev_Name_Char_Handle + "], portListenTask = [" + portListenTask + "]");
    }

    /**
     * Opcode     Parameter Total Length         Service_UUID_Type         UUID                                                                              Service_Type   Max_Attribute_Records
     * [0x01,  0x02,0xFD,        0x13,                            0x02,           0x99,0x99,0x99,0x99,0x99,0x99,0x99,0x99,0x99,0x99,0x99,0x99,0x99,0x99,0x99,0x99,    0x01,             0x12]
     * 添加服务,一个服务，下面需要 7+1 = 8  个特征值，服务UUID 16字节
     *
     * @param portListenTask
     */
    public static void gattAddService(SerialPortListenTask portListenTask) {
        XLog.d(TAG, "gattAddService() called with: portListenTask = [" + portListenTask + "]");
        int ParameterTotalLength = 19;
        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.ACI_GATT_ADD_SERVICE_opCode, 0, buffer, 1, 2);
        buffer[3] = (byte) ParameterTotalLength;
        buffer[4] = 0x02;//Service_UUID_Type 0x02: 128-bit UUID
        System.arraycopy(PeripharalManager.getInstance().SERVICE_UUID, 0, buffer, 5, 16);
        buffer[21] = 0x01;//Service_Type 0x01: Primary Service
        // This
//        parameter specifies the maximum number of attribute records that can be added to this
//        service (including the service attribute, include attribute, characteristic attribute,
//                characteristic value attribute and characteristic descriptor attribute).
        buffer[22] = 0x32;//0x32：50字节， 最大容纳50记录属性,太小了容纳不下8个特征值，Max_Attribute_Records Maximum number of attribute records that can be added to this service
        //send
        portListenTask.sendData(OpCode.ACI_GATT_ADD_SERVICE_opCode, buffer);
    }

    /**
     * HCI_COMMAND_PKT    ACI_GATT_ADD_CHAR_opCode      Parameter Total Length  Service_Handle     Char_UUID_Type          Char_UUID_128                                                                     Char_Value_Length   Char_Properties  Security_Permissions    GATT_Evt_Mask    Enc_Key_Size     Is_Variable
     * [0x01,                0x04,0xFD,                         0x19, (25)              0x0C,0x00,         0x02,           0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x10,0x11,0x11,0x11,0x11,0x11,0x11,             0x01,            0x0E,          0x00,                      0x01,           0x07,          0x00]
     * <p>
     * 添加特征值（根据service_handle）
     *
     * @param portListenTask
     * @param service_handle  service句柄
     * @param CHAR_UUID       特征值UUID
     * @param Char_Properties 特征值的属性
     */
    public static void gattAddCharacteristic(SerialPortListenTask portListenTask, byte[] service_handle, byte[] CHAR_UUID, byte Char_Properties) {
        XLog.d(TAG, "gattAddCharacteristic() called with: portListenTask = [" + portListenTask + "], CHAR_UUID = [" + CHAR_UUID + "], service_handle = [" + service_handle + "], Char_Properties = [" + Char_Properties + "]");
        int ParameterTotalLength = 25;
        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.ACI_GATT_ADD_CHAR_opCode, 0, buffer, 1, 2);
        buffer[3] = (byte) ParameterTotalLength;
        System.arraycopy(service_handle, 0, buffer, 4, 2);
        buffer[6] = 0x02;//Char_UUID_Type 0x01 = 16-bit UUID, 0x02 = 128-bit UUID
        System.arraycopy(CHAR_UUID, 0, buffer, 7, 16);
        //Char_Value_Length Maximum length of the characteristic value. (50字节->0x32)
        buffer[23] = 0x32;
        //
        buffer[24] = Char_Properties;//Char_Properties 属性集
        //Security_Permissions
// 0x00: None,
// 0x01: AUTHEN_READ (Need authentication to read),
// 0x02: AUTHOR_READ (Need authorization to read),
// 0x04: ENCRY_READ (link should be encrypted to read),
// 0x08: AUTHEN_WRITE (need authentication to write),
// 0x10: AUTHOR_WRITE (need authorization to write),
// 0x20: ENCRY_WRITE (link should be encrypted to write)
        buffer[25] = 0x00;
// GATT_Evt_Mask
// 0x01 = ATTR_WRITE,
// 0x02 = INTIMATE_WHEN_WRITE_N_WAIT,
// 0x04 = INTIMATE_WHEN_READ_N_WAIT
        buffer[26] = 0x01;
//    Enc_Key_Size    minimum encryption key size required to read the characteristic range : 0x07 to 0x10
        buffer[27] = 0x07;
//     Is_Variable   0x00: The attribute has a fixed length value field 0x01: The attribute has a variable length value field
        buffer[28] = 0x01;
        //send
        portListenTask.sendData(OpCode.ACI_GATT_ADD_CHAR_opCode, buffer);
    }

    /**
     * 为特征值添加描述符,针对 notify 的特征值需要
     *
     * @param portListenTask
     * @param service_handle
     * @param Char_Handle
     */
    public static void gattAddCharDescriptor(SerialPortListenTask portListenTask, byte[] service_handle, byte[] Char_Handle) {
        XLog.d(TAG, "gattAddCharDescriptor() called with: portListenTask = [" + portListenTask + "], service_handle = [" + service_handle + "], Char_Handle = [" + Char_Handle + "]");
        int ParameterTotalLength = 28 + Config.CHAR_DESC_VALUE.length;
        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.ACI_GATT_ADD_CHAR_DESC_opCode, 0, buffer, 1, 2);
        buffer[3] = (byte) ParameterTotalLength;
        //service_handle
        System.arraycopy(service_handle, 0, buffer, 4, 2);
        //Char_Handle
        System.arraycopy(Char_Handle, 0, buffer, 6, 2);
        //CHAR_DESC_UUID type0x01: 16-bit UUID (default), 0x02: 128-bit UUID
        buffer[8] = 0x02;
        System.arraycopy(Config.CHAR_DESC_UUID, 0, buffer, 9, 16);
        int Char_Desc_Value_Max_Length = 20;
        buffer[25] = (byte) Char_Desc_Value_Max_Length;
        int Char_Desc_Value_Length = Config.CHAR_DESC_VALUE.length;
        buffer[26] = (byte) Char_Desc_Value_Length;
        System.arraycopy(Config.CHAR_DESC_VALUE, 0, buffer, 27, Config.CHAR_DESC_VALUE.length);
        buffer[27 + Config.CHAR_DESC_VALUE.length] = 0x00;//Security_Permissions
//        0x00: No Access
//        0x01: Readable
//        0x02: Writable
//        0x03: Read/Write
        buffer[28 + Config.CHAR_DESC_VALUE.length] = 0x03;//Access_Permissions
//        0x01: GATT_SERVER_ATTR_WRITE
//        The application will be notified when a client writes
//        to this attribute
//        0x02:
//        GATT_INTIMATE_AND_WAIT_FOR_APPL_AUTH
//        The application will be notified when a write
//        request/write cmd/signed write cmd is received by
//        the server for this attribute
//        0x04:
//        GATT_INTIMATE_APPL_WHEN_READ_N_WAIT
//        The application will be notified when a read request
//        of any type is got for this attribute
        int Gatt_Event_Mask = 0x01;
        buffer[29 + Config.CHAR_DESC_VALUE.length] = (byte) Gatt_Event_Mask;
//        Encryption_Key_Size  The minimum encryption key size requirement for    this attribute. Valid Range: 7 to 16
        buffer[30 + Config.CHAR_DESC_VALUE.length] = 0x07;
//        0x00: The attribute has a fixed length value field
//        0x01: The attribute has a variable length value field
        buffer[31 + Config.CHAR_DESC_VALUE.length] = 0x01;
        //send
        portListenTask.sendData(OpCode.ACI_GATT_ADD_CHAR_DESC_opCode, buffer);
    }

    /**
     * notify app 开门状态
     *               paramLen     service_handler           char_handle      Val_Offset    valLen        values
     * 01 06 FD        0E >            0C 00                 1B 00              00          02         CHAR_NOTIFY_STATUS   STATUS_REASON
     *
     * @param portListenTask
     * @param service_handler
     * @param char_handle
     * @param CHAR_NOTIFY_STATUS
     * @param STATUS_REASON
     */
    public static void updateCharValues(SerialPortListenTask portListenTask, byte[] service_handler, byte[] char_handle, byte CHAR_NOTIFY_STATUS, byte STATUS_REASON) {
        int ParameterTotalLength = 8;
        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.ACI_GATT_UPD_CHAR_VAL_opCode, 0, buffer, 1, 2);
        buffer[3] = (byte) ParameterTotalLength;
        //
        System.arraycopy(service_handler, 0, buffer, 4, 2);
        System.arraycopy(char_handle, 0, buffer, 6, 2);
        int Val_Offset = 0;
        buffer[8] = (byte) Val_Offset;
        //
        int ValLen = 2;//值长度
        buffer[9] = (byte) ValLen;
        buffer[10] = CHAR_NOTIFY_STATUS;
        buffer[11] = STATUS_REASON;
        //send
        portListenTask.sendData(OpCode.ACI_GATT_UPD_CHAR_VAL_opCode, buffer);
    }
    /**
     *
     *
     * @param portListenTask
     * @param Connection_handle
     * @param attrHandle
     * @param CHAR_NOTIFY_STATUS 状态
     * @param STATUS_REASON      状态原因
    //     */
//    public static void AciGattWriteCharValues(SerialPortListenTask portListenTask, byte[] Connection_handle, byte[] attrHandle, byte CHAR_NOTIFY_STATUS, byte STATUS_REASON) {
//        XLog.d(TAG, "AciGattWriteCharValues() called with: portListenTask = [" + portListenTask + "], Connection_handle = [" + Connection_handle + "], attrHandle = [" + attrHandle + "], CHAR_NOTIFY_STATUS = [" + CHAR_NOTIFY_STATUS + "], STATUS_REASON = [" + STATUS_REASON + "]");
//        int ParameterTotalLength = 7;
//        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
//        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
//        System.arraycopy(OpCode.ACI_GATT_WRITE_CHARAC_VAL_opCode, 0, buffer, 1, 2);
//        buffer[3] = (byte) ParameterTotalLength;
//        //
//        System.arraycopy(Connection_handle, 0, buffer, 4, 2);
//        System.arraycopy(attrHandle, 0, buffer, 6, 2);
//        //
//        int ValLen = 2;//值长度
//        buffer[8] = (byte) ValLen;
//        buffer[9] = CHAR_NOTIFY_STATUS;
//        buffer[10] = STATUS_REASON;
//        //
//        portListenTask.sendData(OpCode.ACI_GATT_WRITE_CHARAC_VAL_opCode, buffer);
//    }
//    public static void AciGattUpdateCharValues(SerialPortListenTask portListenTask, byte[] Connection_handle, byte[] attrHandle, byte CHAR_NOTIFY_STATUS, byte STATUS_REASON) {
//        XLog.d(TAG, "AciGattWriteCharValues() called with: portListenTask = [" + portListenTask + "], Connection_handle = [" + Connection_handle + "], attrHandle = [" + attrHandle + "], CHAR_NOTIFY_STATUS = [" + CHAR_NOTIFY_STATUS + "], STATUS_REASON = [" + STATUS_REASON + "]");
//        int ParameterTotalLength = 7;
//        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
//        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
//        System.arraycopy(OpCode.ACI_GATT_WRITE_CHARAC_VAL_opCode, 0, buffer, 1, 2);
//        buffer[3] = (byte) ParameterTotalLength;
//
//        //
//        int ValLen = 2;//值长度
//        buffer[8] = (byte) ValLen;
//        buffer[9] = CHAR_NOTIFY_STATUS;
//        buffer[10] = STATUS_REASON;
//        //
//        portListenTask.sendData(OpCode.ACI_GATT_WRITE_CHARAC_VAL_opCode, buffer);
//    }
}
