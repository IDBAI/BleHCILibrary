package com.revenco.library.others;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/3/6 17:26.</p>
 * <p>CLASS DESCRIBE : 属性值的排序的offset，会根据设置的特征值数量，描述符数量等属性进行偏移</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class AttrOrder {
    /**
     * 接收特征值数据对应的 attr_Handle
     * <p>
     * <p>
     * 属性为 write_Properties，attr_Handle = char_handle + 1;
     * </p>
     * <p>
     * 属性为 nofity_Properties，attr_Handle = char_handle + 3;
     * </p>
     * </p>
     */

    public static final int WRITE_UUID_ATTR_ORDER_OFFSET = 1;
    public static final int NOTIFY_UUID_ATTR_ORDER_OFFSET = 3;
}
