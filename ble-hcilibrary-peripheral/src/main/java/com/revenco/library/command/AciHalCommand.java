package com.revenco.library.command;

import com.revenco.aidllibrary.CommonUtils.XLog;
import com.revenco.library.core.PeripharalManager;
import com.revenco.library.core.SerialPortListenTask;

/**
 *
 */
public class AciHalCommand {
    private static final String TAG = "AciHalCommand";

    /**
     * 配置模式
     * 1 [0x01,0x0C,0xFC,0x03,0x2D,0x01,0x01]
     *
     * @param portListenTask
     */
    public static void writeConfig_ModeData(SerialPortListenTask portListenTask) {
        XLog.d(TAG, "writeConfig_ModeData() called with: portListenTask = [" + portListenTask + "]");
        byte[] buffer = new byte[7];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.ACI_HAL_WRITE_CONFIG_DATA_opCode, 0, buffer, 1, 2);
        buffer[3] = 0x03;//Parameter Total Length
        buffer[4] = AciCommandConfig.ROLE;
        buffer[5] = 0x01;//length
        buffer[6] = AciCommandConfig.MODE_2;//Value
        portListenTask.sendData(OpCode.ACI_HAL_WRITE_CONFIG_DATA_opCode, buffer);
    }

    /**
     * [0x01,0x0C,0xFC,0x08,  0x00,   0x06,        0x66,0x55,0x44,0x33,0x22,0x11]
     * <p>
     * 配置公共地址
     *
     * @param portListenTask
     */
    public static void writeConfig_PublicAddrData(SerialPortListenTask portListenTask) {
        XLog.d(TAG, "writeConfig_PublicAddrData() called with: portListenTask = [" + portListenTask + "]");
        byte[] buffer = new byte[12];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.ACI_HAL_WRITE_CONFIG_DATA_opCode, 0, buffer, 1, 2);
        buffer[3] = 0x08;//Parameter Total Length
        buffer[4] = AciCommandConfig.CONFIG_DATA_PUBADDR_OFFSET;
        buffer[5] = 0x06;//length
        System.arraycopy(PeripharalManager.getInstance().BLE_PUBLIC_MAC_ADDRESS, 0, buffer, 6, 6);//Value
        portListenTask.sendData(OpCode.ACI_HAL_WRITE_CONFIG_DATA_opCode, buffer);
    }

    /**
     * 设置功率
     * [0x01,0x0F,0xFC,0x02,0x01,0x04]
     *
     * @param portListenTask
     */
    public static void writeConfig_TxPowerData(SerialPortListenTask portListenTask) {
        XLog.d(TAG, "writeConfig_TxPowerData() called with: portListenTask = [" + portListenTask + "]");
        byte[] buffer = new byte[6];
        buffer[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.ACI_HAL_SET_TX_POWER_LEVEL_opCode, 0, buffer, 1, 2);
        buffer[3] = 0x02;//Parameter Total Length
        buffer[4] = AciCommandConfig.En_High_Power_ON;//En_High_Power ,Can be only 0 or 1. Set high power bit on or off.
        buffer[5] = 0x07;//PA_Level PA Level.
        portListenTask.sendData(OpCode.ACI_HAL_SET_TX_POWER_LEVEL_opCode, buffer);
    }
}
