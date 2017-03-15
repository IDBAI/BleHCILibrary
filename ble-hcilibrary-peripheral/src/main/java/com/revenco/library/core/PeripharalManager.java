package com.revenco.library.core;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.revenco.library.Receive.AppEventReceive;
import com.revenco.library.command.AciHciCommand;
import com.revenco.library.command.FlowControl;
import com.revenco.library.utils.XLog;

/**
 * company:wanzhong
 * Created by Administrator on 2017/2/27.
 * class describe: 管理周边数据提供服务
 * class_version: 1.0.0
 */
public class PeripharalManager {
    private static final String TAG = "PeripharalManager";
    public static PeripharalManager instance;
    /**
     * 外部传递管理
     */
    public byte[] BLE_PUBLIC_MAC_ADDRESS;
    /**
     * 外部传递管理
     */
    public byte[] SERVICE_UUID;
    private Context context;
    private Messenger messenger;
    ServiceConnection connect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
        }
    };
    private SerialPortListenTask listenTask;
    private FlowControl flowControl;
    private AppEventReceive appEventReceive;

    public static PeripharalManager getInstance() {
        if (instance == null)
            instance = new PeripharalManager();
        return instance;
    }

    public AppEventReceive getAppEventReceive() {
        return appEventReceive;
    }

    public void setServiceUuid(byte[] serviceUuid) throws Exception {
        if (serviceUuid.length != 16) {
            throw new Exception("serviceUuid length must 16 bytes!");
        }
        SERVICE_UUID = serviceUuid;
    }

    public void setBlePublicMacAddress(byte[] blePublicMacAddress) throws Exception {
        if (blePublicMacAddress.length != 6) {
            throw new Exception("blePublicMacAddress length must 6 bytes!");
        }
        BLE_PUBLIC_MAC_ADDRESS = blePublicMacAddress;
    }

    public SerialPortListenTask getListenTask() {
        return listenTask;
    }

    public void init(Context context) {
        this.context = context;
        //1启动服务
        Intent service = new Intent(context, PeripheralService.class);
        context.bindService(service, connect, Service.BIND_AUTO_CREATE);
        //2启动串口监听
        listenTask = new SerialPortListenTask(context);
        listenTask.execute();
        //3、启动流程监控
        flowControl = new FlowControl();
        context.registerReceiver(flowControl, getFlowControlFilter());
        //4、启动app数据接收解析器
        appEventReceive = new AppEventReceive();
        context.registerReceiver(appEventReceive, getappEventReceiveFilter());
    }

    public void onDestory(Context context) {
        if (flowControl != null) {
            context.unregisterReceiver(flowControl);
        }
        if (appEventReceive != null) {
            context.unregisterReceiver(appEventReceive);
        }
    }

    private IntentFilter getappEventReceiveFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DealHCIEvent.ACTION_APP_CONNECT_STATUS);
        filter.addAction(DealHCIEvent.ACTION_REVEIVE_ATTRIBUTE_VALUES);
        return filter;
    }

    private IntentFilter getFlowControlFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(FlowControl.ACTION_OPEN_SUCCESS);
        filter.addAction(FlowControl.ACTION_HWRESET_SUCCESS);
        filter.addAction(FlowControl.ACTION_CONFIG_MODE_SUCCESS);
        filter.addAction(FlowControl.ACTION_CONFIG_PUBADDR_SUCCESS);
        filter.addAction(FlowControl.ACTION_SET_TX_POWER_LEVEL_SUCCESS);
        filter.addAction(FlowControl.ACTION_GATT_INIT_SUCCESS);
        filter.addAction(FlowControl.ACTION_GAP_INIT_SUCCESS);
        filter.addAction(FlowControl.ACTION_GATT_UPDATE_CHAR_VAL_SUCCESS);
        filter.addAction(FlowControl.ACTION_GATT_ADD_SERVICE_SUCCESS);
        filter.addAction(FlowControl.ACTION_GATT_ADD_CHAR_SUCCESS);
        filter.addAction(FlowControl.ACTION_ACI_GATT_ADD_CHAR_DESC_SUCCESS);
        filter.addAction(FlowControl.ACTION_SET_SCAN_RESPONSE_DATA_SUCCESS);
        filter.addAction(FlowControl.ACTION_ACI_GAP_SET_DISCOVERABLE_SUCCESS);
        filter.addAction(FlowControl.ACTION_ACI_GAP_UPDATE_ADV_DATA_SUCCESS);
        filter.addAction(FlowControl.ACTION_HCI_DISCONNECT);
        filter.addAction(FlowControl.ACTION_ENABLE_ADVERTISING);
        filter.addAction(FlowControl.ACTION_RESETHW_INIT);
        filter.addAction(FlowControl.ACTION_VERIFY_CERTIFICATE_RESULT);
        return filter;
    }

    //--------------------------command control----------------start
    public void testnotify() {
        XLog.d(TAG, "TODO 测试，发送 notify");
        Intent intent = new Intent(FlowControl.ACTION_VERIFY_CERTIFICATE_RESULT);
        intent.putExtra(FlowControl.EXTRA_VERIFY_STATUS, Config.CHAR_NOTIFY_STATUS_SUCCESS_VALUE);
        intent.putExtra(FlowControl.EXTRA_VERIFY_REASON, Config.SUCCESS_REASON);
        context.sendBroadcast(intent);
    }

    /**
     * 开启入口
     */
    public void start() throws Exception {
        XLog.d(TAG, "start() called");
        if (SERVICE_UUID == null && BLE_PUBLIC_MAC_ADDRESS == null) {
            throw new Exception("must call setServiceUuid and setBlePublicMacAddress first!");
        }
        AciHciCommand.bleHwReset(listenTask);
    }
    //--------------------------command control----------------end

    private void sendMessage(Object listenTask, int what) {
        if (messenger != null) {
            Message message = Message.obtain(null, what);
            message.obj = listenTask;
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
