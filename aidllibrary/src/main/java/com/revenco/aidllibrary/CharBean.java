package com.revenco.aidllibrary;

import com.revenco.aidllibrary.CommonUtils.ConvertUtil;

/**
 * <p>PROJECT : BleHCILibrary</p>
 * <p>COMPANY : wanzhong</p>
 * <p>AUTHOR : Administrator on 2017/2/28 15:52.</p>
 * <p>CLASS DESCRIBE :自定义的特征值实体类</p>
 * <p>CLASS_VERSION : 1.0.0</p>
 */
public class CharBean {
    /**
     * 是否已经被设置，设置成功之后赋值为TRUE
     */
    public boolean hasSetting = false;
    public byte[] char_uuid;
    /**
     * 特征值属性
     */
    public byte char_prop;
    /**
     * char 所处的service
     */
    public byte[] service_handler;
    /**
     * 设置成功之后返回的handler
     */
    public byte[] char_handle;
    /**
     * 接收特征值数据对应的 attr_Handle
     * <p>
     * <p>
     * 属性为 write_Properties，attr_Handle = char_handle + 1;
     * </p>
     * <p>
     * 属性为 nofity_Properties，attr_Handle = char_handle + 2;
     * </p>
     * </p>
     */
    public byte[] attr_Handle;

    public CharBean(byte[] service_handler, byte[] char_uuid, byte char_prop) {
        this.service_handler = service_handler;
        hasSetting = false;
        this.char_uuid = char_uuid;
        this.char_prop = char_prop;
        char_handle = null;
    }

    @Override
    public String toString() {
        return "CharBean{" +
                "hasSetting=" + hasSetting +
                ", char_uuid=" + ConvertUtil.byte2HexStrWithoutSpace(char_uuid) +
                ", char_prop=" + char_prop +
                ", service_handler=" + ConvertUtil.byte2HexStrWithoutSpace(service_handler) +
                ", char_handle=" + ConvertUtil.byte2HexStrWithoutSpace(char_handle) +
                ", attr_Handle=" + ConvertUtil.byte2HexStrWithoutSpace(attr_Handle) +
                '}';
    }
}
