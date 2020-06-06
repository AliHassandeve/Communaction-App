package com.Intrahubproject.intrahub;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;


public class VideoPlayerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String Videouri, videotime, videodate;
    private MaterialTextView videoplaytimeanddate;
    private VideoView videoView;
    private ProgressBar bufferprogress;
    private ImageView plaorpushbutton;
    private Boolean isplaying;

    private int current = 0;
    private int duraction = 0;

    private MaterialTextView starttime, endtime;
    private ProgressBar currentprogress;

    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private DatabaseReference MuserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);



        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){


            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(VideoPlayerActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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





        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        currentprogress = findViewById(R.id.VideoProgressiD);
        currentprogress.setMax(100);
        starttime = findViewById(R.id.ProgressVideoTimerID);
        endtime = findViewById(R.id.VideoDuractionTimerID);

        plaorpushbutton =findViewById(R.id.ViewPlayButtonID);
        isplaying = false;
        bufferprogress = findViewById(R.id.BufferProgressbarID);
        bufferprogress.setVisibility(View.INVISIBLE);
        videoView = findViewById(R.id.ViewViewID);
        videoplaytimeanddate = findViewById(R.id.VideoDateandtime);
        toolbar = findViewById(R.id.VidePlayerToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backicon);

        Videouri = getIntent().getStringExtra("VIDELINK");
        videotime = getIntent().getStringExtra("VIDEOTIME");
        videodate = getIntent().getStringExtra("VIDEODATE");

        videoplaytimeanddate.setText(videotime+" | "+videodate);


        videoView.setVideoURI(Uri.parse(Videouri));
        videoView.requestFocus();

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        bufferprogress.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        bufferprogress.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duraction = mp.getDuration() / 1000;
                String duractionstring = String.format("%02d:%02d", duraction / 60, duraction % 60);
                endtime.setText(duractionstring);
            }
        });

        videoView.start();
        isplaying = true;
        plaorpushbutton.setImageResource(R.drawable.paushicon);

        new Videoprogress().execute();

        plaorpushbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isplaying){
                    videoView.pause();
                    isplaying = false;
                    plaorpushbutton.setImageResource(R.drawable.playbutonwhiteicon);
                }
                else {
                    videoView.start();
                    isplaying = true;
                    plaorpushbutton.setImageResource(R.drawable.paushicon);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        MuserDatabase.child(CurrentUserID).child("adminstatas").setValue("online");
        super.onStart();
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            isplaying = false;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(VideoPlayerActivity.this, "fadein-to-fadeout");

    }

   /* @Override
    protected void onStart() {
        super.onStart();
        isplaying = false;
    }*/

    @Override
    protected void onStop() {
        MuserDatabase.child(CurrentUserID).child("adminstatas").setValue("offline");
        super.onStop();
        isplaying = false;
    }

    @Override
    protected void onDestroy() {
        MuserDatabase.child(CurrentUserID).child("adminstatas").setValue("offline");
        super.onDestroy();
    }

    public class Videoprogress extends AsyncTask<Void, Integer, Void>{

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {

            do {

                if(isplaying){
                    current = videoView.getCurrentPosition()/1000;
                    publishProgress(current);
                }




                 //   int currentPrecent = current * 100/duraction;






            }while (currentprogress.getProgress() <= 100);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            try {

                int currentpresent = values[0] * 100/duraction;
                currentprogress.setProgress(currentpresent);

                String currentString = String.format("%02d:%02d", values[0]/60, values[0] % 60);
                starttime.setText(currentString);

            }catch (Exception e){

            }

            super.onProgressUpdate(values);


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isplaying = false;
    }
}
