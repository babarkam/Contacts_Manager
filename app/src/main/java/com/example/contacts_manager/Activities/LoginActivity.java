package com.example.contacts_manager.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contacts_manager.Models.UserModel;
import com.example.contacts_manager.R;
import com.example.contacts_manager.Utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
ImageView uncover;
TextInputEditText email, password;
Boolean uncovered = false;
Button login;
TextView signUp, reset;
UserModel userModel;
DatabaseReference databaseReference;
SessionManager sessionManager;
AlertDialog.Builder builder;
String token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        builder = new AlertDialog.Builder(this);
        uncover = findViewById(R.id.iv_loginUncover);
        password = findViewById(R.id.et_loginPassword);
        email = findViewById(R.id.et_loginEmail);
        login = findViewById(R.id.btn_loginLogin);
        signUp = findViewById(R.id.tv_loginSignup);
        reset = findViewById(R.id.tv_loginReset);

        sessionManager = new SessionManager(this);

        if (sessionManager.getLoginStatus()) {
            Intent loggedIn = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(loggedIn);
            finish();
        }


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resetIntent = new Intent(LoginActivity.this, ResetActivity.class);
                startActivity(resetIntent);
                finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });


        uncover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!uncovered) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    uncovered = true;
                }
                else if (uncovered) {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    uncovered = false;
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogin();
            }
        });

    }

    public void confirmLogin() {

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("Email").equalTo(email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String pwd = dataSnapshot.child("Password").getValue(String.class);
                        if (pwd.equals(password.getText().toString())) {
                            Toast.makeText(LoginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();

                            sessionManager.setData(dataSnapshot.child("UserID").getValue(String.class),
                                    dataSnapshot.child("Name").getValue(String.class), dataSnapshot.child("Email").getValue(String.class),
                                    dataSnapshot.child("Password").getValue(String.class), dataSnapshot.child("DateOfBirth").getValue(String.class),
                                    dataSnapshot.child("City").getValue(String.class), dataSnapshot.child("Country").getValue(String.class), dataSnapshot.child("FCMtoken").getValue(String.class));
                            getToken();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "No account exists. Please sign up below", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        builder.setMessage("Do you want to close the application").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.setTitle("Closing App");
        alert.show();
    }

    public String getToken() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    token = task.getResult();
                    DatabaseReference tokenReference = FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getuserId()).child("FCMtoken");
                    tokenReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            tokenReference.setValue(token);
                            sessionManager.setFCMtoken(token);
                            Intent intent2 = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent2);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
        return token;
    }
}