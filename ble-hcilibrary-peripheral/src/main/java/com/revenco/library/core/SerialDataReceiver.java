package com.revenco.library.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

import com.revenco.library.utils.XLog;

/**
 * company:wanzhong
 * Created by Administrator on 2017/2/27.
 * class describe: 串口数据接收类
 * class_version: 1.0.0
 */
public class SerialDataReceiver extends BroadcastReceiver {
    private static final String TAG = "SerialDataReceiver";
    private static SparseArray<SerialDataListener> listeners;

    static {
        listeners = new SparseArray<>();
    }

    public static boolean registListener(int id, SerialDataListener listener) {
        if (listener == null) {
            return false;
        }
        if (listeners.indexOfKey(id) > 0) {
            return false;
        }
        listeners.put(id, listener);
        return true;
    }

    public static void unregistListener(int id) {
        listeners.remove(id);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        XLog.d(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
        if (listeners.size() == 0)
            return;
        String action = intent.getAction();
        String device = intent.getStringExtra(SerialPortListenTask.EXTRA_SERIAL_DEVICE);
        switch (action) {
            case SerialPortListenTask.ACTION_STATUS_CHANGE:
                int status = intent.getIntExtra(SerialPortListenTask.EXTRA_STATE, SerialPortListenTask.STATE_NONE);
                for (int i = 0; i < listeners.size(); i++) {
                    SerialDataListener listener = listeners.valueAt(i);
                    if (listener != null)
                        listener.onStatusChange(device, status);
                }
                break;
            case SerialPortListenTask.ACTION_RECEIVE_DATA:
                byte[] data = intent.getByteArrayExtra(SerialPortListenTask.EXTRA_DATA);
                byte[] opCode = intent.getByteArrayExtra(SerialPortListenTask.EXTRA_OPCODE);
                for (int i = 0; i < listeners.size(); i++) {
                    SerialDataListener listener = listeners.valueAt(i);
                    if (listener != null)
                        listener.onDataReveice(device,opCode, data);
                }
                break;
        }
    }

    public interface SerialDataListener {
        void onDataReveice(String devices, byte[] opcode, byte[] data);

        void onStatusChange(String devices, int status);
    }
}
