package com.Intrahubproject.intrahub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class LoginActivity extends AppCompatActivity {

    private MaterialTextView gotoregpage;
    private FirebaseAuth Mauth;
    private TextInputLayout email, password;
    private RelativeLayout  loginbutton;
    private ProgressDialog Mprogress;
    private RelativeLayout facebookbutton;
    private RelativeLayout adminbtn;

    private MaterialButton samplerecode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        samplerecode = findViewById(R.id.RecodButtponID);
        samplerecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), RecodActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });


        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(LoginActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

            dialog.setContentView(R.layout.internet_permission);
            dialog.show();


            RelativeLayout button = dialog.findViewById(R.id.WifiOnbuttonID);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                    dialog.dismiss();
                }
            });

        }



        adminbtn = findViewById(R.id.RequestAdminoginID);
        adminbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomsheed_helper bottomsheed_helper = new bottomsheed_helper();
                bottomsheed_helper.show(getSupportFragmentManager(), "open");

            }
        });

        facebookbutton = findViewById(R.id.FaceBookButtonID);
        facebookbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        Mprogress = new ProgressDialog(LoginActivity.this);
        Mauth = FirebaseAuth.getInstance();
        gotoregpage = findViewById(R.id.LogintextID);
        gotoregpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                CustomIntent.customType(LoginActivity.this, "fadein-to-fadeout");
            }
        });

        email = findViewById(R.id.LEmailID);
        password = findViewById(R.id.LPasswordID);
        loginbutton = findViewById(R.id.LoginButtonID);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailtext = email.getEditText().getText().toString();
                String passwordtext = password.getEditText().getText().toString();

                if(emailtext.isEmpty()){
                    email.getEditText().setError("Email require");
                }
                else if(passwordtext.isEmpty()){
                    password.getEditText().setError("Password require");
                }
                else {
                    Mprogress.setTitle("Please wait ...");
                    Mprogress.setMessage("We are reading your email and password");
                    Mprogress.setCanceledOnTouchOutside(false);
                    Mprogress.show();
                    Mauth.signInWithEmailAndPassword(emailtext, passwordtext)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Mprogress.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Mprogress.dismiss();
                                        email.getEditText().setText(null);
                                        password.getEditText().setText(null);
                                        Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Mprogress.dismiss();
                                    email.getEditText().setText(null);
                                    password.getEditText().setText(null);
                                    Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();

                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        FirebaseUser Muser = Mauth.getCurrentUser();
        if(Muser !=  null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        super.onStart();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(LoginActivity.this, "fadein-to-fadeout");
    }
}
