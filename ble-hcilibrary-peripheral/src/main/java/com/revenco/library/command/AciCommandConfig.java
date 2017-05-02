package com.revenco.library.command;

public class AciCommandConfig {
    //  Supported HCI events
//	The table below lists the HCI events supported by the BlueNRG-MS.
//	=========================eventCode------start
    public static final byte Disconnect_Complete = 0x05;
    public static final byte Encryption_Change_Event = 0x08;
    public static final byte Read_Remote_Version_Information_Complete = 0x0C;
    public static final byte Command_Complete_Event = 0x0E;
    public static final byte Command_Status_Event = 0x0F;
    public static final byte Hardware_Error_Event = 0x10;
    public static final byte Number_Of_Completed_Packets = 0x13;
    public static final byte Encryption_Key_Refresh_Complete = 0x30;
    public static final byte HCI_VENDOR_ERROR = (byte) 0xFF;
    //=======================eventCode----------subEventCode
    public static final byte LE__Event_code_Group = 0x3E;
    public static final byte LE_Connection_Complete_Sub_event_code = 0x01;
    public static final byte LE_Advertising_Report_Sub_event_code = 0x02;
    public static final byte LE_Connection_Update_Complete_Sub_event_code = 0x03;
    public static final byte LE_Read_Remote_Used_Features_Complete_Sub_event_code = 0x04;
    public static final byte LE_Long_Term_Key_Request_Event_Sub_event_code = 0x05;
    //	=========================eventCode------start
    //	========================= event status------start
//    0x00: BLE_STATUS_SUCCESS
//    0x12: ERR_INVALID_HCI_CMD_PARAMS
    public static final byte EVENT_BLE_STATUS_SUCCESS = 0x00;
    public static final byte ERR_INVALID_HCI_CMD_PARAMS = 0x12;
    //	========================= event status------end
    //add char command return status-------start
    public static final byte STATUS_ADD_CHAR_SUCCESS = 0x00;
    public static final byte STATUS_ADD_CHAR_Error = 0x47;
    public static final byte STATUS_ADD_CHAR_Out_of_Memory = 0x1F;
    public static final byte STATUS_ADD_CHAR_Invalid_handle = 0x60;
    public static final byte STATUS_ADD_CHAR_Invalid_parameter = 0x61;
    public static final byte STATUS_ADD_CHAR_Out_of_handle = 0x62;
    public static final byte STATUS_ADD_CHAR_Insufficient_resources = 0x64;
    public static final byte STATUS_ADD_CHAR_Character_Already_Exists = 0x66;
    //add char command return status-------end
    //add char desc command return status-------start
    public static final byte STATUS_Success = 0x00;//Success
    public static final byte STATUS_Error = 0x47;//Error
    public static final byte STATUS_OOM = 0x1F;//Out of Memory
    public static final byte STATUS_InValid_Handle = 0x60;//Invalid handle
    public static final byte STATUS_InValid_Param = 0x61;//Invalid parameter
    public static final byte STATUS_OOH = 0x62;//Out of handle
    public static final byte STATUS_InValid_Operation = 0x63;//Invalid Operation
    public static final byte STATUS_Insufficient_resources = 0x64;//Insufficient resources
    //add char desc command return status-------end
    //	========================= offsets------start
    public static final byte CONFIG_DATA_PUBADDR_OFFSET = 0x00;
    public static final byte CONFIG_DATA_DIV_OFFSET = 0x06;
    public static final byte CONFIG_DATA_ER_OFFSET = 0x08;
    public static final byte CONFIG_DATA_IR_OFFSET = 0x18;
    public static final byte LL_WITHOUT_HOST = 0x2C;
    public static final byte ROLE = 0x2D;
    //	========================= offsets------end
    //	========================= stack mode------start
    public static final byte MODE_1 = 0x01;
    public static final byte MODE_2 = 0x02;
    public static final byte MODE_3 = 0x03;
    //	========================= stack mode------end
//	========================= En_High_Power------start
    public static final byte En_High_Power_ON = 0x01;
    public static final byte En_High_Power_OFF = 0x00;
//	========================= En_High_Power------end
//If En_High_Power=0
// 0 = -18 dBm, 1 = -15 dBm, 2 = -12 dBm,
// 3 = -9 dBm, 4 = -6 dBm, 5 = -2 dBm,
// 6 =  0 dBm, 7 =  5 dBm.
//
// If En_High_Power=1
// 0 = -14 dBm, 1 = -11 dBm, 2 = -8 dBm,
// 3 = -5 dBm, 4 = -2 dBm, 5 =  2 dBm,
// 6 =  4 dBm, 7 =  8 dBm
//    Offset of the element in the configuration data structure which has to be written.
// The valid offsets are:
// 0x00 = CONFIG_DATA_PUBADDR_OFFSET,
// 0x06 = CONFIG_DATA_DIV_OFFSET,
// 0x08 = CONFIG_DATA_ER_OFFSET,
// 0x18 = CONFIG_DATA_IR_OFFSET,
// 0x2C = LL_WITHOUT_HOST,
// 0x2D = ROLE
    /**
     * Role_ALL = Peripheral|Broadcaster|Central|Observer
     * Bitmap of allowed roles:
     * 0x01 : Peripheral
     * 0x02 : Broadcaster
     * 0x04 : Central
     * 0x08 : Observer
     */
    public static final byte Role_Peripheral = 0x01;
    public static final byte Role_Broadcaster = 0x02;
    public static final byte Role_Central = 0x04;
    public static final byte Role_Observer = 0x08;
    public static final byte Role_ALL = Role_Peripheral | Role_Broadcaster | Role_Central | Role_Observer;//0x0F;
//
    //    0x00: Privacy is not enabled. 0x01: Privacy is enabled.
    public static final byte privacy_enabled_YES = 0x01;
    public static final byte privacy_enabled_NO = 0x00;
    // HCI TYPE
    public static final byte HCI_COMMAND_PKT = 0x01;
    public static final byte HCI_ACLDATA_PKT = 0x02;
    public static final byte HCI_SCODATA_PKT = 0x03;
    public static final byte HCI_EVENT_PKT = 0x04;
    public static final byte HCI_VENDOR_PKT = (byte) 0xFF;
//
//    // ROLE
//    public static final byte CONFIG_GAP_CENTRAL_ROLE = 0x03;
//    public static final byte GAP_BROADCASTER_ROL = 0x02;
//    public static final byte GAP_CENTRAL_ROLE = 0x04;
//    public static final byte GAP_OBSERVER_ROLE = 0x08;
//    // Event Type
//    public static final byte EVT_LE_META_EVENT = 0x3E;
//    public static final byte EVT_LE_CONN_COMPLETE = 0x01;
//    public static final byte EVT_LE_ADVERTISING_REPORT = 0x02;
//    public static final byte AD_TYPE_COMPLETE_LOCAL_NAME = 0x09;
//    public static final byte EVT_VENDOR = (byte) 0xFF;
//    public static final int EVT_BLUE_GAP_PROCEDURE_COMPLETE = 0x0407;
//    public static final int EVT_BLUE_ATT_READ_BY_TYPE_RESP = 0x0C06;
//    public static final int EVT_BLUE_GATT_PROCEDURE_COMPLETE = 0x0C10;
//    public static final int EVT_BLUE_GAP_PASS_KEY_REQUEST = 0x0402;
//    //PREF_ADVERTISING_REAL_DATA
//    public static final byte FACTORY_DATA[] = {(byte) 0xff, 0x30, 0x00, 0x05};
//    public static final byte PREF_WANZHONG_DEVICE_NAME[] = {'W', 'Z', '/', 'B', 'L', 'E'};
//    //device status
//    public static final byte OFFLINE = 0X00;
//    public static final byte CONNECTABLE = 0X01;
//    public static final byte CONNECTED = 0X03;
}
