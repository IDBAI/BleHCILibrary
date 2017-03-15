package com.revenco.library.core;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;

import com.revenco.library.command.AciCommandConfig;
import com.revenco.library.command.FlowControl;
import com.revenco.library.command.HCIVendorEcode;
import com.revenco.library.command.OpCode;
import com.revenco.library.utils.ConvertUtil;
import com.revenco.library.utils.XLog;

import java.util.ArrayList;
import java.util.Arrays;

import static com.revenco.library.command.AciCommandConfig.Command_Complete_Event;
import static com.revenco.library.command.AciCommandConfig.Command_Status_Event;
import static com.revenco.library.command.AciCommandConfig.Disconnect_Complete;
import static com.revenco.library.command.AciCommandConfig.HCI_VENDOR_ERROR;
import static com.revenco.library.command.AciCommandConfig.Hardware_Error_Event;
import static com.revenco.library.command.AciCommandConfig.LE_Advertising_Report_Sub_event_code;
import static com.revenco.library.command.AciCommandConfig.LE_Connection_Complete_Sub_event_code;
import static com.revenco.library.command.AciCommandConfig.LE_Connection_Update_Complete_Sub_event_code;
import static com.revenco.library.command.AciCommandConfig.LE_Long_Term_Key_Request_Event_Sub_event_code;
import static com.revenco.library.command.AciCommandConfig.LE_Read_Remote_Used_Features_Complete_Sub_event_code;
import static com.revenco.library.command.AciCommandConfig.LE__Event_code_Group;

/**
 * <p> company:wanzhong</p>
 * <p> Created by Administrator on 2017/2/27.</p>
 * <p> class describe: BLE外围数据提供，事件解析等服务，由 PeripharalManager 所管理</p>
 * <p> class_version: 1.0.0</p>
 */
