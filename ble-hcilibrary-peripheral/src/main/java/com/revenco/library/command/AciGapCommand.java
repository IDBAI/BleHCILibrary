package com.revenco.library.command;

import com.revenco.library.core.PeripharalManager;
import com.revenco.library.core.SerialPortListenTask;
import com.revenco.library.utils.XLog;

/**
 *
 */
public class AciGapCommand {
    private static final String TAG = "AciGapCommand";

    /**
     * [0x01,0x8A,0xFC,0x03,0x0F,0x00,0x10]
     *
     * @param portListenTask
     */
    public static void aciGapInit(SerialPortListenTask portListenTask) {
        XLog.d(TAG, "aciGapInit() called with: portListenTask = [" + portListenTask + "]");
        byte[] buffer = new byte[7];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.ACI_GAP_INIT_opCode, 0, buffer, 1, 2);
        buffer[3] = 0x03;//Parameter Total Length
        buffer[4] = AciCommandConfig.Role;
        buffer[5] = AciCommandConfig.privacy_enabled_NO;
        buffer[6] = 0x10;//Length of the device name characteristic,最多可以设置10个字母长度名称
        portListenTask.sendData(OpCode.ACI_GAP_INIT_opCode, buffer);
    }
    //仅仅在广播包中设置Name，测试成功
//    public static void aciGapSetDiscoverable(SerialPortListenTask portListenTask) {
//        XLog.d(TAG, "aciGapSetDiscoverable() called with: portListenTask = [" + portListenTask + "]");
//        byte[] LocalName = ("\t" + Config.DEVICE_NAME).getBytes();//前面需要添加\t
//        int Local_Name_Length = LocalName.length;
//        int ParameterTotalLength = 13 + Local_Name_Length;//21-8 = 13
//        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
//        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
//        System.arraycopy(OpCode.ACI_GAP_SET_DISCOVERABLE_opCode, 0, buffer, 1, 2);
//        buffer[3] = (byte) ParameterTotalLength;
//        //
//        buffer[4] = 0x00;//Advertising_Type,0x00 Connectable undirected advertising (ADV_IND) (default)
//        byte[] Advertising_Interval_Min = {(byte) 0x50, 0x00};//0x01E0 300ms
//        byte[] Advertising_Interval_Max = {(byte) 0x50, 0x00};//0x01E0 300ms
//        System.arraycopy(Advertising_Interval_Min, 0, buffer, 5, 2);
//        System.arraycopy(Advertising_Interval_Max, 0, buffer, 7, 2);
//        buffer[9] = 0x00;//Own_Address_Type
//        buffer[10] = 0x00;//Advertising_Filter_Policy
//        //Local_Name_Length
//        buffer[11] = (byte) Local_Name_Length;
//        //LocalName
//        System.arraycopy(LocalName, 0, buffer, 12, Local_Name_Length);
//        //Service_Uuid_length
//        buffer[12 + Local_Name_Length] = 0x00;
//        //UUIDs
////        buffer[13 + Local_Name_Length] = 0x07;//0x07:Complete list of 128-bit UUIDs available
//        //
//        //
//        byte[] Slave_Conn_Interval_Min = {0x06, 0x00};
//        byte[] Slave_Conn_Interval_Max = {0x06, 0x00};
//        System.arraycopy(Slave_Conn_Interval_Min, 0, buffer, 13 + Local_Name_Length, 2);
//        System.arraycopy(Slave_Conn_Interval_Max, 0, buffer, 15 + Local_Name_Length, 2);
//        //
//        //send
//        portListenTask.sendData(OpCode.ACI_GAP_SET_DISCOVERABLE_opCode, buffer);
//    }
    //在广播包中设置Name 和 2字节的UUID，测试成功
