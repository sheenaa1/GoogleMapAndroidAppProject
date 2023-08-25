package com.example.googlemapproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.googlemapproject.Activity2;
import com.example.googlemapproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout layout;
    String info = "";
    JSONObject jsonObject;
    JSONArray results;
    String lat;
    String lon;

    String location;
    String parking_url;
    String info2 = "";
    JSONObject jsonObject2;
    JSONArray on_street;
    JSONArray off_street;
    String street0;
    String street1;
    String street2;

    String off0;
    String off1;
    String off2;

    String number;
    String state;
    String zipcode;
    GeoCode obj = new GeoCode();
    Parking obj2 = new Parking();

    Button button;
    EditText number_text;
    EditText street_text;
    EditText city_text;
    EditText state_text;
    EditText zipcode_text;

    final Context context = this;

    //address to try out:
    //110 Nassau St., Princeton, NJ 08542
    //36 College Ave, New Brunswick, NJ 08901
    //225 River St, Hoboken, NJ 07030

    //2695 N Beachwood Dr, Los Angeles, CA 90068
    //1681 Broadway, New York, NY 10019

    public String getStreetType()
    {
        String street = String.valueOf(street_text.getText());
        String ans = street;
        boolean type = false;

        if(street.contains(" "))
        {
            String[] array = street.split(" ");
            String street_type = array[array.length-1];
            Log.d("TAG_INFO", "street_type: "+street_type);
            ans = "";

            if(street_type.equals("Street") || street_type.equals("St."))
            {
                type = true;
            }

            else if(street_type.equals("Avenue") || street_type.equals("Ave"))
            {
                type = true;
            }

            else if(street_type.equals("Dr") || street_type.equals("Drive"))
            {
                type = true;
            }

            else if(street_type.equals("Lane"))
            {
                type = true;
            }

            else if(street_type.equals("Rd") || street_type.equals("Road"))
            {
                type = true;
            }

            else if(street_type.equals("Way"))
            {
                type = true;
            }

            for(int i = 0; i < array.length; i++)
            {
                if(i != array.length-1)
                    ans += array[i]+"%20";


                if(i == array.length-1)
                    ans+= array[array.length-1];
            }
        }
        //Log.d("TAG_INFO", "final ans: "+ans);
        return ans;
    }

    public String getCityFormat()
    {
        String city = String.valueOf(city_text.getText());
        String answer = "";

        if(city.contains(" "))
        {
            String[] city_name = city.split(" ");
            answer = city_name[0]+"%20"+city_name[1];
        }
        return answer;
    }

    private class GeoCode extends AsyncTask<Void, Void, String>
    {
        protected String doInBackground(Void... voids)
        {

            location = number_text.getText()+"%20"+getStreetType()+",%20"+getCityFormat()+",%20"+state_text.getText()+"%20"+zipcode_text.getText();

            String geocoding = "http://open.mapquestapi.com/geocoding/v1/address?key=xLntXs0SV7RHnAUkbGJSdvUoWGitHV47&location="+location;

            URL myURL = null;
            try {
                myURL = new URL(geocoding);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            URLConnection urlConnection = null;
            try {
                urlConnection = myURL.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            InputStream inputStream = new InputStream() {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            };

            try {
                inputStream = urlConnection.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            try {
                info = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return info;
        }//end of doInBackground

        protected void onPostExecute(String result)
        {
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(jsonObject != null)
            {
                try {
                    results = jsonObject.getJSONArray("results");
                    JSONObject latLng = results.getJSONObject(0).getJSONArray("locations").getJSONObject(0).getJSONObject("latLng");
                    lat = latLng.getString("lat");
                    lon = latLng.getString("lng");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                parking_url = "https://guidance.streetline.com/v3/guidance/by-point?latitude="+lat+"&longitude="+lon+"&limit=60";

                if(parking_url != null)
                    obj2.execute();
            }//end of if

        }//end of onPostExecute
    }//end of GeoCode

    private class Parking extends AsyncTask<Void, Void, String>
    {
        protected String doInBackground(Void... voids)
        {
            Log.d("TAG_INFO","parkin_url: "+parking_url);
            if(parking_url != null)
            {
                URL myURL2 = null;
                try {
                    myURL2 = new URL(parking_url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                URLConnection urlConnection2 = null;
                try {
                    urlConnection2 = myURL2.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                InputStream inputStream2 = new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return 0;
                    }
                };

                try {
                    inputStream2 = urlConnection2.getInputStream();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                BufferedReader br2 = new BufferedReader(new InputStreamReader(inputStream2));

                /*try {
                    info2 = br2.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                String line = "";
                try {
                    line = br2.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while(line != null)
                {
                    info2 += line;
                    try {
                        line = br2.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            else
                Log.d("TAG_INFO","park_url is null");
            return info2;
        }//end of doInBackground

        protected void onPostExecute(String result)
        {
            try {
                jsonObject2 = new JSONObject(result);
            } catch (JSONException e) {
                Log.d("TAG_INFO","jsonObject result catch: "+e);
                e.printStackTrace();
            }

            if(jsonObject2 != null)
            {
                double street0_coordinates[] = new double[2];
                double street1_coordinates[] = new double[2];
                double street2_coordinates[] = new double[2];

                double off0_coordinates[] = new double[2];
                double off1_coordinates[] = new double[2];
                double off2_coordinates[] = new double[2];

                try
                {
                    on_street = jsonObject2.getJSONArray("on_street");

                    if(on_street != null)
                    {
                        int i = 0;
                        int j = 0;
                        String name_check = on_street.getJSONObject(i).getString("name");
                        int count = 0;
                        String on_names[] = new String[3];
                        List<Double> on_c = new ArrayList<>();

                        while(count < 3 && i <= on_street.length()-1)
                        {
                            if(name_check != "null")
                            {
                                count++;

                                on_names[j] = name_check;
                                j++;
                            }
                            JSONArray coordinates = on_street.getJSONObject(i).getJSONObject("geojson_center").getJSONArray("coordinates");
                            on_c.add(coordinates.getDouble(0));
                            on_c.add(coordinates.getDouble(1));
                            i++;
                            if(i <= on_street.length()-1)
                                name_check = on_street.getJSONObject(i).getString("name");
                        }

                        if(count < 3)
                        {
                            for(int x = count; x < on_names.length; x++)
                            {
                                on_names[x] = "none";
                            }
                        }

                        street0 = on_names[0];
                        street0_coordinates[0] = on_c.get(0);
                        street0_coordinates[1] = on_c.get(1);

                        street1 = on_names[1];
                        street1_coordinates[0] = on_c.get(2);
                        street1_coordinates[1] = on_c.get(3);

                        street2 = on_names[2];
                        street2_coordinates[0] = on_c.get(4);
                        street2_coordinates[1] = on_c.get(5);
                    }
                    else
                    {
                        street0 = "none";
                        street1 = "none";
                        street2 = "none";
                    }

                    off_street = jsonObject2.getJSONArray("off_street");

                    if(off_street != null)
                    {
                        int i = 0;
                        int j = 0;
                        String name_check = off_street.getJSONObject(i).getString("name");
                        int count = 0;
                        String off_names[] = new String[3];
                        List<Double> off_c = new ArrayList<>();

                        while(count < 3 && i <= off_street.length()-1)
                        {
                            if(name_check != "null")
                            {
                                count++;

                                off_names[j] = name_check;
                                j++;
                            }

                            JSONArray coordinates = off_street.getJSONObject(i).getJSONObject("geojson_center").getJSONArray("coordinates");
                            off_c.add(coordinates.getDouble(0));
                            off_c.add(coordinates.getDouble(1));

                            i++;
                            if(i <= off_street.length()-1)
                                name_check = off_street.getJSONObject(i).getString("name");
                        }

                        if(count < 3)
                        {
                            for(int x = count; x < off_names.length; x++)
                            {
                                off_names[x] = "none";
                            }
                        }



                        off0 = off_names[0];
                        off0_coordinates[0] = off_c.get(0);
                        off0_coordinates[1] = off_c.get(1);

                        off1 = off_names[1];
                        off1_coordinates[0] = off_c.get(2);
                        off1_coordinates[1] = off_c.get(3);

                        off2 = off_names[2];
                        off2_coordinates[0] = off_c.get(4);
                        off2_coordinates[1] = off_c.get(5);
                    }
                    else
                    {
                        off0 = "none";
                        off1 = "none";
                        off2 = "none";
                    }

                } catch (JSONException e) {
                    Log.d("TAG_INFO","catch of jsonObject2 if "+e);
                    e.printStackTrace();
                }

                Intent intent = new Intent(context, Activity2.class);

                intent.putExtra("street0",street0);
                intent.putExtra("street1",street1);
                intent.putExtra("street2",street2);
                intent.putExtra("off0",off0);
                intent.putExtra("off1",off1);
                intent.putExtra("off2",off2);
                intent.putExtra("street0_coordinates",street0_coordinates);
                intent.putExtra("street1_coordinates",street1_coordinates);
                intent.putExtra("street2_coordinates",street2_coordinates);
                intent.putExtra("off0_coordinates",off0_coordinates);
                intent.putExtra("off1_coordinates",off1_coordinates);
                intent.putExtra("off2_coordinates",off2_coordinates);
                intent.putExtra("location",location);

                startActivity(intent);
            }//end of if
            else
                Log.d("TAG_INFO","jsonObject2 is null");

        }//end of onPostExecute
    }//end of Parking

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.id_layout);
        number_text = findViewById(R.id.num_editText);
        street_text = findViewById(R.id.street_editText);
        city_text = findViewById(R.id.city_editText);
        state_text = findViewById(R.id.state_editText);
        zipcode_text = findViewById(R.id.zipcode_editText);

        addListenerOnButton();
    }

    public void addListenerOnButton()
    {
        button = findViewById(R.id.main_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                obj.execute();
            }//end of onClick
        });

    }//end of addListenerOnButton

}