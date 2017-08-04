package com.revenco.aidllibrary.CommonUtils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * Created by Administrator on 2016/10/28.
 */
public class ConvertUtil {
    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 十六进制转换字符串
     *
     * @param hexStr str Byte字符串(Byte之间无分隔符 如:[616C6B])
     * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * bytes转换成十六进制字符串
     *
     * @param b byte数组
     * @return String 每个Byte值之间空格分隔
     */
    @SuppressLint("DefaultLocale")
    public static String byte2HexStrWithoutSpace(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(""); // 修改不加空格
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * @param b 中间添加空格，为了日志可显示清晰
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String byte2HexStrWithSpace(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * bytes转换成十六进制字符串,以自定义的符号分割
     *
     * @param b
     * @param s 分割符
     * @return
     */
    public static String byte2HexStrWithComma(byte[] b, String s) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(s);
        }
        sb = sb.deleteCharAt(sb.length() - 1);//删除尾部多余的分隔符
        return sb.toString().toUpperCase().trim();
    }

    /**
     * @param b      长度仅仅支持1~4位
     * @param offset
     * @return
     */
    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        try {
            if (b.length < 4) {
                byte[] padData = new byte[4 - b.length];
                for (int i = 0; i < b.length; i++) {
                    padData[i] = (byte) 0x00;
                }
                b = merge(padData, b);
            }
            for (int i = 0; i < 4; i++) {
                int shift = (4 - 1 - i) * 8;
                value += (b[i + offset] & 0x000000FF) << shift;// 往高位游
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * bytes字符串转换为Byte值
     *
     * @param src src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        System.out.println(l);
        byte[] ret = new byte[l];
        try {
            for (int i = 0; i < l; i++) {
                m = i * 2 + 1;
                n = m + 1;
                ret[i] = (byte) (Integer.parseInt(src.substring(i * 2, m) + src.substring(m, n), 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * String的字符串转换成unicode的String
     *
     * @return String 每个unicode之间无分隔符
     * @throws Exception
     */
    public static String strToUnicode(String strText) throws Exception {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128)
                str.append("\\u" + strHex);
            else
                // 低位在前面补00
                str.append("\\u00" + strHex);
        }
        return str.toString();
    }

    /**
     * unicode的String转换成String的字符串
     *
     * @param hex hex 16进制值字符串 （一个unicode为2byte）
     * @return String 全角字符串
     */
    public static String unicodeToString(String hex) {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++) {
            String s = hex.substring(i * 6, (i + 1) * 6);
            // 高位需要补上00再转
            String s1 = s.substring(2, 4) + "00";
            // 低位直接转
            String s2 = s.substring(4);
            // 将16进制的string转为int
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
            // 将int转换为字符
            char[] chars = Character.toChars(n);
            str.append(new String(chars));
        }
        return str.toString();
    }

    /**
     * 分割数据
     *
     * @param b     需要分割的数据
     * @param start 去掉数据前面的多少位
     * @param end   去掉数据后面的多少位
     * @return 分割后的数据
     * @function 主要用于将主机学习到的编码从返回数据中提出
     */
    public static byte[] split(byte[] b, int start, int end) {
        int count = b.length - start - end;
        byte[] result = new byte[count];
        System.arraycopy(b, start, result, 0, count);
        return result;
    }

    /**
     * @param b
     * @param start  索引值位置开始
     * @param length 需要分割多少位,字节数
     * @return
     */
    public static byte[] splitbyte(byte[] b, int start, int length) {
        byte[] result = new byte[length];
        try {
            System.arraycopy(b, start, result, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
     * 普通字符串转byte[]
     *
     * @param str
     * @return
     */
    public static byte[] str2Bytes(String str) {
        try {
            byte[] result = str.getBytes("ISO-8859-1");
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取校验码
     *
     * @param b
     * @return
     */
    public static byte getCRC(byte[] b) {
        byte sum = 0;
        for (byte by : b)
            sum += by;
        return sum;
    }

    /**
     * 替换原byte[]中的校验码,插座检验码
     *
     * @param b
     * @return
     */
    public static byte[] replaceCRC_Plug(byte[] b) {
        byte crc1 = 0;
        byte crc2 = 0;
        for (int i = 0; i < b.length; i++) {
            crc1 += b[i];
            crc2 ^= b[i];
        }
        b[b.length - 2] = crc1;
        b[b.length - 1] = crc2;
        return b;
    }

    /**
     * 遥控红外数据的发送之前的校验
     *
     * @param b
     * @return
     */
    public static byte[] replaceCRC_IR(byte[] b) {
        byte crc1 = 0;
        byte crc2 = 0;
        for (int i = 0; i < b.length; i++) {
            crc1 += b[i];
            crc2 ^= b[i];
        }
        b[b.length - 2] = crc1;
        b[b.length - 1] = crc2;
        return b;
    }

    /**
     * 将16进制字符串转换为整型值
     *
     * @param hexStr
     * @return
     */
    public static long hexStr2Long(String hexStr) {
        long result = 0L;
        if (TextUtils.isEmpty(hexStr))
            return result;
        for (int i = 0; i < hexStr.length(); i += 2) {
            int leftMove = ((hexStr.length() - i) / 2 - 1) * 8;
            long rr = Integer.parseInt(hexStr.substring(i, i + 2), 16);
            long rrs = rr << leftMove;
            result += rrs;
        }
        return result;
    }

    /**
     * 将整型值转换为16进制字符串
     *
     * @param i
     * @param length 转换后字符串的长度
     * @return
     */
    public static String long2HexStr(long i, int length) {
        StringBuffer result = new StringBuffer();
        String hexStr = Long.toHexString(i);
        int len = hexStr.length();
        while (len < length) {
            result.append("0");
            len++;
        }
        return result.toString() + hexStr;
    }
//    public static byte[] makeSendData(byte[] SN, byte[] psw, byte[] msgData, byte[] type, byte[] command, byte[] params, byte[] datalength, byte[] data) {
//        /**
//         * 标志码 发送端信息 接收端代码 控制码 数据码 检验码 标志码 7E 设备SN 密码 信息代码 分类 命令 参数 数据长度 数据 检验值
//         * 7E 16Byte 4Byte 4byte 1Byte 1Byte 1Byte 4Byte 可变长度 1Byte
//         */
//        byte[] CRC = new byte[1];
//        CRC[0] = ConvertUtil.getCRC(merge(SN, psw, msgData, type, command, params, datalength, data));
//
//        return merge(Enumer.G_Sign, SN, psw, msgData, type, command, params, datalength, data, CRC, Enumer.G_Sign);
//    }

    public static byte[] getRandombyte(int size) {
        Random random = new Random();
        byte[] b = new byte[size];
        for (int i = 0; i < size; i++) {
            Integer is = random.nextInt(9);
            b[i] = Byte.parseByte(is.toString());
        }
        return b;
    }

    /**
     * 固件升级返回数据校验
     *
     * @param Buf
     * @return
     */
    public static boolean VerifyReturnData(byte[] Buf) {
        if (Buf == null)
            return false;
        byte crc1 = 0x00;
        byte crc2 = 0x00;
        int length = Buf.length;
        for (int i = 0; i < length - 2; i++) {
            crc1 += Buf[i];
            crc2 ^= Buf[i];
        }
        return crc1 == Buf[length - 2] && crc2 == Buf[length - 1];
    }

    //
    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("US-ASCII");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("US-ASCII");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    /**
     * 将一个byte 转换为 十六进制字符
     *
     * @param by
     * @return
     */
    public static String byte2HexString(byte by) {
        String string = Integer.toHexString(by & 0xFF);
        return (string.length() == 1) ? "0" + string : string;
    }

    /**
     * "D973F2E8-B19E-11E2-9E96-0800200C9A66"
     * <p>
     * 11 22 33 44 55 66 33 55 77 99 88 88 11 33 55 77
     *
     * @param serviceuuid
     * @return
     */
    public static String byteServiceUUID2string(byte[] serviceuuid) {
        if (serviceuuid == null || serviceuuid.length != 16)
            return null;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            sb.append(byte2HexString(serviceuuid[i]));
        }
        sb.append("-");
        for (int i = 4; i < 6; i++) {
            sb.append(byte2HexString(serviceuuid[i]));
        }
        sb.append("-");
        for (int i = 6; i < 8; i++) {
            sb.append(byte2HexString(serviceuuid[i]));
        }
        sb.append("-");
        for (int i = 8; i < 10; i++) {
            sb.append(byte2HexString(serviceuuid[i]));
        }
        sb.append("-");
        for (int i = 10; i < 16; i++) {
            sb.append(byte2HexString(serviceuuid[i]));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * * "D973F2E8-B19E-11E2-9E96-0800200C9A66"
     * <p>
     * 11 22 33 44 55 66 33 55 77 99 88 88 11 33 55 77
     *
     * @param serviceuuid
     * @return
     */
    public static byte[] stringServiceUUID2byte(String serviceuuid) {
        serviceuuid = serviceuuid.replace("-", "");
        byte[] bytes = hexStr2Bytes(serviceuuid);
        return bytes;
    }
}
