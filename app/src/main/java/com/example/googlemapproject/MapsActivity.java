package com.example.googlemapproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends Activity2 implements OnMapReadyCallback, OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    String info3 = "";
    JSONObject jsonObject1 = new JSONObject();
    double lat;
    double lon;
    String direction_url;
    String destination;
    String point;

    JSONArray routes;
    JSONObject zero;

    List<LatLng> LatLnglist = new ArrayList<>();

    static double tr_lat = 0;
    static double tr_lon = 0;
    static double bl_lat = 0;
    static double bl_lon = 0;

    double start_lat = 0;
    double start_lon = 0;
    double end_lat = 0;
    double end_lon = 0;

    static double temp;
    static double temp2;

    ArrayList<LatLng> poly_coordinates = new ArrayList<>();

    Direction obj = new Direction();

    private GoogleMap mMap;

    Button button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent3 = getIntent();
        double[] coordinates = intent3.getDoubleArrayExtra("coordinates");
        destination = intent3.getStringExtra("location");
        setResult(RESULT_OK, getIntent());
        lon = coordinates[0];
        lat = coordinates[1];

        try
        {
            if(bl_lat == 0)
                obj.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        drawAllPolyLines(poly_coordinates);
        moveCameraToWantedArea();
    }

    //Direction asynch method
    private class Direction extends AsyncTask<Void, Void, String>
    {
        protected String doInBackground(Void... voids)
        {
            direction_url = "https://maps.googleapis.com/maps/api/directions/json?origin="+lat+","+lon+"&destination="+destination+"&key=AIzaSyBbNFIG3L0j12UovBXWJOz3eRJpyaSqBSc";
            Log.d("TAG_INFO","direction_url: "+direction_url);
            URL myURL3 = null;
            try {
                myURL3 = new URL(direction_url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            URLConnection urlConnection3 = null;
            try {
                urlConnection3 = myURL3.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            InputStream inputStream3 = new InputStream() {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            };

            try {
                inputStream3 = urlConnection3.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }

            BufferedReader br3 = new BufferedReader(new InputStreamReader(inputStream3));

            String line = "";
            try {
                line = br3.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while(line != null)
            {
                info3 += line;
                try {
                    line = br3.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return info3;
        }//end of doInBackground

        protected void onPostExecute(String result)
        {
            try {
                jsonObject1 = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(jsonObject1 != null)
            {
                try
                {
                    JSONObject overview_polyline = jsonObject1.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline");
                    String pts = overview_polyline.getString("points");
                    poly_coordinates = decodePoly(pts);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }//end of if

        }//end of onPostExecute
    }//end of Direction

    public void PolyLinetoCoordinates(Context context)
    {
        if(jsonObject1 != null)
        {
            try {
                start_lat = jsonObject1.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getDouble("lat");
                start_lon = jsonObject1.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getDouble("lng");

                end_lat = jsonObject1.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getDouble("lat");
                end_lon = jsonObject1.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getDouble("lng");

                tr_lat = jsonObject1.getJSONArray("routes").getJSONObject(0).getJSONObject("bounds").getJSONObject("northeast").getDouble("lat");
                tr_lon = jsonObject1.getJSONArray("routes").getJSONObject(0).getJSONObject("bounds").getJSONObject("northeast").getDouble("lng");

                bl_lat = jsonObject1.getJSONArray("routes").getJSONObject(0).getJSONObject("bounds").getJSONObject("southwest").getDouble("lat");
                bl_lon = jsonObject1.getJSONArray("routes").getJSONObject(0).getJSONObject("bounds").getJSONObject("southwest").getDouble("lng");

                temp = (bl_lat+0.002);
                temp2 = (tr_lat-0.002);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LatLnglist.add(new LatLng(start_lat,start_lon));
        LatLnglist.add(new LatLng(end_lat,end_lon));
    }

    /**
     * BOUND1 is the relative coordinates of the bottom-left corner of the bounded area on the map.
     * BOUND2 is the relative coordinates of the top-right corner of the bounded area on the map.
     */

    private void moveCameraToWantedArea()
    {
        PolyLinetoCoordinates(this); //sets temp here
        final LatLng BOUND1 = new LatLng(temp, bl_lon);
        final LatLng BOUND2 = new LatLng(temp2, tr_lon);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
        {
            @Override
            public void onMapLoaded()
            {
                // Set up the bounds coordinates for the area we want the user's viewpoint to be.
                LatLngBounds bounds = new LatLngBounds.Builder().include(BOUND1).include(BOUND2).build();

                int BOUNDS_PADDING = 4; //idk
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, BOUNDS_PADDING));
            }
        });
    }

    private void drawAllPolyLines(ArrayList<LatLng> polyline_coordinates)
    {
        JSONArray steps = null;

        mMap.addPolyline(new PolylineOptions().color(getResources()
                .getColor(R.color.purple_200))
                .width(12)
                .clickable(true)
                .addAll(polyline_coordinates));
    }

    private ArrayList<LatLng> decodePoly(String encoded)
    {
        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    } //end of decodePoly
}

/**
 * Helper class that will delay triggering the OnMapReady callback
 * until both the GoogleMap and the
 * View having completed initialization.
 */
class OnMapAndViewReadyListener implements ViewTreeObserver.OnGlobalLayoutListener, OnMapReadyCallback
{
    /** A listener that needs to wait for both the GoogleMap and the View to be initialized. */
    public interface OnGlobalLayoutAndMapReadyListener {
        void onMapReady(GoogleMap googleMap);
    }
    private final SupportMapFragment mapFragment;
    private final View mapView;
    private final OnGlobalLayoutAndMapReadyListener devCallback;
    private boolean isViewReady;
    private boolean isMapReady;

    private GoogleMap googleMap;
    /** Constructor. */
    public OnMapAndViewReadyListener(SupportMapFragment mapFragment, OnGlobalLayoutAndMapReadyListener devCallback)
    {
        this.mapFragment = mapFragment;
        mapView = mapFragment.getView();
        this.devCallback = devCallback;
        isViewReady = false;
        isMapReady = false;
        googleMap = null;
        // Register listener when creating an OnMapAndViewReadyListener object.
        registerListeners();
    }
    /**
     * Method to help register a Global Layout Listener for mapView.
     * This is necessary to determining if map view has completed layout.
     **/
    private void registerListeners()
    {
        // View layout.
        if ((mapView.getWidth() != 0) && (mapView.getHeight() != 0)) {
        // View has already completed layout.
            isViewReady = true;
        } else {
        // Map has not undergone layout, register a View observer.
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
        // GoogleMap. Note if the GoogleMap is already ready it will still fire the callback later.
        mapFragment.getMapAsync(this);
    }
    /**
     * Use the onMapReady(GoogleMap) callback method to get a handle to the GoogleMap object.
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
// NOTE: The GoogleMap API specifies the listener is removed just prior to invocation.
        this.googleMap = googleMap;
        isMapReady = true;
        fireCallbackIfReady();
    }
    /**
     * Callback method to be invoked when the global layout state or the visibility of views within
     * the view tree changes.
     **/
    @SuppressWarnings("deprecation")  // We use the new method when supported
    @SuppressLint("NewApi")  // We check which build version we are using.
    @Override
    public void onGlobalLayout() {
// Remove our listener.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
        {
            mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        } else {
            mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        isViewReady = true;
        fireCallbackIfReady();
    }
    /**
     * If map view is ready, trigger the callback.
     **/
    private void fireCallbackIfReady() {
        if (isViewReady && isMapReady) {
            devCallback.onMapReady(googleMap);
        }
    }
}



