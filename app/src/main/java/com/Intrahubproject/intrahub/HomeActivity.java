package com.Intrahubproject.intrahub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.Intrahubproject.intrahub.Model.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class HomeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private Pageheler pageheler;
    private FirebaseAuth Mauth;
    private DatabaseReference Muserdatabase;
    private String CurrentusrID, imageget;
    private CircleImageView profileimage;
    private DatabaseReference MreaduserDatabase;
    private DatabaseReference MOnlineStatasDatabase;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);





        relativeLayout = findViewById(R.id.RelativeLayoutID);
        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings

            Snackbar snackbar =  Snackbar.make(relativeLayout, "you are online",Snackbar.LENGTH_SHORT);

            View view = snackbar.getView();
             view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            snackbar.show();
        }
        else {
            final Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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





        MOnlineStatasDatabase = FirebaseDatabase.getInstance().getReference().child("state");


        profileimage = findViewById(R.id.ProfileImageID);

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        Muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users");



        Muserdatabase.keepSynced(true);
        Mauth = FirebaseAuth.getInstance();
        CurrentusrID = Mauth.getCurrentUser().getUid();

        MreaduserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentusrID);
        MreaduserDatabase.keepSynced(true);

        MreaduserDatabase.child("adminstatas").setValue("online");

        Muserdatabase.child(CurrentusrID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild("imageuri")){
                                 imageget = dataSnapshot.child("imageuri").getValue().toString();

                                Picasso.with(getApplicationContext()).load(imageget).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.defaultimeg).into(profileimage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(getApplicationContext()).load(imageget).placeholder(R.drawable.defaultimeg).into(profileimage);

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        tabLayout = findViewById(R.id.TabLayoutID);
        toolbar = findViewById(R.id.HomeToolbarID);
        viewPager = findViewById(R.id.ViewPagerID);
        setSupportActionBar(toolbar);
        pageheler = new Pageheler(getSupportFragmentManager());
        viewPager.setAdapter(pageheler);
        tabLayout.setupWithViewPager(viewPager);

        Muserdatabase.child(CurrentusrID).child("typestatas").setValue("notyping...");
        fcm_token();

        onlineStatas("online");

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbarmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.LogOutID){
            Muserdatabase.child(CurrentusrID).child("adminstatas").setValue("offline");

            Mauth.signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
        }

        if(item.getItemId() == R.id.SearchID){
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            CustomIntent.customType(HomeActivity.this, "bottom-to-up");
        }
        if(item.getItemId() == R.id.ProfileID){
            Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
        }





        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
    }

    @Override
    protected void onStart() {
        MreaduserDatabase.child("adminstatas").setValue("online");
        onlineStatas("online");
        FirebaseUser Muser = Mauth.getCurrentUser();
        if(Muser == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
        }
        else {
            MreaduserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild("username")){
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        super.onStart();
    }


    private void fcm_token() {



        String refreshedToken = FirebaseInstanceId.getInstance().getToken();


        //  if (refreshedToken!="")



        Log.i("TAG", "fcm_token: "+refreshedToken);


        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference referance=db.getReference("Token");
        Token token=new Token(refreshedToken,true);
        referance.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);


    }

    @Override
    protected void onStop() {
        MreaduserDatabase.child("adminstatas").setValue("offline");
        onlineStatas("offline");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        MreaduserDatabase.child("adminstatas").setValue("offline");
        onlineStatas("offline");
        super.onDestroy();
    }



    private void onlineStatas(String statas){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
        String OnlineTime =  simpleDateFormattime.format(calendar.getTime());

        Map onlinemap = new HashMap();
        onlinemap.put("statas", statas);
        onlinemap.put("last_seen", OnlineTime);


        MOnlineStatasDatabase.child(CurrentusrID).updateChildren(onlinemap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){

                        }
                        else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }



}