//    public static void aciGapSetDiscoverable(SerialPortListenTask portListenTask) {
//        XLog.d(TAG, "aciGapSetDiscoverable() called with: portListenTask = [" + portListenTask + "]");
//        byte[] LocalName = ("\t" + Config.DEVICE_NAME).getBytes();//前面需要添加\t
//        int Local_Name_Length = LocalName.length;
//        int ParameterTotalLength = 14 + 2 + Local_Name_Length;//21-8 = 13 + 1字节UUIDs = 14 + UUID 16字节
//        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
//        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
//        System.arraycopy(OpCode.ACI_GAP_SET_DISCOVERABLE_opCode, 0, buffer, 1, 2);
//        buffer[3] = (byte) ParameterTotalLength;
//        //
//        buffer[4] = 0x00;//Advertising_Type,0x00 Connectable undirected advertising (ADV_IND) (default)
//        byte[] Advertising_Interval_Min = {(byte) 0x50, 0x00};//0x01E0 300ms
//        byte[] Advertising_Interval_Max = {(byte) 0x50, 0x00};//0x01E0 300ms
//        System.arraycopy(Advertising_Interval_Min, 0, buffer, 5, 2);
//        System.arraycopy(Advertising_Interval_Max, 0, buffer, 7, 2);
//        buffer[9] = 0x00;//Own_Address_Type
//        buffer[10] = 0x00;//Advertising_Filter_Policy
//        //Local_Name_Length
//        buffer[11] = (byte) Local_Name_Length;
//        //LocalName
//        System.arraycopy(LocalName, 0, buffer, 12, Local_Name_Length);
//        //Service_Uuid_length AD type + UUID length = 17 (0x11)
//        buffer[12 + Local_Name_Length] = 0x03;
//        //UUIDs
//        buffer[13 + Local_Name_Length] = 0x02;//Complete list of 128-bit UUIDs available
//        //
//        System.arraycopy(new byte[]{(byte) 0x99, (byte) 0x99},0,buffer,14 + Local_Name_Length,2);
//        //
//        byte[] Slave_Conn_Interval_Min = {0x06, 0x00};
//        byte[] Slave_Conn_Interval_Max = {0x06, 0x00};
//        System.arraycopy(Slave_Conn_Interval_Min, 0, buffer, 16 + Local_Name_Length, 2);
//        System.arraycopy(Slave_Conn_Interval_Max, 0, buffer, 18 + Local_Name_Length, 2);
//        //
//        //send
//        portListenTask.sendData(OpCode.ACI_GAP_SET_DISCOVERABLE_opCode, buffer);
//    }

    /**
     * Advertising_Type:
     * 0x00 Connectable undirected advertising (ADV_IND) (default)
     * 0x02 Scannable undirected advertising (ADV_SCAN_IND)
     * 0x03 Non connectable undirected advertising (ADV_NONCONN_IND)
     * 0x04 - 0xFF Reserved for future use
     * <p>
     * Advertising_Interval_Min:
     * Minimum advertising interval for non-directed advertising.
     * Range: 0x0020 to 0x4000
     * Default: N = 0x0800 (1.28 second) Time = N * 0.625 msec
     * Time Range: 20 ms to 10.24 sec
     * 当前设置 N = 0x01E0   Time = N * 0.625 = 300 ms
     * 当前设置 N = 0x00A0   Time = N * 0.625 = 100 ms
     * 当前设置 N = 0x0050   Time = N * 0.625 = 50 ms
     * 当前设置 N = 0x0020   Time = N * 0.625 = 20 ms
     * <p>
     * Advertising_Interval_Max:
     * Maximum advertising interval for non-directed advertising.
     * Range: 0x0020 to 0x4000
     * Default: N = 0x0800 (1.28 seconds) Time = N * 0.625 msec
     * Time Range: 20 ms to 10.24 sec
     * <p>
     * Own_Address_Type:
     * 0x00 Public Device Address
     * 0x01 Random Device Address
     * 0x02 - 0xFF Reserved for future use
     * <p>
     * <p>
     * Advertising_Filter_Policy:
     * 0x00 Allow Scan Request from Any, Allow Connect Request from Any (default).
     * 0x01 Allow Scan Request from White List Only, Allow Connect Request from Any.
     * 0x02 Allow Scan Request from Any, Allow Connect Request from White List Only.
     * 0x03 Allow Scan Request from White List Only, Allow Connect Request from     White List Only.
     * 0x04 - 0xFF Reserved for future use.
     * <p>
     * Local_Name_Length:
     * Length of the Local name string in octets. If length is set to 0x00, Local_Name parameter should not be used.
     * <p>
     * Local_Name:
     * Local name of the device. This is an ASCII string without NULL character.
     * <p>
     * Service_Uuid_length:
     * Length of the Service Uuid List in octets. If there is no service to be advertised, set this field to 0x00.
     * <p>
     * Service_Uuid_List:(由于Service_Uuid_length为0，uuid_list就可以被缺省)
     * This is the list of the UUIDs AD Types as defined in Volume 3, Section 11.1.1 of GAP Specification.
     * <p>
     * <p>
     * Slave_Conn_Interval_Min:
     * Slave connection internal minimum value.
     * Connection interval is defined in the following manner:
     * connIntervalmin = Slave_Conn_Interval_Min * 1.25ms
     * Slave_Conn_Interval_Min range: 0x0006 to 0x0C80
     * Value of 0xFFFF indicates no specific minimum.
     * <p>
     * Slave_Conn_Interval_Max:
     * Slave connection internal maximum value.
     * ConnIntervalmax = Slave_Conn_Interval_Max * 1.25ms
     * Slave_Conn_Interval_Max range: 0x0006 to 0x0C80
     * Slave_ Conn_Interval_Max shall be equal to or greater than the Slave_Conn_Interval_Min.
     * Value of 0xFFFF indicates no specific maximum.
     * <p>
     * * 开启广播
     * HCI_COMMAND_PKT         ACI_GAP_SET_DISCOVERABLE_opCode                  Parameter Total Length   Advertising_Type  Advertising_Interval_Min     Advertising_Interval_Max    Own_Address_Type   Advertising_Filter_Policy   Local_Name_Length        Local_Name                              Service_Uuid_length      Slave_Conn_Interval_Min     Slave_Conn_Interval_Max
     * [0x01,                                               0x83,0xFC,           0x15, (21)                0x00,                    0x20,0x00,         0x20,0x00,                      0x00,           0x00,                       0x08,               0x09,0x42,0x6C,0x75,0x65,0x4E,0x52,0x47,             0x00,                0x00,0x00,               0x00,0x00]
     * <p>
     * //在广播包中不设置不重要的Name，仅仅设置了重要的16字节UUID
     * </p>
     *
     * @param portListenTask
     */
    public static void aciGapSetDiscoverable(SerialPortListenTask portListenTask) {
        XLog.d(TAG, "aciGapSetDiscoverable() called with: portListenTask = [" + portListenTask + "]");
        int ParameterTotalLength = 30;//21-8 = 13 + 1字节UUIDs = 14 + UUID 16字节
        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.ACI_GAP_SET_DISCOVERABLE_opCode, 0, buffer, 1, 2);
        buffer[3] = (byte) ParameterTotalLength;
        //
        buffer[4] = 0x00;//Advertising_Type,0x00 Connectable undirected advertising (ADV_IND) (default)
        byte[] Advertising_Interval_Min = {(byte) 0x50, 0x00};//0x01E0 300ms
        byte[] Advertising_Interval_Max = {(byte) 0x50, 0x00};//0x01E0 300ms
        System.arraycopy(Advertising_Interval_Min, 0, buffer, 5, 2);
        System.arraycopy(Advertising_Interval_Max, 0, buffer, 7, 2);
        buffer[9] = 0x00;//Own_Address_Type
        buffer[10] = 0x00;//Advertising_Filter_Policy
        //Local_Name_Length
        buffer[11] = 0x00;
        //Service_Uuid_length: AD type + UUID length = 17 (0x11)
        buffer[12] = 0x11;
        //UUIDs
        buffer[13] = 0x07;//0x07:Complete list of 128-bit UUIDs available
        //设置UUID
        System.arraycopy(PeripharalManager.getInstance().SERVICE_UUID, 0, buffer, 14, 16);
        //
        byte[] Slave_Conn_Interval_Min = {0x06, 0x00};
        byte[] Slave_Conn_Interval_Max = {0x06, 0x00};
        System.arraycopy(Slave_Conn_Interval_Min, 0, buffer, 30, 2);
        System.arraycopy(Slave_Conn_Interval_Max, 0, buffer, 32, 2);
        //send
        portListenTask.sendData(OpCode.ACI_GAP_SET_DISCOVERABLE_opCode, buffer);
    }

    //不设置name，也不设置Service UUID
