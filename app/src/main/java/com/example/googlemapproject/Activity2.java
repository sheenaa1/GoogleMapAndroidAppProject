package com.example.googlemapproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class Activity2 extends MainActivity{

    Button on_1;
    Button on_2;
    Button on_3;
    Button off_1;
    Button off_2;
    Button off_3;

    String location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout2);

        Log.d("TAG_INFO","in Activity2");

        on_1 = findViewById(R.id.on_1);
        on_2 = findViewById(R.id.on_2);
        on_3 = findViewById(R.id.on_3);
        off_1= findViewById(R.id.off_1);
        off_2 = findViewById(R.id.off_2);
        off_3 = findViewById(R.id.off_3);

        Intent intent2 = getIntent();

        String street_0 = intent2.getStringExtra("street0");
        on_1.setText(street_0);

        String street_1 = intent2.getStringExtra("street1");
        on_2.setText(street_1);

        String street_2 = intent2.getStringExtra("street2");
        on_3.setText(street_2);

        String off0 = intent2.getStringExtra("off0");
        off_1.setText(off0);

        String off1 = intent2.getStringExtra("off1");
        off_2.setText(off1);

        String off2 = intent2.getStringExtra("off2");
        off_3.setText(off2);

        setResult(RESULT_OK, getIntent()); //what is this??

        //get lat and long coordinates from each street to pass into intent extras
        //button onClick listeners --> create intents in each which will move to map page

        final Context context = this;
        Intent option = new Intent(context, MapsActivity.class);

        on_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = intent2.getStringExtra("location");
                double[] street0_coordinates = intent2.getDoubleArrayExtra("street0_coordinates");
                option.putExtra("coordinates",street0_coordinates);
                option.putExtra("location",location);

                if(!street_0.equals("none"))
                    startActivity(option);
            }
        });

        on_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = intent2.getStringExtra("location");
                double[] street1_coordinates = intent2.getDoubleArrayExtra("street1_coordinates");
                option.putExtra("coordinates",street1_coordinates);
                option.putExtra("location",location);

                if(!street_1.equals("none"))
                    startActivity(option);
            }
        });

        on_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = intent2.getStringExtra("location");
                double[] street2_coordinates = intent2.getDoubleArrayExtra("street2_coordinates");
                option.putExtra("coordinates",street2_coordinates);
                option.putExtra("location",location);

                if(!street_2.equals("none"))
                    startActivity(option);
            }
        });

        off_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = intent2.getStringExtra("location");
                double[] off0_coordinates = intent2.getDoubleArrayExtra("off0_coordinates");
                option.putExtra("coordinates",off0_coordinates);
                option.putExtra("location",location);

                if(!off0.equals("none"))
                startActivity(option);
            }
        });

        off_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = intent2.getStringExtra("location");
                double[] off1_coordinates = intent2.getDoubleArrayExtra("off1_coordinates");
                option.putExtra("coordinates",off1_coordinates);
                option.putExtra("location",location);

                if(!off1.equals("none"))
                    startActivity(option);
            }
        });

        off_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = intent2.getStringExtra("location");
                double[] off2_coordinates = intent2.getDoubleArrayExtra("off2_coordinates");
                option.putExtra("coordinates",off2_coordinates);
                option.putExtra("location",location);

                if(!off2.equals("none"))
                    startActivity(option);
            }
        });
    }


}
