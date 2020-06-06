package com.Intrahubproject.intrahub;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessagViewHolder> {

    private List<MessageHolder> messageHolderListView;
    private Handler handlero;
    private DatabaseReference MmusicLink;
    private ProgressDialog Mprogress;

    public MessageAdapter(List<MessageHolder> messageHolderListView) {
        this.messageHolderListView = messageHolderListView;
    }

    private DatabaseReference MMessageDatabase;
    private FirebaseAuth Mauth;
    private String SenderID;


    @NonNull
    @Override
    public MessagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_template, parent, false);

        return new  MessagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagViewHolder holder, int position) {

        Mprogress = new ProgressDialog(holder.itemView.getContext());

        Mauth = FirebaseAuth.getInstance();
        SenderID = Mauth.getCurrentUser().getUid();
        MMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Message");
        MMessageDatabase.keepSynced(true);
        MmusicLink = FirebaseDatabase.getInstance().getReference().child("Music");

        final  MessageHolder messageHolder = messageHolderListView.get(position);
        String MessageSenderID = messageHolder.getFrom();
        String fromtype = messageHolder.getType();

        MMessageDatabase.child(MessageSenderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.sendermessagelayout.setVisibility(View.GONE);
        holder.recivermessagelayout.setVisibility(View.GONE);

        holder.senderimagelayout.setVisibility(View.GONE);
        holder.reciverimagelayout.setVisibility(View.GONE);

        holder.senderpdflayout.setVisibility(View.GONE);
        holder.reciverpdflayout.setVisibility(View.GONE);

        holder.senderdoclayout.setVisibility(View.GONE);
        holder.reciverdoclayout.setVisibility(View.GONE);

        //audio
        holder.senderauidolayout.setVisibility(View.GONE);
        holder.reciveraudiolayout.setVisibility(View.GONE);
        //audio

        /// video
        holder.sendervideolayout.setVisibility(View.GONE);
        holder.recivervideolayout.setVisibility(View.GONE);
        /// video

        if(fromtype.equals("text")){
            if(MessageSenderID.equals(SenderID)){
                holder.sendermessagelayout.setVisibility(View.VISIBLE);

                if(messageHolder.getMessage().length() <= 15){
                    holder.sendermessagetexttime.setVisibility(View.GONE);
                    holder.texttimeright.setText(messageHolder.getTime());
                    holder.sendermessagetext.setText(messageHolder.getMessage());
                }
                else {
                    holder.texttimeright.setVisibility(View.GONE);
                    holder.sendermessagetext.setText(messageHolder.getMessage());
                    holder.sendermessagetexttime.setText(messageHolder.getTime());
                }



            }
            else {
                holder.recivermessagelayout.setVisibility(View.VISIBLE);
                if(messageHolder.getMessage().length() <= 15){
                    holder.recivertexttimeright.setText(messageHolder.getTime());
                    holder.recivermessagetext.setText(messageHolder.getMessage());
                    holder.recivermessagetexttime.setVisibility(View.GONE);
                }
                else {
                   holder.recivertexttimeright.setVisibility(View.GONE);
                    holder.recivermessagetext.setText(messageHolder.getMessage());
                    holder.recivermessagetexttime.setText(messageHolder.getTime());
                }


            }
        }
        if(fromtype.equals("image")){
            if(MessageSenderID.equals(SenderID)){
                holder.senderimagelayout.setVisibility(View.VISIBLE);
                holder.senderimagetime.setText(messageHolder.getTime());

                Picasso.with(holder.context).load(messageHolder.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.senderimage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(holder.context).load(messageHolder.getMessage()).into(holder.senderimage);

                    }
                });

                holder.senderimagelayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder Mbuilder = new AlertDialog.Builder(holder.itemView.getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        View Mview = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.fullscreenlayout, null, false);
                        Mbuilder.setView(Mview);

                        ImageView imageView = Mview.findViewById(R.id.FullScreenImageID);
                        Picasso.with(holder.context).load(messageHolder.getMessage()).into(imageView);

                        Picasso.with(holder.context).load(messageHolder.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                            }
                        });

                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();
                    }
                });



            }
            else {
                holder.reciverimagelayout.setVisibility(View.VISIBLE);
                holder.reciverimagetime.setText(messageHolder.getTime());

                Picasso.with(holder.context).load(messageHolder.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.reciverimage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(holder.context).load(messageHolder.getMessage()).into(holder.reciverimage);

                    }
                });

                holder.reciverimagelayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {




                        AlertDialog.Builder Mbuilder = new AlertDialog.Builder(holder.itemView.getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        View Mview = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.fullscreenlayout, null, false);
                        Mbuilder.setView(Mview);

                        ImageView imageView = Mview.findViewById(R.id.FullScreenImageID);
                        Picasso.with(holder.context).load(messageHolder.getMessage()).into(imageView);

                        Picasso.with(holder.context).load(messageHolder.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                            }
                        });

                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();
                    }


                });
            }
        }

        if(fromtype.equals("pdf")){
            if(MessageSenderID.equals(SenderID)){
                holder.senderpdflayout.setVisibility(View.VISIBLE);
                holder.senderpdftime.setText(messageHolder.getTime());

                holder.senderpdfdownloadbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messageHolder.getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });

            }
            else {
                holder.reciverpdflayout.setVisibility(View.VISIBLE);
                holder.reciverpdftime.setText(messageHolder.getTime());

                holder.reciverpdfdownloadutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messageHolder.getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }
        if(fromtype.equals("doc")){
            if(MessageSenderID.equals(SenderID)){
                holder.senderdoclayout.setVisibility(View.VISIBLE);
                holder.senderdoctime.setText(messageHolder.getTime());
                holder.senderdocdownloadbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messageHolder.getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
            else {
                holder.reciverdoclayout.setVisibility(View.VISIBLE);
                holder.reciverdoctime.setText(messageHolder.getTime());
                holder.reciverdocdownloadbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messageHolder.getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }

        }

        if(fromtype.equals("audio")){
            if(MessageSenderID.equals(SenderID)){

                holder.senderauidolayout.setVisibility(View.VISIBLE);
//                holder.senderaudiosikbar.setMax(100);
                holder.senderaudiotime.setText(messageHolder.getTime());


                holder.senderauidolayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Mprogress.setMessage("Please wait ...");
                        Mprogress.show();
                        Mprogress.setCanceledOnTouchOutside(false);

                        MmusicLink.child(SenderID).child("myaudio").setValue(messageHolder.getMessage()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    MusicBottomSheedDialog musicBottomSheedDialog = new MusicBottomSheedDialog();
                                    musicBottomSheedDialog.show(((FragmentActivity)holder.itemView.getContext()).getSupportFragmentManager(), "open");
                                    Mprogress.dismiss();
                                }
                                else {
                                    Mprogress.dismiss();
                                    Toasty.error(holder.itemView.getContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Mprogress.dismiss();
                                Toasty.error(holder.itemView.getContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                            }
                        });


                    }
                });


            }
            else{
                holder.reciveraudiolayout.setVisibility(View.VISIBLE);
                holder.reciveraudiotimer.setText(messageHolder.getTime());
                holder.reciveraudiolayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Mprogress.setMessage("Please wait ...");
                        Mprogress.show();
                        Mprogress.setCanceledOnTouchOutside(false);

                        MmusicLink.child(SenderID).child("myaudio").setValue(messageHolder.getMessage())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            MusicBottomSheedDialog musicBottomSheedDialog = new MusicBottomSheedDialog();
                                            musicBottomSheedDialog.show(((FragmentActivity)holder.itemView.getContext()).getSupportFragmentManager(), "open");

                                            Mprogress.dismiss();
                                        }
                                        else {
                                            Mprogress.dismiss();
                                            Toasty.error(holder.itemView.getContext(), task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toasty.error(holder.itemView.getContext(), e.getMessage().toString(), Toasty.LENGTH_LONG).show();
                                    }
                                });


                    }
                });
            }
        }

        if(fromtype.equals("video")){
            if(MessageSenderID.equals(SenderID)) {

                holder.sendervideolayout.setVisibility(View.VISIBLE);

                holder.sendervideplaybuttonID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.itemView.getContext(), VideoPlayerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("VIDELINK", messageHolder.getMessage());
                        intent.putExtra("VIDEOTIME", messageHolder.getTime());
                        intent.putExtra("VIDEODATE", messageHolder.getDate());

                        holder.itemView.getContext().startActivity(intent);
                        CustomIntent.customType(holder.itemView.getContext(), "fadein-to-fadeout");

                    }
                });
            }
            else {
                holder.recivervideolayout.setVisibility(View.VISIBLE);
                holder.reciverplaybuttonID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.itemView.getContext(), VideoPlayerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("VIDELINK", messageHolder.getMessage());
                        intent.putExtra("VIDEOTIME", messageHolder.getTime());
                        intent.putExtra("VIDEODATE", messageHolder.getDate());

                        holder.itemView.getContext().startActivity(intent);
                        CustomIntent.customType(holder.itemView.getContext(), "fadein-to-fadeout");
                    }
                });
            }
        }





    }




    private String milisecondtimer(long milisecond) {

        String timerstring = "";
        String secondstring;

        int hours = (int)(milisecond / (1000 * 60 * 60));
        int minitus = (int)(milisecond % (1000 * 60 * 60)) / (1000 * 60);
        int second = (int) ((milisecond % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if(hours > 0){
            timerstring = hours + ":";
        }
        if(second < 10){
            secondstring = "0"+second;
        }
        else {
            secondstring = ""+second;
        }

        timerstring = timerstring+minitus+":"+secondstring;

        return timerstring;
    }


    @Override
    public int getItemCount() {
        return messageHolderListView.size();
    }

    public class MessagViewHolder extends RecyclerView.ViewHolder{




        /// text message
        private RelativeLayout sendermessagelayout, recivermessagelayout;
        private MaterialTextView sendermessagetext, recivermessagetext;
        private Context context;
        private MaterialTextView sendermessagetexttime, recivermessagetexttime;
        private MaterialTextView texttimeright, recivertexttimeright;
        /// text message

        /// image
        private RelativeLayout senderimagelayout, reciverimagelayout;
        private MaterialTextView senderimagetime, reciverimagetime;
        private ImageView senderimage, reciverimage;
        /// image

        ///pdf
        private RelativeLayout senderpdflayout, reciverpdflayout;
        private MaterialTextView senderpdftime, reciverpdftime;
        private RelativeLayout senderpdfdownloadbutton, reciverpdfdownloadutton;
        ///pdf

        //doc
        private RelativeLayout senderdoclayout, reciverdoclayout;
        private MaterialTextView senderdoctime, reciverdoctime;
        private RelativeLayout senderdocdownloadbutton, reciverdocdownloadbutton;
        //doc


        /// audio
        private SeekBar senderaudiosikbar;
        private ImageView senderaudioplaybutton;
        private RelativeLayout senderauidolayout, reciveraudiolayout;
        private MaterialTextView senderaudiotime, reciveraudiotimer;
        /// audio

        /// audio companent
        private MediaPlayer mediaPlayer;
        private Runnable runnable;
        private Handler handler;
        /// audio companent

        /// video file
        private RelativeLayout sendervideolayout;
        private MaterialTextView sendervidevdeotimrID;
        private ImageView sendervideplaybuttonID;

        private RelativeLayout recivervideolayout;
        private MaterialTextView recivervideotimerID;
        private ImageView reciverplaybuttonID;
        /// video file

        public MessagViewHolder(@NonNull View itemView) {
            super(itemView);


            /// text message function
            sendermessagelayout = itemView.findViewById(R.id.SenderMesssageLayoutID);
            recivermessagelayout = itemView.findViewById(R.id.ReciverMessageLayoutID);
            sendermessagetext = itemView.findViewById(R.id.SenderMessage);
            recivermessagetext = itemView.findViewById(R.id.ReciverMessageTextID);
            sendermessagetexttime = itemView.findViewById(R.id.Sendermessafetime);
            recivermessagetexttime = itemView.findViewById(R.id.ReciverMessageTime);
            texttimeright = itemView.findViewById(R.id.SendermessafetimeRight);
            recivertexttimeright = itemView.findViewById(R.id.ReciverMessageTimeRight);
            /// text message function

            /// image
            senderimagelayout = itemView.findViewById(R.id.SenderImageLayoutID);
            senderimage = itemView.findViewById(R.id.SenderImageID);
            senderimagetime = itemView.findViewById(R.id.SenderImageTimeID);

            reciverimagelayout = itemView.findViewById(R.id.ReciverImageLayoutID);
            reciverimagetime = itemView.findViewById(R.id.ReciverImageTimeID);
            reciverimage = itemView.findViewById(R.id.ReciverImageID);
            /// image

            ///pdf
            senderpdflayout = itemView.findViewById(R.id.SenderPdfLayoutID);
            senderpdftime = itemView.findViewById(R.id.SenderPdfTimeID);
            senderpdfdownloadbutton = itemView.findViewById(R.id.SendPdfDownloadButtonID);

            reciverpdflayout = itemView.findViewById(R.id.ReciverPdfLayoutID);
            reciverpdftime = itemView.findViewById(R.id.ReciverPdfTimeID);
            reciverpdfdownloadutton = itemView.findViewById(R.id.ReciverPdfDownloaButtonID);
            ///pdf

            //doc
            senderdoclayout = itemView.findViewById(R.id.SenderDocLayoutID);
            senderdoctime = itemView.findViewById(R.id.SenderDocTimeID);
            senderdocdownloadbutton = itemView.findViewById(R.id.SendDocDownloadButtonID);

            reciverdoclayout = itemView.findViewById(R.id.ReciverDocLayoutID);
            reciverdoctime = itemView.findViewById(R.id.ReciverDocTimeID);
            reciverdocdownloadbutton = itemView.findViewById(R.id.ReciverDocDownloaButtonID);
            //doc

            /// audio
            /*senderaudiosikbar = itemView.findViewById(R.id.SenderAudioSikbarID);*/
            senderaudioplaybutton = itemView.findViewById(R.id.PlayorposeButtonID);
            senderauidolayout = itemView.findViewById(R.id.SenderAudioLayoutID);
            /*senderaudiostarttime = itemView.findViewById(R.id.senderaudiocurrenttime);
            senderaudioendtime = itemView.findViewById(R.id.senderaudiototalttime);*/
            reciveraudiotimer = itemView.findViewById(R.id.ReciverMusicTimeID);
            senderaudiotime = itemView.findViewById(R.id.SenderMusicTimeID);
            reciveraudiolayout = itemView.findViewById(R.id.ReciverAudioLayoutID);
            /// audio

            /// video
            sendervideolayout = itemView.findViewById(R.id.SenderVideoLayoutID);
            sendervidevdeotimrID = itemView.findViewById(R.id.SenderViDeoPlayTimeID);
            sendervideplaybuttonID = itemView.findViewById(R.id.SenderVideplaybuttonID);

            recivervideolayout = itemView.findViewById(R.id.ReciverVideoLayoutID);
            sendervidevdeotimrID = itemView.findViewById(R.id.ReciverViDeoPlayTimeID);
            reciverplaybuttonID = itemView.findViewById(R.id.ReciverVideplaybuttonID);
            /// video

        }

    }


}