//    public static void aciGapSetDiscoverable(SerialPortListenTask portListenTask) {
//        XLog.d(TAG, "aciGapSetDiscoverable() called with: portListenTask = [" + portListenTask + "]");
//        int ParameterTotalLength = 13;//21-8 = 13 + 1字节UUIDs = 14
//        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
//        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
//        System.arraycopy(OpCode.ACI_GAP_SET_DISCOVERABLE_opCode, 0, buffer, 1, 2);
//        buffer[3] = (byte) ParameterTotalLength;
//        //
//        buffer[4] = 0x00;//Advertising_Type,0x00 Connectable undirected advertising (ADV_IND) (default)
//        byte[] Advertising_Interval_Min = {(byte) 0x50, 0x00};//0x01E0 300ms
//        byte[] Advertising_Interval_Max = {(byte) 0x50, 0x00};//0x01E0 300ms
//        System.arraycopy(Advertising_Interval_Min, 0, buffer, 5, 2);
//        System.arraycopy(Advertising_Interval_Max, 0, buffer, 7, 2);
//        buffer[9] = 0x00;//Own_Address_Type
//        buffer[10] = 0x00;//Advertising_Filter_Policy
//        //Local_Name_Length
//        buffer[11] = 0x00;
//        //Service_Uuid_length: AD type + UUID length = 17 (0x11)
//        buffer[12] = 0x00;
//        //UUIDs
////        buffer[13] = 0x00;//0x07:Complete list of 128-bit UUIDs available
//        //
//        byte[] Slave_Conn_Interval_Min = {0x06, 0x00};
//        byte[] Slave_Conn_Interval_Max = {0x06, 0x00};
//        System.arraycopy(Slave_Conn_Interval_Min, 0, buffer, 13, 2);
//        System.arraycopy(Slave_Conn_Interval_Max, 0, buffer, 15, 2);
//        //send
//        portListenTask.sendData(OpCode.ACI_GAP_SET_DISCOVERABLE_opCode, buffer);
//    }

    /**
     * 把Service UUID 广播更新出来 -------> 测试失败，暂无使用
     *
     * @param portListenTask
     */
    public static void AciGapUpdateADVData(SerialPortListenTask portListenTask) {
        XLog.d(TAG, "AciGapUpdateADVData() called with: portListenTask = [" + portListenTask + "]");
//        int ParameterTotalLength = 16;
//        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
//        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
//        System.arraycopy(OpCode.ACI_GAP_UPDATE_ADV_DATA_opCode, 0, buffer, 1, 2);
//        int AdvDataLen = 15;
//        buffer[3] = (byte) ParameterTotalLength;
//        buffer[4] = (byte) AdvDataLen;
//        //把Service UUID 广播出来
//        System.arraycopy(Config.SERVICE_UUID, 0, buffer, 5, 15);
//        byte[] buffer = {0x01, (byte) 0x8E, (byte) 0xFC, 0x10, 0x0F, 0x0E, (byte) 0xFF, 0x00, 0x30, 0x53, 0x54, 0x5F, 0x41, 0x44, 0x56, 0x5F, 0x44, 0x61, 0x74, 0x61};
//         01 8E FC 10 0F 88 66 44 22 00 11 33 55 77 99 08 00 20 0C 9A
//         01 8E FC 10 0F 0E FF 00 30 53 54 5F 41 44 56 5F 44 61 74 61

        byte[] buffer = {0x01, (byte) 0x8E, (byte) 0xFC,0x10 ,0x0F , (byte) 0x88,0x66 ,0x44 ,0x22 ,0x00 ,0x11 ,0x33 ,0x55 ,0x77 , (byte) 0x99,0x08 ,0x00 ,0x20 ,0x0C , (byte) 0x9A};
        //send
        portListenTask.sendData(OpCode.ACI_GAP_UPDATE_ADV_DATA_opCode, buffer);
    }
}
