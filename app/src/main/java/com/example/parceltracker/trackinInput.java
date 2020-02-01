package com.example.parceltracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class trackinInput extends AppCompatActivity implements View.OnClickListener {

    private Button buttonTracking;
    private FirebaseAuth mAuth;
    private EditText editTextInputTracking;
    //String trackingKey;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    //new tesst code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackin_input);

        buttonTracking=(Button)findViewById(R.id.buttonTracking);
        buttonTracking.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        editTextInputTracking = (EditText)findViewById(R.id.editTextInputTracking);


        databaseReference = FirebaseDatabase.getInstance().getReference("FeedbackandRating");

        firebaseAuth = FirebaseAuth.getInstance();




    }

    @Override
    public void onClick(View v) {
        if (v==buttonTracking){

            if(TextUtils.isEmpty(editTextInputTracking.getText().toString())){
                Snackbar snackbar = Snackbar.make(
                        getWindow().getDecorView().getRootView(),
                        "Field can not be empty",
                        Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.WHITE);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
            else{

            Intent intent = new Intent(trackinInput.this,StatusLocation.class);
           // Toast.makeText(this, editTextInputTracking.getText().toString(), Toast.LENGTH_LONG).show();

            intent.putExtra("trackingText",editTextInputTracking.getText().toString());
            startActivity(intent);
            }
        }
    }

    private class ListView {
    }
}
