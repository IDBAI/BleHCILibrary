package com.revenco.library.command;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import com.revenco.library.Bean.CharBean;
import com.revenco.library.core.AttrOrder;
import com.revenco.library.core.Config;
import com.revenco.library.core.PeripharalManager;
import com.revenco.library.core.PeripheralService;
import com.revenco.library.utils.ConvertUtil;
import com.revenco.library.utils.Utils;
import com.revenco.library.utils.XLog;
import com.revenco.library.utils.byteUtils;

import static com.revenco.library.Receive.AppEventReceive.RESET_HW;
import static com.revenco.library.command.FlowControl.ConfigProcess.config_mode;
import static com.revenco.library.command.FlowControl.ConfigProcess.config_publicAddress;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/2/27 17:26.</p>
 * <p>CLASS DESCRIBE :负责流程控制,数据处理等工作</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class FlowControl extends BroadcastReceiver {
    public static final String ACTION_OPEN_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_OPEN_SUCCESS";
    public static final String ACTION_HWRESET_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_HWRESET_SUCCESS";
    public static final String ACTION_CONFIG_MODE_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_CONFIG_MODE_SUCCESS";
    public static final String ACTION_CONFIG_PUBADDR_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_CONFIG_PUBADDR_SUCCESS";
    public static final String ACTION_SET_TX_POWER_LEVEL_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_SET_TX_POWER_LEVEL_SUCCESS";
    public static final String ACTION_GATT_INIT_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_GATT_INIT_SUCCESS";
    public static final String ACTION_GAP_INIT_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_GAP_INIT_SUCCESS";
    public static final String ACTION_GATT_UPDATE_CHAR_VAL_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_GATT_UPDATE_CHAR_VAL_SUCCESS";
    public static final String ACTION_GATT_ADD_SERVICE_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_GATT_ADD_SERVICE_SUCCESS";
    public static final String ACTION_GATT_ADD_CHAR_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_GATT_ADD_CHAR_SUCCESS";
    public static final String ACTION_ACI_GATT_ADD_CHAR_DESC_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_ACI_GATT_ADD_CHAR_DESC_SUCCESS";
    public static final String ACTION_SET_SCAN_RESPONSE_DATA_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_SET_SCAN_RESPONSE_DATA_SUCCESS";
    public static final String ACTION_ACI_GAP_SET_DISCOVERABLE_SUCCESS = "com.revenco.blehcilibrary.command.ACI_GAP_SET_DISCOVERABLE";
    public static final String ACTION_ACI_GAP_UPDATE_ADV_DATA_SUCCESS = "com.revenco.blehcilibrary.command.ACTION_ACI_GAP_UPDATE_ADV_DATA_SUCCESS";
    public static final String ACTION_HCI_DISCONNECT = "com.revenco.blehcilibrary.command.ACTION_HCI_DISCONNECT";
    /**
     * 证书开门校验结果通知事件
     */
    public static final String ACTION_VERIFY_CERTIFICATE_RESULT = "com.revenco.blehcilibrary.command.ACTION_VERIFY_CERTIFICATE_RESULT";
    /**
     * enable，温和方式复位广播状态
     */
    public static final String ACTION_ENABLE_ADVERTISING = "com.revenco.blehcilibrary.command.ACTION_ENABLE_ADVERTISING";
    /**
     * 暴力方式初始化广播
     */
    public static final String ACTION_RESETHW_INIT = "com.revenco.blehcilibrary.command.ACTION_RESETHW_INIT";
    public static final String EXTRA_Service_Handle = "EXTRA_Service_Handle";
    public static final String EXTRA_Char_Handle = "EXTRA_Char_Handle";
    public static final String EXTRA_Char_DESC_Handle = "EXTRA_Char_DESC_Handle";
    public static final String EXTRA_Dev_Name_Char_Handle = "EXTRA_Dev_Name_Char_Handle";
    public static final String EXTRA_VERIFY_STATUS = "EXTRA_VERIFY_STATUS";
    public static final String EXTRA_VERIFY_REASON = "EXTRA_VERIFY_REASON";
    /**
     * 特征值集合
     */
    public static final int CHAR_SET_SIZE = 8;
    private static final String TAG = "FlowControl";
    private static final byte WRITE_PROPERTIES = CharacteristicProperty.PROPERTY_WRITE | CharacteristicProperty.PROPERTY_WRITE_NO_RESPONSE;
    private static final byte NOFITY_PROPERTIES = CharacteristicProperty.PROPERTY_NOTIFY;
    /**
     * 当前已经配置的进度
     */
    public static volatile ConfigProcess currentHasConfig = ConfigProcess.config_none;
    /**
     * 特征值实体集合
     */
    private static SparseArray<CharBean> charBeanSparseArray = new SparseArray<>(CHAR_SET_SIZE);

    public static SparseArray<CharBean> getCharBeanSparseArray() {
        return charBeanSparseArray;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = null;
        switch (intent.getAction()) {
            case ACTION_OPEN_SUCCESS://1 开启成功
                XLog.d(TAG, "1 开启成功");
                AciHciCommand.bleHwReset(PeripharalManager.getInstance().getListenTask());
                currentHasConfig = ConfigProcess.config_none;
                break;
            case ACTION_HWRESET_SUCCESS://2 reset 成功
                XLog.d(TAG, "2 reset 成功");
                AciHalCommand.writeConfig_ModeData(PeripharalManager.getInstance().getListenTask());
                currentHasConfig = ConfigProcess.config_none;
                break;
            //配置开始
            case ACTION_CONFIG_MODE_SUCCESS://3 config mode 成功
                XLog.d(TAG, "3 config mode 成功");
                AciHalCommand.writeConfig_PublicAddrData(PeripharalManager.getInstance().getListenTask());
                currentHasConfig = config_mode;
                break;
            case ACTION_CONFIG_PUBADDR_SUCCESS://4 config address 成功
                XLog.d(TAG, "4 config address 成功");
                AciHalCommand.writeConfig_TxPowerData(PeripharalManager.getInstance().getListenTask());
                currentHasConfig = config_publicAddress;
                break;
            case ACTION_SET_TX_POWER_LEVEL_SUCCESS://5 set tx power 成功
                XLog.d(TAG, "5 set tx power 成功");
                //初始化GATT
                AciGattCommand.bleGattInit(PeripharalManager.getInstance().getListenTask());
                break;
            case ACTION_GATT_INIT_SUCCESS://6 gatt init 成功
                XLog.d(TAG, "6 gatt init 成功");
                //初始化GAP
                AciGapCommand.aciGapInit(PeripharalManager.getInstance().getListenTask());
                break;
            case ACTION_GAP_INIT_SUCCESS://7 gap init 成功
                XLog.d(TAG, "7 gap init 成功");
                //TODO go on,--->you can add service and char
                AciGattCommand.gattAddService(PeripharalManager.getInstance().getListenTask());
                break;
            case ACTION_GATT_ADD_SERVICE_SUCCESS://9 add service 成功
                XLog.d(TAG, "8 add service 成功");
                extras = intent.getExtras();
                if (extras != null) {
                    byte[] service_handle = extras.getByteArray(EXTRA_Service_Handle);
                    //开始构造char 集合，包含 CHAR_SET_SIZE 个char
                    initCharSet(service_handle);
                    //开始添加
                    startAddChar();
                }
                break;
            case ACTION_GATT_ADD_CHAR_SUCCESS://10.0 add 8 个char 成功
                extras = intent.getExtras();
                if (extras != null) {
                    byte[] Char_Handle = extras.getByteArray(EXTRA_Char_Handle);
                    //设置已添加标识
                    setCharHandlerAndMark(Char_Handle);
                    //再次添加
                    boolean isAddSuccess = startAddChar();
                    if (!isAddSuccess) {//全部已经被添加了 --> 添加失败,设置广播数据
                        XLog.d(TAG, "9.0 add 8 个char 成功");
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
                    }
                }
                break;
            case ACTION_ACI_GATT_ADD_CHAR_DESC_SUCCESS:
                extras = intent.getExtras();
                if (extras != null) {
                    byte[] Char_desc_Handle = extras.getByteArray(EXTRA_Char_DESC_Handle);
                    XLog.d(TAG, "Char_desc_Handle = " + ConvertUtil.byte2HexStrWithSpace(Char_desc_Handle));
                }
                XLog.d(TAG, "10 为 notify 添加描述符成功");
                AciHciCommand.setBleScanResponseData(PeripharalManager.getInstance().getListenTask());
                break;
            case ACTION_SET_SCAN_RESPONSE_DATA_SUCCESS://11 设置扫描响应数据成功
                XLog.d(TAG, "11 设置扫描响应数据成功");
                AciGapCommand.aciGapSetDiscoverable(PeripharalManager.getInstance().getListenTask());
                break;
            case ACTION_ACI_GAP_SET_DISCOVERABLE_SUCCESS://12 开启广播成功
                XLog.d("TTT", "12 开启广播成功");
                XLog.d(TAG, "12 开启广播成功");
                XLog.d(TAG, "set PeripheralService.isIniting = false;");
                PeripheralService.isIniting = false;
                break;
//                不使用
//                AciGapCommand.AciGapUpdateADVData(PeripharalManager.getInstance().getListenTask());
//                break;
//            case ACTION_ACI_GAP_UPDATE_ADV_DATA_SUCCESS:
//                XLog.d(TAG, "13 更新广播数据成功");
//                break;
            case ACTION_HCI_DISCONNECT:
                removeResetHwHandler();
                XLog.d(TAG, "准备断开连接");
                AciHciCommand.hciDisconnect(PeripharalManager.getInstance().getListenTask(),
                        PeripharalManager.getInstance().getAppEventReceive().getConnection_handle());
                break;
            case ACTION_ENABLE_ADVERTISING:
                removeResetHwHandler();
                XLog.d(TAG, "aciGapSetDiscoverable -->enable 广播！");
                AciGapCommand.aciGapSetDiscoverable(PeripharalManager.getInstance().getListenTask());
                break;
            case ACTION_RESETHW_INIT:
                removeResetHwHandler();
                XLog.d(TAG, "bleHwReset -->复位广播！");
                AciHciCommand.bleHwReset(PeripharalManager.getInstance().getListenTask());
                break;
            case ACTION_VERIFY_CERTIFICATE_RESULT:
                XLog.d(TAG, "准备通知开门结果");
                extras = intent.getExtras();
                if (extras != null) {
                    byte status = extras.getByte(EXTRA_VERIFY_STATUS);
                    byte reason = extras.getByte(EXTRA_VERIFY_REASON);
                    CharBean notifyCharBean = getNotifyCharBean();
                    AciGattCommand.updateCharValues(PeripharalManager.getInstance().getListenTask(), notifyCharBean.service_handler, notifyCharBean.char_handle, status, reason);
                }
                break;
            case ACTION_GATT_UPDATE_CHAR_VAL_SUCCESS://8 gatt update char val 成功
//                XLog.d("TTT", "通知开门结果成功--> TODO 重启广播");
//                XLog.d(TAG, "通知开门结果成功--> TODO 重启广播");
//                //TODO 重启广播
//                context.sendBroadcast(new Intent(FlowControl.ACTION_ENABLE_ADVERTISING));
                XLog.d(TAG, "通知开门结果成功--> 断开连接");
                context.sendBroadcast(new Intent(FlowControl.ACTION_HCI_DISCONNECT));
                break;
        }
    }

    private void removeResetHwHandler() {
        XLog.d(TAG, "安全移除计时");
        PeripharalManager.getInstance().getAppEventReceive().mhandler.removeMessages(RESET_HW);
    }

    private void printCharBean() {
        for (int i = 0; i < CHAR_SET_SIZE; i++) {
            CharBean charbean = charBeanSparseArray.get(i);
            XLog.e(TAG, charbean.toString());
        }
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
     * 配置进度
     */
    public enum ConfigProcess {
        config_none,
        config_mode,
        config_publicAddress,
    }
}
