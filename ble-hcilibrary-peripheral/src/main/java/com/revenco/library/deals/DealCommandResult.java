package com.revenco.library.deals;

import com.revenco.library.command.AciCommandConfig;
import com.revenco.library.core.Helper;
import com.revenco.library.interfaces.FlowControlListener;
import com.revenco.library.others.ConfigProcess;
import com.revenco.library.others.FlowStatus;
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

    /**
     * @param listener
     * @param currentOpCode {0x03,0x0C};
     * @param paramContent  01 03 0c 00
     */
    public static void dealHWResetResult(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealHWResetResult() called ");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：01
        if (Arrays.equals(currentOpCode, opcode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* start success!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_HWRESET_SUCCESS);
            } else {
                XLog.e(TAG, "reset error,reset HW again!!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_RESETHW_INIT);
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
     * @param listener
     * @param reason_Code 01
     */
    public static void dealWithErrorResult(FlowControlListener listener, byte[] reason_Code) {
        XLog.d(TAG, "dealWithErrorResult() called ");
        switch (reason_Code[0]) {
            case 01:
                XLog.d(TAG, "Firmware started properly!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_HWRESET_SUCCESS);
                break;
            default:
                XLog.e(TAG, "unknow error,reset HW again!!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_RESETHW_INIT);
                break;
        }
    }

    /**
     * [0x04,0x0E,0x04,0x01,0x0C,0xFC,0x00]
     * [0x04,0x0E,0x04,0x01,0x0C,0xFC,0x00]
     * [0x04,0x0E,0x04,0x01,0x0F,0xFC,0x00]
     *
     * @param listener
     * @param currentOpCode 0x0C,0xFC,
     * @param paramContent  ,0x01,0x0C,0xFC,0x00]
     */
    public static void dealConfigDataResult(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealConfigDataResult() called ");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            boolean isOtherError = false;
            switch (status) {
                case AciCommandConfig.EVENT_BLE_STATUS_SUCCESS:
                    if (Helper.currentHasConfig == ConfigProcess.config_none) {
                        XLog.d(TAG, "* config mode  success!");
                        if (listener != null)
                            listener.flowStatusChange(FlowStatus.STATUS_CONFIG_MODE_SUCCESS);
                    } else if (Helper.currentHasConfig == ConfigProcess.config_mode) {
                        XLog.d(TAG, "* config public address  success!");
                        if (listener != null)
                            listener.flowStatusChange(FlowStatus.STATUS_CONFIG_PUBADDR_SUCCESS);
                    }
                    break;
                case AciCommandConfig.ERR_INVALID_HCI_CMD_PARAMS:
                    XLog.e(TAG, "ERR_INVALID_HCI_CMD_PARAMS!!!");
                    isOtherError = true;
                    break;
                default:
                    XLog.e(TAG, "error,please check it!");
                    isOtherError = true;
                    break;
            }
            if (isOtherError) {
                XLog.e(TAG, "error!reset HW again!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_RESETHW_INIT);
            }
        }
    }

    /**
     * @param listener
     * @param currentOpCode
     * @param paramContent  0x01,0x01,0xFD,0x00
     */
    public static void dealGattInitResult(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealGattInitResult() called ");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* gatt init success!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_GATT_INIT_SUCCESS);
            } else {
                XLog.e(TAG, "gatt init failed, reset HW again!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_RESETHW_INIT);
            }
        }
    }

    /**
     * @param listener
     * @param currentOpCode
     * @param paramContent  [0x01,0x8A,0xFC,0x00,       0x05,0x00,    0x06,0x00,    0x08,0x00]
     */
    public static void dealGapInitResult(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealGapInitResult() called ");
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
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_GAP_INIT_SUCCESS, Service_Handle, Dev_Name_Char_Handle);
            } else {
                XLog.e(TAG, "gap init failed, reset HW again!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_RESETHW_INIT);
            }
        }
    }

    /**
     * //[0x04,0x0E,0x04,0x01,0x06,0xFD,0x00]
     *
     * @param listener
     * @param currentOpCode 0x06,0xFD,
     * @param paramContent  0x01,0x06,0xFD,0x00
     */
    public static void dealGattUpdateCharValResult(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealGattUpdateCharValResult() called ");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* gatt update char val success!----notify success!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_GATT_UPDATE_CHAR_VAL_SUCCESS);
            } else {
                XLog.e(TAG, "* gatt update char val failed!----notify failed!");
                XLog.e(TAG, "reset HW again!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_RESETHW_INIT);
            }
        }
    }

    public static void dealSetTxPowerResult(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealSetTxPowerResult() called ");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* set txt Power  success!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_SET_TX_POWER_LEVEL_SUCCESS);
            } else {
                XLog.e(TAG, "set txt Power error, reset HW again!!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_RESETHW_INIT);
            }
        }
    }

    /**
     * [0x01,0x02,0xFD,0x00,0x0C,0x00]
     *
     * @param listener
     * @param currentOpCode 0x02,0xFD,                             Service_Handle
     * @param paramContent  [0x01,0x02,0xFD,0x00,                0x0C,0x00]
     */
    public static void dealGattAddService(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealGattAddService() called ");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            boolean isOtherError = false;
            switch (status) {
                case 0x00:
                    XLog.d(TAG, "* gatt add service  success!");
                    //
                    byte[] Service_Handle = new byte[2];
                    //拷贝Handler
                    System.arraycopy(paramContent, 4, Service_Handle, 0, 2);
                    if (listener != null)
                        listener.flowStatusChange(FlowStatus.STATUS_GATT_ADD_SERVICE_SUCCESS, Service_Handle);
                    break;
                case 0x47:
                    XLog.e(TAG, "Error");
                    isOtherError = true;
                    break;
                case 0x1F:
                    XLog.e(TAG, "Out of memory");
                    isOtherError = true;
                    break;
                case 0x61:
                    XLog.e(TAG, "Invalid parameter");
                    isOtherError = true;
                    break;
                case 0x62:
                    XLog.e(TAG, "Out of handle");
                    isOtherError = true;
                    break;
                case 0x64:
                    XLog.e(TAG, "Insufficient resources");
                    isOtherError = true;
                    break;
            }
            if (isOtherError) {
                XLog.e(TAG, "gatt add service failed,reset HW again!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_RESETHW_INIT);
            }
        }
    }

    /**
     * [0x04,0x0E,0x06,0x01,0x04,0xFD,0x00,0x0D,0x00]
     *
     * @param listener
     * @param currentOpCode 0x04,0xFD,
     * @param paramContent  0x01,0x04,0xFD,0x00,0x0D,0x00
     */
    public static void dealGattAddChar(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealGattAddChar() called ");
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
                    if (listener != null)
                        listener.flowStatusChange(FlowStatus.STATUS_GATT_ADD_CHAR_SUCCESS, Char_Handle);
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
                default:
                    XLog.e(TAG, "error,please check it!");
                    isOtherError = true;
                    break;
            }
            if (isOtherError) {
                // TODO: 2017/3/15  添加特征值遇到错误，强制 resetHW
                XLog.e(TAG, "// TODO: 2017/3/15  添加特征值遇到错误，reset HW again!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_RESETHW_INIT);
            }
        }
    }

    /**
     * [0x04,0x0E,0x04,0x01,0x09,0x20,0x00]
     *
     * @param listener
     * @param currentOpCode 0x09,0x20,
     * @param paramContent  0x01,0x09,0x20,0x00
     */
    public static void dealSetScanResponseData(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealSetScanResponseData() called ");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* set scan response data  success!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_SET_SCAN_RESPONSE_DATA_SUCCESS);
            } else {
                XLog.e(TAG, "set scan response data failed, reset HW again!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_RESETHW_INIT);
            }
        }
    }

    /**
     * @param listener
     * @param currentOpCode 0x83,0xFC,
     * @param paramContent  0x01,0x83,0xFC,0x00
     */
    public static void dealAciGapDisCoverable(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        XLog.d(TAG, "dealAciGapDisCoverable() called ");
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            boolean isOtherError = false;
//            0x00: Success
//            0x42: Invalid parameter
//            0x0C: Command disallowed
//            0x11: Unsupported feature
            switch (status) {
                case (byte) 0x00:
                    XLog.d(TAG, "* aci gap set discoverable success!");
                    if (listener != null)
                        listener.flowStatusChange(FlowStatus.STATUS_ACI_GAP_SET_DISCOVERABLE_SUCCESS);
                    break;
                case (byte) 0x42:
                    XLog.e(TAG, "Invalid parameter");
                    isOtherError = true;
                    break;
                case (byte) 0x0C:
                    XLog.e(TAG, "Command disallowed");
                    isOtherError = true;
                    break;
                case (byte) 0x11:
                    XLog.e(TAG, "Unsupported feature");
                    isOtherError = true;
                    break;
                default:
                    XLog.e(TAG, "error,please check it!");
                    isOtherError = true;
                    break;
            }
            if (isOtherError) {
                XLog.e(TAG, "* aci gap set discoverable failed,reset HW again!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_RESETHW_INIT);
            }
        }
    }

    public static void dealAciGapDeleteADType(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* aci gap update ADV data success!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.ACI_GAP_DELETE_AD_TYPE_SUCCESS);
            }
        }
    }
    public static void dealAciGapUpdateADVData(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            if (status == AciCommandConfig.EVENT_BLE_STATUS_SUCCESS) {
                XLog.d(TAG, "* aci gap update ADV data success!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_ACI_GAP_UPDATE_ADV_DATA_SUCCESS);
            }
        }
    }

    public static void dealAddCharDesc(FlowControlListener listener, byte[] currentOpCode, byte[] paramContent) {
        byte[] opcode = new byte[2];
        System.arraycopy(paramContent, 1, opcode, 0, 2);//第0位为：Num_HCI_Command_Packets
        if (Arrays.equals(opcode, currentOpCode)) {
            byte status = paramContent[3];
            boolean isOtherError = false;
            switch (status) {
                case AciCommandConfig.STATUS_Success:
                    XLog.d(TAG, "* aci gatt add char desc success!");
                    byte[] Char_Desc_Handle = new byte[2];
                    //拷贝Handler
                    System.arraycopy(paramContent, 4, Char_Desc_Handle, 0, 2);
                    if (listener != null)
                        listener.flowStatusChange(FlowStatus.STATUS_ACI_GATT_ADD_CHAR_DESC_SUCCESS, Char_Desc_Handle);
                    break;
                case AciCommandConfig.STATUS_Error:
                    XLog.e(TAG, "STATUS_Error");
                    isOtherError = true;
                    break;
                case AciCommandConfig.STATUS_OOM:
                    XLog.e(TAG, "STATUS_OOM");
                    isOtherError = true;
                    break;
                case AciCommandConfig.STATUS_InValid_Handle:
                    XLog.e(TAG, "STATUS_InValid_Handle");
                    isOtherError = true;
                    break;
                case AciCommandConfig.STATUS_InValid_Param:
                    XLog.e(TAG, "STATUS_InValid_Param");
                    isOtherError = true;
                    break;
                case AciCommandConfig.STATUS_OOH:
                    XLog.e(TAG, "STATUS_OOH");
                    isOtherError = true;
                    break;
                case AciCommandConfig.STATUS_InValid_Operation:
                    XLog.e(TAG, "STATUS_InValid_Operation");
                    isOtherError = true;
                    break;
                case AciCommandConfig.STATUS_Insufficient_resources:
                    XLog.e(TAG, "STATUS_Insufficient_resources");
                    isOtherError = true;
                    break;
                default:
                    isOtherError = true;
                    break;
            }
            if (isOtherError) {
                XLog.e(TAG, "* aci gatt add char desc failed,reset HW again!");
                if (listener != null)
                    listener.flowStatusChange(FlowStatus.STATUS_RESETHW_INIT);
            }
        }
    }
}
