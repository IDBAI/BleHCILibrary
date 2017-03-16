package com.revenco.library.core;

/**
 * Created by Administrator on 2017/2/27.
 * 硬编码配置----暂定，更app的配置意义对应
 */
public class Config {
    /**
     * 硬编码-连接的最大时长
     */
    public static final long CONNECT_MAX_TIME = 5500L;
    /**
     * 串口BLE设备
     **/
    public static final String SERIAL_BLE_DEVICE = "/dev/ttyS2";
    /**
     * 串口波特率
     **/
    public static final int SERIAL_BAUDRATE = 115200;
    /**
     *
     */
    public static final String DEVICE_NAME = "WZBeacon";
    /**
     * write UUID，硬编码-----------------------start
     */
    public static final byte[] CHAR_UUID_WRITE_00 = {0x11, 0x11, 0x11, 0x11, (byte) 0x96, (byte) 0xE2, 0x11, (byte) 0x9E, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x96, 0x11, 0x11, 0x11, 0x11};
    public static final byte[] CHAR_UUID_WRITE_01 = {0x22, 0x22, 0x22, 0x22, (byte) 0x96, (byte) 0xE2, 0x11, (byte) 0x9E, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x96, 0x22, 0x22, 0x22, 0x22};
    public static final byte[] CHAR_UUID_WRITE_02 = {0x33, 0x33, 0x33, 0x33, (byte) 0x96, (byte) 0xE2, 0x11, (byte) 0x9E, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x96, 0x33, 0x33, 0x33, 0x33};
    public static final byte[] CHAR_UUID_WRITE_03 = {0x44, 0x44, 0x44, 0x44, (byte) 0x96, (byte) 0xE2, 0x11, (byte) 0x9E, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x96, 0x44, 0x44, 0x44, 0x44};
    public static final byte[] CHAR_UUID_WRITE_04 = {0x55, 0x55, 0x55, 0x55, (byte) 0x96, (byte) 0xE2, 0x11, (byte) 0x9E, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x96, 0x55, 0x55, 0x55, 0x55};
    public static final byte[] CHAR_UUID_WRITE_05 = {0x66, 0x66, 0x66, 0x66, (byte) 0x96, (byte) 0xE2, 0x11, (byte) 0x9E, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x96, 0x66, 0x66, 0x66, 0x66};
    public static final byte[] CHAR_UUID_WRITE_06 = {0x77, 0x77, 0x77, 0x77, (byte) 0x96, (byte) 0xE2, 0x11, (byte) 0x9E, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x96, 0x77, 0x77, 0x77, 0x77};
    /**
     * write UUID，硬编码-----------------------end
     */
    ///***////
    /**
     * notify UUID，硬编码-----------------------start
     */
    public static final byte[] CHAR_UUID_NOTYFY = {(byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x96, (byte) 0xE2, 0x11, (byte) 0x9E, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x96, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88};
    /**
     * notify UUID，硬编码-----------------------end
     */
    /**
     * 描述符UUID，硬编码
     */
    public static final byte[] CHAR_DESC_UUID = {(byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88};
    /**
     * 硬编码
     */
    public static final byte[] CHAR_DESC_VALUE = {(byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99};
    /**
     * 硬编码--通知开门成功 状态
     */
    public static final byte CHAR_NOTIFY_STATUS_SUCCESS_VALUE = (byte) 0x88;
    /**
     * 硬编码--通知开门失败 状态
     */
    public static final byte CHAR_NOTIFY_STATUS_FAILED_VALUE = (byte) 0xFF;
    /**
     * REASON——
     */
    public static final byte SUCCESS_REASON = 0x00;
}
