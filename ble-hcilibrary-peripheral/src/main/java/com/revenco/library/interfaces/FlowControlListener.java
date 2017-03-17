package com.revenco.library.interfaces;

import com.revenco.library.Bean.AppConnectBean;
import com.revenco.library.others.FlowStatus;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/3/17 17:14.</p>
 * <p>CLASS DESCRIBE :流程控制监听</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public interface FlowControlListener {
    void flowStatusChange(FlowStatus status, byte[]... data);

    void appConnect(AppConnectBean appConnectBean);

    void receiveAttVal(FlowStatus status, String appBleMac, byte[] char_uuid, byte[] values);

    void verifyCertificate(byte status,byte reason);
}
