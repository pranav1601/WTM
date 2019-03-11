package com.octagrimme.brow.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mukesh.OtpView;
import com.octagrimme.brow.R;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    String mVerificationId, phoneNumber, otpCode;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    LottieAnimationView ripple;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("phoneNum", MODE_PRIVATE);

        final ViewGroup transitionsContainer = findViewById(R.id.VG1);
        final ConstraintLayout constraintLayout1 = findViewById(R.id.VG2);
        final ConstraintLayout constraintLayout2 = findViewById(R.id.VG3);
        final Button help_button = findViewById(R.id.btn_help);

        ripple = findViewById(R.id.lav_ripple);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {

            boolean visible = true;

            @Override
            public void onClick(View view) {
                phoneNumber = ((TextInputEditText)findViewById(R.id.et_phone_number)).getText().toString();
                phoneNumber = "+91" + phoneNumber;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phoneNum", phoneNumber);
                TransitionManager.beginDelayedTransition(transitionsContainer, new Slide(Gravity.END));
                constraintLayout2.setVisibility(visible ? View.VISIBLE : View.GONE);
                constraintLayout1.setVisibility(visible ? View.GONE : View.VISIBLE);
                verifyPhoneNnumberWithOtp(phoneNumber);
            }
        });

        findViewById(R.id.btn_otp).setOnClickListener(view -> {
            ((OtpView) findViewById(R.id.cv_otp)).setOtpCompletionListener(s -> otpCode = s);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCode);
            signInWithPhoneAuthCredential(credential);
        });

        help_button.setOnClickListener(new View.OnClickListener() {

            boolean visible = true;

            @Override
            public void onClick(View view) {
               /* TransitionManager.beginDelayedTransition(transitionsContainer);
                help_button.setVisibility(visible ? View.GONE : View.VISIBLE);*/

                TransitionManager.beginDelayedTransition(transitionsContainer, new Slide(Gravity.END));
                constraintLayout1.setVisibility(visible ? View.VISIBLE : View.GONE);
                help_button.setVisibility(visible ? View.GONE : View.VISIBLE);
                ripple.setVisibility(visible ? View.GONE : View.VISIBLE);
                ripple.pauseAnimation();
            }
        });

        if (user != null){
            launchHome();
        }

        Dexter.withActivity(LoginActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    void verifyPhoneNnumberWithOtp(String phoneNumber){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases the phone number can be instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.
                        Log.d(TAG, "onVerificationCompleted:" + credential);

                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w(TAG, "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            // ...
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            // ...
                        }

                        // Show a message and update the UI
                        // ...
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, token);

                        Toast.makeText(LoginActivity.this, "Code Recieved, Processing", Toast.LENGTH_SHORT).show();
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
                        Log.d(TAG, "onCodeSent:" + verificationId);

                        // Save verification ID and resending token so we can use them later
                        mVerificationId = verificationId;
                        mResendToken = token;

                        // ...
                    }
                });     // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();

                        SharedPreferences sharedPreferences = getSharedPreferences("Information", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("UserPhoneNumber", user.getPhoneNumber());
                        editor.apply();
                        launchHome();

                        // ...
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                });
    }

    void launchHome(){
        Intent intent = new Intent(LoginActivity.this, DisasterMapsActivity.class);
        startActivity(intent);
        finishAfterTransition();
    }


}

