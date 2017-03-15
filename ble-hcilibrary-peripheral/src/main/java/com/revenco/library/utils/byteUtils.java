package com.revenco.library.utils;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/3/1 19:44.</p>
 * <p>CLASS DESCRIBE :</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class byteUtils {
    /**
     * @param bytes 长度仅仅支持1~4位
     * @return
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        try {
            if (bytes.length < 4) {
                byte[] padData = new byte[4 - bytes.length];
                for (int i = 0; i < bytes.length; i++) {
                    padData[i] = (byte) 0x00;
                }
                bytes = merge(padData, bytes);
            }
            value = byte2Int(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 合并数据
     *
     * @param b
     * @return
     */
    public static byte[] merge(byte[]... b) {
        int count = 0;
        for (byte[] mb : b) {
            count += mb.length;
        }
        byte[] result = new byte[count];
        int leng = 0;
        for (int i = 0; i < b.length; i++) {
            System.arraycopy(b[i], 0, result, leng, b[i].length);
            leng += b[i].length;
        }
        return result;
    }

    /**
     * byte 数组与 int 的相互转换
     * java 中int 4个字节，
     *
     * @param b 只支持4个字节的数组，一个32位的int 表示4个字节数组（4*8）
     * @return
     */
    private static int byte2Int(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }
}
