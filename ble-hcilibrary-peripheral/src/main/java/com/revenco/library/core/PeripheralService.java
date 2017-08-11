package com.revenco.library.core;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.SparseArray;

import com.revenco.aidllibrary.AppConnectBean;
import com.revenco.aidllibrary.CharBean;
import com.revenco.aidllibrary.CommonUtils.AppConnectStatus;
import com.revenco.aidllibrary.CommonUtils.ConfigProcess;
import com.revenco.aidllibrary.CommonUtils.Constants;
import com.revenco.aidllibrary.CommonUtils.ConvertUtil;
import com.revenco.aidllibrary.CommonUtils.FlowStatus;
import com.revenco.aidllibrary.CommonUtils.Helper;
import com.revenco.aidllibrary.CommonUtils.Utils;
import com.revenco.aidllibrary.CommonUtils.XLog;
import com.revenco.aidllibrary.CommonUtils.byteUtils;
import com.revenco.aidllibrary.MasterHelper;
import com.revenco.aidllibrary.interfaces.FlowControlListener;
import com.revenco.aidllibrary.interfaces.SerialPortStatusDataListener;
import com.revenco.certificateverifylib.core.SignVerify;
import com.revenco.library.command.AciCommandConfig;
import com.revenco.library.command.AciGapCommand;
import com.revenco.library.command.AciGattCommand;
import com.revenco.library.command.AciHalCommand;
import com.revenco.library.command.AciHciCommand;
import com.revenco.library.command.CharacteristicProperty;
import com.revenco.library.command.HCIVendorEcode;
import com.revenco.library.command.OpCode;
import com.revenco.library.deals.DealCommandResult;
import com.revenco.library.deals.DealHCIEvent;
import com.revenco.library.others.AttrOrder;
import com.revenco.library.others.CommandOptions;
import com.revenco.library.others.Config;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.revenco.aidllibrary.CommonUtils.ConfigProcess.config_mode;
import static com.revenco.aidllibrary.CommonUtils.ConfigProcess.config_publicAddress;
import static com.revenco.aidllibrary.CommonUtils.FlowStatus.STATUS_RESETHW_INIT;
import static com.revenco.aidllibrary.CommonUtils.Helper.ACTION_APP_CONNECT_STATUS;
import static com.revenco.aidllibrary.CommonUtils.Helper.ACTION_REVEIVE_ATTRIBUTE_VALUES;
import static com.revenco.aidllibrary.CommonUtils.Helper.ACTON_FLOWCONTROL_STATUS;
import static com.revenco.aidllibrary.CommonUtils.Helper.CHAR_SET_SIZE;
import static com.revenco.aidllibrary.CommonUtils.Helper.EXTRA_APPBEAN;
import static com.revenco.aidllibrary.CommonUtils.Helper.EXTRA_APPMAC;
import static com.revenco.aidllibrary.CommonUtils.Helper.EXTRA_CHAR_UUID;
import static com.revenco.aidllibrary.CommonUtils.Helper.EXTRA_CHAR_VALUES;
import static com.revenco.aidllibrary.CommonUtils.Helper.MSG_REMOVE_WAITING_TIMER;
import static com.revenco.aidllibrary.CommonUtils.Helper.MSG_TEST_SEND_NOTIFY;
import static com.revenco.aidllibrary.CommonUtils.Helper.charBeanSparseArray;
import static com.revenco.aidllibrary.CommonUtils.Helper.currentHasConfig;
import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.VerifySuccess;
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
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class PeripheralService extends Service implements SerialPortStatusDataListener, FlowControlListener {
    private static final String TAG = "PeripheralService";
    private static final byte WRITE_PROPERTIES = CharacteristicProperty.PROPERTY_WRITE | CharacteristicProperty.PROPERTY_WRITE_NO_RESPONSE;
    /**
     * 使用indicate属性
     */
    private static final byte NOFITY_PROPERTIES = CharacteristicProperty.PROPERTY_INDICATE | CharacteristicProperty.PROPERTY_NOTIFY | CharacteristicProperty.PROPERTY_READ;
    /**
     * 最多发送notify的次数
     */
    private static final int MAX_NOFITY_TIME = 4;
    /**
     * 暴力等待重置广播事件，等待时间
     */
    private static final long INIT_HW_DELAY = 300L;
    private static final int INIT_HW_WHAT = 10000;
    private static final int APP_CONNECT_WHAT = 10001;
    /**
     * 是否在init进行中
     */
    public static volatile boolean isIniting = false;
    public static volatile FlowStatus CURRENT_STATUS = FlowStatus.STATUS_NONE;
    private int sendNotifyTime = 0;
    private Messenger messenger = new Messenger(new BLEHandler());
    private HashMap<byte[], byte[]> UUIDAttrValuesHashMap = new HashMap<>();
    private String lastConnectAppmac;
    private byte[][] uuidlist = {
            Config.CHAR_UUID_WRITE_00,
            Config.CHAR_UUID_WRITE_01,
            Config.CHAR_UUID_WRITE_02,
            Config.CHAR_UUID_WRITE_03,
            Config.CHAR_UUID_WRITE_04,
            Config.CHAR_UUID_WRITE_05,
            Config.CHAR_UUID_WRITE_06
    };
    private byte[] connection_handle;
    private byte identifier;
    private byte isAccept;
    private Handler mhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_HW_WHAT:
                    isIniting = true;
                    XLog.e(TAG, "等待超过 " + INIT_HW_DELAY + " ms , reset HW !");
                    PeripheralService.this.flowStatusChange(STATUS_RESETHW_INIT);
                    break;
                case APP_CONNECT_WHAT:
                    XLog.d(TAG, "时间到，断开连接！");
                    PeripheralService.this.flowStatusChange(FlowStatus.STATUS_HCI_READY_DISCONNECT);
                    break;
            }
            return false;
        }
    });

    /**
     * @param devices
     * @param currentOpCode 本地缓存标识
     * @param data          串口返回的原始数据包
     */
    @Override
    public void onDataReceive(String devices, byte[] currentOpCode, byte[] data) {
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

    @Override
    public IBinder onBind(Intent intent) {
        XLog.d(TAG, "onBind() called with: intent = [" + intent + "]");
        PeripharalManager.getInstance().getListenTask().setSerialPortDataListener(this);
        return messenger.getBinder();
    }

    public void removeWaitingResetHWTimer() {
        XLog.d(TAG, "移除等待指令的reset HW 计时");
        if (mhandler != null)
            mhandler.removeMessages(INIT_HW_WHAT);
    }

    /**
     * 根据currentopCode 去寻找 对应的返回包,仅仅需要对 0x0E返回的数据进行查找
     *
     * @param currentOpCode
     * @param data
     * @return
     */
    private synchronized byte[] FindPackage(byte[] currentOpCode, byte[] data) {
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
    private synchronized void ParseData(byte[] currentOpCode, byte[] data) {
        XLog.d(TAG, "ParseData() called with: currentOpCode = [" + ConvertUtil.byte2HexStrWithSpace(currentOpCode) + "], data = [" + ConvertUtil.byte2HexStrWithSpace(data) + "]");
        byte serailFlag = data[0];
        switch (serailFlag) {
            case AciCommandConfig.HCI_EVENT_PKT:
                try {
                    parseHCIEventPKT(currentOpCode, data);
                    //TODO note : 非指令完成事件，即被动接受app的事件，才解析可能存在的多包，否则容易出现逻辑错误，因为
                    //HCI指令解析完成将触发下一个HCI指令，如果解析了后续的多包，则出现逻辑异常！
                    if (data[1] != Command_Complete_Event) {
                        parseExitMorePack(currentOpCode, data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                XLog.d(TAG, "出现异常数据，非0x04开头");
                for (int i = 0; i < data.length; i++) {
                    if (data[i] == (byte) 0x04) {
                        try {
                            int valLen = data.length - i;
                            byte[] values = new byte[valLen];
                            System.arraycopy(data, i, values, 0, valLen);
                            XLog.d(TAG, "处理了异常数据");
                            parseHCIEventPKT(currentOpCode, values);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //此处一般越界,进行reset
                            this.flowStatusChange(STATUS_RESETHW_INIT);
                        }
                        break;
                    } else if (i == data.length - 1) {
                        //遍历到最后一个，仍然不为0x04
                        this.flowStatusChange(STATUS_RESETHW_INIT);
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
        XLog.d(TAG, "parseHCIEventPKT() called ");
        if (data[1] == LE__Event_code_Group) { //3E 组，判断SubEvent
            //data[2] 为Parameter Total Length
            //data[3] : Subevent_Code
            switch (data[3]) {
                case LE_Connection_Complete_Sub_event_code://有app连接事件
                    DealHCIEvent.dealConnectCompleteEvent(getApplicationContext(), this, data);
                    break;
                case LE_Connection_Update_Complete_Sub_event_code://app连接中，出现连接间隔被更改，产生连接更新事件
                    DealHCIEvent.dealConnectUpdate(getApplicationContext(), this, data);
                    break;
                case LE_Advertising_Report_Sub_event_code://do nothing
                    XLog.d(TAG, "le_advertising_report_sub_event_code");
                    break;
                case LE_Read_Remote_Used_Features_Complete_Sub_event_code://do nothing
                    XLog.d(TAG, "le_read_remote_used_features_complete_sub_event_code");
                    break;
                case LE_Long_Term_Key_Request_Event_Sub_event_code://do nothing
                    XLog.d(TAG, "le_long_term_key_request_event_sub_event_code");
                    break;
            }
        } else {
            switch (data[1]) {
                case Disconnect_Complete:// 0x05:断开连接事件
                    XLog.d(TAG, "0x05:断开连接事件");
                    removeWaitingResetHWTimer();
                    if (isIniting) {
                        XLog.d(TAG, "isIniting = true!");
                        return;
                    }
                    DealHCIEvent.dealDisconnectEvent(data);
                    XLog.d(TAG, "0x05:断开连接事件-->enable 广播！");
                    this.flowStatusChange(FlowStatus.STATUS_ENABLE_ADVERTISING);
                    break;
                case Command_Complete_Event:// 0x0E:指令完成回调
                    commandCompleteEvent(currentOpCode, data);
                    break;
                case Command_Status_Event:
//                      04 0F  04 00 01 1C FD
//                      04 0F 04 00 01 06 04 size = 7
//                      [0x04,0x0F,0x04,0x00,0x01,0x06,0x04]
//                      04 0F 04 01 01 50 00 size = 7
//                    04 0F         04  3A 01 06 04                      04 05 04 00 19 08 13 size = 14
                    XLog.e(TAG, "command_status_event !");
                    CommandOptions.CommandStatusEvent(mhandler, INIT_HW_WHAT, INIT_HW_DELAY, data);
                    break;
                case Hardware_Error_Event://do nothing
                    XLog.e(TAG, "hardware_error_event !");
                    break;
                case HCI_VENDOR_ERROR://0xFF: 厂商自定义HCI EVENT
                    HciVendorEvent(data);
                    break;
            }
        }
    }

    /**
     * .
     * 解析了可能存在的多包，解析当前包的下一包
     *
     * @param currentOpCode
     * @param data
     */
    private void parseExitMorePack(byte[] currentOpCode, byte[] data) {
        // 尝试解析下一包
        int len = data[2];
        int totalLen = len + 3;
        if (data.length > totalLen) {//后面可能存在其他包
            XLog.e(TAG, "解析了可能存在的多包！");
            byte[] next = new byte[data.length - totalLen];
            System.arraycopy(data, totalLen, next, 0, data.length - totalLen);
            parseHCIEventPKT(currentOpCode, next);
        }
    }

    /**
     * @param data 完整单包，或者是多包
     */
    private void HciVendorEvent(byte[] data) {
        XLog.d(TAG, "HciVendorEvent() called with: data = [" + ConvertUtil.byte2HexStrWithSpace(data) + "]");
        ArrayList<byte[]> arrayList = null;
        try {
            arrayList = CommandOptions.splitFFPackage(data);
            for (byte[] bytes : arrayList) {
                ParseHciVendorEvent(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析厂商特定包
     *
     * @param data 完整单包
     */
    private void ParseHciVendorEvent(byte[] data) {
        XLog.d(TAG, "ParseHciVendorEvent() called with: \n" +
                "data = [" + ConvertUtil.byte2HexStrWithSpace(data) + "]");
        byte[] Ecode = new byte[2];
        System.arraycopy(data, 3, Ecode, 0, 2);
        if (Arrays.equals(Ecode, HCIVendorEcode.EVT_BLUE_INITIALIZED_Ecode)) {
            byte[] Reason_Code = new byte[2];
            System.arraycopy(data, 5, Reason_Code, 0, 1);
            DealCommandResult.dealWithErrorResult(this, Reason_Code);
        }
        //HCI EVENT
        else if (Arrays.equals(Ecode, HCIVendorEcode.EVT_BLUE_GATT_ATTRIBUTE_MODIFIED_Ecode)) {
            //TODO
            XLog.d(TAG, "属性被更改！");
            DealHCIEvent.dealGattAttributeValues(getApplication(), this, data);
        } else if (Arrays.equals(Ecode, HCIVendorEcode.EVT_BLUE_GATT_ERROR_RESPONSE_Ecode)) {
            XLog.e(TAG, "evt_blue_gatt_error_response!!");
            DealHCIEvent.dealGattProErrorRes(data);
        } else if (Arrays.equals(Ecode, HCIVendorEcode.EVT_BLUE_GATT_PROCEDURE_COMPLETE_Ecode)) {
            XLog.d(TAG, "evt_blue_gatt_procedure_complete!");
            DealHCIEvent.dealGattProComplete(this, data);
        } else if (Arrays.equals(Ecode, HCIVendorEcode.EVT_BLUE_GATT_PROCEDURE_TIMEOUT_Ecode)) {
            XLog.e(TAG, "evt_blue_gatt_procedure_timeout_ecode");
            //准备断开连接
            this.flowStatusChange(FlowStatus.STATUS_HCI_READY_DISCONNECT);
        }
        //请求更新参数返回
        else if (Arrays.equals(Ecode, HCIVendorEcode.EVT_BLUE_L2CAP_CONN_UPDATE_RESP_Ecode)) {
            this.flowStatusChange(FlowStatus.STATUS_L2CAP_CONN_UPDATE_RESP, data);
        } else {
            XLog.e(TAG, "请解析其他事件！data：" + ConvertUtil.byte2HexStrWithSpace(data));
        }
    }

    /**
     * 事件完成
     *
     * @param currentOpCode 0x01,0xFD
     * @param data          原始数据
     */
    private synchronized void commandCompleteEvent(byte[] currentOpCode, byte[] data) {
        /**
         * 需要容错处理，解析多帧，不完整帧等意外数据
         * 找与currentOpCode对应的包
         * */
        data = FindPackage(currentOpCode, data);
        if (data == null) {
            XLog.e(TAG, "TODO: 2017/3/14 当做遇到严重错误，进行reset操作");
            this.flowStatusChange(STATUS_RESETHW_INIT);
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
        //1、reset
        if (Arrays.equals(currentOpCode, OpCode.hwReset_command_opCode)) {
            DealCommandResult.dealHWResetResult(this, currentOpCode, paramContent);
        }
        //2、config
        else if (Arrays.equals(currentOpCode, OpCode.ACI_HAL_WRITE_CONFIG_DATA_opCode)) {
            DealCommandResult.dealConfigDataResult(this, currentOpCode, paramContent);
        }
        //3、set tx power
        else if (Arrays.equals(currentOpCode, OpCode.ACI_HAL_SET_TX_POWER_LEVEL_opCode)) {
            DealCommandResult.dealSetTxPowerResult(this, currentOpCode, paramContent);
        }
        //4、gatt init
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GATT_INIT_opCode)) {
            DealCommandResult.dealGattInitResult(this, currentOpCode, paramContent);
        }
        //5、gap init
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GAP_INIT_opCode)) {
            DealCommandResult.dealGapInitResult(this, currentOpCode, paramContent);
        }
        //6、gatt add service
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GATT_ADD_SERVICE_opCode)) {
            DealCommandResult.dealGattAddService(this, currentOpCode, paramContent);
        }
        //7、gatt add char
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GATT_ADD_CHAR_opCode)) {
            DealCommandResult.dealGattAddChar(this, currentOpCode, paramContent);
        }
        //8 添加描述符
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GATT_ADD_CHAR_DESC_opCode)) {
            DealCommandResult.dealAddCharDesc(this, currentOpCode, paramContent);
        }
        //9、设置广播数据
        else if (Arrays.equals(currentOpCode, OpCode.HCI_LE_SET_SCAN_RESPONSE_DATA_opCode)) {
            DealCommandResult.dealSetScanResponseData(this, currentOpCode, paramContent);
        }
        //10、ACI_Gap 开启广播
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GAP_SET_DISCOVERABLE_opCode)) {
            DealCommandResult.dealAciGapDisCoverable(this, currentOpCode, paramContent);
        }
        //11 更新广播数据
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GAP_UPDATE_ADV_DATA_opCode)) {
            DealCommandResult.dealAciGapUpdateADVData(this, currentOpCode, paramContent);
        }
        //12、notify
        else if (Arrays.equals(currentOpCode, OpCode.ACI_GATT_UPD_CHAR_VAL_opCode)) {
            DealCommandResult.dealGattUpdateCharValResult(this, currentOpCode, paramContent);
        }
        // 更新参数反馈响应
        else if (Arrays.equals(currentOpCode, OpCode.Aci_L2CAP_Connection_Parameter_Update_Response_opCode)) {
            DealCommandResult.dealL2CAPVal(this, currentOpCode, paramContent);
        } else {
            XLog.e(TAG, "error!check it!");
            //修复
            this.flowStatusChange(STATUS_RESETHW_INIT);
        }
    }

    /**
     * /**
     * 流程状态改变，执行发送指令的命令
     *
     * @param status
     */
    @Override
    public void flowStatusChange(FlowStatus status, byte[]... datas) {
        CURRENT_STATUS = status;
        switch (status) {
            case STATUS_HWRESET_SUCCESS://2 reset 成功
                XLog.d(TAG, "1 reset 成功");
                AciHalCommand.writeConfig_ModeData(PeripharalManager.getInstance().getListenTask());
                currentHasConfig = ConfigProcess.config_none;
                break;
            //配置开始
            case STATUS_CONFIG_MODE_SUCCESS://3 config mode 成功
                XLog.d(TAG, "2 config mode 成功");
                AciHalCommand.writeConfig_PublicAddrData(PeripharalManager.getInstance().getListenTask());
                currentHasConfig = config_mode;
                break;
            case STATUS_CONFIG_PUBADDR_SUCCESS://4 config address 成功
                XLog.d(TAG, "3 config address 成功");
                AciHalCommand.writeConfig_TxPowerData(PeripharalManager.getInstance().getListenTask());
                currentHasConfig = config_publicAddress;
                break;
            case STATUS_SET_TX_POWER_LEVEL_SUCCESS://5 set tx power 成功
                XLog.d(TAG, "4 set tx power 成功");
                //初始化GATT
                AciGattCommand.bleGattInit(PeripharalManager.getInstance().getListenTask());
                break;
            case STATUS_GATT_INIT_SUCCESS://6 gatt init 成功
                XLog.d(TAG, "5 gatt init 成功");
                //初始化GAP
                AciGapCommand.aciGapInit(PeripharalManager.getInstance().getListenTask());
                break;
            case STATUS_GAP_INIT_SUCCESS://7 gap init 成功
                XLog.d(TAG, "6 gap init 成功");
                //TODO go on,--->you can add service and char
                AciGattCommand.gattAddService(PeripharalManager.getInstance().getListenTask());
                break;
            case STATUS_GATT_ADD_SERVICE_SUCCESS://9 add service 成功
                XLog.d(TAG, "7 add service 成功");
                if (datas != null) {
                    byte[] service_handle = datas[0];
                    //开始构造char 集合，包含 CHAR_SET_SIZE 个char
                    initCharSet(service_handle);
                    //开始添加
                    startAddChar();
                }
                break;
            case STATUS_GATT_ADD_CHAR_SUCCESS://10.0 add 8 个char 成功
                if (datas != null) {
                    byte[] Char_Handle = datas[0];
                    //设置已添加标识
                    setCharHandlerAndMark(Char_Handle);
                    //再次添加
                    boolean isAddSuccess = startAddChar();
                    if (!isAddSuccess) {//全部已经被添加了 --> 添加完成,设置广播数据
                        XLog.d(TAG, "8.0 add 8 个char 成功");
                        //debug
//                        printCharBean();
//                        方案1，添加描述符
//                        CharBean notifyCharBean = getNotifyCharBean();
//                        if (notifyCharBean != null) {
//                            //可添加描述符
//                            XLog.d(TAG, "notifyCharBean : " + notifyCharBean.toString());
//                            AciGattCommand.gattAddCharDescriptor(PeripharalManager.getInstance().getListenTask(), notifyCharBean.service_handler, notifyCharBean.char_handle);
//                        } else {
//                            AciHciCommand.setBleScanResponseData(PeripharalManager.getInstance().getListenTask());
//                        }
                        //方案2 测试不添加描述符
                        AciHciCommand.setBleScanResponseData(PeripharalManager.getInstance().getListenTask());
//                        AciHciCommand.setBleScanResponseDataForTest(PeripharalManager.getInstance().getListenTask());
                    }
                }
                break;
//            不需要添加描述符
//            case STATUS_ACI_GATT_ADD_CHAR_DESC_SUCCESS:
//                if (datas != null) {
//                    byte[] Char_desc_Handle = datas[0];
//                    XLog.d(TAG, "Char_desc_Handle = " + ConvertUtil.byte2HexStrWithSpace(Char_desc_Handle));
//                }
//                XLog.d(TAG, "9 为 notify 添加描述符成功");
//                AciHciCommand.setBleScanResponseData(PeripharalManager.getInstance().getListenTask());
//                break;
            case STATUS_SET_SCAN_RESPONSE_DATA_SUCCESS://11 设置扫描响应数据成功
                XLog.d(TAG, "10 设置扫描响应数据成功");
                AciGapCommand.aciGapSetDiscoverable(PeripharalManager.getInstance().getListenTask());
//                AciGapCommand.aciGapSetDiscoverableForTestName(PeripharalManager.getInstance().getListenTask());
                break;
            case STATUS_ACI_GAP_SET_DISCOVERABLE_SUCCESS://12 开启广播成功
                //1、必须要移除app连接的计时
                //2、必须要移除等待指令Reset的计时器
                removeAppConnectTimer();
                PeripharalManager.getInstance().sendMsg2Service(MSG_REMOVE_WAITING_TIMER);
                XLog.d(TAG, "11 开启广播成功");
                PeripheralService.isIniting = false;
                break;
            case STATUS_HCI_READY_DISCONNECT:
                removeAppConnectTimer();
                XLog.d(TAG, "准备断开连接");
                AciHciCommand.hciDisconnect(PeripharalManager.getInstance().getListenTask(), connection_handle);
                break;
            case STATUS_ENABLE_ADVERTISING:
                removeAppConnectTimer();
                XLog.d(TAG, "aciGapSetDiscoverable -->enable 广播！");
                AciGapCommand.aciGapSetDiscoverable(PeripharalManager.getInstance().getListenTask());
                break;
            case STATUS_RESETHW_INIT:
                removeAppConnectTimer();
                XLog.d(TAG, "bleHwReset -->reset 广播！");
                AciHciCommand.bleHwReset(PeripharalManager.getInstance().getListenTask());
                break;
            case STATUS_GATT_UPDATE_CHAR_VAL_SUCCESS://8 gatt update char val ---> notify 成功
                XLog.d(TAG, "通知开门结果成功--> 断开连接");
                sendNotifyTime++;
                if (sendNotifyTime < MAX_NOFITY_TIME) {//再次发送notify
                    PeripharalManager.getInstance().getListenTask().resendlastData();
                } else {
                    sendNotifyTime = 0;
                    //此处做等待notify 发送到app
                    try {
                        Thread.sleep(30);
                        this.flowStatusChange(FlowStatus.STATUS_HCI_READY_DISCONNECT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case STATUS_L2CAP_CONN_UPDATE_RESP:
                try {
                    setResponse(datas[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        sendCurrentBleStatus(status);
    }

    /**
     * 发送蓝牙的当前状态
     *
     * @param status
     */
    private void sendCurrentBleStatus(FlowStatus status) {
        Intent intent = new Intent(ACTON_FLOWCONTROL_STATUS);
        intent.putExtra(Constants.ACTON_FLOWCONTROL_STATUS_VALUES, status);
        sendBroadcast(intent);
    }

    private void setResponse(byte[] data) throws Exception {
        byte Parameter_Total_Length = data[2];
        byte[] connect_handle = new byte[2];
        System.arraycopy(data, 5, connect_handle, 0, 2); // 5 6
        byte Event_Data_Length = data[7];
        byte Code = data[8];
        identifier = data[9];
        byte L2CAP_Length = data[10];
        isAccept = data[11];
    }

    @Override
    public void appConnect(AppConnectBean connectBean) {
        initHashMap(connectBean.appMac);
        lastConnectAppmac = connectBean.appMac;
        connection_handle = connectBean.Connection_Handle;
        if (connectBean.status == AppConnectStatus.status_connected) {
            XLog.d(TAG, "开始" + Config.CONNECT_MAX_TIME + "ms计时！");
            mhandler.sendEmptyMessageDelayed(APP_CONNECT_WHAT, Config.CONNECT_MAX_TIME);
        } else if (connectBean.status == AppConnectStatus.status_connect_update) {
        }
        Intent intent = new Intent(ACTION_APP_CONNECT_STATUS);
        intent.putExtra(EXTRA_APPBEAN, connectBean);
        sendBroadcast(intent);
    }

    @Override
    public void receiveAttVal(String appBleMac, byte[] char_uuid, byte[] values) {
        XLog.d(TAG, "receiveAttVal() called ");
        String uuid_str = ConvertUtil.byte2HexStrWithSpace(char_uuid);
        String values_str = ConvertUtil.byte2HexStrWithSpace(values);
        String textstr = "appBleMac : " + appBleMac + "\n" + "uuid : " + uuid_str + "\n" + "vaules : " + values_str;
        Intent intent = new Intent(ACTION_REVEIVE_ATTRIBUTE_VALUES);
        intent.putExtra(EXTRA_APPMAC, appBleMac);
        intent.putExtra(EXTRA_CHAR_UUID, char_uuid);
        intent.putExtra(EXTRA_CHAR_VALUES, values);
        sendBroadcast(intent);
        XLog.d(TAG, textstr);
        //填充证书
        fillMap(appBleMac, char_uuid, values);
    }

    @Override
    public void verifyCertificate(byte status, byte reason) {
        XLog.d(TAG, "发送notify，通知app 校验证书结果！");
        CharBean notifyCharBean = getNotifyCharBean();
        if (notifyCharBean == null) {
            XLog.e(TAG, "notifyCharBean is null!");
            return;
        }
        AciGattCommand.updateCharValues(PeripharalManager.getInstance().getListenTask(), notifyCharBean.service_handler, notifyCharBean.char_handle, status, reason);
    }

    private CharBean getNotifyCharBean() {
        CharBean charBean = null;
        if (charBeanSparseArray != null) {
            for (int i = 0; i < CHAR_SET_SIZE; i++) {
                CharBean bean = charBeanSparseArray.get(i);
                if (bean.char_prop == NOFITY_PROPERTIES) {
                    charBean = bean;
                    break;
                }
            }
        }
        return charBean;
    }

    private void fillMap(String appMac, byte[] uuid, byte[] values) {
//      转换uuid对象到枚举对象
        byte[] keyuuid = CommandOptions.convertUuid(uuid);
        if (appMac.equalsIgnoreCase(lastConnectAppmac)) {
            UUIDAttrValuesHashMap.put(keyuuid, values);
            lastConnectAppmac = appMac;
            XLog.d(TAG, "UUIDAttrValuesHashMap.size = " + UUIDAttrValuesHashMap.size());
            //debug();
            //最后一个
            if (Arrays.equals(keyuuid, Config.CHAR_UUID_WRITE_06)) {
                byte[] bytes = mergeCertificate();
                verifyCerticate(bytes);
            }
        } else {
            XLog.e(TAG, "app mac 不一致！");
        }
    }

    /**
     * 校验证书
     *
     * @param bytes 证书数据
     */
    private void verifyCerticate(byte[] bytes) {
        PublicKey publicKey = null;
        String deviceId = null;
        String userId = null;
        //TODO 校验证书
//        SignVerify.openDoorStatus verify = SignVerify.verify(this.getApplicationContext(), publicKey, bytes, lastConnectAppmac, deviceId, userId);
        SignVerify.openDoorStatus verify = SignVerify.openDoorStatus.VerifySuccess;
        switch (verify) {
            case VerifySuccess:
                sendResultNotify(Config.CHAR_NOTIFY_STATUS_SUCCESS_VALUE, VerifySuccess.getReson());
                sendBroadcast(new Intent(Helper.ACTION_CERTIFICATE_VERIFY_SUCCESS));
                XLog.d(TAG, "TODO 校验成功，发送 notify");
                break;
            default:
                sendResultNotify(Config.CHAR_NOTIFY_STATUS_FAILED_VALUE, verify.getReson());
                Intent intent = new Intent(Helper.ACTION_CERTIFICATE_VERIFY_FAILED);
                intent.putExtra(Helper.EXTRA_CERTIFICATE_VERIFY_FAILED_REASON, verify.getReson());
                sendBroadcast(intent);
                XLog.d(TAG, "TODO 校验失败，发送 notify -> " + MasterHelper.ParseError(verify.getReson()));
                break;
        }
    }

    /**
     * 发送开门校验结果
     *
     * @param charNotifyStatus
     * @param Reason
     */
    private void sendResultNotify(byte charNotifyStatus, byte Reason) {
        this.verifyCertificate(charNotifyStatus, Reason);
        UUIDAttrValuesHashMap.clear();
    }

    /**
     * 拼接证书
     *
     * @return
     */
    private byte[] mergeCertificate() {
        XLog.d(TAG, "mergeCertificate() called");
        int totallen = 0;
        byte[] destbytes = new byte[totallen];
        byte[] temp = new byte[220];
        byte[] values;
        for (byte[] uuid : uuidlist) {
            values = UUIDAttrValuesHashMap.get(uuid);
            if (values != null) {
                totallen += values.length;
                System.arraycopy(values, 0, temp, totallen - values.length, values.length);
                XLog.d(TAG, "uuid：" + ConvertUtil.byte2HexStrWithSpace(uuid));
                XLog.d(TAG, "values：" + ConvertUtil.byte2HexStrWithSpace(values));
                XLog.d(TAG, "values.length：" + values.length);
            }
        }
        if (totallen > 0) {
            destbytes = new byte[totallen];
            System.arraycopy(temp, 0, destbytes, 0, totallen);
            XLog.d(TAG, "证书数据：" + ConvertUtil.byte2HexStrWithSpace(destbytes));
            return destbytes;
        } else
            return destbytes;
    }

    private void initHashMap(String currentAppMac) {
        XLog.d(TAG, "initHashMap() called with: currentAppMac = [" + currentAppMac + "]");
        if (TextUtils.isEmpty(currentAppMac))
            return;
        if (!currentAppMac.equalsIgnoreCase(lastConnectAppmac))
            UUIDAttrValuesHashMap.clear();
    }

    private void initCharSet(byte[] service_Handle) {
        if (charBeanSparseArray == null)
            charBeanSparseArray = new SparseArray<>(CHAR_SET_SIZE);
        charBeanSparseArray.clear();//清空
//        write_
        charBeanSparseArray.put(0, new CharBean(service_Handle, Config.CHAR_UUID_WRITE_00, WRITE_PROPERTIES));
        charBeanSparseArray.put(1, new CharBean(service_Handle, Config.CHAR_UUID_WRITE_01, WRITE_PROPERTIES));
        charBeanSparseArray.put(2, new CharBean(service_Handle, Config.CHAR_UUID_WRITE_02, WRITE_PROPERTIES));
        charBeanSparseArray.put(3, new CharBean(service_Handle, Config.CHAR_UUID_WRITE_03, WRITE_PROPERTIES));
        charBeanSparseArray.put(4, new CharBean(service_Handle, Config.CHAR_UUID_WRITE_04, WRITE_PROPERTIES));
        charBeanSparseArray.put(5, new CharBean(service_Handle, Config.CHAR_UUID_WRITE_05, WRITE_PROPERTIES));
        charBeanSparseArray.put(6, new CharBean(service_Handle, Config.CHAR_UUID_WRITE_06, WRITE_PROPERTIES));
//        nofity_
        charBeanSparseArray.put(7, new CharBean(service_Handle, Config.CHAR_UUID_NOTYFY, NOFITY_PROPERTIES));
    }

    /**
     * 开始添加特征值
     *
     * @return false：全部已经添加完毕，true：正常添加中
     */
    private boolean startAddChar() {
        CharBean charBean = getCharBean();
        if (charBean != null) {
            AciGattCommand.gattAddCharacteristic(PeripharalManager.getInstance().getListenTask(),
                    charBean.service_handler, charBean.char_uuid, charBean.char_prop);
            return true;
        }
        return false;
    }

    /**
     * 1、设置已添加标识hasSetting，2、设置attr_handle，3、设置char_handle
     *
     * @param Char_Handle
     */
    private void setCharHandlerAndMark(byte[] Char_Handle) {
        if (charBeanSparseArray != null) {
            for (int i = 0; i < CHAR_SET_SIZE; i++) {
                CharBean charbean = charBeanSparseArray.get(i);
                if (!charbean.hasSetting) {
                    charbean.hasSetting = true;
                    charbean.char_handle = Char_Handle;
                    //Char_Handle为小端数据结构，需要转为大端Java默认的结构，才能正确转int 需要转换大小端-----start
                    byte[] char_handle = new byte[2];
                    char_handle[0] = Char_Handle[1];
                    char_handle[1] = Char_Handle[0];
                    //Char_Handle 为小端数据结构，需要转为大端Java默认的结构，才能正确转int 需要转换大小端-----end
                    switch (charbean.char_prop) {
                        case WRITE_PROPERTIES:
                            int att1 = byteUtils.byteArrayToInt(char_handle) + AttrOrder.WRITE_UUID_ATTR_ORDER_OFFSET;
                            byte[] bytes1 = Utils.intToByteArray(att1);
                            //bytes1{0x00,0x00,0x00,0x0D} 为四个字节，大端结构，赋值给Attr_Handle，小端存储
                            charbean.attr_Handle = new byte[2];
                            charbean.attr_Handle[0] = bytes1[3];
                            charbean.attr_Handle[1] = bytes1[2];
                            //charbean.attr_Handle 为 {0x0D,0x00} 小端结构，便于接收到ble数据进行比较
                            break;
                        case NOFITY_PROPERTIES:
                            int att2 = byteUtils.byteArrayToInt(char_handle) + AttrOrder.NOTIFY_UUID_ATTR_ORDER_OFFSET;
                            byte[] bytes2 = Utils.intToByteArray(att2);
                            //bytes2 为四个字节，取高位并反向大小端,赋值给Attr_Handle
                            charbean.attr_Handle = new byte[2];
//                              由于不需要解析 notify 的数据，暂不解析 notify 的attr_handle
//                            charbean.attr_Handle[0] = bytes2[3];
//                            charbean.attr_Handle[1] = bytes2[2];
                            break;
                    }
                    break;
                }
            }
        }
    }

    private void removeAppConnectTimer() {
        XLog.d(TAG, "移除app" + Config.CONNECT_MAX_TIME + "ms连接的计时器");
        if (mhandler != null)
            mhandler.removeMessages(APP_CONNECT_WHAT);
    }

    private CharBean getCharBean() {
        CharBean charBean = null;
        if (charBeanSparseArray != null) {
            for (int i = 0; i < CHAR_SET_SIZE; i++) {
                CharBean bean = charBeanSparseArray.get(i);
                if (!bean.hasSetting) {
                    charBean = bean;
                    break;
                }
            }
        }
        return charBean;
    }

    //___________________________________________Manager 与 Service 通讯_________
    private class BLEHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //TODO 接收manager 发过来的event
            XLog.d(TAG, "接收manager 发过来的event");
            switch (msg.what) {
                case MSG_REMOVE_WAITING_TIMER:
                    removeWaitingResetHWTimer();
                    break;
                case MSG_TEST_SEND_NOTIFY:
                    verifyCertificate(Config.CHAR_NOTIFY_STATUS_SUCCESS_VALUE, VerifySuccess.getReson());
                    break;
                case Helper.MSG_SEND_STATUS_RESET_INIT:
                    flowStatusChange(STATUS_RESETHW_INIT);
                    break;
                case Helper.MSG_SEND_STATUS_TO_CLIENT:
                    sendCurrentBleStatus(CURRENT_STATUS);
                    break;
            }
        }
    }
}
