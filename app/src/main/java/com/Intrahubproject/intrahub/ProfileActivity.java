package com.Intrahubproject.intrahub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private RelativeLayout submitbutton;
    private TextInputLayout inputprofilename;
    private CircleImageView profiliage;
    private Uri imageuri = null;
    private String ImageDownloadUri;
    private StorageReference MfileStores;
    private DatabaseReference MuserDatabase;
    private DatabaseReference MnotifactionUsers;
    private ProgressDialog Mprogress;
    private ProgressDialog MprogressPhoto;

    private int ShortMessage;
    private int negativeValShort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        MprogressPhoto = new ProgressDialog(ProfileActivity.this);

        MnotifactionUsers = FirebaseDatabase.getInstance().getReference().child("NotifactionUSsers");
        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(ProfileActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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




        Mprogress = new ProgressDialog(ProfileActivity.this);
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MuserDatabase.keepSynced(true);
        MfileStores = FirebaseStorage.getInstance().getReference().child("Profileimage");
        profiliage = findViewById(R.id.ProfilePicID);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();
        inputprofilename = findViewById(R.id.ProfileInputID);


        //short user data
        MuserDatabase.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            ShortMessage = (int) dataSnapshot.getChildrenCount();
                            negativeValShort = (~(ShortMessage - 1));
                        }
                        else {
                            negativeValShort = 0;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        //short user data


        profiliage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileActivity.this);
            }
        });

        submitbutton = findViewById(R.id.SubmitButtonID);
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputprofilename.getEditText().getText().toString();

                if(username.isEmpty()){
                    inputprofilename.getEditText().setError("username require");
                }
                else {
                    Mprogress.setCanceledOnTouchOutside(false);
                    Mprogress.setMessage("setup your profile");
                    Mprogress.setTitle("Please wait ...");
                    Mprogress.show();
                    Map<String, Object> usermap = new HashMap<>();
                    usermap.put("username", username);
                    usermap.put("imageuri", ImageDownloadUri);
                    usermap.put("myid", CurrentUserID);
                    usermap.put("devices_token", FirebaseInstanceId.getInstance().getToken().toString());
                    usermap.put("adminstatas", "online");
                    usermap.put("search", username.toLowerCase());
                    usermap.put("short", negativeValShort);


                    MnotifactionUsers.child(CurrentUserID).updateChildren(usermap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                }
                            });


                    MuserDatabase.child(CurrentUserID).updateChildren(usermap)
                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               Mprogress.dismiss();
                               Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                               startActivity(intent);
                               CustomIntent.customType(ProfileActivity.this, "fadein-to-fadeout");
                               finish();
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
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                MprogressPhoto.setTitle("Uploading ...");
                MprogressPhoto.setMessage("please wait uploading your photo");
                MprogressPhoto.setCanceledOnTouchOutside(false);
                MprogressPhoto.show();

                imageuri = result.getUri();
                profiliage.setImageURI(imageuri);

                StorageReference filepath = MfileStores.child(imageuri.getLastPathSegment());
                filepath.putFile(imageuri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    MprogressPhoto.dismiss();
                                    ImageDownloadUri = task.getResult().getDownloadUrl().toString();
                                }
                                else {
                                    MprogressPhoto.dismiss();
                                    Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                MprogressPhoto.dismiss();
                                Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                            }
                        });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                MprogressPhoto.dismiss();
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(ProfileActivity.this, "fadein-to-fadeout");

    }



}
