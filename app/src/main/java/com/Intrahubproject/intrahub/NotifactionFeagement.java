package com.Intrahubproject.intrahub;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotifactionFeagement extends Fragment {

   private RecyclerView notifactionview;
   private DatabaseReference MmessageDatabase;
   private RecyclerView recyclerView;
   private RelativeLayout aleartlayout;
   private  final String NOTIFCATION_ID = "sample_notifaction";
   private final int NOTIFACTIONCHANNEL_ID = 001;
   private String Current_date;
   private String brothcastmessage;


    public NotifactionFeagement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifaction_feagement, container, false);

        aleartlayout = view.findViewById(R.id.AleartLayoutID);
        recyclerView = view.findViewById(R.id.NotifactionViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MmessageDatabase = FirebaseDatabase.getInstance().getReference().child("AdminMessage");
        MmessageDatabase.keepSynced(true);

        notifactionview = view.findViewById(R.id.NotifactionViewID);
        notifactionview.setHasFixedSize(true);
        notifactionview.setLayoutManager(new LinearLayoutManager(getContext()));


        /// currenttime
        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("dd-MM-yyyy");
        Current_date = simpleDateFormat_time.format(calendar_date.getTime());
        /// currenttime



        return view;
    }


    @Override
    public void onStart() {



        FirebaseRecyclerAdapter<AdminMessagePogo, NotifactionViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AdminMessagePogo, NotifactionViewHolder>(
                AdminMessagePogo.class,
                R.layout.notifaction_update_layout,
                NotifactionViewHolder.class,
                MmessageDatabase
        ) {
            @Override
            protected void populateViewHolder(final NotifactionViewHolder notifactionViewHolder, final AdminMessagePogo adminMessagePogo, int i) {

                String UID = getRef(i).getKey();
                MmessageDatabase.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){



                                    aleartlayout.setVisibility(View.INVISIBLE);
                                    if(dataSnapshot.hasChild("AdminMessage")){
                                        String AdminMessageget = dataSnapshot.child("AdminMessage").getValue().toString();
                                        notifactionViewHolder.setMessageset(AdminMessageget);
                                    }
                                    if(dataSnapshot.hasChild("current_time")){
                                        String current_timeget = dataSnapshot.child("current_time").getValue().toString();
                                        notifactionViewHolder.setimeset(current_timeget);
                                    }
                                    if(dataSnapshot.hasChild("current_date")){
                                        String current_dateget = dataSnapshot.child("current_date").getValue().toString();
                                        notifactionViewHolder.setDateset(current_dateget);

                                        if(current_dateget.equals(Current_date)){

                                             brothcastmessage = dataSnapshot.child("AdminMessage").getValue().toString();
                                            open_notifaction(brothcastmessage);
                                        }
                                    }
                                }
                                else {

                                    aleartlayout.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };


        recyclerView.setAdapter(firebaseRecyclerAdapter);
        super.onStart();
    }

   public static class  NotifactionViewHolder extends RecyclerView.ViewHolder{

        private MaterialTextView time, date, message;
        private Context context;
        private View Mview;

        public NotifactionViewHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            time = Mview.findViewById(R.id.NotifactionTimerID);
            date = Mview.findViewById(R.id.NotifactionDateID);
            message = Mview.findViewById(R.id.MessagePostTextID);
        }

        public void  setimeset(String tim){
            time.setText(tim);
        }
        public void setDateset(String dat){
            date.setText(dat);
        }
        public void setMessageset(String mess){
            message.setText(mess);
        }
    }


    private void open_notifaction(String message){

        cheack_android_version();

        NotificationCompat.Builder notifactionbuilder = new NotificationCompat.Builder(getContext(), NOTIFCATION_ID);
        notifactionbuilder.setSmallIcon(R.drawable.applogo);
        notifactionbuilder.setContentText(message);
        notifactionbuilder.setContentTitle("Announcement");
        notifactionbuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
        notificationManagerCompat.notify(NOTIFACTIONCHANNEL_ID, notifactionbuilder.build());

    }

    private void cheack_android_version(){
        CharSequence name = "name";
        String descptrion = "des";
        int Importance = NotificationManager.IMPORTANCE_DEFAULT;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFCATION_ID, name, Importance);
            notificationChannel.setDescription(descptrion);

            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }
}
