package com.example.contacts_manager.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contacts_manager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
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

import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class ResetActivity extends AppCompatActivity {
    TextInputEditText email, password, password2;
    ImageView uncover, uncover2, back;
    Boolean uncovered = false;
    Boolean uncovered2 = false;
    RelativeLayout rel1, rel2;
    Button submit, reset;
    DatabaseReference databaseReference;
    DatabaseReference changer;
    String phoneNumber = "";
    BottomSheetDialog bottomSheetDialog;


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.et_resetEmail);
        password = findViewById(R.id.et_resetPassword);
        password2 = findViewById(R.id.et_resetPassword2);
        rel1 = findViewById(R.id.rel_reset_1);
        rel2 = findViewById(R.id.rel_reset_2);
        uncover = findViewById(R.id.iv_resetUncover);
        uncover2 = findViewById(R.id.iv_resetUncover2);
        back = findViewById(R.id.iv_resetBack);
        submit = findViewById(R.id.btn_resetSubmit);
        reset = findViewById(R.id.btn_resetReset);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(ResetActivity.this, LoginActivity.class);
                startActivity(back);
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


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().isEmpty()) {
                    databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.orderByChild("Email").equalTo(email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                     phoneNumber = dataSnapshot.child("Phone").getValue(String.class);
                                    PhoneAuthOptions options =
                                            PhoneAuthOptions.newBuilder(mAuth)
                                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                    .setActivity(ResetActivity.this)                 // Activity (for callback binding)
                                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                                    .build();
                                    PhoneAuthProvider.verifyPhoneNumber(options);

                                    changer = FirebaseDatabase.getInstance().getReference("Users").child(dataSnapshot.child("UserID").getValue(String.class)).child("Password");

                                }
                            }
                        }




                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    changer.setValue(password.getText().toString());
                    Toast.makeText(ResetActivity.this, "Password reset", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(ResetActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
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
                    Toast.makeText(ResetActivity.this, "Invalid request", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(ResetActivity.this, "The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show();
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
                            /*writeToDB();*/
                            /*Intent intent = new Intent(ResetActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();*/
                            bottomSheetDialog.dismiss();
                            submit.setVisibility(View.GONE);
                            rel1.setVisibility(View.VISIBLE);
                            rel2.setVisibility(View.VISIBLE);
                            reset.setVisibility(View.VISIBLE);

                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(ResetActivity.this, "Invalid", Toast.LENGTH_SHORT).show();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public void errorDialog(String code){
        bottomSheetDialog = new BottomSheetDialog(this);
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
    public boolean validation() {
        if (password.getText().toString().isEmpty()) {
            password.setError("Missing");
            return false;
        }
        if (password2.getText().toString().isEmpty()) {
            password2.setError("Missing");
            return false;
        }
        if (!password.getText().toString().equals(password2.getText().toString())) {
            password.setError("Do not match");
            password2.setError("Do not match");
            return false;
        }

        return true;
    }
}