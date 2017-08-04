package com.revenco.library.core;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.revenco.aidllibrary.CommonUtils.Helper;
import com.revenco.aidllibrary.CommonUtils.XLog;
import com.revenco.library.command.AciHciCommand;

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
    private SerialPortListenTask listenTask;
    ServiceConnection connect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            XLog.d(TAG, "3 启动串口监听");
            //3 启动串口监听
            messenger = new Messenger(service);
            listenTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            try {
                Thread.sleep(30);
                start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            XLog.e(TAG, "onServiceDisconnected!");
            if (listenTask != null)
                listenTask.stoplistener();
            messenger = null;
            start(context);
        }
    };

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

    public void start(Context context) {
        this.context = context;
        //1 初始化串口监听
        listenTask = new SerialPortListenTask(context);
        XLog.d(TAG, "1 初始化串口监听");
        //2 启动服务,内部监听了串口数据
        Intent service = new Intent(context, PeripheralService.class);
        context.bindService(service, connect, Service.BIND_AUTO_CREATE);
        XLog.d(TAG, "2 启动服务,内部监听了串口数据");
    }

    public void destory() {
        XLog.d(TAG, "destory() called");
        if (listenTask != null)
            listenTask.stoplistener();
    }

    /**
     * 测试
     */
    public void resetHW() throws Exception {
        start();
    }

    private void start() throws Exception {
        XLog.d(TAG, "start() called");
        if (SERVICE_UUID == null && BLE_PUBLIC_MAC_ADDRESS == null) {
            throw new Exception("must call setServiceUuid and setBlePublicMacAddress first!");
        }
        AciHciCommand.bleHwReset(listenTask);
    }

    //--------------------------command control----------------start
    public void testnotify() {
        XLog.d(TAG, "TODO 测试，发送 notify");
        sendMsg2Service(Helper.MSG_TEST_SEND_NOTIFY);
    }

    /**
     * 通过manager 发送消息给  Service 周边服务
     *
     * @param what
     */
    public void sendMsg2Service(int what) {
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
