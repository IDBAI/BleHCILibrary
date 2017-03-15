package com.revenco.library.core;

import android.content.Context;
import android.content.Intent;

import com.revenco.library.command.AciCommandConfig;
import com.revenco.library.command.AciHciCommand;
import com.revenco.library.command.FlowControl;
import com.revenco.library.utils.XLog;

import java.util.Arrays;

import static com.revenco.library.command.AciCommandConfig.STATUS_ADD_CHAR_Character_Already_Exists;
import static com.revenco.library.command.AciCommandConfig.STATUS_ADD_CHAR_Error;
import static com.revenco.library.command.AciCommandConfig.STATUS_ADD_CHAR_Insufficient_resources;
import static com.revenco.library.command.AciCommandConfig.STATUS_ADD_CHAR_Invalid_handle;
import static com.revenco.library.command.AciCommandConfig.STATUS_ADD_CHAR_Invalid_parameter;
import static com.revenco.library.command.AciCommandConfig.STATUS_ADD_CHAR_Out_of_Memory;
import static com.revenco.library.command.AciCommandConfig.STATUS_ADD_CHAR_Out_of_handle;
import static com.revenco.library.command.AciCommandConfig.STATUS_ADD_CHAR_SUCCESS;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/2/27 17:13.</p>
 * <p>CLASS DESCRIBE :指令返回结果处理类</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class DealCommandResult {
    private static final String TAG = "DealCommandResult";

    private static void sendBroadCast(Context context, String action) {
        XLog.d(TAG, "sendBroadCast() called with: context = [" + context + "], action = [" + action + "]");
        Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

    private static void sendBroadCastWithServiceAndDev(Context context, byte[] Service_Handle, byte[] Dev_Name_Char_Handle, String action) {
        XLog.d(TAG, "sendBroadCastWithServiceAndDev() called with: context = [" + context + "], Service_Handle = [" + Service_Handle + "], Dev_Name_Char_Handle = [" + Dev_Name_Char_Handle + "], action = [" + action + "]");
        Intent intent = new Intent(action);
        intent.putExtra(FlowControl.EXTRA_Service_Handle, Service_Handle);
        intent.putExtra(FlowControl.EXTRA_Dev_Name_Char_Handle, Dev_Name_Char_Handle);
        context.sendBroadcast(intent);
    }

    private static void sendBroadCastWithService(Context context, byte[] Service_Handle, String action) {
        XLog.d(TAG, "sendBroadCastWithService() called with: context = [" + context + "], Service_Handle = [" + Service_Handle + "], action = [" + action + "]");
        Intent intent = new Intent(action);
        intent.putExtra(FlowControl.EXTRA_Service_Handle, Service_Handle);
        context.sendBroadcast(intent);
    }

    private static void sendBroadCastWithChar(Context context, byte[] Char_Handle, String action) {
        XLog.d(TAG, "sendBroadCastWithChar() called with: context = [" + context + "], Char_Handle = [" + Char_Handle + "], action = [" + action + "]");
        Intent intent = new Intent(action);
        intent.putExtra(FlowControl.EXTRA_Char_Handle, Char_Handle);
        context.sendBroadcast(intent);
    }

    private static void sendBroadCastWithCharDesc(Context context, byte[] char_desc_handle, String action) {
        XLog.d(TAG, "sendBroadCastWithCharDesc() called with: context = [" + context + "], char_desc_handle = [" + char_desc_handle + "], action = [" + action + "]");
        Intent intent = new Intent(action);
        intent.putExtra(FlowControl.EXTRA_Char_DESC_Handle, char_desc_handle);
        context.sendBroadcast(intent);
    }

    /**
     * @param currentOpCode {0x03,0x0C};
     * @param paramContent  01 03 0c 00
     */
    public static void dealHWResetResult(Context context, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealHWResetResult() called with: context = [" + context + "], currentOpCode = [" + currentOpCode + "], paramContent = [" + paramContent + "]");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：01
        if (Arrays.equals(currentOpCode, opcode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* start success!");
                sendBroadCast(context, FlowControl.ACTION_HWRESET_SUCCESS);
            }
        }
    }

    /**
     * // In which mode BlueNRG started.
     * // 0x01: Firmware started properly,
     * // 0x02: Updater mode entered with ACI command,
     * // 0x03: 'Updater mode entered due to bad Blue Flag',
     * // 0x04: 'Updater mode entered due to IRQ pin',
     *
     * @param context
     * @param reason_Code 01
     */
    public static void dealWithErrorResult(Context context, byte[] reason_Code) {
        XLog.d(TAG, "dealWithErrorResult() called with: context = [" + context + "], reason_Code = [" + reason_Code + "]");
        switch (reason_Code[0]) {
            case 01:
                XLog.d(TAG, "Firmware started properly!");
                sendBroadCast(context, FlowControl.ACTION_HWRESET_SUCCESS);
                break;
        }
    }

    /**
     * 处理open aciton的结果
     *
     * @param currentOpCode {0x01,0x10};
     * @param paramContent  [0x01,0x01,0x10,0x00,0x06,0x07,0x31,0x06,0x30,0x00,0x10,0x00]
     */
    public static void dealOpenResult(Context context, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealOpenResult() called with: context = [" + context + "], currentOpCode = [" + currentOpCode + "], paramContent = [" + paramContent + "]");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* open success!");
                sendBroadCast(context, FlowControl.ACTION_OPEN_SUCCESS);
            } else if (status == AciCommandConfig.ERR_INVALID_HCI_CMD_PARAMS) {
                XLog.e(TAG, "* open failed,ERR_INVALID_HCI_CMD_PARAMS,i will HwReset");
                AciHciCommand.bleHwReset(PeripharalManager.getInstance().getListenTask());
            }
        }
    }

    /**
     * [0x04,0x0E,0x04,0x01,0x0C,0xFC,0x00]
     * [0x04,0x0E,0x04,0x01,0x0C,0xFC,0x00]
     * [0x04,0x0E,0x04,0x01,0x0F,0xFC,0x00]
     *
     * @param context
     * @param currentOpCode 0x0C,0xFC,
     * @param paramContent  ,0x01,0x0C,0xFC,0x00]
     */
    public static void dealConfigDataResult(Context context, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealConfigDataResult() called with: context = [" + context + "], currentOpCode = [" + currentOpCode + "], paramContent = [" + paramContent + "]");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                if (FlowControl.currentHasConfig == FlowControl.ConfigProcess.config_none) {
                    XLog.d(TAG, "* config mode  success!");
                    sendBroadCast(context, FlowControl.ACTION_CONFIG_MODE_SUCCESS);
                } else if (FlowControl.currentHasConfig == FlowControl.ConfigProcess.config_mode) {
                    XLog.d(TAG, "* config public address  success!");
                    sendBroadCast(context, FlowControl.ACTION_CONFIG_PUBADDR_SUCCESS);
                }
            } else if (status == AciCommandConfig.ERR_INVALID_HCI_CMD_PARAMS) {
                XLog.e(TAG, "ERR_INVALID_HCI_CMD_PARAMS!!!");
                context.sendBroadcast(new Intent(FlowControl.ACTION_RESETHW_INIT));
            } else {
                XLog.e(TAG, "error,please check it!");
                context.sendBroadcast(new Intent(FlowControl.ACTION_RESETHW_INIT));
            }
        }
    }

    /**
     * @param context
     * @param currentOpCode
     * @param paramContent  0x01,0x01,0xFD,0x00
     */
    public static void dealGattInitResult(Context context, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealGattInitResult() called with: context = [" + context + "], currentOpCode = [" + currentOpCode + "], paramContent = [" + paramContent + "]");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* gatt init success!");
                sendBroadCast(context, FlowControl.ACTION_GATT_INIT_SUCCESS);
            }
        }
    }

    /**
     * @param context
     * @param currentOpCode
     * @param paramContent  [0x01,0x8A,0xFC,0x00,       0x05,0x00,    0x06,0x00,    0x08,0x00]
     */
    public static void dealGapInitResult(Context context, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealGapInitResult() called with: context = [" + context + "], currentOpCode = [" + currentOpCode + "], paramContent = [" + paramContent + "]");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* gap init success!");
                //
                byte[] Service_Handle = new byte[2];
                byte[] Dev_Name_Char_Handle = new byte[2];
                byte[] Appearance_Char_Handle = new byte[2];//not used
                //拷贝Handler
                System.arraycopy(paramContent, 4, Service_Handle, 0, 2);
                System.arraycopy(paramContent, 6, Dev_Name_Char_Handle, 0, 2);
                System.arraycopy(paramContent, 8, Appearance_Char_Handle, 0, 2);
                //
                sendBroadCastWithServiceAndDev(context, Service_Handle, Dev_Name_Char_Handle, FlowControl.ACTION_GAP_INIT_SUCCESS);
            }
        }
    }

    /**
     * //[0x04,0x0E,0x04,0x01,0x06,0xFD,0x00]
     *
     * @param context
     * @param currentOpCode 0x06,0xFD,
     * @param paramContent  0x01,0x06,0xFD,0x00
     */
    public static void dealGattUpdateCharValResult(Context context, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealGattUpdateCharValResult() called with: context = [" + context + "], currentOpCode = [" + currentOpCode + "], paramContent = [" + paramContent + "]");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* gatt update char val success!----notify success!");
                sendBroadCast(context, FlowControl.ACTION_GATT_UPDATE_CHAR_VAL_SUCCESS);
            }
        }
    }

    public static void dealSetTxPowerResult(Context context, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealSetTxPowerResult() called with: context = [" + context + "], currentOpCode = [" + currentOpCode + "], paramContent = [" + paramContent + "]");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* set txt Power  success!");
                sendBroadCast(context, FlowControl.ACTION_SET_TX_POWER_LEVEL_SUCCESS);
            }
        }
    }

    /**
     * [0x01,0x02,0xFD,0x00,0x0C,0x00]
     *
     * @param context
     * @param currentOpCode 0x02,0xFD,                             Service_Handle
     * @param paramContent  [0x01,0x02,0xFD,0x00,                0x0C,0x00]
     */
    public static void dealGattAddService(Context context, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealGattAddService() called with: context = [" + context + "], currentOpCode = [" + currentOpCode + "], paramContent = [" + paramContent + "]");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* gatt add service  success!");
                //
                byte[] Service_Handle = new byte[2];
                //拷贝Handler
                System.arraycopy(paramContent, 4, Service_Handle, 0, 2);
                sendBroadCastWithService(context, Service_Handle, FlowControl.ACTION_GATT_ADD_SERVICE_SUCCESS);
            }
        }
    }

    /**
     * [0x04,0x0E,0x06,0x01,0x04,0xFD,0x00,0x0D,0x00]
     *
     * @param context
     * @param currentOpCode 0x04,0xFD,
     * @param paramContent  0x01,0x04,0xFD,0x00,0x0D,0x00
     */
    public static void dealGattAddChar(Context context, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealGattAddChar() called with: context = [" + context + "], currentOpCode = [" + currentOpCode + "], paramContent = [" + paramContent + "]");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            boolean isOtherError = false;
            switch (status) {
                case STATUS_ADD_CHAR_SUCCESS:
                    XLog.d(TAG, "* gatt add char  success!");
                    byte[] Char_Handle = new byte[2];
                    //拷贝Handler
                    System.arraycopy(paramContent, 4, Char_Handle, 0, 2);
                    sendBroadCastWithChar(context, Char_Handle, FlowControl.ACTION_GATT_ADD_CHAR_SUCCESS);
                    break;
                case STATUS_ADD_CHAR_Error:
                    XLog.e(TAG, "STATUS_ADD_CHAR_Error");
                    isOtherError = true;
                    break;
                case STATUS_ADD_CHAR_Out_of_Memory:
                    XLog.e(TAG, "STATUS_ADD_CHAR_Out_of_Memory");
                    isOtherError = true;
                    break;
                case STATUS_ADD_CHAR_Invalid_handle:
                    XLog.e(TAG, "STATUS_ADD_CHAR_Invalid_handle");
                    isOtherError = true;
                    break;
                case STATUS_ADD_CHAR_Invalid_parameter:
                    XLog.e(TAG, "STATUS_ADD_CHAR_Invalid_parameter");
                    isOtherError = true;
                    break;
                case STATUS_ADD_CHAR_Out_of_handle:
                    XLog.e(TAG, "STATUS_ADD_CHAR_Out_of_handle");
                    isOtherError = true;
                    break;
                case STATUS_ADD_CHAR_Insufficient_resources:
                    XLog.e(TAG, "STATUS_ADD_CHAR_Insufficient_resources");
                    isOtherError = true;
                    break;
                case STATUS_ADD_CHAR_Character_Already_Exists:
                    XLog.e(TAG, "STATUS_ADD_CHAR_Character_Already_Exists");
                    isOtherError = true;
                    break;
            }
            if (isOtherError) {
                // TODO: 2017/3/15  添加特征值遇到错误，强制 resetHW
                XLog.e(TAG, "// TODO: 2017/3/15  添加特征值遇到错误，强制 resetHW");
                context.sendBroadcast(new Intent(FlowControl.ACTION_RESETHW_INIT));
            }
        }
    }

    /**
     * [0x04,0x0E,0x04,0x01,0x09,0x20,0x00]
     *
     * @param context
     * @param currentOpCode 0x09,0x20,
     * @param paramContent  0x01,0x09,0x20,0x00
     */
    public static void dealSetScanResponseData(Context context, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealSetScanResponseData() called with: context = [" + context + "], currentOpCode = [" + currentOpCode + "], paramContent = [" + paramContent + "]");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* set scan response data  success!");
                sendBroadCast(context, FlowControl.ACTION_SET_SCAN_RESPONSE_DATA_SUCCESS);
            }
        }
    }

    /**
     * @param context
     * @param currentOpCode 0x83,0xFC,
     * @param paramContent  0x01,0x83,0xFC,0x00
     */
    public static void dealAciGapDisCoverable(Context context, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealAciGapDisCoverable() called with: context = [" + context + "], currentOpCode = [" + currentOpCode + "], paramContent = [" + paramContent + "]");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
//            0x00: Success
//            0x42: Invalid parameter
//            0x0C: Command disallowed
//            0x11: Unsupported feature
            switch (status) {
                case (byte) 0x00:
                    XLog.d(TAG, "* aci gap set discoverable success!");
                    sendBroadCast(context, FlowControl.ACTION_ACI_GAP_SET_DISCOVERABLE_SUCCESS);
                    break;
                case (byte) 0x42:
                    XLog.e(TAG, "Invalid parameter");
                    context.sendBroadcast(new Intent(FlowControl.ACTION_RESETHW_INIT));
                    break;
                case (byte) 0x0C:
                    XLog.e(TAG, "Command disallowed");
                    context.sendBroadcast(new Intent(FlowControl.ACTION_RESETHW_INIT));
                    break;
                case (byte) 0x11:
                    XLog.e(TAG, "Unsupported feature");
                    context.sendBroadcast(new Intent(FlowControl.ACTION_RESETHW_INIT));
                    break;
            }
        }
    }

    public static void dealAciGapUpdateADVData(Context context, byte[] currentOpCode, byte[] paramContent) {
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* aci gap update ADV data success!");
                sendBroadCast(context, FlowControl.ACTION_ACI_GAP_UPDATE_ADV_DATA_SUCCESS);
            }
        }
    }

    public static void dealAddCharDesc(Context context, byte[] currentOpCode, byte[] paramContent) {
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            switch (status) {
                case AciCommandConfig.STATUS_Success:
                    XLog.d(TAG, "* aci gatt add char desc success!");
                    byte[] Char_Desc_Handle = new byte[2];
                    //拷贝Handler
                    System.arraycopy(paramContent, 4, Char_Desc_Handle, 0, 2);
                    sendBroadCastWithCharDesc(context, Char_Desc_Handle, FlowControl.ACTION_ACI_GATT_ADD_CHAR_DESC_SUCCESS);
                    break;
                case AciCommandConfig.STATUS_Error:
                    XLog.e(TAG, "STATUS_Error");
                    break;
                case AciCommandConfig.STATUS_OOM:
                    XLog.e(TAG, "STATUS_OOM");
                    break;
                case AciCommandConfig.STATUS_InValid_Handle:
                    XLog.e(TAG, "STATUS_InValid_Handle");
                    break;
                case AciCommandConfig.STATUS_InValid_Param:
                    XLog.e(TAG, "STATUS_InValid_Param");
                    break;
                case AciCommandConfig.STATUS_OOH:
                    XLog.e(TAG, "STATUS_OOH");
                    break;
                case AciCommandConfig.STATUS_InValid_Operation:
                    XLog.e(TAG, "STATUS_InValid_Operation");
                    break;
                case AciCommandConfig.STATUS_Insufficient_resources:
                    XLog.e(TAG, "STATUS_Insufficient_resources");
                    break;
            }
        }
    }
}
