package com.Intrahubproject.intrahub;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.se.omapi.SEService;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class MusicBottomSheedDialog extends BottomSheetDialogFragment {

    private FirebaseAuth Mauth;
    private DatabaseReference Mmusicdatabase;
    private String CurrentUserID;
    private String uri;
    private Boolean isplaying;
    private Handler handler = new Handler();
    private SeekBar playsikbar;
    private TextView textcurrenttime, texttotoltime;
    private ImageView playpush;
    private MediaPlayer mediaPlayer;
    private ProgressDialog Mprogress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.music_bottomsheed_dioloag, container, false);



        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();
        Mmusicdatabase = FirebaseDatabase.getInstance().getReference().child("Music").child(CurrentUserID);

        Mmusicdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("myaudio")){
                        uri = dataSnapshot.child("myaudio").getValue().toString();

                        if(!uri.isEmpty()){

                            prepairmedaplayer(uri);

                        }
                        else {

                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        textcurrenttime = view.findViewById(R.id.StartTimerID);
        texttotoltime = view.findViewById(R.id.EndTimerID);
        playsikbar = view.findViewById(R.id.SeekbarID);
          playpush = view.findViewById(R.id.PlaypausIconID);

          mediaPlayer = new MediaPlayer();


          mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
              @Override
              public boolean onInfo(MediaPlayer mp, int what, int extra) {

                  if(what == mp.MEDIA_INFO_BUFFERING_START){
                      playpush.setEnabled(false);
                      Toasty.error(getContext(), "buffering", Toasty.LENGTH_LONG).show();
                  }
                  else if(what == mp.MEDIA_INFO_BUFFERING_END){
                      playpush.setEnabled(true);
                      Toasty.error(getContext(), "not buffering", Toasty.LENGTH_LONG).show();
                  }

                  return false;
              }
          });







        playsikbar.setMax(100);

        playpush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    playpush.setImageResource(R.drawable.playicon_color);
                }
                else {
                    mediaPlayer.start();
                    playpush.setImageResource(R.drawable.pushcolour);
                    updatesikbar();
                }



            }
        });


        playsikbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

              SeekBar seekBar = (SeekBar) v;
              int playposition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
              mediaPlayer.seekTo(playposition);
              textcurrenttime.setText(milisecondtimer(mediaPlayer.getCurrentPosition()));

                return false;
            }
        });

        return view;
    }



    private void prepairmedaplayer(String myuri){

        if(!myuri.isEmpty()){
            try {
                mediaPlayer.setDataSource(myuri);
                mediaPlayer.prepare();
                texttotoltime.setText(milisecondtimer(mediaPlayer.getDuration()));
            }
            catch (Exception e){
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        }


    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updatesikbar();
            long currentDuraction = mediaPlayer.getDuration();
            textcurrenttime.setText(milisecondtimer(currentDuraction));
        }
    };

    private void updatesikbar(){
        if(mediaPlayer.isPlaying()){
            playsikbar.setProgress((int) (((float)mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));

            handler.postDelayed(updater, 1000);
        }
    }

    private String milisecondtimer(long miliscond){
        String timerString = "";
        String secondString;

        int hours = (int)(miliscond / (1000 * 60 * 60));
        int minitus =  (int)(miliscond % (1000 * 60 * 60)) / (100 * 60);
        int second = (int) ((miliscond % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if(hours > 0){
            timerString = hours + ":";
        }
        if(second < 10){
            secondString = ""+second;
        }
        else {
            secondString = ""+second;
        }

        timerString = timerString + minitus + ":"+secondString;

        return  timerString;
    }


    @Override
    public void onStop() {
        mediaPlayer.stop();
        super.onStop();
    }
}
