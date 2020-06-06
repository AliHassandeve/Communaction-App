package com.Intrahubproject.intrahub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class RegisterActivity extends AppCompatActivity {

    private MaterialTextView loginpagego;
    private TextInputLayout email, password, rpassword;
    private RelativeLayout registationbutton;
    private ProgressDialog Mprogress;
    private FirebaseAuth Mauth;
    private String CurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Mauth = FirebaseAuth.getInstance();
        Mprogress = new ProgressDialog(RegisterActivity.this);
        email = findViewById(R.id.RLEmailID);
        password = findViewById(R.id.RLPasswordID);
        rpassword = findViewById(R.id.LRPasswordID);
        registationbutton = findViewById(R.id.RLoginButtonID);


        loginpagego = findViewById(R.id.RLogintextID);
        loginpagego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                startActivity(intent);
                CustomIntent.customType(RegisterActivity.this, "fadein-to-fadeout");
            }
        });


        registationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailtext = email.getEditText().getText().toString();
                final String passwordtext = password.getEditText().getText().toString();
                String rpasswordtext = rpassword.getEditText().getText().toString();

                if(emailtext.isEmpty()){
                    email.getEditText().setError("Email require");
                }
                else if(passwordtext.isEmpty()){
                    password.getEditText().setError("Password require");
                }
                else if(rpasswordtext.isEmpty()){
                    rpassword.getEditText().setError("Password require");
                }
                else if(!rpasswordtext.equals(passwordtext)){
                    password.getEditText().setError("Password not match");
                    rpassword.getEditText().setError("Password not match");
                }
                else if(rpasswordtext.length() <= 8){
                    Toasty.info(getApplicationContext(), "Password need 8 char", Toasty.LENGTH_LONG).show();
                }
                else {
                    Mprogress.setTitle("Please wait ...");
                    Mprogress.setMessage("We are creating your account");
                    Mprogress.setCanceledOnTouchOutside(false);
                    Mprogress.show();
                    Mauth.createUserWithEmailAndPassword(emailtext, passwordtext)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Mprogress.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Mprogress.dismiss();
                                        email.getEditText().setText(null);
                                        password.getEditText().setText(null);
                                        rpassword.getEditText().setText(null);
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
                                    rpassword.getEditText().setText(null);
                                    Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        FirebaseUser Mauser = Mauth.getCurrentUser();
        if(Mauser != null){
            Intent intent  = new Intent(RegisterActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        super.onStart();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(RegisterActivity.this, "fadein-to-fadeout");
    }
}
