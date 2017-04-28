package com.revenco.library.others;

/**
 * Created by Administrator on 2017/2/27.
 * 硬编码配置----暂定，更app的配置意义对应
 */
public class Config {
    /**
     * 硬编码-连接的最大时长
     */
    public static final long CONNECT_MAX_TIME = 5 * 1000L;
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
    public static final String DEVICE_NAME = "WZB";
//    public static final String DEVICE_NAME = "w";
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

    ////------------以前的uuid
//    // D973F2E1-B19E-11E2-9E96-0800200C9A66
//    // D973F2E2-B19E-11E2-9E96-0800200C9A66
//    // D973F2E3-B19E-11E2-9E96-0800200C9A66
//    // D973F2E4-B19E-11E2-9E96-0800200C9A66
//    // D973F2E5-B19E-11E2-9E96-0800200C9A66
//    // D973F2E6-B19E-11E2-9E96-0800200C9A66
//    // D973F2E7-B19E-11E2-9E96-0800200C9A66
//    // D973F2E8-B19E-11E2-9E96-0800200C9A66
//    /**
//     * write UUID，硬编码-----------------------start
//     */
//    public static final byte[] CHAR_UUID_WRITE_00 = {(byte) 0xD9, 0x73, (byte) 0xF2, (byte) 0xE1, (byte) 0xB1, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x9E, (byte) 0x96, (byte) 0x08, (byte) 0x00, 0x20, 0x0C, (byte) 0x9A, 0x66};
//    public static final byte[] CHAR_UUID_WRITE_01 = {(byte) 0xD9, 0x73, (byte) 0xF2, (byte) 0xE2, (byte) 0xB1, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x9E, (byte) 0x96, (byte) 0x08, (byte) 0x00, 0x20, 0x0C, (byte) 0x9A, 0x66};
//    public static final byte[] CHAR_UUID_WRITE_02 = {(byte) 0xD9, 0x73, (byte) 0xF2, (byte) 0xE3, (byte) 0xB1, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x9E, (byte) 0x96, (byte) 0x08, (byte) 0x00, 0x20, 0x0C, (byte) 0x9A, 0x66};
//    public static final byte[] CHAR_UUID_WRITE_03 = {(byte) 0xD9, 0x73, (byte) 0xF2, (byte) 0xE4, (byte) 0xB1, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x9E, (byte) 0x96, (byte) 0x08, (byte) 0x00, 0x20, 0x0C, (byte) 0x9A, 0x66};
//    public static final byte[] CHAR_UUID_WRITE_04 = {(byte) 0xD9, 0x73, (byte) 0xF2, (byte) 0xE5, (byte) 0xB1, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x9E, (byte) 0x96, (byte) 0x08, (byte) 0x00, 0x20, 0x0C, (byte) 0x9A, 0x66};
//    public static final byte[] CHAR_UUID_WRITE_05 = {(byte) 0xD9, 0x73, (byte) 0xF2, (byte) 0xE6, (byte) 0xB1, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x9E, (byte) 0x96, (byte) 0x08, (byte) 0x00, 0x20, 0x0C, (byte) 0x9A, 0x66};
//    public static final byte[] CHAR_UUID_WRITE_06 = {(byte) 0xD9, 0x73, (byte) 0xF2, (byte) 0xE7, (byte) 0xB1, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x9E, (byte) 0x96, (byte) 0x08, (byte) 0x00, 0x20, 0x0C, (byte) 0x9A, 0x66};
//    /**
//     * write UUID，硬编码-----------------------end
//     */
//    ///***////
//    /**
//     * notify UUID，硬编码-----------------------start
//     */
//    public static final byte[] CHAR_UUID_NOTYFY = {(byte) 0xD9, 0x73, (byte) 0xF2, (byte) 0xE8, (byte) 0xB1, (byte) 0x9E, 0x11, (byte) 0xE2, (byte) 0x9E, (byte) 0x96, (byte) 0x08, (byte) 0x00, 0x20, 0x0C, (byte) 0x9A, 0x66};
//    /**
//     * notify UUID，硬编码-----------------------end
//     */
}
