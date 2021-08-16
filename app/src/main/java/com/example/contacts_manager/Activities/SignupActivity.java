package com.example.contacts_manager.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contacts_manager.Models.UserModel;
import com.example.contacts_manager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class SignupActivity extends AppCompatActivity {

TextInputEditText name, phone, email, password, password2, DOB, city, country;
ImageView uncover, uncover2, date, back;
Boolean uncovered = false;
Boolean uncovered2 = false;
Calendar calendar;
Button create;
UserModel userModel;
DatabaseReference databaseReference;
DatabaseReference emailCheck;
PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
String mVerificationId;
PhoneAuthProvider.ForceResendingToken mResendToken;
private FirebaseAuth mAuth;
String token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.et_signupName);
        phone = findViewById(R.id.et_signupPhone);
        email = findViewById(R.id.et_signupEmail);
        password = findViewById(R.id.et_signupPassword);
        password2 = findViewById(R.id.et_signupPassword2);
        DOB = findViewById(R.id.et_signupDOB);
        city = findViewById(R.id.et_signupCity);
        country = findViewById(R.id.et_signupCountry);
        uncover = findViewById(R.id.iv_signupUncover);
        uncover2 = findViewById(R.id.iv_signupUncover2);
        date = findViewById(R.id.iv_signupDOB);
        back = findViewById(R.id.iv_signupBack);
        calendar = Calendar.getInstance();
        create = findViewById(R.id.btn_signupCreate);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent2);
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
        uncover2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!uncovered2) {
                    password2.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    uncovered2 = true;
                }
                else if (uncovered2) {
                    password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    uncovered2 = false;
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String day = String.valueOf(dayOfMonth), monthh = String.valueOf(month + 1);

                        if (Integer.valueOf(day) < 10) {
                            day = "0" + day;
                        }
                        if (Integer.valueOf(monthh) < 10) {
                            monthh = "0" + monthh;
                        }
                        DOB.setText(day + "/" + monthh + "/" + year);
                    }
                };
                new DatePickerDialog(SignupActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
                }
            });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                errorDialog(credential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);


                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(SignupActivity.this, "Invalid request", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(SignupActivity.this, "The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show();
                }


                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                errorDialog("");

            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            getToken();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SignupActivity.this, "Invalid", Toast.LENGTH_SHORT).show();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public void validation() {

        if (name.getText().toString().isEmpty()) {
            name.setError("Missing");
            return;
        }
        if (phone.getText().toString().isEmpty()) {
            phone.setError("Missing");
            return;
        }
        if (email.getText().toString().isEmpty()) {
            email.setError("Missing");
            return;
        }
        if (password.getText().toString().isEmpty()) {
            password.setError("Missing");
            return;
        }
        if (password2.getText().toString().isEmpty()) {
            password2.setError("Missing");
            return;
        }
        if (!password.getText().toString().equals(password2.getText().toString())) {
            password.setError("Do not match");
            password2.setError("Do not match");
            return;
        }
        if (DOB.getText().toString().isEmpty()) {
            DOB.setError("Missing");
            return;
        }
        if (city.getText().toString().isEmpty()) {
            city.setError("Missing");
            return;
        }
        if (country.getText().toString().isEmpty()) {
            country.setError("Missing");
            return;
        }
        emailCheck = FirebaseDatabase.getInstance().getReference("Users");
        emailCheck.orderByChild("Email").equalTo(email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    email.setError("Email not available");
                    Toast.makeText(SignupActivity.this, "An account with this email already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phone.getText().toString())       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(SignupActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void writeToDB() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                Map<String, Object> users = new HashMap<>();
                String key = databaseReference.push().getKey();
                databaseReference.child(key).setValue(users);

                users.put("UserID", key);
                users.put("Name", name.getText().toString());
                users.put("Phone", phone.getText().toString());
                users.put("Email", email.getText().toString());
                users.put("Password", password.getText().toString());
                users.put("DateOfBirth", DOB.getText().toString());
                users.put("City", city.getText().toString());
                users.put("Country", country.getText().toString());
                users.put("FCMtoken", token);
                databaseReference.child(key).setValue(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void errorDialog(String code){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.pin_verify);

        TextView tv_title = bottomSheetDialog.findViewById(R.id.tv_verifyTitle);
        EditText et_message = bottomSheetDialog.findViewById(R.id.et_verifyMessage);
        TextView btn_ok = bottomSheetDialog.findViewById(R.id.btn_ok);
        TextView btn_close = bottomSheetDialog.findViewById(R.id.btn_close);
        et_message.setText(code);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, et_message.getText().toString());
                signInWithPhoneAuthCredential(credential);
               /* if (mVerificationId.equals(et_message.getText().toString())) {
                    bottomSheetDialog.dismiss();

                }
                else {
                    et_message.setError("Invalid code");
                }*/

            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    public String getToken() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener((new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    token = task.getResult();
                    writeToDB();
                }
            }
        }));
        return token;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

        }
    }
}