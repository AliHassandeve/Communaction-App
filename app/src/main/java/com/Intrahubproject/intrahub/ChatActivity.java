package com.Intrahubproject.intrahub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Intrahubproject.intrahub.Common.Common;
import com.Intrahubproject.intrahub.Model.Myresponce;
import com.Intrahubproject.intrahub.Model.Notification;
import com.Intrahubproject.intrahub.Model.Sender;

import com.Intrahubproject.intrahub.Model.Token;
import com.Intrahubproject.intrahub.Remote.APIservice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;
import retrofit2.Call;
import retrofit2.Response;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;



public class ChatActivity extends AppCompatActivity {


    private String CurentTime;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private MediaRecorder mRecorder;
    private String AudioDownloader;
    private String mFileName = null;
    private String output_file;
    private int Counter;


    private Toolbar toolbar;
    private String ReciverID;
    private DatabaseReference MuserDatabase;
    private CircleImageView friendprofileimage;
    private MaterialTextView friensname;
    private EditText inputmessage;
    private ImageView sendbutton;
    private ImageView opendrawerbutton;
    private String openposition = "notopen";
    private RelativeLayout buttondrawer;
    private String messagetype ;
    private String SenderID;
    private FirebaseAuth Mauth;
    private DatabaseReference Mroodref;
    private String CurrentTime, CurrentDate;

    private RecyclerView messageview;
    private MessageAdapter messageAdapter;
    private List<MessageHolder> usermesssagelist = new ArrayList<>();
    private DatabaseReference Messageref;
    private MaterialTextView userstatas;
    private LinearLayout photosbuttonID, pdfbutton, docbutton;
    private StorageReference ImageStores;
    private String imagedownloadurl;
    private StorageReference MpdfRef;
    private String PdfDownloaduri;
    private StorageReference MdocStores;
    private String imageuriget, messagetext, usernameget;
    private APIservice mService;

    private DatabaseReference MmessageDatabase;
    private FloatingActionButton submitbutton;
    private LinearLayout audiobutton;

    private LinearLayout videobuttonID;

    /// user info
    private String myname,myimage;
    private StorageReference audiofiledata;
    private StorageReference MvideStores;
    /// user info

    private int ShortMessage = 0;
    private int negativeValShort;
    private DatabaseReference MOnlineStatasDatabase;
    private TextView isonlinetext;

    private ProgressBar recoderprogressbar;

    private Boolean issend = false;
    private RelativeLayout AudioButton;
    private ImageView voiceicon;

    /// audio recoder
    private MediaRecorder mediaRecorder;
    private StorageReference MrecoderFile;
    private String fileName = null;
    private ProgressDialog Mprogtrssbar;

    /// audio recoder


