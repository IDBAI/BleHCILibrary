package com.revenco.library.Bean;

import com.revenco.library.others.AppConnectStatus;
import com.revenco.library.utils.ConvertUtil;

import java.io.Serializable;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/3/1 15:52.</p>
 * <p>CLASS DESCRIBE :连接的app模型</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class AppConnectBean implements Serializable {
    private static final long serialVersionUID = 6349199861046043887L;
    public String appMac;
    public byte[] Connection_Handle;
    public byte Role;
    public byte Peer_Address_Type;
    public byte[] Conn_Interval;
    public byte[] Conn_Latency;
    public byte[] Supervision_Timeout;
    public byte Master_Clock_Accuracy;
    public AppConnectStatus status;

    public AppConnectBean(String appMac, byte[] connection_Handle, byte role, byte peer_Address_Type, byte[] conn_Interval, byte[] conn_Latency, byte[] supervision_Timeout, byte master_Clock_Accuracy, AppConnectStatus status) {
        this.appMac = appMac;
        Connection_Handle = connection_Handle;
        Role = role;
        Peer_Address_Type = peer_Address_Type;
        Conn_Interval = conn_Interval;
        Conn_Latency = conn_Latency;
        Supervision_Timeout = supervision_Timeout;
        Master_Clock_Accuracy = master_Clock_Accuracy;
        this.status = status;
    }

    public AppConnectBean(String appMac, byte[] connection_Handle, byte[] conn_Interval, byte[] conn_Latency, byte[] supervision_Timeout, AppConnectStatus status) {
        this.appMac = appMac;
        Connection_Handle = connection_Handle;
        Conn_Interval = conn_Interval;
        Conn_Latency = conn_Latency;
        Supervision_Timeout = supervision_Timeout;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppConnectBean that = (AppConnectBean) o;
        return appMac.equals(that.appMac);
    }

    @Override
    public String toString() {
        return "AppConnectBean{" +
                "appMac='" + appMac + '\'' +
                ", Connection_Handle=" + ConvertUtil.byte2HexStrWithSpace(Connection_Handle) +
                ", Role_ALL=" + Role +
                ", Peer_Address_Type=" + Peer_Address_Type +
                ", Conn_Interval=" + ConvertUtil.byte2HexStrWithSpace(Conn_Interval) +
                ", Conn_Latency=" + ConvertUtil.byte2HexStrWithSpace(Conn_Latency) +
                ", Supervision_Timeout=" + ConvertUtil.byte2HexStrWithSpace(Supervision_Timeout) +
                ", Master_Clock_Accuracy=" + Master_Clock_Accuracy +
                ", status=" + status +
                '}';
    }

    public String toTestString() {
        return
                "appMac='" + appMac + '\'' +
                        ", Connection_Handle=" + ConvertUtil.byte2HexStrWithSpace(Connection_Handle) +
                        ", Conn_Interval=" + ConvertUtil.byte2HexStrWithSpace(Conn_Interval) +
                        ", Conn_Latency=" + ConvertUtil.byte2HexStrWithSpace(Conn_Latency) +
                        ", Supervision_Timeout=" + ConvertUtil.byte2HexStrWithSpace(Supervision_Timeout) +
                        ", status=" + status;
    }

    @Override
    public int hashCode() {
        return appMac.hashCode();
    }
}
