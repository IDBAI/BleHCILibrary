//package com.revenco.library.Receive;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.content.LocalBroadcastManager;
//import android.text.TextUtils;
//
//import com.revenco.library.Bean.AppConnectBean;
//import com.revenco.library.command.FlowControl;
//import com.revenco.library.others.AppConnectStatus;
//import com.revenco.library.others.Config;
//import com.revenco.library.deals.DealHCIEvent;
//import com.revenco.library.utils.ConvertUtil;
//import com.revenco.library.utils.XLog;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Set;
//
//import static android.content.Intent.FLAG_RECEIVER_FOREGROUND;
//
///**
// * <p>PROJECT : BleHCILibrary</p>
// * <p>COMPANY : wanzhong</p>
// * <p>AUTHOR : Administrator on 2017/3/6 17:26.</p>
// * <p>CLASS DESCRIBE : </p>
// * <p>CLASS_VERSION : 1.0.0</p>
// */
//public class AppEventReceive extends BroadcastReceiver {
//    public static final int CONNECT_RESET_HW = 10001;
//    private static final String TAG = "AppEventReceive";
//    private Context context;
//    public Handler mhandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            if (msg.what == CONNECT_RESET_HW) {
//                XLog.d(TAG, "时间到，断开连接！");
//                Intent intent1 = new Intent(FlowControl.ACTION_HCI_READY_DISCONNECT);
//                intent1.addFlags(FLAG_RECEIVER_FOREGROUND);//TODO 测试效率
//                LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
//            }
//            return false;
//        }
//    });
//    private HashMap<byte[], byte[]> UUIDAttrValuesHashMap = new HashMap<>();
//    private String lastConnectAppmac;
//    private byte[][] uuidlist = {
//            Config.CHAR_UUID_WRITE_00,
//            Config.CHAR_UUID_WRITE_01,
//            Config.CHAR_UUID_WRITE_02,
//            Config.CHAR_UUID_WRITE_03,
//            Config.CHAR_UUID_WRITE_04,
//            Config.CHAR_UUID_WRITE_05,
//            Config.CHAR_UUID_WRITE_06
//    };
//    private byte[] connection_handle;
//
//    public byte[] getConnection_handle() {
//        return connection_handle;
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        this.context = context;
//        switch (intent.getAction()) {
//            case DealHCIEvent.ACTION_APP_CONNECT_STATUS:
//                AppConnectBean connectBean = (AppConnectBean) intent.getSerializableExtra(DealHCIEvent.EXTRA_APPBEAN);
//                XLog.d("TTT", connectBean.toString());
//                initHashMap(connectBean.appMac);
//                lastConnectAppmac = connectBean.appMac;
//                connection_handle = connectBean.Connection_Handle;
//                if (connectBean.status == AppConnectStatus.status_connected) {
//                    XLog.d(TAG, "开始" + Config.CONNECT_MAX_TIME + "ms计时！");
//                    mhandler.sendEmptyMessageDelayed(CONNECT_RESET_HW, Config.CONNECT_MAX_TIME);
//                }
//                break;
//            case DealHCIEvent.ACTION_REVEIVE_ATTRIBUTE_VALUES:
//                String appMac = intent.getStringExtra(DealHCIEvent.EXTRA_APPMAC);
//                byte[] uuid = intent.getByteArrayExtra(DealHCIEvent.EXTRA_CHAR_UUID);
//                byte[] values = intent.getByteArrayExtra(DealHCIEvent.EXTRA_CHAR_VALUES);
//                String uuid_str = ConvertUtil.byte2HexStrWithSpace(uuid);
//                String values_str = ConvertUtil.byte2HexStrWithSpace(values);
//                String textstr = "appMac : " + appMac + "\n" + "uuid : " + uuid_str + "\n" + "vaules : " + values_str;
//                XLog.d("GOOD", textstr);
//                fillMap(appMac, uuid, values);
//                break;
//        }
//    }
//
//    private void fillMap(String appMac, byte[] uuid, byte[] values) {
////      转换uuid对象到枚举对象
//        byte[] keyuuid = convertUuid(uuid);
//        if (appMac.equalsIgnoreCase(lastConnectAppmac)) {
//            UUIDAttrValuesHashMap.put(keyuuid, values);
//            lastConnectAppmac = appMac;
//            XLog.d(TAG, "UUIDAttrValuesHashMap.size = " + UUIDAttrValuesHashMap.size());
//            //debug();
//            //最后一个
//            if (Arrays.equals(keyuuid, Config.CHAR_UUID_WRITE_06)) {
//                if (mergeCertificate()) {
//                    verifyCerticate();
//                }
//            }
//        } else {
//            XLog.e(TAG, "app mac 不一致！");
//        }
//    }
//
//    private byte[] convertUuid(byte[] uuid) {
//        if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_00))
//            return Config.CHAR_UUID_WRITE_00;
//        else if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_01))
//            return Config.CHAR_UUID_WRITE_01;
//        else if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_02))
//            return Config.CHAR_UUID_WRITE_02;
//        else if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_03))
//            return Config.CHAR_UUID_WRITE_03;
//        else if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_04))
//            return Config.CHAR_UUID_WRITE_04;
//        else if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_05))
//            return Config.CHAR_UUID_WRITE_05;
//        else if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_06))
//            return Config.CHAR_UUID_WRITE_06;
//        else if (Arrays.equals(uuid, Config.CHAR_UUID_NOTYFY))
//            return Config.CHAR_UUID_NOTYFY;
//        else
//            return new byte[1];
//    }
//
//    private void debug() {
//        XLog.d(TAG, "map 的大小：" + UUIDAttrValuesHashMap.size());
//        Set<byte[]> keySet = UUIDAttrValuesHashMap.keySet();
//        for (byte[] key : keySet) {
//            byte[] values = UUIDAttrValuesHashMap.get(key);
//            XLog.d(TAG, "key = " + ConvertUtil.byte2HexStrWithSpace(key));
//            XLog.d(TAG, "values = " + ConvertUtil.byte2HexStrWithSpace(values));
//        }
//    }
//
//    /**
//     * 校验证书
//     */
//    private void verifyCerticate() {
//        if (true) {
//            //TODO 校验成功，发送 notify
//            XLog.d("TTT", "模拟证书校验100ms");
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            XLog.d("TTT", "TODO 校验成功，发送 notify");
//            Intent intent = new Intent(FlowControl.ACTION_VERIFY_CERTIFICATE_RESULT);
//            intent.putExtra(FlowControl.EXTRA_VERIFY_STATUS, Config.CHAR_NOTIFY_STATUS_SUCCESS_VALUE);
//            intent.putExtra(FlowControl.EXTRA_VERIFY_REASON, Config.SUCCESS_REASON);
//            intent.addFlags(FLAG_RECEIVER_FOREGROUND);//TODO 测试效率
//            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//            UUIDAttrValuesHashMap.clear();
//        } else {
//            //TODO 开门失败 发送 notify
//            XLog.d("TTT", "TODO 开门失败 发送 notify");
//        }
//    }
//
//    /**
//     * 拼接证书
//     *
//     * @return
//     */
//    private boolean mergeCertificate() {
//        XLog.d("TTT", "mergeCertificate() called");
//        byte[] temp = new byte[220];
//        int totallen = 0;
//        byte[] values;
//        for (byte[] uuid : uuidlist) {
//            values = UUIDAttrValuesHashMap.get(uuid);
//            if (values != null) {
//                totallen += values.length;
//                System.arraycopy(values, 0, temp, totallen - values.length, values.length);
//                XLog.d(TAG, "uuid：" + ConvertUtil.byte2HexStrWithSpace(uuid));
//                XLog.d(TAG, "values：" + ConvertUtil.byte2HexStrWithSpace(values));
//                XLog.d(TAG, "values.length：" + values.length);
//            }
//        }
//        if (totallen > 0) {
//            byte[] destbytes = new byte[totallen];
//            System.arraycopy(temp, 0, destbytes, 0, totallen);
//            XLog.d(TAG, "证书数据：" + ConvertUtil.byte2HexStrWithSpace(destbytes));
//            return true;
//        } else
//            return false;
//    }
//
//    private void initHashMap(String currentAppMac) {
//        XLog.d("TTT", "initHashMap() called with: currentAppMac = [" + currentAppMac + "]");
//        if (TextUtils.isEmpty(currentAppMac))
//            return;
//        if (!currentAppMac.equalsIgnoreCase(lastConnectAppmac))
//            UUIDAttrValuesHashMap.clear();
//    }
//}
