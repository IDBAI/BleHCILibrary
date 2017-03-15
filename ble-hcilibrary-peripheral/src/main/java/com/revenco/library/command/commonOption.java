package com.revenco.library.command;

import com.revenco.library.core.SerialPortListenTask;
import com.revenco.library.utils.XLog;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/2/27 15:54.</p>
 * <p>CLASS DESCRIBE :</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class commonOption {
    private static final String TAG = "commonOption";
    /**
     * [0x01,0x01,0x10,0x00]
     *
     * @param portListenTask
     */
    public static void open(SerialPortListenTask portListenTask) {
        XLog.d(TAG, "open() called with: portListenTask = [" + portListenTask + "]");
        byte[] data = new byte[4];
        data[0] = AciCommandConfig.HCI_COMMAND_PKT;
        System.arraycopy(OpCode.open_command_opCode, 0, data, 1, 2);
        data[3] = 0x00;//length
        portListenTask.sendData(OpCode.open_command_opCode, data);
    }
}
