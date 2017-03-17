package com.revenco.library.core;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.revenco.library.command.AciHciCommand;
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

    public static PeripharalManager getInstance() {
        if (instance == null)
            instance = new PeripharalManager();
        return instance;
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
        //1启动串口监听
        listenTask = new SerialPortListenTask(context);
        listenTask.execute();
        //2启动服务,内部监听了串口数据
        Intent service = new Intent(context, PeripheralService.class);
        context.bindService(service, connect, Service.BIND_AUTO_CREATE);
    }

    //--------------------------command control----------------start
    public void testnotify() {
        XLog.d(TAG, "TODO 测试，发送 notify");
        sendMsg2PeripheralService(PeripheralService.MSG_TEST_SEND_NOTIFY);
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

    /**
     * 通过manager 发送消息给 PeripheralService 周边服务
     *
     * @param what
     */
    public void sendMsg2PeripheralService(int what) {
        sendMessage(null, what);
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
