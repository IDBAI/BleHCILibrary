package com.revenco.library.deals;

import android.content.Context;
import android.util.SparseArray;

import com.revenco.aidllibrary.AppConnectBean;
import com.revenco.aidllibrary.CharBean;
import com.revenco.aidllibrary.CommonUtils.AppConnectStatus;
import com.revenco.aidllibrary.CommonUtils.ConvertUtil;
import com.revenco.aidllibrary.CommonUtils.Helper;
import com.revenco.aidllibrary.CommonUtils.Tools;
import com.revenco.aidllibrary.CommonUtils.XLog;
import com.revenco.aidllibrary.interfaces.FlowControlListener;
import com.revenco.library.command.AciCommandConfig;
import com.revenco.library.command.HCIVendorEcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/3/1 11:35.</p>
 * <p>CLASS DESCRIBE :处理HCI事件类，一般是app连接产生。</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class DealHCIEvent {
    private static final String TAG = "DealHCIEvent";

    /**
     * [0x04,0x3E,0x13, 0x01,    0x00,   0x01,0x08,  0x01,         0x01,    0x92,0xA7,0x22,0xBC,0x92,0x4B,          0x27,0x00,      0x00,0x00,  0xD0,0x07,  0x05]
     * <p>
     * <p>
     * 04 0E 04 01 83 FC 00
     * <p>
     * 04 3E 13 01 00 10
     * <p>
     * 08 01 00 D4 4B CB 64 A3 14                         27 00 00 00 D0 07                   05]
     *
     * @param context
     * @param listener
     * @param data     // 0x00 Connection is master
     *                 // 0x01 Connection is slave
     *                 // 0x02-0xFF Reserved for future use
     *                 //
     *                 // 0x00 Peer is using a Public Device Address
     *                 // 0x01 Peer is using a Random Device Address
     *                 // 0x02-0xFF Reserved for future use
     *                 //
     *                 // Public Device Address or Random Device Address of the peer device
     *                 //
     *                 // Connection interval used on this connection.
     *                 // Range: 0x0006 to 0x0C80 Time = N * 1.25 msec
     *                 // Time Range: 7.5 msec to 4000 msec.
     *                 //
     *                 //Connection latency for this connection.
     *                 // Range: 0x0006 to 0x0C80 Time = N * 1.25 msec
     *                 // Time Range: 7.5 msec to 4000 msec.
     *                 //
     *                 // N = 0xXXXX
     *                 // Supervision timeout for the LE Link.
     *                 // (See [Vol 6] Part B, Section 4.5.2)
     *                 // Range: 0x000A to 0x0C80
     *                 // Time = N * 10 msec
     *                 // Time Range: 100 msec to 32 seconds
     *                 // 0x0000 - 0x0009 and 0x0C81 - 0xFFFF Reserved for future use
     *                 //
     *                 // 0x00 500 ppm
     *                 // 0x01 250 ppm
     *                 // 0x02 150 ppm
     *                 // 0x03 100 ppm
     *                 // 0x04 75 ppm
     *                 // 0x05 50 ppm
     *                 // 0x06 30 ppm
     *                 // 0x07 20 ppm
     *                 // 0x08-0xFF Reserved for future use
     *                 //
     */
    public static void dealConnectCompleteEvent(Context context, FlowControlListener listener, byte[] data) {
        XLog.d(TAG, "dealConnectCompleteEvent() called with: context = [" + context + "], data = [" + data + "]");
        int status = data[4];
        switch (status) {
            case AciCommandConfig.EVENT_BLE_STATUS_SUCCESS:
                XLog.d(TAG, "have an app connected!");
                byte[] Connection_Handle = new byte[2];
                System.arraycopy(data, 5, Connection_Handle, 0, 2);
                byte Role = data[7];
                byte Peer_Address_Type = data[8];
                byte[] Peer_Address = new byte[6];//TODO app的mac
                System.arraycopy(data, 9, Peer_Address, 0, 6);
                //存储
                String appMac = ConvertUtil.byte2HexStrWithComma(Peer_Address, ":");
                Tools.setConnectAppBleMacAddr(context, appMac);
                byte[] Conn_Interval = new byte[2];
                System.arraycopy(data, 15, Conn_Interval, 0, 2);
                byte[] Conn_Latency = new byte[2];
                System.arraycopy(data, 17, Conn_Latency, 0, 2);
                byte[] Supervision_Timeout = new byte[2];
                System.arraycopy(data, 19, Supervision_Timeout, 0, 2);
                byte Master_Clock_Accuracy = data[21];
                //
                AppConnectBean appConnectBean = new AppConnectBean(appMac, Connection_Handle, Role, Peer_Address_Type, Conn_Interval, Conn_Latency, Supervision_Timeout, Master_Clock_Accuracy, AppConnectStatus.status_connected);
                publicAppConnec(listener, appConnectBean);
                break;
        }
    }

    /**
     * [0x04,0x3E,0x0A,     0x03,    0x00,      0x01,0x08,   0x06,0x00,0x00,0x00,0xD0,0x07]
     *
     * @param context
     * @param listener
     * @param data
     */
    public static void dealConnectUpdate(Context context, FlowControlListener listener, byte[] data) {
        XLog.d(TAG, "dealConnectUpdate() called ");
        int status = data[4];
        switch (status) {
            case AciCommandConfig.EVENT_BLE_STATUS_SUCCESS:
                XLog.d(TAG, "connected had update!");
                byte[] Connection_Handle = new byte[2];
                System.arraycopy(data, 5, Connection_Handle, 0, 2);
                byte[] Conn_Interval = new byte[2];
                System.arraycopy(data, 7, Conn_Interval, 0, 2);
                byte[] Conn_Latency = new byte[2];
                System.arraycopy(data, 9, Conn_Latency, 0, 2);
                byte[] Supervision_Timeout = new byte[2];
                System.arraycopy(data, 11, Supervision_Timeout, 0, 2);
                //
                String appMac = Tools.getConnectAppBleMacAddr(context);
                AppConnectBean appConnectBean = new AppConnectBean(appMac, Connection_Handle, Conn_Interval, Conn_Latency, Supervision_Timeout, AppConnectStatus.status_connect_update);
                publicAppConnec(listener, appConnectBean);
                break;
        }
    }

    /**
     * 处理接收到的特征值数据values
     * [0x04,0xFF,0x0B,         0x01,0x0C,          0x01,0x08,          0x04,0x00,      0x02,   0x00,0x00,0x01,0x00]
     * <<<------------ ：04 FF 0B 01 0C 01 08 1D 00 02 00 00 01 00 size = 14
     *
     * @param context
     * @param listener
     * @param data     //
     */
    public static void dealGattAttributeValues(Context context, FlowControlListener listener, byte[] data) {
        XLog.d(TAG, "dealGattAttributeValues() called ");
        //解析多帧，可能存在的多帧同事过来
        List<byte[]> frameList = parseMultFrame(data);
        XLog.d(TAG, "解析了连续 " + frameList.size() + " 帧！");
        for (byte[] frame : frameList) {
            dealsingleFrame(context, listener, frame);
        }
    }

    /**
     * 解析多帧或单帧
     * <p>
     * <p>
     * // EVENT        opcode  Connection_Handle     attr_handle      valueLen       offset
     * //    04 FF 1D  01 0C     01 08             10 00          14          00 00           11 22 33 44 55 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02
     * //    04 FF 1D  01 0C     01 08             12 00          14          00 00           11 22 33 44 55 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03       size = 64
     * //    04 FF 1D  01 0C     01 08             12 00          14          00 00           11 22 33 44 55 00 00 00 00 00 00 00 00 00 00 00 00 00 00 04  完整帧
     * //    04 FF 1D  01 0C     01 08             12 00          14          00 00           11 22 33 44 55 00 00 00 00 09         不完整帧
     * //    04 FF 1D  01 0C     01 08             18 00          14          00 00           11 22 33 44 55 00 00 00 00 00 00 00 00 00 00 00 00 00 00 06
     * //    04 FF 0B  01 0C     01 08             1E 00          02          00 00           01 00   notify
     *
     * @param data
     * @return
     */
    private static List<byte[]> parseMultFrame(byte[] data) {
        List<byte[]> framesList = new ArrayList<>();
        for (int i = 0; i < data.length; ) {
            if (data[i] == 0x04 && data[i + 1] == (byte) 0xFF) {//要添加 (byte) ，否则BUG
                byte parambyteLen = data[i + 2];//后续的参数长度
                int frameLen = parambyteLen + 3;
                byte[] frame = new byte[frameLen];
                System.arraycopy(data, i, frame, 0, frameLen);
                framesList.add(frame);
                i += frameLen;//跨出一帧
            } else {
                i++;
            }
        }
        return framesList;
    }

    /**
     * 处理单帧
     *
     * @param context
     * @param listener
     * @param data
     */
    private static void dealsingleFrame(Context context, FlowControlListener listener, byte[] data) {
        XLog.d(TAG, "data = " + ConvertUtil.byte2HexStrWithSpace(data));
        XLog.d(TAG, "data = " + ConvertUtil.byte2HexStrWithSpace(data));
        //The connection handle which modified the attribute.
        byte[] Connection_Handle = new byte[2];
        System.arraycopy(data, 5, Connection_Handle, 0, 2);
        //Handle of the attribute that was modified.
        byte[] Attr_Handle = new byte[2];
        System.arraycopy(data, 7, Attr_Handle, 0, 2);
        //Length of the data.
        XLog.d(TAG, "接收到数据，其 Attr_Handle = " + ConvertUtil.byte2HexStrWithSpace(Attr_Handle));
        int Data_Length = data[9];
        if (Data_Length > 0) {
            //Offset from which the write has been performed by the peer device.
            byte[] Offset = new byte[2];
            System.arraycopy(data, 10, Offset, 0, 2);
            //The new value (length is data_length)
            byte[] Attr_Data = new byte[Data_Length];
            System.arraycopy(data, 12, Attr_Data, 0, Data_Length);
            //
            publicAttrValues(context, listener, Attr_Handle, Attr_Data);
        }
    }

    /**
     * [0x04,0x05,0x04,0x00,0x01,0x08,0x08]
     *
     * @param data
     */
    public static void dealDisconnectEvent(byte[] data) {
        XLog.d(TAG, "dealDisconnectEvent() called ");
        int status = data[3];
        switch (status) {
            case AciCommandConfig.EVENT_BLE_STATUS_SUCCESS:
                byte reason = data[6];
                switch (reason) {
                    case 0x08:
                        XLog.e(TAG, "Connection Timeout!");
                        break;
                    case 0x13:
                        XLog.e(TAG, "Remote user Terminated Connection!");
                        break;
                }
                break;
        }
    }

    /**
     * 发布app连接状态
     *
     * @param listener
     * @param appConnectBean
     */
    private static void publicAppConnec(FlowControlListener listener, AppConnectBean appConnectBean) {
        XLog.d(TAG, "publicAppConnec() called ");
        if (listener != null)
            listener.appConnect(appConnectBean);
    }

    /**
     * 发布接收到的有效数据
     *
     * @param context
     * @param listener
     * @param Attr_Handle
     * @param attr_data
     */
    private static void publicAttrValues(Context context, FlowControlListener listener, byte[] Attr_Handle, byte[] attr_data) {
        XLog.d(TAG, "publicAttrValues() called with: " + "\n"
                + " attr_Handle = [" + ConvertUtil.byte2HexStrWithSpace(Attr_Handle) + "]," +
                "\n attr_data = [" + ConvertUtil.byte2HexStrWithSpace(attr_data) + "]");
        SparseArray<CharBean> charBeanSparseArray = Helper.charBeanSparseArray;
        for (int i = 0; i < charBeanSparseArray.size(); i++) {
            CharBean charBean = charBeanSparseArray.get(i);
            if (Arrays.equals(charBean.attr_Handle, Attr_Handle)) {//匹配
                String appMac = Tools.getConnectAppBleMacAddr(context);
                XLog.d(TAG, "发布数据！"
                        + "\n char_uuid :" + ConvertUtil.byte2HexStrWithSpace(charBean.char_uuid)
                        + "\n attr_data :" + ConvertUtil.byte2HexStrWithSpace(attr_data));
                if (listener != null)
                    listener.receiveAttVal(appMac, charBean.char_uuid, attr_data);
            }
        }
    }

    /**
     * @param listener
     * @param data     完整包
     */
    public static void dealGattProComplete(FlowControlListener listener, byte[] data) {
        XLog.d(TAG, "dealGattProComplete() called ");
        XLog.d(TAG, "解析 procedure 完成事件！");
        byte[] connect_handle = new byte[2];
        System.arraycopy(data, 5, connect_handle, 0, 2);
        int data_len = data[7];
        byte[] data_buffer = new byte[data_len];
        System.arraycopy(data, 8, data_buffer, 0, data_len);
        byte Evt_Blue_Gap_Procedure_Complete_status = data_buffer[0];
        switch (Evt_Blue_Gap_Procedure_Complete_status) {
            case HCIVendorEcode.BLE_STATUS_SUCCESS:
                //TODO 发送成功事件
                XLog.d(TAG, "gatt procedure complete success!");
                XLog.d(TAG, "TODO 发送成功事件");
                break;
            case HCIVendorEcode.BLE_STATUS_FAILED:
                XLog.e(TAG, "BLE_STATUS_FAILED!");
                break;
            case HCIVendorEcode.ERR_AUTH_FAILURE:
                XLog.e(TAG, "ERR_AUTH_FAILURE");
                break;
            case HCIVendorEcode.INVALID_PARAMETERS:
                XLog.e(TAG, "INVALID_PARAMETERS");
                break;
        }
    }

    /**
     * @param data
     */
    public static void dealGattProErrorRes(byte[] data) {
        XLog.d(TAG, "解析 procedure 返回错误数据");
        byte[] connect_handle = new byte[2];
        System.arraycopy(data, 5, connect_handle, 0, 2);
        int data_len = data[7];
        byte[] data_buffer = new byte[data_len];
        System.arraycopy(data, 8, data_buffer, 0, data_len);
    }
}
