package com.uroad.malaysiaetc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.uroad.lib.activity.BaseUroadActivity;
import com.uroad.malaysiaetc.R;

/**
 * Created by liwen on 2017/12/13.
 */

public class QuancunCardInfoActivity extends BaseUroadActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentLayout(R.layout.activity_card_charge);
        setTitle("");
        initView();
        initListener();
        loadData();
    }

    private void initView() {
    }

    private void initListener() {

    }

    private void loadData() {

    }

    /**
     * @param context
     * @param deviceAddress
     *            要连接的蓝牙设备的mac地址
     * @param deviceName
     *            要连接的蓝牙设备名称
     * @param myDeviceName
     *            我的蓝牙名称
     * @param cardNo
     *            要圈存的卡号（若读出来的卡号和他不一样，弹出提示，退出）
     * @param chargeMoney
     *            圈存金额
     * */
    public static void openActivity(Context context, String deviceAddress,
                                    String deviceName, String myDeviceName, String cardNo,
                                    String chargeMoney, String type) {
        Intent intent = new Intent();
        intent.setClass(context, QuancunCardInfoActivity.class);
        // 判空，预防空指针异常
        if (TextUtils.isEmpty(deviceAddress)) {
            Toast.makeText(context, "deviceAddress = null",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(deviceName)) {
            Toast.makeText(context, "deviceName = null",Toast.LENGTH_SHORT).show();
            return;
        }
        // if (TextUtils.isEmpty(cardNo)) {
        // ToastUtil.show(context, "cardNo = null");
        // return;
        // }
        // if (TextUtils.isEmpty(chargeMoney)) {
        // ToastUtil.show(context, "chargeMoney = null");
        // return;
        // }
        if (TextUtils.isEmpty(myDeviceName)) {
            Toast.makeText(context, "myDeviceName = null",Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("deviceAddress", deviceAddress);
        intent.putExtra("deviceName", deviceName);
        intent.putExtra("cardNo", cardNo);
        intent.putExtra("myDeviceName", myDeviceName);
        intent.putExtra("chargeMoney", chargeMoney);
        intent.putExtra("type", type);
        context.startActivity(intent);

    }

}
