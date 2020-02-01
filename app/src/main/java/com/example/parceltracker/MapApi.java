package com.example.parceltracker;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.Marker;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapApi extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {
    private final static int MY_PERMISSIONS_REQUEST = 32;

    private GoogleMap mMap;
    private LatLng mOrigin;
    private LatLng mDestination;
    String originlat,originlonggi,destinationlatt,destinationlonggi,input,strlat,strlong,orginlat,orginlong;
    double ovalue1,ovalue2,dvalue1,dvalue2;
    Button getDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_api);


        final DatabaseReference rootRef;
        input = getIntent().getStringExtra("v");



        Log.d("inp",input);


        getDirection = findViewById(R.id.btnDelivery);
        getDirection.setOnClickListener(this);


        rootRef = FirebaseDatabase.getInstance().getReference("TrackingInformation").child(input);



        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    strlat= dataSnapshot.child("destinationlat").getValue().toString();
                    strlong = dataSnapshot.child("destinationlong").getValue().toString();
                    orginlong= dataSnapshot.child("originlong").getValue().toString();
                    orginlat=dataSnapshot.child("originlat").getValue().toString();



                    ovalue1 = Double.parseDouble(orginlat);
                    ovalue2 = Double.parseDouble(orginlong);
                    dvalue1 = Double.parseDouble(strlat);
                    dvalue2 = Double.parseDouble(strlong);

                    Log.d("chaity",strlat);
                    Log.d("Sup",String.valueOf(ovalue1));
                    Log.d("Uiu",orginlat);
                    Log.d("tyt",orginlong);



                    mOrigin = new LatLng(ovalue1, ovalue2);
                    mDestination = new LatLng(dvalue1, dvalue2);

                    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapApi.this);

                   // if ((ovalue1==dvalue1) && (ovalue2-dvalue2)<=0.00004){

                    //}



                    Snackbar snackbar = Snackbar.make(
                            getWindow().getDecorView().getRootView(),
                            "Tracking information is being loaded",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.BLACK);
                    snackbar.show();
                }
                else{

                    Snackbar snackbar = Snackbar.make(
                            getWindow().getDecorView().getRootView(),
                            "Invalid tracking ID",
                            Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.RED);
                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                Toast.makeText(MapApi.this, "Doesn't exist", Toast.LENGTH_LONG).show();

            }



        });


    }

    /**
     * This method will manipulate the Google Map on the main screen
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Google map setup
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Show marker on the screen and adjust the zoom level





        mMap.clear();
        mMap.clear();

        mMap.addMarker(new MarkerOptions().position(mOrigin).title("Origin"));
        mMap.addMarker(new MarkerOptions().position(mDestination).title("Destination"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin,8f));
        new TaskDirectionRequest().execute(buildRequestUrl(mOrigin,mDestination));

        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(ovalue1,ovalue2))
                .zoom(7)
                .bearing(0)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null);

    }

    /**
     * Create requested url for Direction API to get routes from origin to destination
     *
     * @param origin
     * @param destination
     * @return
     */
    private String buildRequestUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        String output = "json";
        String APIKEY = "AIzaSyBsd684Iq9jxjZN3wzMN-bDHFoV4g9kmTs";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key="+APIKEY;
        Log.d("TAG", url);
        return url;
    }

    /**
     * Request direction from Google Direction API
     *
     * @param requestedUrl see {@link #buildRequestUrl(LatLng, LatLng)}
     * @return JSON data routes/direction
     */
    private String requestDirection(String requestedUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(requestedUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        httpURLConnection.disconnect();
        return responseString;
    }

    @Override
    public void onClick(View view) {


        if(view==getDirection){
            startActivity(new Intent(MapApi.this, FeedbackandratingActivity.class));
        }

    }

    //Get JSON data from Google Direction
    public class TaskDirectionRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            //Json object parsing
            TaskParseDirection parseResult = new TaskParseDirection();
            parseResult.execute(responseString);
        }
    }


    //Parse JSON Object from Google Direction API & display it on Map
    public class TaskParseDirection extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonString) {
            List<List<HashMap<String, String>>> routes = null;
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(jsonString[0]);
                DirectionParser parser = new DirectionParser();
                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            ArrayList<LatLng> points = null;
            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList<LatLng>();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lon = Double.parseDouble(Objects.requireNonNull(point.get("lng")));

                    boolean add = points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15f);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Request app permission for API 23/ Android 6.0
     *
     * @param permission
     */

    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    MY_PERMISSIONS_REQUEST);
        }
    }

}