public class PeripheralService extends Service {
    private static final int REVEICE_ID = 90;
    private static final String TAG = "PeripheralService";
    /**
     * 暴力等待重置广播事件，等待时间
     */
    private static final long INIT_HW_TIME = 100L;
    private static final int INIT_HW_WHAT = 100;
    /**
     * 是否在init进行中
     */
    public static volatile boolean isIniting = false;
    private Messenger messenger = new Messenger(new BLEHandler());
    private Handler mhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_HW_WHAT:
                    isIniting = true;
                    XLog.d(TAG, "等待超过 " + INIT_HW_TIME + " ms!");
                    XLog.d(TAG, "暴力init 广播！");
                    getApplicationContext().sendBroadcast(new Intent(FlowControl.ACTION_RESETHW_INIT));
                    break;
            }
            return false;
        }
    });
    private SerialDataReceiver.SerialDataListener listner = new SerialDataReceiver.SerialDataListener() {
        /**
         * @param devices
         * @param currentOpCode 本地缓存标识
         * @param data 串口返回的原始数据包
         *
         */
        @Override
        public void onDataReveice(String devices, byte[] currentOpCode, byte[] data) {
            // TODO 解析数据
            switch (devices) {
                case Config.SERIAL_BLE_DEVICE:
                    ParseData(currentOpCode, data);
                    break;
            }
        }

        @Override
        public void onStatusChange(String devices, int status) {
        }
    };

    public PeripheralService() {
    }

    /**
     * 根据currentopCode 去寻找 对应的返回包,仅仅需要对 0x0E返回的数据进行查找
     *
     * @param currentOpCode
     * @param data
     * @return
     */
    private byte[] FindPackage(byte[] currentOpCode, byte[] data) {
        XLog.d(TAG, "FindPackage() called with");
        XLog.d(TAG, " currentOpCode = [" + ConvertUtil.byte2HexStrWithSpace(currentOpCode) + "]");
        XLog.d(TAG, " data = [" + ConvertUtil.byte2HexStrWithSpace(data) + "]");
        byte[] destbyte = null;
        int start = 0;
        int end = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == currentOpCode[0] && ((i + 1) < data.length) && data[i + 1] == currentOpCode[1]) {
                //判断向前索引是否会越界
                if ((i - 4) >= 0) {
                    start = i;
                    end = i + 1;
                    break;
                }
            }
        }
        if ((end - start) == 1) {
            //find
            int paramlength = data[start - 2];
            int totalLength = paramlength + 3;
            destbyte = new byte[totalLength];
            System.arraycopy(data, start - 4, destbyte, 0, totalLength);
            XLog.d(TAG, "destbyte = [" + ConvertUtil.byte2HexStrWithSpace(destbyte) + "]");
        }
        return destbyte;
    }

    /**
     * 解析数据
     *
     * @param currentOpCode 本地缓存的opCode
     * @param data          数据为原始数据
     */
    private void ParseData(byte[] currentOpCode, byte[] data) {
        XLog.d(TAG, "ParseData() called with: currentOpCode = [" + currentOpCode + "], data = [" + data + "]");
        byte serailFlag = data[0];
        switch (serailFlag) {
            case AciCommandConfig.HCI_EVENT_PKT:
                try {
                    parseHCIEventPKT(currentOpCode, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
//             16 04 0E 04 01 03 0C 00  size = 8 (0~7)
//                兼容非04 开头的异常数据
                XLog.d(TAG, "出现异常数据，非0x04开头");
                for (int i = 0; i < data.length; i++) {
                    if (data[i] == (byte) 0x04) {
                        int valLen = data.length - i;
                        byte[] values = new byte[valLen];
                        System.arraycopy(data, i, values, 0, valLen);
                        XLog.d(TAG, "处理了异常数据");
                        try {
                            parseHCIEventPKT(currentOpCode, values);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                break;
        }
        return;
    }

    /**
     * @param currentOpCode
     * @param data          解析数据 必须是04 开头
     */
    private void parseHCIEventPKT(byte[] currentOpCode, byte[] data) {
        if (data[1] == LE__Event_code_Group) {//3E 组，判断SubEvent
            //data[2] 为Parameter Total Length
            //data[3] : Subevent_Code
            switch (data[3]) {
                case LE_Connection_Complete_Sub_event_code://有app连接事件
                    DealHCIEvent.dealConnectCompleteEvent(getApplicationContext(), data);
                    break;
                case LE_Connection_Update_Complete_Sub_event_code://app连接中，出现连接间隔被更改，产生连接更新事件
                    DealHCIEvent.dealConnectUpdate(getApplicationContext(), data);
                    break;
                case LE_Advertising_Report_Sub_event_code://do nothing
                    XLog.d(TAG, "LE_Advertising_Report_Sub_event_code");
                    break;
                case LE_Read_Remote_Used_Features_Complete_Sub_event_code://do nothing
                    XLog.d(TAG, "LE_Read_Remote_Used_Features_Complete_Sub_event_code");
                    break;
                case LE_Long_Term_Key_Request_Event_Sub_event_code://do nothing
                    XLog.d(TAG, "LE_Long_Term_Key_Request_Event_Sub_event_code");
                    break;
            }
        } else {
            switch (data[1]) {
                case Disconnect_Complete:// 0x05:断开连接事件
                    if (isIniting) {
                        XLog.d(TAG, "isIniting = true!");
                        return;
                    }
                    if (mhandler != null)
                        mhandler.removeMessages(INIT_HW_WHAT);
                    DealHCIEvent.dealDisconnectEvent(data);
                    XLog.d(TAG, "0x05:断开连接事件-->//TODO ACTION_ENABLE_ADVERTISING");
                    //TODO ACTION_ENABLE_ADVERTISING
                    sendBroadcast(new Intent(FlowControl.ACTION_ENABLE_ADVERTISING));
                    break;
                case Command_Complete_Event:// 0x0E:指令完成回调
                    commandCompleteEvent(currentOpCode, data);
                    break;
                case Command_Status_Event:
//                      04 0F  04 00 01 1C FD
//                      04 0F 04 00 01 06 04 size = 7
//                      [0x04,0x0F,0x04,0x00,0x01,0x06,0x04]
//                      04 0F 04 01 01 50 00 size = 7
                    XLog.e(TAG, "Command_Status_Event !");
                    try {
                        CommandStatusEvent(data);
                    } catch (Exception e) {//捕获越界异常
                        e.printStackTrace();
                    }
                    break;
                case Hardware_Error_Event://do nothing
                    XLog.e(TAG, "Hardware_Error_Event !");
                    break;
                case HCI_VENDOR_ERROR://0xFF: 厂商自定义HCI EVENT
                    HciVendorEvent(data);
                    break;
            }
        }
    }

    /**
     * //                      04 0F  04 00 01 1C FD
     * //                      04 0F 04 00 01 06 04 size = 7
     * //                      [0x04,0x0F,0x04,0x00,0x01,0x06,0x04]
     * //                      04 0F 04 01 01 50 00 size = 7
     * <p>
     * 解析指令的状态
     *
     * @param data
     */
    private void CommandStatusEvent(byte[] data) {
        int ParameterTotalLength = data[2];
        int status = data[3];
        int Num_HCI_Command_Packets = data[4];
        byte[] opCode = new byte[2];
        System.arraycopy(data, 5, opCode, 0, 2);
        XLog.d(TAG, "opCode = " + ConvertUtil.byte2HexStrWithSpace(opCode));
        XLog.d(TAG, "status = " + Integer.toHexString(status));
        XLog.d(TAG, "不需要判断status");
        //不需要判断status
        //写特征值成功
        if (Arrays.equals(opCode, OpCode.ACI_GATT_WRITE_CHARAC_VAL_opCode)) {
        }
        //断开连接指令状态成功
        else if (Arrays.equals(opCode, OpCode.HCI_Disconnect_opCode)) {
            XLog.d(TAG, "* hci disconnect success! ");
            // TODO: 2017/3/10
            XLog.d(TAG, "断开连接指令状态成功");
            XLog.d(TAG, "等待断开指令返回！");
            if (mhandler != null)
                mhandler.sendEmptyMessageDelayed(INIT_HW_WHAT, INIT_HW_TIME);
        } else {
            // TODO: 2017/3/14 当做异常，暴力复位
            XLog.e(TAG, "// TODO: 2017/3/14 当做异常，暴力复位 ");
            if (mhandler != null)
                mhandler.sendEmptyMessageDelayed(INIT_HW_WHAT, INIT_HW_TIME);
        }
    }

    /**
     * @param data 完整包 eg：
     *             <p>
     *             //   [0x04,0xFF,0x03,    0x01,0x00, 0x01]
     *             //
     *             //   [0x04,0xFF,0x0B,    0x01,0x0C, 0x01,0x08,0x1D,0x00,0x02,0x00,0x00,0x01,0x00]
     *             <p>
     *             <p>
     *             04 FF 09 > 11 0C	 01 08 04 12 1E 00 01 		04 FF 06 > 10 0C 01 08 01 41
     *             <p>
     *             分割 0x04 0xFF 多包粘连情况
     * @param data
     * @return
     */
    private ArrayList<byte[]> splitFFPackage(byte[] data) throws Exception {
        ArrayList<byte[]> list = new ArrayList<>();
        for (int i = 0; i < data.length; ) {
            if (data[i] == (byte) 0x04 && data[i + 1] == (byte) 0xFF) {
                int paramLen = data[i + 2];
                int totalLen = 3 + paramLen;
                byte[] dest = new byte[totalLen];
                System.arraycopy(data, i, dest, 0, totalLen);
                list.add(dest);
                i += totalLen;//跨出一帧
            } else {
                i++;
            }
        }
        return list;
    }

    /**
     * @param data 完整单包，或者是多包
     */
    private void HciVendorEvent(byte[] data) {
        XLog.d(TAG, "HciVendorEvent() called with: data = [" + ConvertUtil.byte2HexStrWithSpace(data) + "]");
        ArrayList<byte[]> arrayList = null;
        try {
            arrayList = splitFFPackage(data);
            for (byte[] bytes : arrayList) {
                ParseHciVendorEvent(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param data 完整单包
     */
    private void ParseHciVendorEvent(byte[] data) {
        XLog.d(TAG, "ParseHciVendorEvent() called with: data = [" + ConvertUtil.byte2HexStrWithSpace(data) + "]");
        byte[] Ecode = new byte[2];
        System.arraycopy(data, 3, Ecode, 0, 2);
        if (Arrays.equals(Ecode, HCIVendorEcode.EVT_BLUE_INITIALIZED_Ecode)) {
            byte[] Reason_Code = new byte[2];
            System.arraycopy(data, 5, Reason_Code, 0, 1);
            DealCommandResult.dealWithErrorResult(getApplicationContext(), Reason_Code);
        }
        //HCI EVENT
        else if (Arrays.equals(Ecode, HCIVendorEcode.EVT_BLUE_GATT_ATTRIBUTE_MODIFIED_Ecode)) {
            //TODO
            XLog.d(TAG, "属性被更改！");
            DealHCIEvent.dealGattAttributeValues(getApplicationContext(), data);
        } else if (Arrays.equals(Ecode, HCIVendorEcode.EVT_BLUE_GATT_ERROR_RESPONSE_Ecode)) {
            XLog.e(TAG, "EVT_BLUE_GATT_ERROR_RESPONSE!!");
            DealHCIEvent.dealGattProErrorRes(getApplicationContext(), data);
        } else if (Arrays.equals(Ecode, HCIVendorEcode.EVT_BLUE_GATT_PROCEDURE_COMPLETE_Ecode)) {
            XLog.d(TAG, "EVT_BLUE_GATT_PROCEDURE_COMPLETE!");
            DealHCIEvent.dealGattProComplete(getApplicationContext(), data);
        } else if (Arrays.equals(Ecode, HCIVendorEcode.EVT_BLUE_GATT_PROCEDURE_TIMEOUT_Ecode)) {
            XLog.e(TAG, "EVT_BLUE_GATT_PROCEDURE_TIMEOUT_Ecode");
//            //TODO 重启广播
//            getApplicationContext().sendBroadcast(new Intent(FlowControl.ACTION_ENABLE_ADVERTISING));
            getApplicationContext().sendBroadcast(new Intent(FlowControl.ACTION_HCI_DISCONNECT));
        } else {
            XLog.e(TAG, "请解析其他事件！data：" + ConvertUtil.byte2HexStrWithSpace(data));
        }
//                    paramlen          ecode      connect_handle       data_len        data buffer
//        04 FF         09               11 0C      	 01 08           04               12 1E 00 01  <EVT_BLUE_GATT_ERROR_RESPONSE_Ecode>
//
//  		04 FF       06                10 0C          01 08              01             41           <Evt_Blue_Gatt_Procedure_Complete>
//Indicates whether the procedure completed with error
//        (BLE_STATUS_FAILED) or was successful
//        (BLE_STATUS_SUCCESS).
    }

    /**
     * 事件完成
     *
     * @param currentOpCode 0x01,0xFD
     * @param data          原始数据
     */
    private void commandCompleteEvent(byte[] currentOpCode, byte[] data) {
        /**
         * 需要容错处理，解析多帧，不完整帧等意外数据
         * 找与currentOpCode对应的包
         * */
        data = FindPackage(currentOpCode, data);
        if (data == null) {
            XLog.e(TAG, "can not find legal buffer,please check it !!");
            // TODO: 2017/3/14 当做遇到严重错误，进行reset操作
            XLog.e(TAG, "TODO: 2017/3/14 当做遇到严重错误，进行reset操作");
            sendBroadcast(new Intent(FlowControl.ACTION_RESETHW_INIT));
            return;
        }
        //
        //data[2] 为Parameter Total Length
        int paramLen = data[2];
        byte[] paramContent = new byte[paramLen];//参数内容,不包括长度
        System.arraycopy(data, 3, paramContent, 0, paramLen);
        XLog.d(TAG, "commandCompleteEvent() called with");
        XLog.d(TAG, "currentOpCode : " + ConvertUtil.byte2HexStrWithSpace(currentOpCode));
        XLog.d(TAG, "context : " + ConvertUtil.byte2HexStrWithSpace(paramContent));
        //1、open
        if (Arrays.equals(currentOpCode, OpCode.open_command_opCode)) {
            DealCommandResult.dealOpenResult(getApplicationContext(), currentOpCode, paramContent);
        }
        //2、start
        else if (Arrays.equals(currentOpCode, OpCode.hwReset_command_opCode)) {
            DealCommandResult.dealHWResetResult(getApplicationContext(), currentOpCode, paramContent);
        }
        //3、config
        else if (Arrays.equals(currentOpCode, OpCode.ACI_HAL_WRITE_CONFIG_DATA_opCode)) {
            DealCommandResult.dealConfigDataResult(getApplicationContext(), currentOpCode, paramContent);
        }
        //4、set tx power
        else if (Arrays.equals(currentOpCode, OpCode.ACI_HAL_SET_TX_POWER_LEVEL_opCode)) {
            DealCommandResult.dealSetTxPowerResult(getApplicationContext(), currentOpCode, paramContent);
        }
        //5、gatt init
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GATT_INIT_opCode)) {
            DealCommandResult.dealGattInitResult(getApplicationContext(), currentOpCode, paramContent);
        }
        //6、gap init
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GAP_INIT_opCode)) {
            DealCommandResult.dealGapInitResult(getApplicationContext(), currentOpCode, paramContent);
        }
        //7、gatt add service
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GATT_ADD_SERVICE_opCode)) {
            DealCommandResult.dealGattAddService(getApplicationContext(), currentOpCode, paramContent);
        }
        //8、gatt add char
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GATT_ADD_CHAR_opCode)) {
            DealCommandResult.dealGattAddChar(getApplicationContext(), currentOpCode, paramContent);
        }
        //9 添加描述符
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GATT_ADD_CHAR_DESC_opCode)) {
            DealCommandResult.dealAddCharDesc(getApplicationContext(), currentOpCode, paramContent);
        }
        //10、设置广播数据
        else if (Arrays.equals(currentOpCode, OpCode.HCI_LE_SET_SCAN_RESPONSE_DATA_opCode)) {
            DealCommandResult.dealSetScanResponseData(getApplicationContext(), currentOpCode, paramContent);
        }
        //11、ACI_Gap 开启广播
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GAP_SET_DISCOVERABLE_opCode)) {
            DealCommandResult.dealAciGapDisCoverable(getApplicationContext(), currentOpCode, paramContent);
        }
        //12 更新广播数据
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GAP_UPDATE_ADV_DATA_opCode)) {
            DealCommandResult.dealAciGapUpdateADVData(getApplicationContext(), currentOpCode, paramContent);
        }
        //13、notify
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GATT_UPD_CHAR_VAL_opCode)) {
            DealCommandResult.dealGattUpdateCharValResult(getApplicationContext(), currentOpCode, paramContent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        SerialDataReceiver.registListener(REVEICE_ID, listner);
        return messenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        SerialDataReceiver.unregistListener(REVEICE_ID);
        return super.onUnbind(intent);
    }

    private class BLEHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //TODO 接收manager 发过来的event
            switch (msg.what) {
            }
        }
    }
}
