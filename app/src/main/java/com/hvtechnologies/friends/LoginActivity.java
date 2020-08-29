package com.hvtechnologies.friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    Button login, register ;
    EditText Nametxt, Passtxt;

    TextView forgotPass ;
    String user_id;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database ;
    private ProgressDialog mLoginProgress;
    DatabaseReference  mDatabaseReference ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.LoginActBtn);
        register = (Button) findViewById(R.id.RegActBtn);
        forgotPass = (TextView)findViewById(R.id.ForgotPasswordTextView);

        mLoginProgress = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Nametxt = (EditText) findViewById(R.id.NameText);
        Passtxt = (EditText) findViewById(R.id.PassText);

        mAuth = FirebaseAuth.getInstance();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");


        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder( LoginActivity.this)
                        .setCancelable(false);
                View mView = getLayoutInflater().inflate(R.layout.dialog_box_forgot_password, null);
                final EditText EditTextForgotPass = (EditText) mView.findViewById(R.id.EditTextForgotPassword);
                AlertDialog.Builder builder = mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        if (!EditTextForgotPass.getText().toString().isEmpty()) {

                            mLoginProgress.setMessage("Sending Mail");
                            mLoginProgress.setCanceledOnTouchOutside(false);
                            mLoginProgress.show();
                            mAuth.sendPasswordResetEmail(EditTextForgotPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(LoginActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                                        mLoginProgress.dismiss();
                                        dialog.dismiss();
                                    } else {

                                        Toast.makeText(LoginActivity.this, "Email Does Not Exist", Toast.LENGTH_SHORT).show();
                                        mLoginProgress.dismiss();
                                        dialog.dismiss();
                                    }

                                }
                            });


                        }


                    }
                });

                mBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });


                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();


            }
        });




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = Nametxt.getText().toString();
                final String Pass = Passtxt.getText().toString();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(Pass)) {

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    mAuth.signInWithEmailAndPassword(email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                String user_id = mAuth.getCurrentUser().getUid();
                                String token_id= FirebaseInstanceId.getInstance().getToken();
                                Map addValue = new HashMap();
                                addValue.put("device_token",token_id);
                                addValue.put("online","true");

                                mDatabaseReference.child(user_id).updateChildren(addValue, new DatabaseReference.CompletionListener(){

                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        if(databaseError==null){

                                            //---OPENING MAIN ACTIVITY---
                                            Log.e("Login : ","Logged in Successfully" );
                                            Toast.makeText(getApplicationContext(), "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(LoginActivity.this, databaseError.toString()  , Toast.LENGTH_SHORT).show();
                                            Log.e("Error is : ",databaseError.toString());

                                        }
                                    }
                                });


                            } else {

                                try
                                {
                                    throw task.getException();
                                }
                                // if user enters wrong email.
                                catch (FirebaseAuthInvalidUserException invalidEmail)
                                {
                                    mLoginProgress.hide();
                                    Toast.makeText(LoginActivity.this, "Invalid Email" , Toast.LENGTH_SHORT).show();


                                }
                                // if user enters wrong password.
                                catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                                {
                                    mLoginProgress.hide();
                                    Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();

                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(LoginActivity.this,  e.getMessage() ,
                                            Toast.LENGTH_SHORT).show();
                                    mLoginProgress.dismiss();
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "FIELDS CANNOT BE EMPTY", Toast.LENGTH_SHORT).show();

                }
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);


            }
        });



    }
}
