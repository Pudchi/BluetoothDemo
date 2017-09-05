package com.pinlun.bluetoothdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class DeviceListActivity extends AppCompatActivity {

    private static final String TAG = "DeviceListActivity";

    /**
     * Return Intent extra
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    /**
     * Member fields
     */
    private BluetoothAdapter mBtAdapter;

    /**
     * Newly discovered devices
     */
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        setResult(Activity.RESULT_CANCELED);


    }
}
