package com.example.parceltracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.BreakIterator;

public class StatusLocation extends AppCompatActivity implements View.OnClickListener {

    String trackingText;
    TextView trackingadd1;
    TextView trackingadd2;
    TextView trackingadd3;
    TextView trackingadd4,trackingadd5,trackingadd6,trackingadd7,trackingadd8;
    private Button mapbtnView;
    private Button btnChangeDeliveryAddress;

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    String user1;
    String demo="";
    String checkmap;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_location);
        firebaseAuth = FirebaseAuth.getInstance();


        trackingText = getIntent().getStringExtra("trackingText");

        trackingadd1= findViewById(R.id.editTextAdress1);
        trackingadd2= findViewById(R.id.editTextAdress2);
        trackingadd3= findViewById(R.id.editTextAdress3);
        trackingadd4= findViewById(R.id.editTextAdress4);

        trackingadd5= findViewById(R.id.editTextAdress5);
        trackingadd6= findViewById(R.id.editTextAdress6);
        trackingadd7= findViewById(R.id.editTextAdress7);
        trackingadd8= findViewById(R.id.editTextAdress8);

        mapbtnView= findViewById(R.id.btnMapView);
        btnChangeDeliveryAddress=findViewById(R.id.btnChangeDeliveryAddress);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        user1=user.getEmail();

        btnChangeDeliveryAddress.setOnClickListener(this);

        mapbtnView.setOnClickListener(this);

        fetchData();
        //testData();
    }

    private void testData() {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child("User").setValue("lol");
    }

    private void fetchData() {

        final DatabaseReference rootRef;

        rootRef = FirebaseDatabase.getInstance().getReference("TrackingInformation").child(trackingText);



        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    notificationchannel();
                    //findViewById(R.id.notify);
                    trackingadd1.setText("Picked From : "+(dataSnapshot.child("add1").getValue().toString()));
                    notification("New Notification","Check Parcel Status");
                    trackingadd2.setText("Checkpoint1 : "+(dataSnapshot.child("add2").getValue().toString()));
                    //   notification("My first Notification","Notification content2");
                    trackingadd3.setText("Checkpoint2 : "+(dataSnapshot.child("add3").getValue().toString()));
                    //  notification("My first Notification","Notification content3");
                    trackingadd4.setText("Final Checkpoint : "+(dataSnapshot.child("add4").getValue().toString()));
                   username= dataSnapshot.child("username").getValue().toString();


                    checkmap=dataSnapshot.child("add4").getValue().toString();
                    // notification("My first Notification","Notification content4");

                    trackingadd5.setText(dataSnapshot.child("originlat").getValue().toString());
                    trackingadd6.setText(dataSnapshot.child("originlong").getValue().toString());
                    trackingadd7.setText(dataSnapshot.child("destinationlat").getValue().toString());
                    trackingadd8.setText(dataSnapshot.child("destinationlong").getValue().toString());
                    Snackbar snackbar = Snackbar.make(
                            getWindow().getDecorView().getRootView(),
                            "Information is Being Loaded",
                            Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.RED);
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StatusLocation.this, "Doesn't exist", Toast.LENGTH_LONG).show();

            }
        });
    }


    public void onClick(View v) {

        if (TextUtils.isEmpty(checkmap.toString())) {
            Toast.makeText(StatusLocation.this, "Product is not ready to be Delivered.Please wait", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(StatusLocation.this, "Product is  ready to be Delivered.See Map view for Live Location", Toast.LENGTH_SHORT).show();

            if (v == mapbtnView) {
                if (user1.equals(demo)) {
                    //closing this activity
                    finish();
                    //starting login activity
                    Toast.makeText(StatusLocation.this, "Please Register to avail this service", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(StatusLocation.this, SignUpActivity.class));


                }
                else if(!(user1.equals(username))){
                    finish();
                    Toast.makeText(StatusLocation.this, "Enter your own tracking Id for MAP VIEW", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(StatusLocation.this, trackinInput.class));
                }
                else {
                    //Create the `intent`
                    Intent intent = new Intent(StatusLocation.this, MapApi.class);

                    // intent.putExtra("origin",trackingadd5.getText().toString());


                    //Create the bundle
                    Bundle bundle = new Bundle();
                    //Add your data to bundle
                    intent.putExtra("originlat", trackingadd5.getText().toString());
                    intent.putExtra("originlong", trackingadd6.getText().toString());
                    intent.putExtra("destinationlat", trackingadd7.getText().toString());
                    intent.putExtra("destinationlong", trackingadd8.getText().toString());
                    //intent.putExtra("Track", trackingText.toString());


                    // Log.d("SHahan",s);

                    intent.putExtra("v", trackingText);

                    Log.d("cccccccc", trackingText);


                    //Add the bundle to the intent
                    intent.putExtras(bundle);
                    //Fire that second activity
                    // startActivity(intent);
                    startActivity(new Intent(intent));
                }
            }

            }  if (v == btnChangeDeliveryAddress) {
                if (user1.equals(demo)) {
                    //closing this activity
                    finish();
                    //starting login activity
                    Toast.makeText(StatusLocation.this, "Please Register to avail this service ", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(StatusLocation.this, SignUpActivity.class));

                }
                else if(!(user1.equals(username))){
                    finish();
                    Toast.makeText(StatusLocation.this, "Enter your own tracking Id for Changing Delivery Address", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(StatusLocation.this, trackinInput.class));
                }else {
                    //Create the `intent`
                    Intent intent = new Intent(StatusLocation.this, ChangeDeliveryAddress.class);

                    // intent.putExtra("origin",trackingadd5.getText().toString());


                    //Create the bundle
                    Bundle bundle = new Bundle();


                    intent.putExtra("v1", trackingText);

                    Log.d("cccccccc", trackingText);


                    //Add the bundle to the intent
                    intent.putExtras(bundle);
                    //Fire that second activity
                    // startActivity(intent);
                    startActivity(new Intent(intent));
                }

            }




    }

    public void notification(String Title, String Message)
    {
        Intent intent = new Intent(StatusLocation.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(StatusLocation.this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(StatusLocation.this,"Channel Id");
        builder.setSmallIcon(R.drawable.ic_airport_shuttle_black_24dp)
                .setContentTitle(Title)
                .setContentText(Message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setAutoCancel(true);
        NotificationManagerCompat manager = NotificationManagerCompat.from(StatusLocation.this);
        manager.notify(1,builder.build());
    }
    public void notificationchannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "First Notification";
            String Description = "notification description";
            //It's for when you ling click on notification to disable that.
            //It specifies category of which notification you want to show to your users.
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Channel Id",name,importance);
            channel.setDescription(Description);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}


