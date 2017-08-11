package com.revenco.certificateverifylib;

import com.revenco.certificateverifylib.common.UtilsHelper;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);
        byte[] bytes = new byte[8];
        bytes[0] = 0x11;
        bytes[1] = 0x22;
        bytes[2] = 0x33;
        bytes[3] = 0x44;
        bytes[4] = 0x55;
        bytes[5] = 0x66;
        bytes[6] = 0x77;
        bytes[7] = (byte) 0x88;
        String hexStr = UtilsHelper.byte2HexStr(bytes);
        System.out.println("" + hexStr);
        long toLong = UtilsHelper.bytesToLong(bytes);
        System.out.println("" + toLong);
        byte[] byteArray = UtilsHelper.longToBytes(toLong);
        String hexStr1 = UtilsHelper.byte2HexStr(byteArray);
        System.out.println("" + hexStr1);
    }

    @Test
    public void testString() {
        String string = "";
        if (string == null || string.isEmpty())
            System.out.println("nullOrEmpty true");
    }
}