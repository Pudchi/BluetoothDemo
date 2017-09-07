package com.pinlun.bluetoothdemo;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private BluetoothSocket btSocket;
    private BluetoothDevice btDevice;
    private final String TAG = "Connect";
    public static final String PAIRED_DEVICE_ADDRESS = "PAIRED_DEVICE_ADDRESS";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static UUID uuid;
    TextView title;
    LottieAnimationView box_loading;
    Typeface circular_book;
    Button scan, stop;
    String stats = "Discovery";
    private final static int REQUEST_ENABLE_BT = 1;
    String paired_device_info;

    ArrayAdapter<String> device_adapter;
    ArrayList<String> deviceList;
    ListView bt_list;
    static int scan_status = 0;
    BluetoothAdapter bluetoothAdapter;
    AlertDialog.Builder connect_bt, no_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(stats, "Initialized");
        uuid = UUID.randomUUID();

        no_bt = new AlertDialog.Builder(MainActivity.this);
        connect_bt = new AlertDialog.Builder(MainActivity.this);



        no_bt.setPositiveButton("@string/no_bt_button", new DialogInterface.OnClickListener() {
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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
                scan_status = 1;
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
                }, 1500);



                //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            //registerReceiver(bt_receiver, filter);
        }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan_status = 2;
                stop.setVisibility(View.INVISIBLE);
                scan.setVisibility(View.VISIBLE);

                box_loading.cancelAnimation();
                box_loading.setVisibility(View.INVISIBLE);
                //device_adapter.clear();
                device_adapter.add("SCAN STOPPED!");
                device_adapter.notifyDataSetChanged();

                unregisterReceiver(bt_receiver);
                bluetoothAdapter.cancelDiscovery();
            }
        });

        bt_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothAdapter.cancelDiscovery();
                stop.setVisibility(View.INVISIBLE);
                scan.setVisibility(View.VISIBLE);
                box_loading.cancelAnimation();
                box_loading.setVisibility(View.INVISIBLE);

                String selected_device = ((TextView)view).getText().toString();
                if (selected_device != "SCAN STOPPED!") {

                    final String pair_device_name = selected_device.substring(0, selected_device.length() - 19);
                    final String selected_device_address = selected_device.substring(selected_device.length() - 17);
                    paired_device_info = pair_device_name + ": " + selected_device_address;
                    final BluetoothDevice pairing = bluetoothAdapter.getRemoteDevice(selected_device_address);


                    //Toast.makeText(getApplicationContext(), "Connecting: " + pair_device_name, Toast.LENGTH_SHORT).show();




                    System.out.println(pairing.getUuids());

                    connect_bt.setMessage("Device: " + pair_device_name + "\nHW Address: " + selected_device_address).setTitle("Connect to this device?");
                    connect_bt.setPositiveButton("連接", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    initialize_socket(pairing);
                                    pair_connect();

                                }
                            }).start();
                        }
                    });

                    connect_bt.setNegativeButton("返回掃描", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    connect_bt.show();
                }
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

        if (scan_status == 1)
        {
            unregisterReceiver(bt_receiver);
        }


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

    //pair bt----------------------------------------------------------------
    public void initialize_socket(BluetoothDevice device)
    {
        BluetoothSocket tmp = null;
        btDevice = device;

        try{
            tmp = btDevice.createInsecureRfcommSocketToServiceRecord(myUUID);

            Log.d(TAG, "Socket's create() method completed");

        } catch (IOException e) {
                Log.d(TAG, "Socket's create() method failed", e);
            }
            btSocket = tmp;
        }



    public void pair_connect(){

        //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();

        try{
            btSocket.connect();
            Log.d(TAG, "Device Linked");


            Intent transfer_paired_info = new Intent(MainActivity.this, ConnectedActivity.class);
            transfer_paired_info.putExtra(PAIRED_DEVICE_ADDRESS, paired_device_info);
            startActivity(transfer_paired_info);

        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                btSocket.close();
            } catch (IOException closeException) {
                Log.d(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        //manageMyConnectedSocket(mmSocket);

    }

    public void cancel() {
        try {
            btSocket.close();
            Log.d(TAG, "Close socket");
        } catch (IOException e) {
            Log.d(TAG, "Could not close the client socket", e);
        }
    }
}





