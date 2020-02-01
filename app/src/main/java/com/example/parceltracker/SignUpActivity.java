package com.example.parceltracker;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener       {
    private Button buttonRegister;
    private SignInButton signIn;

    private EditText editTextEmail;
    private  EditText editTextpassword;
    private  EditText editTextRepassword;

    private TextView textViewSignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        buttonRegister=(Button)findViewById(R.id.buttonRegister);
        editTextEmail=(EditText)findViewById(R.id.editTextEmail);
        editTextpassword=(EditText)findViewById(R.id.editTextpassword);
        editTextRepassword=(EditText)findViewById(R.id.editTextRepassword);
        textViewSignin=(TextView)findViewById(R.id.textViewSignin);
        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);



        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


    }



    private void registerUser(){


        String email=editTextEmail.getText().toString().trim();
        String password=editTextpassword.getText().toString().trim();
        String Repassword=editTextRepassword.getText().toString().trim();

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
        if(TextUtils.isEmpty(Repassword)){

            //email is empty
            Toast.makeText(this,"Please ReEnter password",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

        }


        if(!(Repassword.equals(password))){

            //email is empty
            Toast.makeText(this,"Please ReEnter password",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

        }
        ///if validation is okay
        //we will first show a progressbar
        // progressDialog.setMessage("Registering User..... ");
        //progressDialog.show();




        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //    Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());

                        //  hideProgressBar();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                            sendEmailVerification();
                        } else {
                            Toast.makeText(SignUpActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });







        //    firebaseAuth.createUserWithEmailAndPassword(email, password)
        //          .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        //            @Override
        //          public void onComplete(@NonNull Task<AuthResult> task) {
        //            //checking if success
        //          if(task.isSuccessful()){
        //           // finish();
        ///         sendEmailVerification();
        //       //startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        //  }else{
        //    //display some message here
        //  Toast.makeText(SignUpActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
        // }
        //   progressDialog.dismiss();
        // }
        //});



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



    public  void  onClick(View view){


        if(view==buttonRegister){
            registerUser();



        }
        if(view==textViewSignin){
            startActivity(new Intent(this, LoginActivity.class));


        }



    }







    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        // sendUserData();
                        Toast.makeText(SignUpActivity.this, "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    }else{
                        Toast.makeText(SignUpActivity.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }










}
