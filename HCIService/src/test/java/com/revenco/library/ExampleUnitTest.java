package com.revenco.library;

import com.revenco.aidllibrary.CommonUtils.ConvertUtil;
import com.revenco.aidllibrary.CommonUtils.byteUtils;
import com.revenco.library.command.CharacteristicProperty;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        //[0x42,0x6C,0x75,0x65,0x4E,0x52,0x47]
        String str = "BlueNRG";
        byte[] bytes = str.getBytes();
        int length = bytes.length;
        System.out.println(ConvertUtil.byte2HexStrWithSpace(bytes));
        System.out.println(length);
    }

    @Test
    public void test2() throws Exception {
        byte prop = CharacteristicProperty.PROPERTY_READ | CharacteristicProperty.PROPERTY_WRITE | CharacteristicProperty.PROPERTY_WRITE_NO_RESPONSE;
        System.out.println(prop);
    }

    /**
     * //      [0x04,0x3E,0x0A,0x03,0x00,0x01,0x08,0x27,0x00,0x00,0x00,0xD0,0x07]
     * //                  length  Num      opcode
     * //      [0x04,0x0E, 0x04,    0x01,   0x83,0xFC,     0x00]
     *
     * @throws Exception
     */
    @Test
    public void test4() throws Exception {
//        byte[] data = new byte[]{04, 0x0E, 04, 01, 0x0C, (byte) 0xFC, 00, 04, 0x0E, 04, 01, 03, 0x0C, 00, 04, (byte) 0xFF, 03, 01, 00, 01};
        byte[] data = new byte[]{04, 0x0E, 04, 01, 03, 0x0C, 00};
        byte[] currentOpCode = new byte[]{0x03, 0x0C};
        System.out.println("FindPackage() called with");
        System.out.println(" currentOpCode = [" + ConvertUtil.byte2HexStrWithSpace(currentOpCode) + "]");
        System.out.println(" data = [" + ConvertUtil.byte2HexStrWithSpace(data) + "]");
        byte[] destbyte = null;
        int start = 0;
        int end = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == currentOpCode[0] && ((i + 1) < data.length) && data[i + 1] == currentOpCode[1]) {
                //判断向前索引是否会越界
                if ((i - 4) >= 0) {
                    start = i;
                    end = i + 1;
                    break;
                }
            }
        }
        if ((end - start) == 1) {
            //find
//            * //      [0x04,0x3E,0x0A,0x03,0x00,0x01,0x08,0x27,0x00,0x00,0x00,0xD0,0x07]
//            * //                  length  Num      opcode
//            * //      [0x04,0x0E, 0x04,    0x01,   0x83,0xFC,     0x00]
            int paramlength = data[start - 2];
            int totalLength = paramlength + 3;
            destbyte = new byte[totalLength];
            System.arraycopy(data, start - 4, destbyte, 0, totalLength);
            System.out.println("destbyte = [" + ConvertUtil.byte2HexStrWithSpace(destbyte) + "]");
        }
    }

    @Test
    public void test3() throws Exception {
        byte[] Char_Handle = {0x00, 0x0D};
        String sss = ConvertUtil.byte2HexStrWithSpace(Char_Handle);
        System.out.println("sss = " + sss);
        int att1 = byteUtils.byteArrayToInt(Char_Handle) + 1;
        System.out.println("att1 = " + att1);
        String att1str = Integer.toHexString(att1);
        System.out.println("att1str = " + att1str);
        byte[] Attr_Handle = att1str.getBytes();
        String hexStrWithSpace = ConvertUtil.byte2HexStrWithSpace(Attr_Handle);
        System.out.println("hexStrWithSpace = " + hexStrWithSpace);
    }

    @Test
    public void test5() throws Exception {
        String name = "\tWZBeacon";
        byte[] nameBytes = name.getBytes();
        String hexStrWithSpace = ConvertUtil.byte2HexStrWithSpace(nameBytes);
        System.out.println(hexStrWithSpace);//09 57 5A 42 65 61 63 6F 6E
    }

    @Test
    public void testCharVal() {
    }
}