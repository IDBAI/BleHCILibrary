package com.revenco.library.command;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/2/28 14:56.</p>
 * <p>CLASS DESCRIBE :</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class CharacteristicProperty {

    // 0x01: Broadcast,
// 0x02: Read,
// 0x04: Write w/o Resp.,
// 0x08: Write,
// 0x10: Notify,
// 0x20: Indicate,
// 0x40: Auth Signed Writes,
// 0x80: Extended Prop.
//        0x0E


    /**
     * Characteristic proprty: Characteristic is broadcastable.
     */
    public static final byte PROPERTY_BROADCAST = 0x01;

    /**
     * Characteristic property: Characteristic is readable.
     */
    public static final byte PROPERTY_READ = 0x02;

    /**
     * Characteristic property: Characteristic can be written without response.
     */
    public static final byte PROPERTY_WRITE_NO_RESPONSE = 0x04;

    /**
     * Characteristic property: Characteristic can be written.
     */
    public static final byte PROPERTY_WRITE = 0x08;

    /**
     * Characteristic property: Characteristic supports notification
     */
    public static final byte PROPERTY_NOTIFY = 0x10;

    /**
     * Characteristic property: Characteristic supports indication
     */
    public static final byte PROPERTY_INDICATE = 0x20;

    /**
     * Characteristic property: Characteristic supports write with signature
     */
    public static final byte PROPERTY_SIGNED_WRITE = 0x40;

    /**
     * Characteristic property: Characteristic has extended properties
     */
    public static final byte PROPERTY_EXTENDED_PROPS = (byte) 0x80;



}
