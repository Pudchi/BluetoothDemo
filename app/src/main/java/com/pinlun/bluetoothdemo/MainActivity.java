package com.pinlun.bluetoothdemo;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    TextView title;
    LottieAnimationView box_loading;
    Typeface circular_book;
    Button scan;
    String stats = "Discovery";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(stats, "Initialized");

        circular_book = TypefaceProvider.getTypeFace(getApplicationContext(), "Circular_book.ttf");

        title = (TextView) findViewById(R.id.titletext);
        title.setTypeface(circular_book);
        scan = (Button) findViewById(R.id.scan_button);
        scan.setTypeface(circular_book);

        box_loading = (LottieAnimationView) findViewById(R.id.loading);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan.setVisibility(View.INVISIBLE);
                box_loading.setVisibility(View.VISIBLE);
                box_loading.playAnimation();
            }
        });
    }
}