    /// camera button
    private RelativeLayout camerabutton;
    private Uri imageuri = null;
    private int TAKE_PICTURE = 0;
    private StorageReference Mcamerastores;
    /// camera button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        Mcamerastores = FirebaseStorage.getInstance().getReference().child("Camera");
        camerabutton = findViewById(R.id.CameraButtonID);
        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ChatActivity.this);
            }
        });


        Mprogtrssbar = new ProgressDialog(ChatActivity.this);
        mRecorder = new MediaRecorder();
        MrecoderFile = FirebaseStorage.getInstance().getReference();
        voiceicon = findViewById(R.id.AAudioIconID);
        AudioButton = findViewById(R.id.AudioLayoutID);
        recoderprogressbar = findViewById(R.id.RecoderProgressbarID);
        recoderprogressbar.setVisibility(View.INVISIBLE);


        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/recode_audio.3gp";

        /// reacoder
        /////
        output_file = Environment.getExternalStorageState() + "/audiorecorder.3gp";
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();



        /////
        /// reacoder

        ///cheack connection

        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

         final  boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(ChatActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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


        ///cheack connection





        MOnlineStatasDatabase = FirebaseDatabase.getInstance().getReference().child("state");

        isonlinetext = findViewById(R.id.UserstatasID);

        videobuttonID = findViewById(R.id.ViewButtonID);
        MvideStores = FirebaseStorage.getInstance().getReference();
        videobuttonID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), 515 );
            }
        });

        audiofiledata = FirebaseStorage.getInstance().getReference();
        audiobutton = findViewById(R.id.AudioButtonID);

        audiobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Select Audio"), 514
                );
            }
        });

        submitbutton = findViewById(R.id.SbuttonID);
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        MmessageDatabase = FirebaseDatabase.getInstance().getReference().child("MessageNotifaction");
        mService= Common.getFCMClient();
        MdocStores = FirebaseStorage.getInstance().getReference();
        docbutton = findViewById(R.id.DocumtnButtonID);
        docbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/msword");
                startActivityForResult(Intent.createChooser(intent, "select DOC file"), 513);
            }
        });

        MpdfRef = FirebaseStorage.getInstance().getReference();
        pdfbutton = findViewById(R.id.PdfButtonID);
        pdfbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(Intent.createChooser(intent, "select PDF file"), 512);
            }
        });
        ImageStores = FirebaseStorage.getInstance().getReference();
        photosbuttonID = findViewById(R.id.PhotosButtonID);
        photosbuttonID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(Intent.ACTION_PICK);
               intent.setType("image/*");
               startActivityForResult(intent, 511);
            }
        });

        userstatas = findViewById(R.id.UserstatasID);

        Messageref = FirebaseDatabase.getInstance().getReference();
        Messageref.keepSynced(true);
        messageview = findViewById(R.id.MessageRecylerViewID);
        messageview.setHasFixedSize(true);
        messageview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        messageAdapter = new MessageAdapter(usermesssagelist);
        messageview.setAdapter(messageAdapter);

        Mroodref = FirebaseDatabase.getInstance().getReference();
        Mroodref.keepSynced(true);
        Mauth = FirebaseAuth.getInstance();
        SenderID = Mauth.getCurrentUser().getUid();

        buttondrawer = findViewById(R.id.ButtonLayoutIDd);
        opendrawerbutton = findViewById(R.id.PlusIconID);
        buttondrawer.setVisibility(View.GONE);
        opendrawerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(openposition.equals("notopen")){
                    buttondrawer.setVisibility(View.GONE);
                    openposition = "open";
                    opendrawerbutton.setImageResource(R.drawable.plusicon);
                }
                else if(openposition.equals("open")){
                    buttondrawer.setVisibility(View.VISIBLE);
                    openposition = "notopen";
                    opendrawerbutton.setImageResource(R.drawable.closeicon);
                }

              //  buttondrawer.setVisibility(View.GONE);
            }
        });

        inputmessage = findViewById(R.id.InputMessageID);
        sendbutton = findViewById(R.id.SendiconID);
        friendprofileimage = findViewById(R.id.ChatProfileImageID);
        friensname = findViewById(R.id.ChatUsernameID);

        ReciverID = getIntent().getStringExtra("KEY");
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MuserDatabase.keepSynced(true);
        toolbar = findViewById(R.id.ChatToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backicon);



        /// get my info
        MuserDatabase.child(SenderID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                                if(dataSnapshot.hasChild("imageuri")){
                                    myimage = dataSnapshot.child("imageuri").getValue().toString();
                                }
                                if(dataSnapshot.hasChild("username")){
                                    myname = dataSnapshot.child("username").getValue().toString();
                                }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        /// get my info


        ///typing statas
        MuserDatabase.child(ReciverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("typestatas")){
                        String typestatasget = dataSnapshot.child("typestatas").getValue().toString();
                        if(typestatasget.equals("typing...")){
                            userstatas.setText("typing...");
                        }
                        else if(typestatasget.equals("notyping...")){
                            userstatas.setText("online");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ///typing statas


        MuserDatabase.child(ReciverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild("imageuri")){
                                 imageuriget = dataSnapshot.child("imageuri").getValue().toString();
                                Picasso.with(getApplicationContext()).load(imageuriget).networkPolicy(NetworkPolicy.OFFLINE).into(friendprofileimage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(getApplicationContext()).load(imageuriget).placeholder(R.drawable.defaultimeg).into(friendprofileimage);

                                    }
                                });
                            }
                            if(dataSnapshot.hasChild("username")){
                                 usernameget = dataSnapshot.child("username").getValue().toString();
                                friensname.setText(usernameget);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        inputmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttondrawer.setVisibility(View.GONE);
                opendrawerbutton.setImageResource(R.drawable.plusicon);
            }
        });




       /* inputmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                final String text = s.toString();
                if(text.isEmpty()){

                    MuserDatabase.child(SenderID).child("typestatas").setValue("notyping...");

                    sendbutton.setImageResource(R.drawable.voiceicon);

                    sendbutton.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            if(event.getAction() == MotionEvent.ACTION_DOWN){
                                recoderprogressbar.setVisibility(View.VISIBLE);
                            }
                             if(event.getAction() == MotionEvent.ACTION_UP){
                                 recoderprogressbar.setVisibility(View.INVISIBLE);
                             }

                            return false;
                        }
                    });
                }
                else  if(!text.isEmpty()){


                    text = null;
              //     Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

                    MuserDatabase.child(SenderID).child("typestatas").setValue("typing...");
                    sendbutton.setImageResource(R.drawable.sendicon);
                    sendbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notifactionhstory();
                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                            send_text_messaeg(text);


                        }
                    });
                }
            }
        });*/


       /// audio click
        /*AudioButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    recoderprogressbar.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "down", Toast.LENGTH_LONG).show();
                    AudioButton.setBackgroundResource(R.drawable.click_background_audio);
                    voiceicon.setImageResource(R.drawable.voicewhiteicon);
                }

               else if(event.getAction() == MotionEvent.ACTION_UP){
                    recoderprogressbar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "up", Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });*/



        // audioclick
        AudioButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {





                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    recoderprogressbar.setVisibility(View.VISIBLE);
                    AudioButton.setBackgroundResource(R.drawable.click_background_audio);
                    AudioButton.setVisibility(View.GONE);
                    voiceicon.setImageResource(R.drawable.voicewhiteicon);
                    voiceicon.setVisibility(View.GONE);


                    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){

                        if(CheckPermissions()) {


                            Calendar calendartime = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm ss");
                            CurentTime = simpleDateFormat.format(calendartime.getTime());


                            mFileName += "/AudioRecording" + CurentTime + ".3gp";
                            Startreacoding();

                        }
                    }

                    else if(CheckPermissions()) {
                        Startreacoding();
                    }


                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    stopAudio();
                    recoderprogressbar.setVisibility(View.INVISIBLE);
                    AudioButton.setBackgroundResource(R.drawable.unclick_voicebackground);
                    AudioButton.setVisibility(View.GONE);
                    voiceicon.setImageResource(R.drawable.voiceicon);
                    voiceicon.setVisibility(View.GONE);
                    voiceicon.setVisibility(View.GONE);
                }
                else {
                    RequestPermissions();
                }

                return true;
            }
        });


    /* AudioButton.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {

             if(event.getAction() == MotionEvent.ACTION_DOWN){
                 start();
             }
             else if(event.getAction() == MotionEvent.ACTION_UP){
                 startstoping();
             }

             return true;
         }
     });*/

        /// audio click

        AudioButton.setVisibility(View.GONE);
     inputmessage.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {

         }







    /// hear input message

         @Override
         public void afterTextChanged(final Editable s) {

             String message = s.toString();
             if(message.isEmpty()){
                 AudioButton.setVisibility(View.GONE);
                 buttondrawer.setVisibility(View.GONE);
                 openposition = "open";
                 opendrawerbutton.setImageResource(R.drawable.plusicon);
                 MuserDatabase.child(SenderID).child("typestatas").setValue("notyping...");

             }
             else {
                 AudioButton.setVisibility(View.GONE);
                 buttondrawer.setVisibility(View.GONE);
                 openposition = "open";
                 opendrawerbutton.setImageResource(R.drawable.plusicon);
                 MuserDatabase.child(SenderID).child("typestatas").setValue("typing...");
                 AudioButton.setVisibility(View.GONE);
             }
         }
     });


     sendbutton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String messagetext = inputmessage.getText().toString();

             if(messagetext.isEmpty()){
                 Toasty.info(getApplicationContext(), "Please enter message", Toasty.LENGTH_LONG).show();
             }
             else {
                 notifactionhstory();
                 send_text_messaeg(messagetext);
             }
         }
     });

        readingMessage();





        /// reading message history
        MmessageDatabase.child(ReciverID)
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
        /// reading message history

        readuseronline();

        onlineStatas("online");
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
        super.finish();
        CustomIntent.customType(ChatActivity.this, "fadein-to-fadeout");

    }

    private void send_text_messaeg(final String message){


        String message_sender_ref = "Message/"+SenderID+"/"+ReciverID;
        String message_reciver_ref = "Message/"+ReciverID+"/"+SenderID;

        DatabaseReference usermessage_key = Mroodref.child("Message").child(SenderID).child(ReciverID).push();

        String messagepushid = usermessage_key.getKey();

        Calendar calendartime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
        CurrentTime = simpleDateFormattime.format(calendartime.getTime());

        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
        CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

        Map<String, Object> messagemap = new HashMap<String, Object>();
        messagemap.put("message", message);
        messagemap.put("time", CurrentTime);
        messagemap.put("date", CurrentDate);
        messagemap.put("from", SenderID);
        messagemap.put("type", "text");

        Map<String, Object> messagebody = new HashMap<String, Object>();
        messagebody.put(message_sender_ref+"/"+messagepushid, messagemap);
        messagebody.put(message_reciver_ref+"/"+messagepushid, messagemap);



        Mroodref.updateChildren(messagebody).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Log.d("TAG", "click");

                    find_username_and_send_notification(message);

                    MuserDatabase.child(SenderID).child("usermessage").setValue(message);
                    MuserDatabase.child(SenderID).child("usermessagetime").setValue(CurrentTime);





                }
                else {
                    Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                    }
                });

        inputmessage.setText("");
    }




    protected void readingMessage(){
        Messageref.child("Message").child(SenderID).child(ReciverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        MessageHolder message = dataSnapshot.getValue(MessageHolder.class);
                        usermesssagelist.add(message);
                        messageAdapter.notifyDataSetChanged();
                        messageview.smoothScrollToPosition(messageAdapter.getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void savetypeingstats(){
        MuserDatabase.child(SenderID).child("typestatas").setValue("typing...");
    }

    private void savenotypeingstatas(){
        MuserDatabase.child(SenderID).child("typestatas").setValue("notyping...");
    }


    @Override
    protected void onStop() {
        MuserDatabase.child(SenderID).child("typestatas").setValue("notyping...");
        MuserDatabase.child(SenderID).child("adminstatas").setValue("offline");
        onlineStatas("offline");
        super.onStop();
    }



    @Override
    protected void onRestart() {
        MuserDatabase.child(SenderID).child("typestatas").setValue("notyping...");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        MuserDatabase.child(SenderID).child("typestatas").setValue("notyping...");
        MuserDatabase.child(SenderID).child("adminstatas").setValue("offline");
        onlineStatas("offline");
        super.onDestroy();
    }


    /*
    /// image get
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Toasty.error(getApplicationContext(), "ok", Toasty.LENGTH_LONG).show();


                Uri imuri = data.getData();
                Toast.makeText(getApplicationContext(), imuri.toString(), Toast.LENGTH_LONG).show();
              StorageReference filepath = ImageStores.child("ImageFile").child(imuri.getLastPathSegment());
                filepath.putFile(imuri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    imagedownloadurl = task.getResult().getDownloadUrl().toString();



                                    String message_sender_ref = "Message/"+SenderID+"/"+ReciverID;
                                    String message_reciver_ref = "Message/"+ReciverID+"/"+SenderID;

                                    DatabaseReference usermessage_key = Mroodref.child("Message").child(SenderID).child(ReciverID).push();

                                    String messagepushid = usermessage_key.getKey();

                                    Calendar calendartime = Calendar.getInstance();
                                    SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
                                    CurrentTime = simpleDateFormattime.format(calendartime.getTime());

                                    Calendar calendardate = Calendar.getInstance();
                                    SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
                                    CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

                                    Map<String, Object> messagemap = new HashMap<String, Object>();
                                    messagemap.put("message", imagedownloadurl);
                                    messagemap.put("time", CurrentTime);
                                    messagemap.put("date", CurrentDate);
                                    messagemap.put("from", SenderID);
                                    messagemap.put("type", "image");

                                    Map<String, Object> messagebody = new HashMap<String, Object>();
                                    messagebody.put(message_sender_ref+"/"+messagepushid, messagemap);
                                    messagebody.put(message_reciver_ref+"/"+messagepushid, messagemap);



                                    Mroodref.updateChildren(messagebody).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){


                                            }
                                            else {
                                                Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                                                }
                                            });




                                }
                                else {
                                    Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                            }
                        });



            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
*/
   @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 511 && resultCode == RESULT_OK){
            Mprogtrssbar.setCanceledOnTouchOutside(false);
            Mprogtrssbar.setMessage("Please wait while uploading photo");
            Mprogtrssbar.setTitle("Uploading ...");
            Mprogtrssbar.show();
            buttondrawer.setVisibility(View.GONE);
            opendrawerbutton.setImageResource(R.drawable.plusicon);

            Uri imageuri = data.getData();



            StorageReference filepath = ImageStores.child("ImageFile").child(imageuri.getLastPathSegment());
                filepath.putFile(imageuri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    imagedownloadurl = task.getResult().getDownloadUrl().toString();



                                    String message_sender_ref = "Message/"+SenderID+"/"+ReciverID;
                                    String message_reciver_ref = "Message/"+ReciverID+"/"+SenderID;

                                    DatabaseReference usermessage_key = Mroodref.child("Message").child(SenderID).child(ReciverID).push();

                                    String messagepushid = usermessage_key.getKey();

                                    Calendar calendartime = Calendar.getInstance();
                                    SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
                                    CurrentTime = simpleDateFormattime.format(calendartime.getTime());

                                    Calendar calendardate = Calendar.getInstance();
                                    SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
                                    CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

                                    Map<String, Object> messagemap = new HashMap<String, Object>();
                                    messagemap.put("message", imagedownloadurl);
                                    messagemap.put("time", CurrentTime);
                                    messagemap.put("date", CurrentDate);
                                    messagemap.put("from", SenderID);
                                    messagemap.put("type", "image");

                                    Map<String, Object> messagebody = new HashMap<String, Object>();
                                    messagebody.put(message_sender_ref+"/"+messagepushid, messagemap);
                                    messagebody.put(message_reciver_ref+"/"+messagepushid, messagemap);



                                    Mroodref.updateChildren(messagebody).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Mprogtrssbar.dismiss();
                                                notifactionimage_sending();

                                                imagenotifactnhistory();
                                                MuserDatabase.child(SenderID).child("usermessage").setValue("send a image");
                                                MuserDatabase.child(SenderID).child("usermessagetime").setValue(CurrentTime);

                                            }
                                            else {
                                                Mprogtrssbar.dismiss();
                                                Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Mprogtrssbar.dismiss();
                                                    Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                                                }
                                            });




                                }
                                else {
                                    Mprogtrssbar.dismiss();
                                    Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                }
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Mprogtrssbar.dismiss();
                                Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                            }
                        });




        }

        if(requestCode == 512 && resultCode == RESULT_OK){
            Mprogtrssbar.setCanceledOnTouchOutside(false);
            Mprogtrssbar.setMessage("Please wait while uploading pdf");
            Mprogtrssbar.setTitle("Uploading ...");
            Mprogtrssbar.show();
            buttondrawer.setVisibility(View.GONE);
            opendrawerbutton.setImageResource(R.drawable.plusicon);

            Uri pdfuri = data.getData();
            buttondrawer.setVisibility(View.GONE);
            opendrawerbutton.setImageResource(R.drawable.plusicon);

            StorageReference filepath = MpdfRef.child("pdf").child(pdfuri.getLastPathSegment());
            filepath.putFile(pdfuri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                PdfDownloaduri = task.getResult().getDownloadUrl().toString();



                                String message_sender_ref = "Message/"+SenderID+"/"+ReciverID;
                                String message_reciver_ref = "Message/"+ReciverID+"/"+SenderID;

                                DatabaseReference usermessage_key = Mroodref.child("Message").child(SenderID).child(ReciverID).push();

                                String messagepushid = usermessage_key.getKey();

                                Calendar calendartime = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
                                CurrentTime = simpleDateFormattime.format(calendartime.getTime());

                                Calendar calendardate = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
                                CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

                                Map<String, Object> messagemap = new HashMap<String, Object>();
                                messagemap.put("message", PdfDownloaduri);
                                messagemap.put("time", CurrentTime);
                                messagemap.put("date", CurrentDate);
                                messagemap.put("from", SenderID);
                                messagemap.put("type", "pdf");

                                Map<String, Object> messagebody = new HashMap<String, Object>();
                                messagebody.put(message_sender_ref+"/"+messagepushid, messagemap);
                                messagebody.put(message_reciver_ref+"/"+messagepushid, messagemap);



                                Mroodref.updateChildren(messagebody)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    Mprogtrssbar.dismiss();
                                                    notifaction_pdf_sending();
                                                    pdfnotifactionhstory();

                                                    MuserDatabase.child(SenderID).child("usermessage").setValue("send a pdf");
                                                    MuserDatabase.child(SenderID).child("usermessagetime").setValue(CurrentTime);
                                                }
                                                else {
                                                    Mprogtrssbar.dismiss();
                                                    Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Mprogtrssbar.dismiss();
                                                Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                                            }
                                        });

                            }
                            else {
                                Mprogtrssbar.dismiss();
                                Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Mprogtrssbar.dismiss();
                            Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                        }
                    });
        }

        if(requestCode == 513 && resultCode == RESULT_OK){
            Mprogtrssbar.setCanceledOnTouchOutside(false);
            Mprogtrssbar.setMessage("Please wait while uploading doc file");
            Mprogtrssbar.setTitle("Uploading ...");
            Mprogtrssbar.show();
            Uri docuri = data.getData();
            buttondrawer.setVisibility(View.GONE);
            opendrawerbutton.setImageResource(R.drawable.plusicon);

            StorageReference filepath = MdocStores.child("docfile").child(docuri.getLastPathSegment());
            filepath.putFile(docuri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){


                                String DocDownlaodui = task.getResult().getDownloadUrl().toString();



                                String message_sender_ref = "Message/"+SenderID+"/"+ReciverID;
                                String message_reciver_ref = "Message/"+ReciverID+"/"+SenderID;

                                DatabaseReference usermessage_key = Mroodref.child("Message").child(SenderID).child(ReciverID).push();

                                String messagepushid = usermessage_key.getKey();

                                Calendar calendartime = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
                                CurrentTime = simpleDateFormattime.format(calendartime.getTime());

                                Calendar calendardate = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
                                CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

                                Map<String, Object> messagemap = new HashMap<String, Object>();
                                messagemap.put("message", DocDownlaodui);
                                messagemap.put("time", CurrentTime);
                                messagemap.put("date", CurrentDate);
                                messagemap.put("from", SenderID);
                                messagemap.put("type", "doc");

                                Map<String, Object> messagebody = new HashMap<String, Object>();
                                messagebody.put(message_sender_ref+"/"+messagepushid, messagemap);
                                messagebody.put(message_reciver_ref+"/"+messagepushid, messagemap);


                                Mroodref.updateChildren(messagebody).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Mprogtrssbar.dismiss();
                                            notifaction_doc_sending();
                                            docnotifactionhistory();
                                            MuserDatabase.child(SenderID).child("usermessage").setValue("send a doc");
                                            MuserDatabase.child(SenderID).child("usermessagetime").setValue(CurrentTime);
                                        }
                                        else {
                                            Mprogtrssbar.dismiss();
                                            Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Mprogtrssbar.dismiss();
                                                Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                                            }
                                        });

                            }
                            else {
                                Mprogtrssbar.dismiss();
                                Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Mprogtrssbar.dismiss();
                            Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                        }
                    });
        }


        if(requestCode ==514 && resultCode == RESULT_OK){
            Mprogtrssbar.setCanceledOnTouchOutside(false);
            Mprogtrssbar.setMessage("Please wait while uploading audio");
            Mprogtrssbar.setTitle("Uploading ...");
            Mprogtrssbar.show();
            Uri audiouri = data.getData();

            StorageReference filepath = audiofiledata.child("AudioFile").child(audiouri.getLastPathSegment());
            filepath.putFile(audiouri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){

                                String AudioDownloaduri = task.getResult().getDownloadUrl().toString();




                                String message_sender_ref = "Message/"+SenderID+"/"+ReciverID;
                                String message_reciver_ref = "Message/"+ReciverID+"/"+SenderID;

                                DatabaseReference usermessage_key = Mroodref.child("Message").child(SenderID).child(ReciverID).push();

                                String messagepushid = usermessage_key.getKey();

                                Calendar calendartime = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
                                CurrentTime = simpleDateFormattime.format(calendartime.getTime());

                                Calendar calendardate = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
                                CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

                                Map<String, Object> messagemap = new HashMap<String, Object>();
                                messagemap.put("message", AudioDownloaduri);
                                messagemap.put("time", CurrentTime);
                                messagemap.put("date", CurrentDate);
                                messagemap.put("from", SenderID);
                                messagemap.put("type", "audio");

                                Map<String, Object> messagebody = new HashMap<String, Object>();
                                messagebody.put(message_sender_ref+"/"+messagepushid, messagemap);
                                messagebody.put(message_reciver_ref+"/"+messagepushid, messagemap);


                                Mroodref.updateChildren(messagebody).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Mprogtrssbar.dismiss();
                                            notifaction_audio_sending();
                                            AudioFileHistory();
                                            MuserDatabase.child(SenderID).child("usermessage").setValue("send a Audio File");
                                            MuserDatabase.child(SenderID).child("usermessagetime").setValue(CurrentTime);
                                        }
                                        else {
                                            Mprogtrssbar.dismiss();
                                            Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Mprogtrssbar.dismiss();
                                                Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                                            }
                                        });

                            }
                            else {
                                Mprogtrssbar.dismiss();
                                Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Mprogtrssbar.dismiss();
                            Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                        }
                    });
        }

        if(requestCode == 515 &&  resultCode == RESULT_OK){

            Mprogtrssbar.setCanceledOnTouchOutside(false);
            Mprogtrssbar.setMessage("Please wait while uploading video");
            Mprogtrssbar.setTitle("Uploading ...");
            Mprogtrssbar.show();
            Uri videouri = data.getData();

            StorageReference filepath = MvideStores.child("Videofile").child(videouri.getLastPathSegment());
            filepath.putFile(videouri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                String VideoDownloduri = task.getResult().getDownloadUrl().toString();

                                String message_sender_ref = "Message/"+SenderID+"/"+ReciverID;
                                String message_reciver_ref = "Message/"+ReciverID+"/"+SenderID;

                                DatabaseReference usermessage_key = Mroodref.child("Message").child(SenderID).child(ReciverID).push();

                                String messagepushid = usermessage_key.getKey();

                                Calendar calendartime = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
                                CurrentTime = simpleDateFormattime.format(calendartime.getTime());

                                Calendar calendardate = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
                                CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

                                Map<String, Object> messagemap = new HashMap<String, Object>();
                                messagemap.put("message", VideoDownloduri);
                                messagemap.put("time", CurrentTime);
                                messagemap.put("date", CurrentDate);
                                messagemap.put("from", SenderID);
                                messagemap.put("type", "video");

                                Map<String, Object> messagebody = new HashMap<String, Object>();
                                messagebody.put(message_sender_ref+"/"+messagepushid, messagemap);
                                messagebody.put(message_reciver_ref+"/"+messagepushid, messagemap);


                                Mroodref.updateChildren(messagebody).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Mprogtrssbar.dismiss();
                                            notifaction_video_sending();
                                            VideoFileHistory();
                                            MuserDatabase.child(SenderID).child("usermessage").setValue("send a Video File");
                                            MuserDatabase.child(SenderID).child("usermessagetime").setValue(CurrentTime);
                                        }
                                        else {
                                            Mprogtrssbar.dismiss();
                                            Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Mprogtrssbar.dismiss();
                                                Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                                            }
                                        });

                            }
                            else {
                                Mprogtrssbar.dismiss();
                                Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Mprogtrssbar.dismiss();
                            Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                        }
                    });
        }

       if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
           CropImage.ActivityResult result = CropImage.getActivityResult(data);
           if (resultCode == RESULT_OK) {
               Mprogtrssbar.setCanceledOnTouchOutside(false);
               Mprogtrssbar.setMessage("Please wait while uploading photo");
               Mprogtrssbar.setTitle("Uploading ...");
               Mprogtrssbar.show();
               buttondrawer.setVisibility(View.GONE);
               opendrawerbutton.setImageResource(R.drawable.plusicon);

               Uri resultUri = result.getUri();



               StorageReference filepath = Mcamerastores.child(resultUri.getLastPathSegment());
               filepath.putFile(resultUri)
                       .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                               if(task.isSuccessful()){

                                   String cameradownlouri = task.getResult().getDownloadUrl().toString();


                                   String message_sender_ref = "Message/"+SenderID+"/"+ReciverID;
                                   String message_reciver_ref = "Message/"+ReciverID+"/"+SenderID;

                                   DatabaseReference usermessage_key = Mroodref.child("Message").child(SenderID).child(ReciverID).push();

                                   String messagepushid = usermessage_key.getKey();

                                   Calendar calendartime = Calendar.getInstance();
                                   SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
                                   CurrentTime = simpleDateFormattime.format(calendartime.getTime());

                                   Calendar calendardate = Calendar.getInstance();
                                   SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
                                   CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

                                   Map<String, Object> messagemap = new HashMap<String, Object>();
                                   messagemap.put("message", cameradownlouri);
                                   messagemap.put("time", CurrentTime);
                                   messagemap.put("date", CurrentDate);
                                   messagemap.put("from", SenderID);
                                   messagemap.put("type", "image");

                                   Map<String, Object> messagebody = new HashMap<String, Object>();
                                   messagebody.put(message_sender_ref+"/"+messagepushid, messagemap);
                                   messagebody.put(message_reciver_ref+"/"+messagepushid, messagemap);

                                   Mroodref.updateChildren(messagebody)
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if(task.isSuccessful()){
                                                       Mprogtrssbar.dismiss();
                                                       notifactionimage_sending();

                                                       imagenotifactnhistory();
                                                       MuserDatabase.child(SenderID).child("usermessage").setValue("send a image");
                                                       MuserDatabase.child(SenderID).child("usermessagetime").setValue(CurrentTime);

                                                   }
                                                   else {
                                                       Mprogtrssbar.dismiss();
                                                       Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                   }
                                               }
                                           })
                                           .addOnFailureListener(new OnFailureListener() {
                                               @Override
                                               public void onFailure(@NonNull Exception e) {
                                                   Mprogtrssbar.dismiss();
                                                   Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                               }
                                           });

                               }
                               else {
                                   Mprogtrssbar.dismiss();
                                   Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                               }
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Mprogtrssbar.dismiss();
                               Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                           }
                       });

           } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
               Mprogtrssbar.dismiss();
               Exception error = result.getError();
           }
       }
    }




    //// send notifaction
    private void find_username_and_send_notification(final String message){


        FirebaseDatabase.getInstance().getReference().child("NotifactionUSsers").child(SenderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                if(dataSnapshot.exists()){





                    if(dataSnapshot.hasChild("username")){
                        Log.d("TAG", "call");
                        String name =dataSnapshot.child("username").getValue().toString();
                        sendNotification(name, message);
                    }


                        // Log.i(TAG, "onDataChange: ");







                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    //// send notifaction


    /// send notifaction to another devices
    private void sendNotification(final String sendername ,  final String textstring) {



        FirebaseDatabase.getInstance().getReference("Token").child(ReciverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Token token=dataSnapshot.getValue(Token.class);


                final String sendermessage= sendername;
                String messagetext = inputmessage.getText().toString();




              /*  HashMap<String,String> data=new HashMap<>();
                data.put("title",title);
                data.put("body",message);*/


                Notification notification=new Notification(sendermessage,textstring.toString());
                Sender noti = new Sender(token.getToken(),notification);


                mService.sendNotification(noti).enqueue(new retrofit2.Callback<Myresponce>() {
                    @Override
                    public void onResponse(Call<Myresponce> call, Response<Myresponce> response) {



                    }

                    @Override
                    public void onFailure(Call<Myresponce> call, Throwable t) {

                        Log.i("STATUS", "onResponse: FAILED ");


                    }
                });







            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
    /// send notifaction to another devices


    private void notifactionhstory(){
        Calendar calendartime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
        CurrentTime = simpleDateFormattime.format(calendartime.getTime());

        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
        CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

        Map<String, Object> messagemap = new HashMap<String, Object>();
        MmessageDatabase.child(ReciverID);
        messagemap.put("messag", inputmessage.getText().toString());
        messagemap.put("friendname", myname);
        messagemap.put("messagetime", CurrentTime);
        messagemap.put("messagedate", CurrentDate);
        messagemap.put("FriendsID", SenderID);
        messagemap.put("MyID", ReciverID);
        messagemap.put("friendprofileimage", myimage);
        messagemap.put("short", negativeValShort);


        MmessageDatabase.child(ReciverID).push().updateChildren(messagemap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

    }


    /// pdf notifaction history
    private void pdfnotifactionhstory(){
        Calendar calendartime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
        CurrentTime = simpleDateFormattime.format(calendartime.getTime());

        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
        CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

        Map<String, Object> messagemap = new HashMap<String, Object>();
        MmessageDatabase.child(ReciverID);
        messagemap.put("messag", "you received a pdf file");
        messagemap.put("friendname", myname);
        messagemap.put("messagetime", CurrentTime);
        messagemap.put("messagedate", CurrentDate);
        messagemap.put("FriendsID", SenderID);
        messagemap.put("friendprofileimage", myimage);
        messagemap.put("MyID", ReciverID);
        messagemap.put("short", negativeValShort);


        MmessageDatabase.child(ReciverID).push().updateChildren(messagemap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

    }
    /// pdf notifaction history

    /// image notifaction history
    private void imagenotifactnhistory(){
        Calendar calendartime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
        CurrentTime = simpleDateFormattime.format(calendartime.getTime());

        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
        CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

        Map<String, Object> messagemap = new HashMap<String, Object>();
        MmessageDatabase.child(ReciverID);
        messagemap.put("messag", "you received a image");
        messagemap.put("friendname", myname);
        messagemap.put("messagetime", CurrentTime);
        messagemap.put("messagedate", CurrentDate);
        messagemap.put("FriendsID", SenderID);
        messagemap.put("friendprofileimage", myimage);
        messagemap.put("MyID", ReciverID);
        messagemap.put("short", negativeValShort);


        MmessageDatabase.child(ReciverID).push().updateChildren(messagemap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }
    /// image notifaction history

    ///doc notifaction history
    private void docnotifactionhistory(){
        Calendar calendartime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
        CurrentTime = simpleDateFormattime.format(calendartime.getTime());

        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
        CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

        Map<String, Object> messagemap = new HashMap<String, Object>();
        MmessageDatabase.child(ReciverID);
        messagemap.put("messag", "you received document file");
        messagemap.put("friendname", myname);
        messagemap.put("messagetime", CurrentTime);
        messagemap.put("messagedate", CurrentDate);
        messagemap.put("FriendsID", SenderID);
        messagemap.put("MyID", ReciverID);
        messagemap.put("friendprofileimage", myimage);
        messagemap.put("short", negativeValShort);


        MmessageDatabase.child(ReciverID).push().updateChildren(messagemap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }
    ///doc notifaction history

    ///audiofile history
    private void AudioFileHistory(){
        Calendar calendartime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
        CurrentTime = simpleDateFormattime.format(calendartime.getTime());

        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
        CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

        Map<String, Object> messagemap = new HashMap<String, Object>();
        MmessageDatabase.child(ReciverID);
        messagemap.put("messag", "you received Audio file");
        messagemap.put("friendname", myname);
        messagemap.put("messagetime", CurrentTime);
        messagemap.put("messagedate", CurrentDate);
        messagemap.put("FriendsID", SenderID);
        messagemap.put("MyID", ReciverID);
        messagemap.put("friendprofileimage", myimage);
        messagemap.put("short", negativeValShort);


        MmessageDatabase.child(ReciverID).push().updateChildren(messagemap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }
    ///audiofile history

    /// video file history
    private void VideoFileHistory(){
        Calendar calendartime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
        CurrentTime = simpleDateFormattime.format(calendartime.getTime());

        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
        CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

        Map<String, Object> messagemap = new HashMap<String, Object>();
        MmessageDatabase.child(ReciverID);
        messagemap.put("messag", "you received Video file");
        messagemap.put("friendname", myname);
        messagemap.put("messagetime", CurrentTime);
        messagemap.put("messagedate", CurrentDate);
        messagemap.put("FriendsID", SenderID);
        messagemap.put("MyID", ReciverID);
        messagemap.put("friendprofileimage", myimage);
        messagemap.put("short", negativeValShort);


        MmessageDatabase.child(ReciverID).push().updateChildren(messagemap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }
    /// video file history


    @Override
    protected void onStart() {
        MuserDatabase.child(SenderID).child("adminstatas").setValue("online");
        onlineStatas("online");
        super.onStart();
    }


    private void readuseronline(){
        MOnlineStatasDatabase.child(ReciverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild("statas")){
                                String isonline = dataSnapshot.child("statas").getValue().toString();
                                if(isonline.equals("online")){
                                    isonlinetext.setText("online");
                                }
                                if(isonline.equals("offline")) {
                                    String seen = dataSnapshot.child("last_seen").getValue().toString();
                                    isonlinetext.setText("Last seen "+seen);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void onlineStatas(String statas){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
        String OnlineTime =  simpleDateFormattime.format(calendar.getTime());

        Map onlinemap = new HashMap();
        onlinemap.put("statas", statas);
        onlinemap.put("last_seen", OnlineTime);


        MOnlineStatasDatabase.child(SenderID).updateChildren(onlinemap)
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






    /// audio recoder


    private void Startreacoding() {

        try {
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
        }

     /*   mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        try {
            mRecorder.prepare();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        mRecorder.start();*/

    }

    public void stopAudio() {



        try {
            mRecorder.stop();
            mRecorder.release();
            Toast.makeText(getApplicationContext(), "stop", Toast.LENGTH_LONG).show();


            savingData();

           /* ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
            NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

            final  boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();

            if(isconnected){

            }
            else {
                Toasty.error(getApplicationContext(), "Please check your internet connection", Toasty.LENGTH_LONG).show();
            }*/


            mRecorder = null;
        } catch (RuntimeException e) {

        }

    }
    /// audio recoder


    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }


    /// audio saving data
    private void savingData(){


       Mprogtrssbar.setTitle("Uploading ...");
       Mprogtrssbar.setMessage("please wait uploading audio file");
       Mprogtrssbar.setCanceledOnTouchOutside(false);
       Mprogtrssbar.show();



        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z", Locale.getDefault());
        String Time_Date = simpleDateFormat.format(calendar.getTime());


        StorageReference filepath = MrecoderFile.child("RecodeAudio").child("AudioFile"+Time_Date+".3gp");
        Uri uri = Uri.fromFile(new File(mFileName));

        filepath.putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){


                            String Recoder = task.getResult().getDownloadUrl().toString();




                            String message_sender_ref = "Message/"+SenderID+"/"+ReciverID;
                            String message_reciver_ref = "Message/"+ReciverID+"/"+SenderID;

                            DatabaseReference usermessage_key = Mroodref.child("Message").child(SenderID).child(ReciverID).push();

                            String messagepushid = usermessage_key.getKey();

                            Calendar calendartime = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
                            CurrentTime = simpleDateFormattime.format(calendartime.getTime());

                            Calendar calendardate = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
                            CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

                            Map<String, Object> messagemap = new HashMap<String, Object>();
                            messagemap.put("message", Recoder);
                            messagemap.put("time", CurrentTime);
                            messagemap.put("date", CurrentDate);
                            messagemap.put("from", SenderID);
                            messagemap.put("type", "sample");

                            Map<String, Object> messagebody = new HashMap<String, Object>();
                            messagebody.put(message_sender_ref+"/"+messagepushid, messagemap);
                            messagebody.put(message_reciver_ref+"/"+messagepushid, messagemap);


                            Mroodref.updateChildren(messagebody)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Mprogtrssbar.dismiss();
                                                Toast.makeText(getApplicationContext(), "saving data", Toast.LENGTH_LONG).show();
                                                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                                                AudioFileHistory();
                                                MuserDatabase.child(SenderID).child("usermessage").setValue("send a Audio File");
                                                MuserDatabase.child(SenderID).child("usermessagetime").setValue(CurrentTime);
                                            }
                                            else {
                                                Mprogtrssbar.dismiss();
                                                Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Mprogtrssbar.dismiss();
                                            Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                                        }
                                    });


                        }
                        else {
                            Mprogtrssbar.dismiss();
                            Toasty.error(getApplicationContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Mprogtrssbar.dismiss();
                        Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                    }
                });
    }
    /// audio saving data

    ///image sending notifaction

        private void notifactionimage_sending(){

            FirebaseDatabase.getInstance().getReference().child("NotifactionUSsers").child(SenderID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {



                    if(dataSnapshot.exists()){





                        if(dataSnapshot.hasChild("username")){
                            Log.d("TAG", "call");
                            String name =dataSnapshot.child("username").getValue().toString();
                            imagenotifaction(name);
                        }


                        // Log.i(TAG, "onDataChange: ");







                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }




    private void imagenotifaction(final String sendername ) {



        FirebaseDatabase.getInstance().getReference("Token").child(ReciverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Token token=dataSnapshot.getValue(Token.class);


                final String sendermessage= sendername;
                String messagetext = inputmessage.getText().toString();




              /*  HashMap<String,String> data=new HashMap<>();
                data.put("title",title);
                data.put("body",message);*/


                Notification notification=new Notification(sendermessage, "Send a image");
                Sender noti = new Sender(token.getToken(),notification);


                mService.sendNotification(noti).enqueue(new retrofit2.Callback<Myresponce>() {
                    @Override
                    public void onResponse(Call<Myresponce> call, Response<Myresponce> response) {



                    }

                    @Override
                    public void onFailure(Call<Myresponce> call, Throwable t) {

                        Log.i("STATUS", "onResponse: FAILED ");


                    }
                });







            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }


    ///image sending notifaction



    /// pdf sending notifaction

    private void notifaction_pdf_sending(){

        FirebaseDatabase.getInstance().getReference().child("NotifactionUSsers").child(SenderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                if(dataSnapshot.exists()){





                    if(dataSnapshot.hasChild("username")){
                        Log.d("TAG", "call");
                        String name =dataSnapshot.child("username").getValue().toString();
                        pdfnotifaction(name);
                    }


                    // Log.i(TAG, "onDataChange: ");







                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }




    private void pdfnotifaction(final String sendername ) {



        FirebaseDatabase.getInstance().getReference("Token").child(ReciverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Token token=dataSnapshot.getValue(Token.class);


                final String sendermessage= sendername;
                String messagetext = inputmessage.getText().toString();




              /*  HashMap<String,String> data=new HashMap<>();
                data.put("title",title);
                data.put("body",message);*/


                Notification notification=new Notification(sendermessage, "Send a pdf");
                Sender noti = new Sender(token.getToken(),notification);


                mService.sendNotification(noti).enqueue(new retrofit2.Callback<Myresponce>() {
                    @Override
                    public void onResponse(Call<Myresponce> call, Response<Myresponce> response) {



                    }

                    @Override
                    public void onFailure(Call<Myresponce> call, Throwable t) {

                        Log.i("STATUS", "onResponse: FAILED ");


                    }
                });







            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    /// pdf sending notifaction

    ///  doc sending notifaction
    private void notifaction_doc_sending(){

        FirebaseDatabase.getInstance().getReference().child("NotifactionUSsers").child(SenderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                if(dataSnapshot.exists()){





                    if(dataSnapshot.hasChild("username")){
                        Log.d("TAG", "call");
                        String name =dataSnapshot.child("username").getValue().toString();
                        docnotifaction(name);
                    }


                    // Log.i(TAG, "onDataChange: ");







                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void docnotifaction(final String sendername ) {

        FirebaseDatabase.getInstance().getReference("Token").child(ReciverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Token token=dataSnapshot.getValue(Token.class);


                final String sendermessage= sendername;
                String messagetext = inputmessage.getText().toString();




              /*  HashMap<String,String> data=new HashMap<>();
                data.put("title",title);
                data.put("body",message);*/


                Notification notification=new Notification(sendermessage, "Send a docx");
                Sender noti = new Sender(token.getToken(),notification);


                mService.sendNotification(noti).enqueue(new retrofit2.Callback<Myresponce>() {
                    @Override
                    public void onResponse(Call<Myresponce> call, Response<Myresponce> response) {



                    }

                    @Override
                    public void onFailure(Call<Myresponce> call, Throwable t) {

                        Log.i("STATUS", "onResponse: FAILED ");


                    }
                });







            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
    ///  doc sending notifaction


    /// audio notifaction
    private void notifaction_audio_sending(){

        FirebaseDatabase.getInstance().getReference().child("NotifactionUSsers").child(SenderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                if(dataSnapshot.exists()){





                    if(dataSnapshot.hasChild("username")){
                        Log.d("TAG", "call");
                        String name =dataSnapshot.child("username").getValue().toString();
                        audionotifaction(name);
                    }


                    // Log.i(TAG, "onDataChange: ");







                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void audionotifaction(final String sendername ) {

        FirebaseDatabase.getInstance().getReference("Token").child(ReciverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Token token=dataSnapshot.getValue(Token.class);


                final String sendermessage= sendername;
                String messagetext = inputmessage.getText().toString();




              /*  HashMap<String,String> data=new HashMap<>();
                data.put("title",title);
                data.put("body",message);*/


                Notification notification=new Notification(sendermessage, "Send a audio");
                Sender noti = new Sender(token.getToken(),notification);


                mService.sendNotification(noti).enqueue(new retrofit2.Callback<Myresponce>() {
                    @Override
                    public void onResponse(Call<Myresponce> call, Response<Myresponce> response) {



                    }

                    @Override
                    public void onFailure(Call<Myresponce> call, Throwable t) {

                        Log.i("STATUS", "onResponse: FAILED ");


                    }
                });







            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
    /// audio notifaction

    /// video notifaction sending
    private void notifaction_video_sending(){

        FirebaseDatabase.getInstance().getReference().child("NotifactionUSsers").child(SenderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                if(dataSnapshot.exists()){





                    if(dataSnapshot.hasChild("username")){
                        Log.d("TAG", "call");
                        String name =dataSnapshot.child("username").getValue().toString();
                        videonotifaction(name);
                    }


                    // Log.i(TAG, "onDataChange: ");







                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void videonotifaction(final String sendername ) {

        FirebaseDatabase.getInstance().getReference("Token").child(ReciverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Token token=dataSnapshot.getValue(Token.class);


                final String sendermessage= sendername;
                String messagetext = inputmessage.getText().toString();




              /*  HashMap<String,String> data=new HashMap<>();
                data.put("title",title);
                data.put("body",message);*/


                Notification notification=new Notification(sendermessage, "Send a video");
                Sender noti = new Sender(token.getToken(),notification);


                mService.sendNotification(noti).enqueue(new retrofit2.Callback<Myresponce>() {
                    @Override
                    public void onResponse(Call<Myresponce> call, Response<Myresponce> response) {



                    }

                    @Override
                    public void onFailure(Call<Myresponce> call, Throwable t) {

                        Log.i("STATUS", "onResponse: FAILED ");


                    }
                });







            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
    /// video notifaction sending




}
