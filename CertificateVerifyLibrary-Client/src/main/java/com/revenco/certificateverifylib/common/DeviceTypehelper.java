package com.revenco.certificateverifylib.common;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/2/17.
 * 设备类型帮助类
 */
public class DeviceTypehelper {
    /**
     * MVI33/35 10.1寸室内机
     * [ro.product.rom_type]: [MMC33]
     * 或
     * [ro.product.rom_type]: [MMC35]
     */
    public static final int deviceType_MVI33_35_10 = 0;
    /**
     * MVI22 7寸室内机
     * [ro.product.rom_type]: [MVI22]
     */
    public static final int deviceType_MVI22_7 = 1;
    /**
     * MVO1600 7寸门口机
     * [ro.product.rom_type]: [MVO1600]
     */
    public static final int deviceType_MVO1600_7 = 2;
    //--------------------------设备类型---------------start
    public static final String MMC3 = "MMC3";
    public static final String MMC33 = "MMC33";
    public static final String MMC35 = "MMC35";
    public static final String MVI22 = "MVI22";//6
    public static final String MVO1600 = "MVO1600";//5
    //--------------------------设备类型---------------end

    public static String getPropInfo(String prop) {
        String cmdOutStr = ShellTools.runCmdAndReturn("/system/bin", new String[]{"getprop"});
        String[] kv = cmdOutStr.split("\n");
        if (kv != null && kv.length != 0) {
            HashMap props = new HashMap();
            for (int i = 0; i < kv.length; ++i) {
                String[] tmp = kv[i].split("]:");
                if (tmp.length == 2) {
                    tmp[0] = tmp[0].replace("[", "");
                    tmp[1] = tmp[1].replace("[", "");
                    tmp[0] = tmp[0].replace("]", "");
                    tmp[1] = tmp[1].replace("]", "");
                    props.put(tmp[0].trim(), tmp[1].trim());
                }
            }
            return (String) props.get(prop);
        } else {
            return "";
        }
    }

    /**
     * @return deviceType_MVI33_35_10
     * deviceType_MVI22_7
     * deviceType_MVO1600_7
     */
    public static int getDeviceType() {
        String propinfo = getPropInfo("ro.product.rom_type");
        String type = "";
        if (propinfo != null)
            type = propinfo.toUpperCase();
        switch (type) {
            case MMC3:
            case MMC33:
            case MMC35:
                return deviceType_MVI33_35_10;
            case MVI22:
                return deviceType_MVI22_7;
            case MVO1600:
                return deviceType_MVO1600_7;
        }
        return -1;
    }

    /**
     *
     */
    public static void getdeviceType() {
        switch (getDeviceType()) {
            case deviceType_MVI33_35_10://10.1寸室内机
                break;
            case deviceType_MVI22_7://7寸室内机
                break;
            case deviceType_MVO1600_7://7寸门口机
                break;
        }
    }

    /**
     * 是否是mvo1600机器
     *
     * @return
     */
    public static boolean isMVO1600Device() {
        if (getDeviceType() == deviceType_MVO1600_7) {
            System.out.println("the device is mvo1600!");
            return true;
        } else {
            return false;
        }
    }
}
