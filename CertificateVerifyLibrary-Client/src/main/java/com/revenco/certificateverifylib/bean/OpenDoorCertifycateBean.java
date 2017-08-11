package com.revenco.certificateverifylib.bean;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/16.
 * 开门凭证实体
 */
public class OpenDoorCertifycateBean implements Serializable {
    private static final long serialVersionUID = -8453654626686188128L;
    private byte[] openDoorData;
    private int certifycateLength;
    private CertifycateBean certifycateBean;
    private int signLength;
    private byte[] sign;

    public CertifycateBean getCertifycateBean() {
        return certifycateBean;
    }

    public byte[] getOpenDoorData() {
        return openDoorData;
    }

    public int getCertifycateLength() {
        return certifycateLength;
    }

    public int getSignLength() {
        return signLength;
    }

    public byte[] getSign() {
        return sign;
    }

    public boolean convertBean(byte[] openDoorData) {
        this.openDoorData = openDoorData;
        certifycateLength = openDoorData[0];
        byte[] certifycatebytes = new byte[certifycateLength];
        System.arraycopy(openDoorData, 1, certifycatebytes, 0, certifycateLength);
        certifycateBean = new CertifycateBean();
        boolean isSuccess = certifycateBean.convertBean(certifycatebytes);
        if (!isSuccess)
            return false;
        signLength = openDoorData[certifycateLength + 1];
        sign = new byte[signLength];
        System.arraycopy(openDoorData, certifycateLength + 1 + 1, sign, 0, signLength);
        return true;
    }

    public class CertifycateBean {
        private static final int LEGAL_LENGTH = 57;//合法长度
        private static final String TAG = "CertifycateBean";
        private int dataLength;
        private byte[] certifycateOriginal;
        //原始证书数据 57 字节长度

        private byte[] type;//   类型，保留字段，1字节
        private byte[] userId;//     用户id 16字节
        private byte[] userBleMac;//手机MAC地址 6字节
        private byte[] deviceId;//设备id 16字节
        private byte[] channelMask;//通道掩码 1字节
        private byte[] issueTime;//签发时间 4字节（默认为当前时间）
        private byte[] timeout;// 失效时间 4字节
        private byte[] counter;// 计数器 8字节
        private byte[] useTimes;// 使用次数 1字节

        public int getDataLength() {
            return dataLength;
        }

        public byte[] getCertifycateOriginal() {
            return certifycateOriginal;
        }

        public byte[] getUserId() {
            return userId;
        }

        public byte[] getUserBleMac() {
            return userBleMac;
        }

        public byte[] getDeviceId() {
            return deviceId;
        }

        public byte[] getChannelMask() {
            return channelMask;
        }

        public byte[] getIssueTime() {
            return issueTime;
        }

        public byte[] getTimeout() {
            return timeout;
        }

        public byte[] getCounter() {
            return counter;
        }

        public byte[] getUseTimes() {
            return useTimes;
        }

        public boolean convertBean(byte[] data) {
            certifycateOriginal = data;
            dataLength = data.length;
            if (dataLength != LEGAL_LENGTH) {
                Log.e(TAG, "CertifycateBean's certifycateOriginal illegal data length.");
                return false;
            }
//        服务端合成数据顺序
//        byte[] certificate = UtilsHelper.merge(type,userId, userBleMac,
//                deviceId, channelMask, issueTime,
//                timeout, counter, useTimes);
//            type
            type = new byte[1];
            System.arraycopy(certifycateOriginal, 0, type, 0, 1);
//            userId 用户id 16字节
            userId = new byte[16];
            System.arraycopy(certifycateOriginal, 1, userId, 0, 16);
//            userBleMac;//手机MAC地址 6字节
            userBleMac = new byte[6];
            System.arraycopy(certifycateOriginal, 17, userBleMac, 0, 6);
//            deviceId;//设备id 16字节
            deviceId = new byte[16];
            System.arraycopy(certifycateOriginal, 23, deviceId, 0, 16);
//            channelMask;//通道掩码 1字节
            channelMask = new byte[1];
            System.arraycopy(certifycateOriginal, 39, channelMask, 0, 1);
//            issueTime;//签发时间 4字节（默认为当前时间）
            issueTime = new byte[4];
            System.arraycopy(certifycateOriginal, 40, issueTime, 0, 4);
//            timeout;// 失效时间 4字节
            timeout = new byte[4];
            System.arraycopy(certifycateOriginal, 44, timeout, 0, 4);
//            counter;// 计数器 8字节
            counter = new byte[8];
            System.arraycopy(certifycateOriginal, 48, counter, 0, 8);
//            useTimes;// 使用次数 1字节
            useTimes = new byte[1];
            System.arraycopy(certifycateOriginal, 56, useTimes, 0, 1);
            return true;
        }
    }
}
