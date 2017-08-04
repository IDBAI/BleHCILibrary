package com.revenco.library;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.revenco.aidllibrary.AppConnectBean;
import com.revenco.aidllibrary.CommonUtils.AppConnectStatus;
import com.revenco.aidllibrary.CommonUtils.Constants;
import com.revenco.aidllibrary.CommonUtils.ConvertUtil;
import com.revenco.aidllibrary.CommonUtils.FlowStatus;
import com.revenco.aidllibrary.CommonUtils.Helper;
import com.revenco.aidllibrary.IHCIServiceAidlInterface;
import com.revenco.library.core.PeripharalManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * HCI 服务端对外接口服务
 */
public class HCIServiceAidlInterfaceImp extends Service {
    private static final String TAG = "HCIServiceAidlInterface";
    private BroadcastReceiver receive;
    private AppConnectBean connectBean;
    private FlowStatus currentStatus;
    private long starttime = -1;
    private IHCIServiceAidlInterface.Stub binder = new IHCIServiceAidlInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
        }

        @Override
        public void initBroadCast() throws RemoteException {
            registHciBroadCast();
        }

        @Override
        public void destroyBroadCast() throws RemoteException {
            destoryHciBroadCast();
        }

        @Override
        public void resetHCI() throws RemoteException {
            try {
                PeripharalManager.getInstance().resetHW();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void testNotify() throws RemoteException {
            try {
                PeripharalManager.getInstance().testnotify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void destory() throws RemoteException {
            try {
                PeripharalManager.getInstance().destory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public HCIServiceAidlInterfaceImp() {
    }

    private void destoryHciBroadCast() {
        if (receive != null)
            unregisterReceiver(receive);
    }

    private void initReceive() {
        receive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Helper.ACTION_APP_CONNECT_STATUS:
                        connectBean = (AppConnectBean) intent.getParcelableExtra(Helper.EXTRA_APPBEAN);
                        Log.d(TAG, connectBean.toString());
                        if (connectBean.status == AppConnectStatus.status_connected) {
                            System.out.println("app开始连接开始计时");
                        }
                        break;
                    case Helper.ACTION_REVEIVE_ATTRIBUTE_VALUES:
                        String appMac = intent.getStringExtra(Helper.EXTRA_APPMAC);
                        byte[] uuid = intent.getByteArrayExtra(Helper.EXTRA_CHAR_UUID);
                        byte[] values = intent.getByteArrayExtra(Helper.EXTRA_CHAR_VALUES);
                        String uuid_str = ConvertUtil.byte2HexStrWithSpace(uuid);
                        String values_str = ConvertUtil.byte2HexStrWithSpace(values);
                        String textstr = "appMac : " + appMac + "\n" + "uuid : " + uuid_str + "\n" + "vaules : " + values_str;
                        Log.d(TAG, textstr);
                        break;
                    case Helper.ACTON_FLOWCONTROL_STATUS:
                        //测试显示UI提示
                        currentStatus = (FlowStatus) intent.getSerializableExtra(Constants.ACTON_FLOWCONTROL_STATUS_VALUES);
                        if (currentStatus.toString().equalsIgnoreCase(FlowStatus.STATUS_HWRESET_SUCCESS.toString()))
                            starttime = SystemClock.uptimeMillis();
                        if (currentStatus.toString().equalsIgnoreCase(FlowStatus.STATUS_ACI_GAP_SET_DISCOVERABLE_SUCCESS.toString())) {
                            try {
                                //开启广播成功，数据分段显示
                                System.out.println("开启广播成功");
                                //
                                //
                            } catch (Exception e) {
                            }
                            if (starttime != -1) {
                                long jiange = SystemClock.uptimeMillis() - starttime;
                                System.out.println("开启广播耗时：" + jiange + " 毫秒");
                            }
                            Date date = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");
                            String timeStr = "广播更新时间：" + format.format(date);
                            System.out.println(timeStr);
                        }
                        break;
                }
            }
        };
    }

    private void registHciBroadCast() {
        initReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Helper.ACTON_FLOWCONTROL_STATUS);
        intentFilter.addAction(Helper.ACTION_REVEIVE_ATTRIBUTE_VALUES);
        intentFilter.addAction(Helper.ACTION_APP_CONNECT_STATUS);
        registerReceiver(receive, intentFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
