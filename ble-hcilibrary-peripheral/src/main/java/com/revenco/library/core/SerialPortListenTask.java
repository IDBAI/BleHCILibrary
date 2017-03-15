package com.revenco.library.core;

import android.content.Context;
import android.content.Intent;
import android.SerialPort;
import android.os.Handler;
import android.os.Message;

import com.revenco.library.utils.ConvertUtil;
import com.revenco.library.utils.XLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * company:wanzhong
 * Created by Administrator on 2017/2/27.
 * class describe:底层串口任务
 * class_version: 1.0.0
 */
public class SerialPortListenTask extends android.os.AsyncTask<Void, byte[], Boolean> {
    public static final String ACTION_STATUS_CHANGE = "COM.REVENCO.BLEHCILIBRARY.ACTION_STATUS_CHANGE";
    public static final String ACTION_RECEIVE_DATA = "COM.REVENCO.BLEHCILIBRARY.ACTION_RECEIVE_DATA";
    public static final String EXTRA_SERIAL_DEVICE = "serial_device";
    public static final String EXTRA_STATE = "state";
    public static final String EXTRA_DATA = "data";
    public static final String EXTRA_OPCODE = "opcode";
    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTED = 100;
    public static final int STATE_DISCONNECTED = 1001;
    public static final int STATE_ERROR = 1002;
    private static final String TAG = "SerialPortListenTask";
    private static final int TIMEOUT_MSG = 10021;
    private static final long TIMEOUT = 3000L;
    private static final int TIMEOUT_MAX_TRY = Integer.MAX_VALUE;
    private int SerialStatus = STATE_NONE;
    private SerialPort serialPort;
    private InputStream inStream;
    private Context context;
    private OutputStream outStream;
    private byte[] lastOpCode;
    private byte[] currentOpCode;
    private int timeouts;
    private byte[] lastSendData;
    private byte[] currentSendData;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == TIMEOUT_MSG) {
                if (Arrays.equals(currentSendData, lastSendData)) {//相同指令
                    if (timeouts < TIMEOUT_MAX_TRY) {
                        timeouts++;
                        XLog.d(TAG, "timeouts = " + timeouts);
                        sendData(currentOpCode, currentSendData);
                    } else {
                        XLog.e(TAG, "超过最大重试发送指令次数！");
                    }
                } else {//不同指令
                    timeouts = 1;
                    XLog.d(TAG, "第一次重发！");
                    sendData(currentOpCode, currentSendData);
                }
            }
            return false;
        }
    });
    private int len;

    public SerialPortListenTask(Context cxt) {
        context = cxt;
        if (serialPort == null) {
            File device = new File(Config.SERIAL_BLE_DEVICE);
            try {
                serialPort = new SerialPort(device, Config.SERIAL_BAUDRATE, 0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (!aBoolean) {
            publicBleSerialStatus(STATE_ERROR);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        closeSerialListener();
        publicBleSerialStatus(STATE_DISCONNECTED);
    }

    private void closeSerialListener() {
        if (serialPort != null)
            serialPort.close();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        XLog.d(TAG, "doInBackground() called with: params = [" + params + "]");
        if (serialPort == null) {
            XLog.e(TAG, "serialPort == null");
            return false;
        }
        XLog.d(TAG, "=====> serial port listen start!!!!!");
        publicBleSerialStatus(STATE_CONNECTED);
        boolean result = true;
        inStream = serialPort.getInputStream();
        if (inStream != null) {
            while (!isCancelled()) {
                byte[] buffer = new byte[1024 * 2];//初始化为0x00 数据
                if (inStream == null) {
                    XLog.e(TAG, "inStream == null,please check!");
                    return false;
                }
                try {
                    XLog.i(TAG, "ready to lister the Serial.");
                    while (((!isCancelled()) && ((len = inStream.read(buffer)) != -1))) {
                        try {
                            XLog.d(TAG, "len = " + len);
                            if (len > 0) {
                                byte[] temp = new byte[len];
                                System.arraycopy(buffer, 0, temp, 0, len);
                                XLog.d(TAG, "<<<------------ ：" + ConvertUtil.byte2HexStrWithSpace(temp) + " size = " + temp.length);
                                //TODO
                                publicBleData(temp);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            XLog.e(TAG, "SerialPortListenTask error 1 !");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    result = isCancelled() ? true : false;
                    XLog.e(TAG, "SerialPortListenTask error 2 !");
                } catch (Exception e1) {
                    e1.printStackTrace();
                    XLog.e(TAG, "SerialPortListenTask error 3 !");
                }
            }
        } else {
            XLog.e(TAG, "inStream == null,please check it!");
        }
        try {
            if (inStream != null) {
                inStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            XLog.e(TAG, "SerialPortListenTask error 4 !");
        } catch (Exception e) {
            e.printStackTrace();
            XLog.e(TAG, "SerialPortListenTask error 5 !");
        }
        try {
            if (serialPort != null) {
                serialPort.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            XLog.e(TAG, "SerialPortListenTask error 6 !");
        }
        XLog.d(TAG, "=====> serial port listen stop!!!!!");
        return result;
    }

    //串口发送数据
    public void sendData(final byte[] opCode, final byte[] data) {
        //
        lastOpCode = currentOpCode;
        lastSendData = currentSendData;
        //
        currentOpCode = opCode;
        currentSendData = data;
        outStream = serialPort.getOutputStream();
        if (outStream != null) {
            try {
                outStream.write(data);
                if (mHandler != null) {
                    mHandler.removeMessages(TIMEOUT_MSG);
                    mHandler.sendEmptyMessageDelayed(TIMEOUT_MSG, TIMEOUT);
                    XLog.d(TAG, "发送指令开始计时！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            XLog.i(TAG, "------------>>> send data length: " + data.length);
            XLog.d(TAG, ConvertUtil.byte2HexStrWithSpace(data));
        }
    }

    public void publicBleSerialStatus(int status) {
        Intent intent = new Intent(ACTION_STATUS_CHANGE);
        intent.putExtra(EXTRA_SERIAL_DEVICE, Config.SERIAL_BLE_DEVICE);
        intent.putExtra(EXTRA_STATE, status);
        context.sendBroadcast(intent);
    }

    public void publicBleData(byte[] data) {
        XLog.d(TAG, "publicBleData() called with: data = [" + data + "]");
        if (mHandler != null) {
            XLog.d(TAG, "移除计时！");
            mHandler.removeMessages(TIMEOUT_MSG);
            timeouts = 0;
        }
        Intent intent = new Intent(ACTION_RECEIVE_DATA);
        intent.putExtra(EXTRA_SERIAL_DEVICE, Config.SERIAL_BLE_DEVICE);
        intent.putExtra(EXTRA_DATA, data);
        intent.putExtra(EXTRA_OPCODE, currentOpCode);
        context.sendBroadcast(intent);
    }
}
