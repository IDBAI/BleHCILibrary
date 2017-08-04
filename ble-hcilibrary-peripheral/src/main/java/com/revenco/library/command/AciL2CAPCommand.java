package com.revenco.library.command;

import com.revenco.aidllibrary.CommonUtils.XLog;
import com.revenco.library.core.SerialPortListenTask;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017-05-16 10:09.</p>
 * <p>CLASS DESCRIBE :</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class AciL2CAPCommand {
    private static final String TAG = "AciL2CAPCommand";

    /**
     * 更新连接参数请求
     *
     * @param serialPortListenTask
     * @param connectHandler
     */
    public static void ConnectionParaUpdateRequest(SerialPortListenTask serialPortListenTask, byte[] connectHandler) {
        XLog.d(TAG, "ConnectionParaUpdateRequest()  请求更新连接参数!");
        int ParameterTotalLength = 0x0A;//10
        byte[] buffer = new byte[1 + 2 + 1 + ParameterTotalLength];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.Aci_L2CAP_Connection_Parameter_Update_Request_opCode, 0, buffer, 1, 2);
        buffer[3] = (byte) ParameterTotalLength;
        System.arraycopy(connectHandler, 0, buffer, 4, 2);
        System.arraycopy(AciCommandConfig.Slave_Conn_Interval_Min, 0, buffer, 6, 2);
        System.arraycopy(AciCommandConfig.Slave_Conn_Interval_Max, 0, buffer, 8, 2);
        System.arraycopy(AciCommandConfig.Slave_Latency, 0, buffer, 10, 2);
        System.arraycopy(AciCommandConfig.Timeout_multiplier, 0, buffer, 12, 2);
        //send
        serialPortListenTask.sendData(OpCode.Aci_L2CAP_Connection_Parameter_Update_Request_opCode, buffer);
    }

    /**
     * 请求更新参数回应
     *
     * @param serialPortListenTask
     * @param connectHandler          Handle received in Evt_Blue_L2CAP_Connection_Update_Req event.
     * @param Slave_Conn_Interval_Min Minimum connection interval received in Evt_Blue_L2CAP_Connection_Update_Req event.
     * @param Slave_Conn_Interval_Max Maximum connection interval received in Evt_Blue_L2CAP_Connection_Update_Req event.
     * @param Slave_Latency           Connection latency received in Evt_Blue_L2CAP_Connection_Update_Req event.
     * @param Supervision_Timeout     Supervision timeout received in Evt_Blue_L2CAP_Connection_Update_Req event.
     * @param Identifier              Identifier received in ACI_L2CAP_Connection_Update_Req event.
     * @param isAccept                0x00: The connection update parameters are not acceptable. 0x01: The connection update parameters are acceptable.
     */
    public static void ConnectionParaUpdateResponse(SerialPortListenTask serialPortListenTask, byte[] connectHandler, byte[] Slave_Conn_Interval_Min, byte[] Slave_Conn_Interval_Max, byte[] Slave_Latency, byte[] Supervision_Timeout, byte Identifier, byte isAccept) {
        XLog.d(TAG, "ConnectionParaUpdateResponse() 反馈更新连接参数！ ");
        int ParameterTotalLength = 0x0C;//12
        byte[] buffer = new byte[1 + 2 + 1 + ParameterTotalLength];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.Aci_L2CAP_Connection_Parameter_Update_Response_opCode, 0, buffer, 1, 2);
        buffer[3] = (byte) ParameterTotalLength;
        System.arraycopy(connectHandler, 0, buffer, 4, 2);
        System.arraycopy(Slave_Conn_Interval_Min, 0, buffer, 6, 2);
        System.arraycopy(Slave_Conn_Interval_Max, 0, buffer, 8, 2);
        System.arraycopy(Slave_Latency, 0, buffer, 10, 2);
        System.arraycopy(Supervision_Timeout, 0, buffer, 12, 2);
        buffer[14] = Identifier;
        buffer[15] = isAccept;
        //send
        serialPortListenTask.sendData(OpCode.Aci_L2CAP_Connection_Parameter_Update_Response_opCode, buffer);
    }

    public static void ConnectionParaUpdateResponse(SerialPortListenTask serialPortListenTask, byte[] connectHandler, byte[] Supervision_Timeout, byte Identifier, byte isAccept) {
        XLog.d(TAG, "ConnectionParaUpdateResponse() 反馈更新连接参数！ ");
        int ParameterTotalLength = 0x0C;//12
        byte[] buffer = new byte[1 + 2 + 1 + ParameterTotalLength];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.Aci_L2CAP_Connection_Parameter_Update_Response_opCode, 0, buffer, 1, 2);
        buffer[3] = (byte) ParameterTotalLength;
        System.arraycopy(connectHandler, 0, buffer, 4, 2);
        System.arraycopy(AciCommandConfig.Slave_Conn_Interval_Min, 0, buffer, 6, 2);
        System.arraycopy(AciCommandConfig.Slave_Conn_Interval_Max, 0, buffer, 8, 2);
        System.arraycopy(AciCommandConfig.Slave_Latency, 0, buffer, 10, 2);
        System.arraycopy(Supervision_Timeout, 0, buffer, 12, 2);
        buffer[14] = Identifier;
        buffer[15] = isAccept;
        //send
        serialPortListenTask.sendData(OpCode.Aci_L2CAP_Connection_Parameter_Update_Response_opCode, buffer);
    }
}
