package com.revenco.aidllibrary.interfaces;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/3/17 16:42.</p>
 * <p>CLASS DESCRIBE :串口数据监听</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public interface SerialPortStatusDataListener {
    void onDataReceive(String devices, byte[] currentOpCode, byte[] data);

    void onStatusChange(String devices, int status);
}
