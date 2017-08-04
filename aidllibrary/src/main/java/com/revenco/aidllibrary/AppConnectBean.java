package com.revenco.aidllibrary;

import android.os.Parcel;
import android.os.Parcelable;

import com.revenco.aidllibrary.CommonUtils.AppConnectStatus;
import com.revenco.aidllibrary.CommonUtils.ConvertUtil;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/3/1 15:52.</p>
 * <p>CLASS DESCRIBE :连接的app模型</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class AppConnectBean implements Parcelable {
    public static final Creator<AppConnectBean> CREATOR = new Creator<AppConnectBean>() {
        @Override
        public AppConnectBean createFromParcel(Parcel in) {
            return new AppConnectBean(in);
        }

        @Override
        public AppConnectBean[] newArray(int size) {
            return new AppConnectBean[size];
        }
    };
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

    protected AppConnectBean(Parcel in) {
        appMac = in.readString();
        Connection_Handle = in.createByteArray();
        Role = in.readByte();
        Peer_Address_Type = in.readByte();
        Conn_Interval = in.createByteArray();
        Conn_Latency = in.createByteArray();
        Supervision_Timeout = in.createByteArray();
        Master_Clock_Accuracy = in.readByte();
    }

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
    public int describeContents() {
        return 0;
    }

    //注意读取变量和写入变量的顺序应该一致 不然得不到正确的结果
    public void readFromParcel(Parcel source) {
        appMac = source.readString();
        Connection_Handle = source.createByteArray();
        Role = source.readByte();
        Peer_Address_Type = source.readByte();
        Conn_Interval = source.createByteArray();
        Conn_Latency = source.createByteArray();
        Supervision_Timeout = source.createByteArray();
        Master_Clock_Accuracy = source.readByte();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appMac);
        dest.writeByteArray(Connection_Handle);
        dest.writeByte(Role);
        dest.writeByte(Peer_Address_Type);
        dest.writeByteArray(Conn_Interval);
        dest.writeByteArray(Conn_Latency);
        dest.writeByteArray(Supervision_Timeout);
        dest.writeByte(Master_Clock_Accuracy);
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
