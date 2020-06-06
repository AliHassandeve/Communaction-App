
package com.Intrahubproject.intrahub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RecodActivity extends AppCompatActivity {


    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private Button button;
    private MediaRecorder mRecorder = null;
    private String mFileName = null;
    private String CurentTime;
    private StorageReference MaudioStores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recod);

        button = findViewById(R.id.buttonid);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();



        MaudioStores = FirebaseStorage.getInstance().getReference();


        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    Calendar calendartime = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm ss");
                    CurentTime = simpleDateFormat.format(calendartime.getTime());

                    mFileName += "/AudioRecording" +CurentTime + ".3gp";
                    Startreacoding();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    stopAudio();
                }

                return false;
            }
        });




    }












    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(RecodActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }



    private void Startreacoding() {

      /*  try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(mFileName);

            mRecorder.prepare();
            mRecorder.start();
            Toast.makeText(getApplicationContext(), "starting", Toast.LENGTH_LONG).show();
        }catch (IllegalStateException e) {
            Log.e("REDORDING :: ",e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("REDORDING :: ",e.getMessage());
            e.printStackTrace();
        }*/



        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        try {
            mRecorder.prepare();
            mRecorder.start();
            Toast.makeText(getApplicationContext(), "starting", Toast.LENGTH_LONG).show();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }




    }

    public void stopAudio() {

       /* try {

            mRecorder.stop();
            mRecorder.release();
            Toast.makeText(getApplicationContext(), "stop", Toast.LENGTH_LONG).show();

            saving_theuri();
            mRecorder = null;
        } catch (RuntimeException e) {

        }*/

        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        saving_theuri();
        Toast.makeText(getApplicationContext(), "stop", Toast.LENGTH_LONG).show();

    }


    private void saving_theuri(){

        Uri uri = Uri.fromFile(new File(mFileName));
        Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_LONG).show();


        StorageReference filepath = MaudioStores.child("SampleAudio").child(uri.getLastPathSegment());
        filepath.putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();

                        }
                        else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });



    }
}
