package com.pinlun.bluetoothdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView title;
    LottieAnimationView box_loading;
    Typeface circular_book;
    Button scan, stop;
    String stats = "Discovery";
    private final static int REQUEST_ENABLE_BT = 1;

    ArrayAdapter<String> device_adapter;
    ArrayList<String> deviceList;
    ListView bt_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(stats, "Initialized");

        AlertDialog.Builder no_bt = new AlertDialog.Builder(getApplicationContext());


        no_bt.setPositiveButton("此裝置不支援藍牙, App 即將關閉!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        no_bt.setMessage("此裝置不支援藍牙, App 即將關閉!").setTitle("Alert!");


        deviceList = new ArrayList<String>();

        circular_book = TypefaceProvider.getTypeFace(getApplicationContext(), "Circular_book.ttf");

        title = (TextView) findViewById(R.id.titletext);
        title.setTypeface(circular_book);
        scan = (Button) findViewById(R.id.scan_button);
        scan.setTypeface(circular_book);
        stop = (Button) findViewById(R.id.stop_scan_button);
        stop.setTypeface(circular_book);
        stop.setVisibility(View.INVISIBLE);
        box_loading = (LottieAnimationView) findViewById(R.id.loading);
        bt_list = (ListView) findViewById(R.id.devicelist);
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
        {

            AlertDialog dialog = no_bt.create();
            dialog.show();
        }

        if (!bluetoothAdapter.isEnabled())
        {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_ENABLE_BT);
        }

        /*Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {

                deviceList.add(device.getName()+": "+device.getAddress());
            }
        }*/

            deviceList.add("No Devices, Press Scan!");


        device_adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.my_list_item, deviceList);
        bt_list.setAdapter(device_adapter);
        //bt_list.setVisibility(View.INVISIBLE);



        //-----------------------------------------------------------------------------------



        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(bt_receiver, filter);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan.setVisibility(View.INVISIBLE);
                stop.setVisibility(View.VISIBLE);
                box_loading.setVisibility(View.VISIBLE);
                box_loading.playAnimation();

                checkBTPermissions();
                device_adapter.clear();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                        registerReceiver(bt_receiver, filter);

                        bluetoothAdapter.startDiscovery();
                    }
                }, 5000);



                //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            //registerReceiver(bt_receiver, filter);
        }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop.setVisibility(View.INVISIBLE);
                scan.setVisibility(View.VISIBLE);

                unregisterReceiver(bt_receiver);
                bluetoothAdapter.cancelDiscovery();
            }
        });
    }

    private final BroadcastReceiver bt_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String device_name = device.getName();
                String device_hw_address = device.getAddress();

                Log.d(stats, "Finish");



                device_adapter.add(device_name + ": "+device_hw_address);
                device_adapter.notifyDataSetChanged();
                box_loading.cancelAnimation();
                box_loading.setVisibility(View.INVISIBLE);
                bt_list.setVisibility(View.VISIBLE);


            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(bt_receiver);
    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
    }
        }else{
            Log.d("TAG", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}
