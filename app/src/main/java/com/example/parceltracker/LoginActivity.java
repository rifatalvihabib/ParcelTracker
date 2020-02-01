package com.example.parceltracker;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {



    private Button buttonSignIn;
    private EditText editTextEmail;
    private  EditText editTextpassword;
    private TextView textViewSignup;
    private  ProgressDialog progressDialog;
    private  FirebaseAuth firebaseAuth;
    private TextView forgotPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth =FirebaseAuth.getInstance();
        editTextEmail=(EditText)findViewById(R.id.editTextEmail);
        editTextpassword=(EditText)findViewById(R.id.editTextpassword);
        buttonSignIn=(Button)findViewById(R.id.buttonSignin);
        textViewSignup=(TextView)findViewById(R.id.textViewSignUp);
        forgotPassword = (TextView)findViewById(R.id.textViewForgotPassword);
        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

    }




    private  void userLogin(){


        String email=editTextEmail.getText().toString().trim();
        String password=editTextpassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)){

            //email is empty
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

        }


        if(TextUtils.isEmpty(password)){

            //email is empty
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

        }

        ///if validation is okay
        //we will first show a progressbar
        // progressDialog.setMessage("Logging in..... ");
        //progressDialog.show();



        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        //hideProgressBar();

                        if (task.isSuccessful()) {



                            onAuthSuccess(task.getResult().getUser());
                            // startActivity(new Intent(LoginActivity.this, FeedbackandratingActivity.class));
                            checkEmailVerification();

                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });









        //firebaseAuth.signInWithEmailAndPassword(email, password)
        // .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        //  @Override
        //public void onComplete(@NonNull Task<AuthResult> task) {
        //progressDialog.dismiss();

        //if the task is successfull
        ///   if(task.isSuccessful()){
        //start the profile activity
        // finish();
        //checkEmailVerification();
        //  //startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        //}
        //  else {
        //        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
        //      }
        ///     }


        //   });

    }

    @Override
    public void onClick(View view) {
        if(view== buttonSignIn){

            userLogin();
        }
        if(view==textViewSignup){

            finish();
            startActivity(new Intent(this,SignUpActivity.class));
        }

        if (view==forgotPassword){

            startActivity(new Intent(this, PasswordActivity.class));
        }

    }



    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        // startActivity(new Intent(SignInActivity.this, MainActivity.class));
        // finish();
    }
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }


    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }



    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        //startActivity(new Intent(MainActivity.this,ProfileActivity.class));

        if(emailflag){
            // finish();

            startActivity(new Intent(LoginActivity.this, trackinInput.class));
        }else{
            Toast.makeText(this, "Verify your email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }





    }





















}
