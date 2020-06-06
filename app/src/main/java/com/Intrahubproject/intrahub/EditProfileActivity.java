package com.Intrahubproject.intrahub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class EditProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference MuserDatabase;
    private StorageReference MprofileStores;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private MaterialTextView username;
    private CircleImageView profileimage;
    private FloatingActionButton submitbutton;
    private TextInputLayout nameinput;
    private Uri imageuri;
    private String ImageDownloadUri, imageuriget;

    private ProgressDialog Mprogress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        Mprogress = new ProgressDialog(EditProfileActivity.this);

        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(EditProfileActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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





        profileimage = findViewById(R.id.UserProfileImageID);
        MprofileStores = FirebaseStorage.getInstance().getReference();
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(EditProfileActivity.this);
            }
        });

        nameinput = findViewById(R.id.UserProfileNameID);
        submitbutton = findViewById(R.id.SubmitButtonID);

        username = findViewById(R.id.ProfileusernameID);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.ProToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backicon);

        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MuserDatabase.keepSynced(true);
        MuserDatabase.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild("username")){
                                String usernameget = dataSnapshot.child("username").getValue().toString();
                                username.setText(usernameget+"'s Profile");
                                nameinput.getEditText().setText(usernameget);
                            }
                            if(dataSnapshot.hasChild("imageuri")){
                                imageuriget  = dataSnapshot.child("imageuri").getValue().toString();

                                Picasso.with(getApplicationContext()).load(imageuriget).networkPolicy(NetworkPolicy.OFFLINE).into(profileimage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(getApplicationContext()).load(imageuriget).placeholder(R.drawable.defaultimeg).into(profileimage);

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameinput.getEditText().getText().toString();
                nameinput.getEditText().setText("");
                if(name.isEmpty()){
                    Toasty.info(getApplicationContext(), "empty", Toasty.LENGTH_LONG).show();
                }
                else {
                    MuserDatabase.child(CurrentUserID).child("username").setValue(name);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        CustomIntent.customType(EditProfileActivity.this, "fadein-to-fadeout");
        super.finish();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Mprogress.setTitle("Uploading ...");
                Mprogress.setMessage("please wait your photo is uploading");
                Mprogress.setCanceledOnTouchOutside(false);
                Mprogress.show();

                imageuri = result.getUri();
                profileimage.setImageURI(imageuri);

                StorageReference filepath = MprofileStores.child("profileimageupdate").child(imageuri.getLastPathSegment());
                filepath.putFile(imageuri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    Mprogress.dismiss();
                                    ImageDownloadUri = task.getResult().getDownloadUrl().toString();

                                    MuserDatabase.child(CurrentUserID).child("imageuri").setValue(ImageDownloadUri);

                                }
                                else {
                                    Mprogress.dismiss();
                                    Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Mprogress.dismiss();
                                Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                            }
                        });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Mprogress.dismiss();
                Exception error = result.getError();
            }
        }
    }

}
