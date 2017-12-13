package com.uroad.malaysiaetc.activity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uroad.malaysiaetc.R;
import com.uroad.malaysiaetc.adapter.PopCommonAdapter;
import com.uroad.malaysiaetc.common.BaseActivity;
import com.uroad.malaysiaetc.util.BlueToothUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwen on 2017/12/13.
 */

public class QuancunConnActivity extends BaseActivity {


    /** 蓝牙工具类 */
    private BlueToothUtil mBlueToothUtil;
    private RelativeLayout rlBleScanList;
    private ListView lvBleScan;
    private TextView tvlistTitle;
    private TextView tvScan;
    private PopCommonAdapter mAdapter;
    /** 搜索到的设备列表名称 */
    private List<String> msgs = new ArrayList<String>();
    /** 搜索到的设备列表 */
    private List<BluetoothDevice> devicelist = new ArrayList<BluetoothDevice>();
    public String mDeviceMacAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etc_connect);
        initView();
        initListener();
        loadData();
    }

    private void initView() {
        rlBleScanList = (RelativeLayout) findViewById(R.id.rl_ble_scan_list);
        lvBleScan = (ListView) findViewById(R.id.lv_ble_scan);
        tvlistTitle = (TextView) findViewById(R.id.tv_list_title);
        tvScan = (TextView) findViewById(R.id.tv_scan);

        mAdapter = new PopCommonAdapter(this, msgs);
        lvBleScan.setAdapter(mAdapter);
    }

    private void initListener() {
        lvBleScan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDeviceMacAddress = devicelist.get(position).getAddress();
                mBlueToothUtil.mDevice = devicelist.get(position);

                QuancunCardInfoActivity.openActivity(QuancunConnActivity.this,
                        devicelist.get(position).getAddress(),
                        devicelist.get(position).getName(),
                        mBlueToothUtil.getMyDeviceName(), "", "", "");
            }
        });


    }

    private void loadData() {
        mBlueToothUtil = new BlueToothUtil(this);
    }
}
