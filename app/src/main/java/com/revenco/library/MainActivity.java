package com.revenco.library;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.revenco.library.Bean.AppConnectBean;
import com.revenco.library.command.FlowControl;
import com.revenco.library.core.DealHCIEvent;
import com.revenco.library.core.PeripharalManager;
import com.revenco.library.utils.ConvertUtil;
import com.revenco.library.utils.XLog;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private EditText appconnect;
    private EditText attrValues;
    private BroadcastReceiver receive;
    private AppConnectBean connectBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.HWReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PeripharalManager.getInstance().start();
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
            }
        });
        appconnect = (EditText) findViewById(R.id.appconnect);
        attrValues = (EditText) findViewById(R.id.attrValues);
        receive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case DealHCIEvent.ACTION_APP_CONNECT_STATUS:
                        connectBean = (AppConnectBean) intent.getSerializableExtra(DealHCIEvent.EXTRA_APPBEAN);
                        appconnect.append(connectBean.toTestString() + "\n");
                        appconnect.scrollTo(0, 1000);
                        XLog.d("GOOD", connectBean.toString());
                        break;
                    case DealHCIEvent.ACTION_REVEIVE_ATTRIBUTE_VALUES:
                        String appMac = intent.getStringExtra(DealHCIEvent.EXTRA_APPMAC);
                        byte[] uuid = intent.getByteArrayExtra(DealHCIEvent.EXTRA_CHAR_UUID);
                        byte[] values = intent.getByteArrayExtra(DealHCIEvent.EXTRA_CHAR_VALUES);
                        String uuid_str = ConvertUtil.byte2HexStrWithSpace(uuid);
                        String values_str = ConvertUtil.byte2HexStrWithSpace(values);
                        String textstr = "appMac : " + appMac + "\n" + "uuid : " + uuid_str + "\n" + "vaules : " + values_str;
                        attrValues.append(textstr + "\n");
                        attrValues.scrollTo(0, 1000);
                        XLog.d("GOOD", textstr);
                        break;
                    case FlowControl.action_disPlAY_beacon_status:
                        //测试显示UI提示
                        TextView textView = (TextView) findViewById(R.id.beanconStatus);
                        String status = intent.getStringExtra("ACTION");
                        textView.setText(status);
                        break;
                }
            }
        };
        registerReceiver(receive, getIntentFilter());
    }

    public IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DealHCIEvent.ACTION_APP_CONNECT_STATUS);
        intentFilter.addAction(DealHCIEvent.ACTION_REVEIVE_ATTRIBUTE_VALUES);
        //测试UI显示
        intentFilter.addAction(FlowControl.action_disPlAY_beacon_status);
        return intentFilter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receive);
    }
}
