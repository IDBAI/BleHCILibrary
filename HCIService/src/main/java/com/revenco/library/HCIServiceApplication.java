package com.revenco.library;

import android.app.Application;

import com.revenco.library.core.PeripharalManager;

/**
 * company:wanzhong
 * Created by Administrator on 2017/2/27.
 * class describe: HCI 服务 启动入口类，这里将会启动 HCI内部库，发送HCI指令驱动硬件层开启蓝牙服务
 * class_version: 1.0.0
 */
public class HCIServiceApplication extends Application {
    /**
     * beacon 的mac地址
     */
    public static final byte[] BLE_PUBLIC_MAC_ADDRESS = {0x66, 0x66, 0x66, 0x66, 0x66, 0x66};
    /**
     * 唯一的一个自己定义的服务UUID,16字节长，用于APP逻辑写入和接收通知
     * <p>
     * <p>
     * 66 9A 0C 20 00 08 99 77 55 33 66 55 44 33 22 11
     * <p>
     * 由于底层是小端模式，所以byte数组需要反向排列！
     */
    public static final byte[] SERVICE_UUID = {0x66, (byte) 0x9A, 0x0C, 0x20, 0x00, 0x08, (byte) 0x99, 0x77, 0x55, 0x33, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11};
    private static final String TAG = "HCIServiceApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            PeripharalManager.getInstance().setBlePublicMacAddress(BLE_PUBLIC_MAC_ADDRESS);
            PeripharalManager.getInstance().setServiceUuid(SERVICE_UUID);
            PeripharalManager.getInstance().start(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
