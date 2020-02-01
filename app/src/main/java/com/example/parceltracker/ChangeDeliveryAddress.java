package com.example.parceltracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeDeliveryAddress extends AppCompatActivity implements View.OnClickListener {


    private Button button;
    private Button buttonLogout;
    private EditText editText;
    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_delivery_address);
        input = getIntent().getStringExtra("v1");
        buttonLogout = (Button) findViewById(R.id.buttonLogout);


        databaseReference = FirebaseDatabase.getInstance().getReference("TrackingInformation").child(input);

        firebaseAuth = FirebaseAuth.getInstance();



        buttonLogout.setOnClickListener(this);

        editText = (EditText) findViewById(R.id.etAdd);



        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                String address = editText.getText().toString();

                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(address,
                        getApplicationContext(), new GeocoderHandler());
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            String Latitude,Longitude;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    Latitude = bundle.getString("Latitude");
                    Longitude= bundle.getString("Longitude");
                    databaseReference.child("destinationlat").setValue(Latitude);
                    databaseReference.child("destinationlong").setValue(Longitude);
                    Log.d("latttt", locationAddress);
                    break;
                default:
                    locationAddress = null;
            }
           // textView.setText(locationAddress);
        }
    }
}
