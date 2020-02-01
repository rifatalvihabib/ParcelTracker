package com.example.parceltracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FeedbackandratingActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;





    RatingBar mRatingBar;
    TextView mRatingScale;
    EditText mFeedback;
    Button mSendFeedback;
    DatabaseReference databaseReference;
    int rating;

String uid;
String email;
String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbackandrating);


        databaseReference = FirebaseDatabase.getInstance().getReference("FeedbackandRating");

        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null


        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();









        final DatabaseReference rootRef;

        rootRef = FirebaseDatabase.getInstance().getReference("users").child(uid);



        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                  email= dataSnapshot.child("email").getValue().toString();
                   username= dataSnapshot.child("username").getValue().toString();



                    Snackbar snackbar = Snackbar.make(
                            getWindow().getDecorView().getRootView(),
                            "Information is being loaded",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.BLACK);
                    snackbar.show();
                }
                else{

                    Snackbar snackbar = Snackbar.make(
                            getWindow().getDecorView().getRootView(),
                            "Invalid ",
                            Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.RED);
                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FeedbackandratingActivity.this, "Doesn't exist", Toast.LENGTH_LONG).show();

            }
        });

















        //initializing views
        //textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        //displaying logged in user name
        //textViewUserEmail.setText(user.getEmail());




        //adding listener to button
        //buttonLogout.setOnClickListener(this);


        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mRatingScale = (TextView) findViewById(R.id.tvRatingScale);
        mFeedback = (EditText) findViewById(R.id.etFeedback);
        mSendFeedback = (Button) findViewById(R.id.btnSubmit);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setText(String.valueOf(v));
                rating = (int) ratingBar.getRating();
                switch (rating) {
                    case 1:
                        mRatingScale.setText("Very bad");

                        break;
                    case 2:
                        mRatingScale.setText("Need some improvement");
                        break;
                    case 3:
                        mRatingScale.setText("Good");
                        break;
                    case 4:
                        mRatingScale.setText("Great");
                        break;
                    case 5:
                        mRatingScale.setText("Awesome. I love it");
                        break;
                    default:
                        mRatingScale.setText("");
                }
            }
        });


        mSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });



       buttonLogout.setOnClickListener(this);







    }

    public void saveData(){
        String feedBack = mFeedback.getText().toString().trim();
        int r = rating;

        //String key = databaseReference.push().getKey();
        Feedback fb = new Feedback(feedBack,r);
        databaseReference.child(username).setValue(fb);
        Toast.makeText(getApplicationContext(),"Thank you for sharing your feedback", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
    }














}
