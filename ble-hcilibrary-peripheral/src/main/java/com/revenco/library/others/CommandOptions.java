package com.revenco.library.others;

import android.os.Handler;

import com.revenco.library.command.OpCode;
import com.revenco.library.utils.ConvertUtil;
import com.revenco.library.utils.XLog;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/3/17 18:45.</p>
 * <p>CLASS DESCRIBE :指令操作工具类</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class CommandOptions {
    private static final String TAG = "CommandOptions";

    /**
     * @param data 完整包 eg：
     *             <p>
     *             //   [0x04,0xFF,0x03,    0x01,0x00, 0x01]
     *             //
     *             //   [0x04,0xFF,0x0B,    0x01,0x0C, 0x01,0x08,0x1D,0x00,0x02,0x00,0x00,0x01,0x00]
     *             <p>
     *             <p>
     *             04 FF 09 > 11 0C	 01 08 04 12 1E 00 01 		04 FF 06 > 10 0C 01 08 01 41
     *             <p>
     *             分割 0x04 0xFF 多包粘连情况
     * @param data
     * @return
     */
    public static ArrayList<byte[]> splitFFPackage(byte[] data) throws Exception {
        XLog.d(TAG, "splitFFPackage() called ");
        ArrayList<byte[]> list = new ArrayList<>();
        for (int i = 0; i < data.length; ) {
            if (data[i] == (byte) 0x04 && data[i + 1] == (byte) 0xFF) {
                int paramLen = data[i + 2];
                int totalLen = 3 + paramLen;
                byte[] dest = new byte[totalLen];
                System.arraycopy(data, i, dest, 0, totalLen);
                list.add(dest);
                i += totalLen;//跨出一帧
            } else {
                i++;
            }
        }
        return list;
    }

    /**
     * //                      04 0F  04 00 01 1C FD
     * //                      04 0F 04 00 01 06 04 size = 7
     * //                      [0x04,0x0F,0x04,0x00,0x01,0x06,0x04]
     * //                      04 0F 04 01 01 50 00 size = 7
     * <p>
     * 解析指令的状态
     *
     * @param mhandler
     * @param what
     * @param delay
     * @param data
     */
    public static void CommandStatusEvent(Handler mhandler, int what, long delay, byte[] data) {
        XLog.d(TAG, "CommandStatusEvent() called ");
        int ParameterTotalLength = data[2];
        int status = data[3];
        int Num_HCI_Command_Packets = data[4];
        byte[] opCode = new byte[2];
        System.arraycopy(data, 5, opCode, 0, 2);
        XLog.d(TAG, "opCode = " + ConvertUtil.byte2HexStrWithSpace(opCode));
        XLog.d(TAG, "status = " + Integer.toHexString(status));
        XLog.d(TAG, "不需要判断status");
        //不需要判断status
        //写特征值成功
        if (Arrays.equals(opCode, OpCode.ACI_GATT_WRITE_CHARAC_VAL_opCode)) {
        }
        //断开连接指令状态成功
        else if (Arrays.equals(opCode, OpCode.HCI_Disconnect_opCode)) {
            XLog.d(TAG, "* hci disconnect success! ");
            XLog.d(TAG, "断开连接指令状态成功");
            XLog.d(TAG, "等待断开指令返回！");
        } else {
            XLog.e(TAG, "// TODO: 2017/3/14 当做异常，暴力复位 ");
        }
        if (mhandler != null) {
            XLog.e("FlowControl", "开始 " + delay + " ms reset HW 计时！");
            mhandler.sendEmptyMessageDelayed(what, delay);
        }
    }

    /**
     * 将uuid 对象转换为特征值的枚举对象
     *
     * @param uuid
     * @return
     */
    public static byte[] convertUuid(byte[] uuid) {
        XLog.d(TAG, "convertUuid() called ");
        if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_00))
            return Config.CHAR_UUID_WRITE_00;
        else if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_01))
            return Config.CHAR_UUID_WRITE_01;
        else if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_02))
            return Config.CHAR_UUID_WRITE_02;
        else if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_03))
            return Config.CHAR_UUID_WRITE_03;
        else if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_04))
            return Config.CHAR_UUID_WRITE_04;
        else if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_05))
            return Config.CHAR_UUID_WRITE_05;
        else if (Arrays.equals(uuid, Config.CHAR_UUID_WRITE_06))
            return Config.CHAR_UUID_WRITE_06;
        else if (Arrays.equals(uuid, Config.CHAR_UUID_NOTYFY))
            return Config.CHAR_UUID_NOTYFY;
        else
            return new byte[1];
    }
}
