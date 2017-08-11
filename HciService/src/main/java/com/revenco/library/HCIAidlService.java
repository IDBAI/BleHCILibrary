package com.revenco.library;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.revenco.aidllibrary.CommonUtils.Helper;
import com.revenco.aidllibrary.IHCIServiceAidlInterface;
import com.revenco.library.core.PeripharalManager;

/**
 * HCI 服务端对外接口服务
 */
public class HCIAidlService extends Service {
    private IHCIServiceAidlInterface.Stub binder = new IHCIServiceAidlInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
        }

        @Override
        public void initBroadCast() throws RemoteException {
        }

        @Override
        public void destroyBroadCast() throws RemoteException {
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

        @Override
        public void getBleStatus() throws RemoteException {
            try {
                PeripharalManager.getInstance().sendMsg2Service(Helper.MSG_SEND_STATUS_TO_CLIENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public HCIAidlService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        try {
            binder.getBleStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }
}
