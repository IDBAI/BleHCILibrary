package com.revenco.library.command;

import com.revenco.library.core.SerialPortListenTask;
import com.revenco.library.utils.XLog;

public class AciHciCommand {
    private static final String TAG = "AciHciCommand";

    /**
     * [0x01,0x03,0x0C,0x00]
     *
     * @param serialPortListenTask
     */
    public static void bleHwReset(SerialPortListenTask serialPortListenTask) {
        XLog.d(TAG, "bleHwReset() called with: serialPortListenTask = [" + serialPortListenTask + "]");
        byte[] buffer = new byte[4];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.hwReset_command_opCode, 0, buffer, 1, 2);
        buffer[3] = (byte) (0x00);
        serialPortListenTask.sendData(OpCode.hwReset_command_opCode, buffer);
    }

    /**
     * 将 blemac 地址和 service uuid 通过广播事件广播出来。
     * HCI_LE_SET_SCAN_RESPONSE_DATA_opCode      Parameter Total Length      Scan_Response_Data_Length
     * [0x01,          0x09,0x20,                                  0x20,(32)               0x00,                     0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00  ]
     * <p>
     * Scan_Response_Data_Length: The number of significant octets in the following data field
     * Scan_Response_Data : 31 octets of data formatted as defined in [Vol 3] Part C, Section 11. All octets zero (default).
     *
     * @param serialPortListenTask
     */
    public static void setBleScanResponseData(SerialPortListenTask serialPortListenTask) {
        XLog.d(TAG, "setBleScanResponseData() called with: serialPortListenTask = [" + serialPortListenTask + "]");
        int ParameterTotalLength = 32;
        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.HCI_LE_SET_SCAN_RESPONSE_DATA_opCode, 0, buffer, 1, 2);
        buffer[3] = (byte) ParameterTotalLength;
        int Scan_Response_Data_Length = 0;
        buffer[4] = (byte) Scan_Response_Data_Length;
        //TODO 把service uuid 广播出来-->无效
//        System.arraycopy(Config.SERVICE_UUID, 0, buffer, 5, 16);
        //TODO 把 mac 广播出来-->无效
//        System.arraycopy(Config.BLE_PUBLIC_MAC_ADDRESS, 0, buffer, 21, 6);
        //send
        serialPortListenTask.sendData(OpCode.HCI_LE_SET_SCAN_RESPONSE_DATA_opCode, buffer);
    }

    /**
     * 终止连接
     *
     * @param serialPortListenTask
     * @param Connection_Handle
     */
    public static void hciDisconnect(SerialPortListenTask serialPortListenTask, byte[] Connection_Handle) {
        XLog.d(TAG, "hciDisconnect() called with: serialPortListenTask = [" + serialPortListenTask + "], Connection_Handle = [" + Connection_Handle + "]");
        int ParameterTotalLength = 3;
        byte[] buffer = new byte[3 + 1 + ParameterTotalLength];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.HCI_Disconnect_opCode, 0, buffer, 1, 2);
        buffer[3] = (byte) ParameterTotalLength;
        System.arraycopy(Connection_Handle, 0, buffer, 4, 2);
        int reason = 0x13;
        buffer[6] = (byte) reason;
        //send
        serialPortListenTask.sendData(OpCode.HCI_Disconnect_opCode, buffer);
    }
}
