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

//
    /**
     *  Role = Peripheral|Broadcaster|Central|Observer
     *  Bitmap of allowed roles: 0x01 : Peripheral 0x02 : Broadcaster 0x04 : Central 0x08 : Observer
     */
    public static final byte Role = 0x0F;
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
//
//// 下面是Event相关的对象
//
//class Hci_Uart_Pckt {
//
//	public	byte  type;
//	public  byte  len=1;
//	public  byte  dataleft[];
//
//	Hci_Uart_Pckt(byte data[]){
//		type = data[0];
//		len = 1;
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
//class Hci_Event_Pckt{
//	public	byte  evt;
//	public  byte  plen;
//	public  byte  len=2;
//	public  byte  dataleft[];
//
//	Hci_Event_Pckt(byte data[]){
//		evt = data[0];
//		plen = data[1];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
//class Evt_Disconn_Complete{
//	public	byte  status;
//	public  int   handle; //取两个字节
//	public  byte  reason;
//	public  byte  len=4;
//
//
//	Evt_Disconn_Complete(byte data[]){
//		status = data[0];
//		handle = data[2]<< 8 | data[1];
//		reason = data[3];
//
//	}
//
//}
//
//class Evt_Le_Meta_Event{
//	public	byte  subevent;
//	public  byte  len=1;
//	public  byte  dataleft[];
//
//	Evt_Le_Meta_Event(byte data[]){
//		subevent = data[0];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
//class Evt_Le_Connection_Complete{
//	public	byte  status;
//	public  byte[]  handle = new byte[2];
//	public	byte  role;
//	public	byte  peer_bdaddr_type;
//	public	byte[] peer_bdaddr = new byte[6];
//	public  int  interval;
//	public  int  latency;
//	public  int  supervision_timeout;
//	public	byte  master_clock_accuracy;
//	public  byte  len=18;
//
//
//	Evt_Le_Connection_Complete(byte data[]){
//		status = data[0];
//		handle[0] = data[1];
//		handle[1] = data[2];
//		role = data[3];
//		peer_bdaddr_type = data[4];
//		System.arraycopy(data, 5, peer_bdaddr, 0, peer_bdaddr.length);
//		interval = data[12]<< 8 | data[11];
//		latency = data[14]<< 8 | data[13];
//		supervision_timeout = data[16]<< 8 | data[15];
//		master_clock_accuracy = data[17];
//
//	}
//}
//
////广播原始数据
//class Le_Advertising_Info{
//	public	byte  num_reports;
//	public  byte  evt_type;
//	public	byte  bdaddr_type;
//	public	byte[] bdaddr = new byte[6];
//	public  byte  data_length;
//	public  byte  len=9; //data_length 放在 dataleft 方便后面判断
//	public  byte  dataleft[]; //最后一个数据是RSSI
//
//	Le_Advertising_Info(byte data[]){
//
//		num_reports = data[0];
//		evt_type = data[1];
//		bdaddr_type = data[2];
//		System.arraycopy(data, 3, bdaddr, 0, bdaddr.length);
//		data_length = data[9];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
////广播数据（设备名字）
//class Le_Advertising_Device_Name{
//	public  byte  name_type; // (lenght of this data)
//	public  byte name[];
//	public  byte  RSSI; //最后一个数据是RSSI
//
//	Le_Advertising_Device_Name(byte data[]){
//
//		name_type = data[0];
//		name = new byte[data.length-1-1];  //长度：去掉前面和后面的RSSI
//		System.arraycopy(data, 1, name, 0, data.length-1-1);
//		RSSI = data[data.length-1];
//	}
//
//}
//
////设备信息结构体
//class Device_Info{
//	public	byte  bdaddr_type;
//	public	byte[] bdaddr = new byte[6];
//	public	byte[]  name;
//	public	byte[]  connect_handle = new byte[2];
//	public	byte  status;
//	public	byte  tx_handle[]=new byte[2];
//	public	byte  rx_handle[]=new byte[2];
//	public	byte  attribute;
//
//	@Override
//	public boolean equals(Object o) {
//		// TODO Auto-generated method stub
//		Device_Info device = new Device_Info();
//		device = (Device_Info)o;
//		if(Arrays.equals(this.bdaddr,device.bdaddr)){
//			return true;
//		}else {
//			return false;
//		}
//
//	}
//}
//
//class Evt_Blue_Aci{
//	public	int  ecode;
//	public  byte  len=2;
//	public  byte  dataleft[];
//
//	Evt_Blue_Aci(byte data[]){
//		ecode = data[1]<< 8 | data[0];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
//class Evt_Gatt_Attr_Modified{
//	public	int  conn_handle;
//	public	int attr_handle;
//	public  byte data_length;
//	public	int offset;
//	public  byte  len=7;
//	public  byte  dataleft[];
//
//	Evt_Gatt_Attr_Modified(byte data[]){
//		conn_handle = data[1]<< 8 | data[0];
//		attr_handle = data[3]<< 8 | data[2];
//		data_length = data[4];
//		offset = data[6]<< 8 | data[5];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
//class Evt_Gatt_Notification{
//	public	int  conn_handle;
//	public  byte event_data_length;
//	public	int  attr_handle;
//	public  byte  len=5;
//	public  byte  dataleft[];
//
//	Evt_Gatt_Notification(byte data[]){
//		conn_handle = data[1]<< 8 | data[0];
//		event_data_length = data[2];
//		attr_handle = data[4]<< 8 | data[3];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
//class Evt_L2cap_Conn_Upd_Resp{
//	public	int  conn_handle;
//	public  byte event_data_length;
//	public	byte  code;
//	public	byte identifier;
//	public	int	 l2cap_length;
//	public	int   result;
//	public  byte  len=9;
//	public  byte  dataleft[];
//
//	Evt_L2cap_Conn_Upd_Resp(byte data[]){
//		conn_handle = data[1]<< 8 | data[0];
//		event_data_length = data[2];
//		code = data[3];
//		identifier = data[4];
//		l2cap_length = data[6]<< 8 | data[5];
//		result = data[8]<< 8 | data[7];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
//class Evt_L2cap_Conn_Upd_Req{
//	public	int  conn_handle;
//	public  byte event_data_length;
//	public	byte identifier;
//	public	int	 l2cap_length;
//	public	int interval_min;
//	public	int interval_max;
//	public	int slave_latency;
//	public	int timeout_mult;
//
//	public  byte  len=14;
//	public  byte  dataleft[];
//
//	Evt_L2cap_Conn_Upd_Req(byte data[]){
//		conn_handle = data[1]<< 8 | data[0];
//		event_data_length = data[2];
//		identifier = data[3];
//		l2cap_length = data[5]<< 8 | data[4];
//		interval_min = data[7]<< 8 | data[6];
//		interval_max = data[9]<< 8 | data[8];
//		slave_latency = data[11]<< 8 | data[10];
//		timeout_mult = data[13]<< 8 | data[12];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
//class Evt_Gatt_Disc_Read_Char_By_Uuid_Resp{
//	public	int  conn_handle;
//	public  byte event_data_length;
//	public	int attr_handle;
//	public  byte  len=5;
//	public  byte  dataleft[];
//
//	Evt_Gatt_Disc_Read_Char_By_Uuid_Resp(byte data[]){
//		conn_handle = data[1]<< 8 | data[0];
//		event_data_length = data[2];
//		attr_handle = data[4]<< 8 | data[3];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
//class Evt_Att_Find_Information_Resp{
//	public	int  conn_handle;
//	public  byte event_data_length;
//	public	byte format;
//	public  byte  len=4;
//	public  byte  dataleft[];
//
//	Evt_Att_Find_Information_Resp(byte data[]){
//		conn_handle = data[1]<< 8 | data[0];
//		event_data_length = data[2];
//		format = data[3];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
//class Evt_Att_Read_By_Group_Resp{
//	public	int  conn_handle;
//	public  byte event_data_length;
//	public  byte attribute_data_length;
//	public  byte  len=4;
//	public  byte  dataleft[];
//
//	Evt_Att_Read_By_Group_Resp(byte data[]){
//		conn_handle = data[1]<< 8 | data[0];
//		event_data_length = data[2];
//		attribute_data_length = data[3];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
//class Evt_Att_Find_By_Type_Val_Resp {
//	public	int  conn_handle;
//	public  byte event_data_length;
//	public  byte  len=3;
//	public  byte  dataleft[];
//
//	Evt_Att_Find_By_Type_Val_Resp(byte data[]) {
//		conn_handle = data[1]<< 8 | data[0];
//		event_data_length = data[2];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
//class Evt_Att_Read_By_Type_Resp{
//	public	byte  conn_handle[] = new byte[2];
//	public  byte event_data_length;
//	public  byte handle_value_pair_length;
//	public  byte  len=4;
//	public  byte  dataleft[];
//
//	Evt_Att_Read_By_Type_Resp(byte data[]){
//		conn_handle[0] =  data[0];
//		conn_handle[1] =  data[1];
//		event_data_length = data[2];
//		handle_value_pair_length = data[3];
//		dataleft = new byte[data.length-len];
//		System.arraycopy(data, len, dataleft, 0, data.length-len);
//	}
//
//	public byte[] getLeftData() {
//		// TODO Auto-generated method stub
//		return  dataleft;
//	}
//}
//
////连接参数
//class Gap_Create_Connection_Cp{
//	  byte  scanInterval[] = new byte[2];
//	  byte scanWindow[]=new byte[2];
//	  byte peer_bdaddr_type;
//	  byte peer_bdaddr[] =  new byte[6];
//	  byte own_bdaddr_type;
//	  byte conn_min_interval[]=new byte[2];
//	  byte conn_max_interval[]= new byte[2];
//	  byte conn_latency[]= new byte[2];
//	  byte supervision_timeout[]= new byte[2];
//	  byte min_conn_length[]= new byte[2];
//	  byte max_conn_length[]= new byte[2];
//	  public static final byte data_len = 0x18;
//}

