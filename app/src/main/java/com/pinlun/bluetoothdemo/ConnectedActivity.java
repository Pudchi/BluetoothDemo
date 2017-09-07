package com.pinlun.bluetoothdemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ConnectedActivity extends AppCompatActivity {

    TextView title, connect_title, connect_device;
    Typeface circular_book;
    Button disconnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        circular_book = TypefaceProvider.getTypeFace(getApplicationContext(), "Circular_book.ttf");

        title = (TextView) findViewById(R.id.titletext);
        title.setTypeface(circular_book);
        connect_title = (TextView) findViewById(R.id.connected_device_title);
        title.setTypeface(circular_book);
        connect_device = (TextView) findViewById(R.id.connected_device);
        connect_device.setTypeface(circular_book);

        disconnect = (Button) findViewById(R.id.disconnect_button);
        disconnect.setTypeface(circular_book);

        Intent get_paired_info = getIntent();
        String get_info = get_paired_info.getStringExtra(MainActivity.PAIRED_DEVICE_ADDRESS);
        connect_device.setText(get_info.substring(0,get_info.length() - 19) + "\n" + get_info.substring(get_info.length() - 17));
    }
}
