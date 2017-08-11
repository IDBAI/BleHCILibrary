package com.revenco.certificateverifylib.core;

import android.content.Context;
import android.text.TextUtils;

import com.revenco.certificateverifylib.bean.OpenDoorCertifycateBean;
import com.revenco.certificateverifylib.common.DeviceTypehelper;
import com.revenco.certificateverifylib.common.UtilsHelper;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Date;

import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.NoPermission_CounterIllegal;
import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.NoPermission_VerifyError;
import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.NoPermission_bleMacIllegal;
import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.NoPermission_deviceIdNotMatch;
import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.NoPermission_timeout;
import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.NoPermission_userIdNotMatch;
import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.VerifySuccess;
import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.certificateIsNullOrEmpty;
import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.convertBeanError;
import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.deviceIdIsNullOrEmpty;
import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.mobileBleMacIsNullOrEmpty;
import static com.revenco.certificateverifylib.core.SignVerify.openDoorStatus.publicKeyIsNull;

/**
 * Created by Administrator on 2017/2/15.
 * 验证开门凭证
 */
public class SignVerify {
    /**
     * @param context
     * @param publicKey
     * @param openDoorCertificate
     * @param mobileBleMac
     * @param deviceId
     * @param userId              可为空
     * @return
     */
    public static openDoorStatus verify(Context context, PublicKey publicKey, byte[] openDoorCertificate, String mobileBleMac, String deviceId, String userId) {
        if (openDoorCertificate == null || openDoorCertificate.length == 0) {
            return certificateIsNullOrEmpty;
        }
        if (publicKey == null) {
            return publicKeyIsNull;
        }
        if (mobileBleMac == null || mobileBleMac.isEmpty()) {
            return mobileBleMacIsNullOrEmpty;
        }
        if (deviceId == null || deviceId.isEmpty()) {
            return deviceIdIsNullOrEmpty;
        }
        OpenDoorCertifycateBean bean = new OpenDoorCertifycateBean();
        boolean isConvertSuccess = bean.convertBean(openDoorCertificate);
        if (!isConvertSuccess) {
            return convertBeanError;
        }
        OpenDoorCertifycateBean.CertifycateBean certifycateBean = bean.getCertifycateBean();
        //校验签名--------------------------start
        boolean verifySign = false;
        try {
            verifySign = ECDSAHelper.verifySign(publicKey, certifycateBean.getCertifycateOriginal(), bean.getSign());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        if (!verifySign) {
            return NoPermission_VerifyError;
        }
        //校验签名--------------------------end
        //校验证书原始数据信息--------------------------start
        //1 mac校验
        String blemac = UtilsHelper.byte2HexStrWithStr(certifycateBean.getUserBleMac(), ":");
        if (!blemac.equalsIgnoreCase(mobileBleMac)) {
            return NoPermission_bleMacIllegal;
        }
        String deviceid = UtilsHelper.byte2HexStrWithStr(certifycateBean.getDeviceId(), ":");
        if (!deviceid.equalsIgnoreCase(deviceId)) {
            return NoPermission_deviceIdNotMatch;
        }
        //2 userid
        if (TextUtils.isEmpty(userId)) {//新用户，userId为空，此时使用证书上的userid给参数赋值
            userId = UtilsHelper.byte2HexStrWithoutSpace(certifycateBean.getUserId());
        }
        String userid = UtilsHelper.byte2HexStrWithoutSpace(certifycateBean.getUserId());
        if (!userid.equalsIgnoreCase(userId)) {
            return NoPermission_userIdNotMatch;
        }
        //3 计数器
        byte[] counter = certifycateBean.getCounter();//计数器
        long couterLong = UtilsHelper.bytesToLong(counter);
        long currentCounter = UtilsHelper.getCounterValues(context, deviceid, userid);
        System.out.println("couterLong = " + couterLong + ",currentCounter = " + currentCounter);
        if (couterLong < currentCounter) {//非法计数器
            return NoPermission_CounterIllegal;
        }
        //MVO1600 RTC没有电源，断电时间会不准，所以不验证时间
        if (!DeviceTypehelper.isMVO1600Device()) {//非MVO1600,需要校验
            //4 issuTime
            byte[] issueTime = certifycateBean.getIssueTime();
            int startTime = UtilsHelper.byteArrayToInt(issueTime);
            //5 timeout
            byte[] timeout = certifycateBean.getTimeout();
            int endTime = UtilsHelper.byteArrayToInt(timeout);
            Date currentDate = new Date();
            int currentTime = (int) currentDate.getTime();
            if (currentTime < startTime || currentTime > endTime) {
                return NoPermission_timeout;
            }
        }
        //6 合法
        UtilsHelper.updateCounterValues(context, deviceid, userid, couterLong);//更新计数器
        return VerifySuccess;
        //校验证书原始数据信息--------------------------end
    }

    public enum openDoorStatus {
        certificateIsNullOrEmpty((byte) 0x01),
        mobileBleMacIsNullOrEmpty((byte) 0x02),
        convertBeanError((byte) 0x03),
        publicKeyIsNull((byte) 0x04),
        deviceIdIsNullOrEmpty((byte) 0x05),
        //
        NoPermission_VerifyError((byte) 0x06),
        NoPermission_bleMacIllegal((byte) 0x07),
        NoPermission_deviceIdNotMatch((byte) 0x08),
        NoPermission_userIdNotMatch((byte) 0x09),
        NoPermission_CounterIllegal((byte) 0x0A),
        NoPermission_timeout((byte) 0x0B),
        //
        VerifySuccess((byte) 0x0C);
        private byte reson;

        openDoorStatus(byte reson) {
            this.reson = reson;
        }

        public byte getReson() {
            return reson;
        }
    }
}
