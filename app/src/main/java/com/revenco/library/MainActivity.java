package com.revenco.library;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.revenco.library.Bean.AppConnectBean;
import com.revenco.library.core.PeripharalManager;
import com.revenco.library.core.PeripheralService;
import com.revenco.library.others.AppConnectStatus;
import com.revenco.library.others.FlowStatus;
import com.revenco.library.utils.ConvertUtil;
import com.revenco.library.utils.Tools;
import com.revenco.library.utils.XLog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private EditText appconnect;
    private EditText attrValues;
    private EditText resettime;
    private BroadcastReceiver receive;
    private AppConnectBean connectBean;
    private long starttime;
    private int testCount;
    private long totalTime;
    private Button resetBtn;
    private FlowStatus currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tools.acquireWakeLock(getApplicationContext(), PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
        resetBtn = (Button) findViewById(R.id.HWReset);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PeripharalManager.getInstance().testResetHW();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.Test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectBean != null)
                    PeripharalManager.getInstance().testnotify();
            }
        });
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appconnect.setText("");
                attrValues.setText("");
                resettime.setText("");
            }
        });
        appconnect = (EditText) findViewById(R.id.appconnect);
        attrValues = (EditText) findViewById(R.id.attrValues);
        resettime = (EditText) findViewById(R.id.resettime);
        receive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case PeripheralService.ACTION_APP_CONNECT_STATUS:
                        connectBean = (AppConnectBean) intent.getSerializableExtra(PeripheralService.EXTRA_APPBEAN);
                        appconnect.append(connectBean.toTestString() + "\n");
                        appconnect.scrollTo(0, 1000);
//                        XLog.d(TAG, connectBean.toString());
                        if (connectBean.status == AppConnectStatus.status_connected) {
//                            app开始连接开始计时
                        }
                        break;
                    case PeripheralService.ACTION_REVEIVE_ATTRIBUTE_VALUES:
                        String appMac = intent.getStringExtra(PeripheralService.EXTRA_APPMAC);
                        byte[] uuid = intent.getByteArrayExtra(PeripheralService.EXTRA_CHAR_UUID);
                        byte[] values = intent.getByteArrayExtra(PeripheralService.EXTRA_CHAR_VALUES);
                        String uuid_str = ConvertUtil.byte2HexStrWithSpace(uuid);
                        String values_str = ConvertUtil.byte2HexStrWithSpace(values);
                        String textstr = "appMac : " + appMac + "\n" + "uuid : " + uuid_str + "\n" + "vaules : " + values_str;
                        attrValues.append(textstr + "\n");
                        attrValues.scrollTo(0, 1000);
//                        XLog.d(TAG, textstr);
                        break;
                    case PeripheralService.ACTON_FLOWCONTROL_STATUS:
                        //测试显示UI提示
                        TextView textView = (TextView) findViewById(R.id.beanconStatus);
                        currentStatus = (FlowStatus) intent.getSerializableExtra("ACTION");
                        textView.setText("当前蓝牙状态：" + currentStatus.toString());
                        if (currentStatus.toString().equalsIgnoreCase(FlowStatus.STATUS_HWRESET_SUCCESS.toString()))
                            starttime = SystemClock.uptimeMillis();
                        if (currentStatus.toString().equalsIgnoreCase(FlowStatus.STATUS_ACI_GAP_SET_DISCOVERABLE_SUCCESS.toString())) {
                            try {
                                textView.setText("当前蓝牙状态：广播成功");
                                //开启广播成功，数据分段显示
                                Editable text = appconnect.getText();
                                String[] split = text.toString().split("\n");
                                String appString = split[split.length - 1 == 0 ? 0 : split.length - 1] + "\n" + split[split.length - 2 == -1 ? 0 : split.length - 2] + "\n\n";
                                appconnect.setText(appString);
                                //
                                attrValues.append("\n\n");
                                String[] split1 = attrValues.getText().toString().split("\n\n");
                                String valuesatt = split1[split1.length - 1 == 0 ? 0 : split1.length - 1] + "\n" + split1[split1.length - 2 == -1 ? 0 : split1.length - 2] + "\n\n";
                                attrValues.setText(valuesatt);
                                //
                            } catch (Exception e) {
                            }
                            if (starttime != -1) {
                                long jiange = SystemClock.uptimeMillis() - starttime;
                                resettime.append("reset：" + jiange * 1.0f / 1000 + " 秒!\n");
                                starttime = -1;
                                testCount++;
                                totalTime += jiange;
                                if (testCount % 5 == 0) {
                                    XLog.d(TAG, "测试：" + testCount + "次，平均：" + (totalTime * 1.0f / testCount) + " ms");
                                    resettime.setText("reset：" + jiange * 1.0f / 1000 + " 秒!\n");
                                }
                            }
                            Date date = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");
                            String timeStr = "广播更新时间：" + format.format(date);
                            attrValues.append(timeStr);
                            attrValues.append("\n\n");
                        }
                        break;
                }
            }
        };
        registerReceiver(receive, getIntentFilter());
    }

    public IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PeripheralService.ACTON_FLOWCONTROL_STATUS);
        intentFilter.addAction(PeripheralService.ACTION_REVEIVE_ATTRIBUTE_VALUES);
        intentFilter.addAction(PeripheralService.ACTION_APP_CONNECT_STATUS);
        return intentFilter;
    }

    @Override
    protected void onDestroy() {
//        XLog.d(TAG, "onDestroy() called");
        super.onDestroy();
        unregisterReceiver(receive);
        Tools.releaseWakeLock();
        PeripharalManager.getInstance().destory();
        System.exit(-1);
    }
}
