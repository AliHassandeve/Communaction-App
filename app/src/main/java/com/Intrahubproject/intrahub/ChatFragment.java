package com.Intrahubproject.intrahub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference MuserDatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);







        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MuserDatabase.keepSynced(true);
        recyclerView = view.findViewById(R.id.ChatRecylearviewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));




        return view;
    }



    @Override
    public void onStart() {

        Query firebasequry = MuserDatabase.orderByChild("short");

        FirebaseRecyclerAdapter<ChatPogoClass, ChatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatPogoClass, ChatViewHolder>(
                ChatPogoClass.class,
                R.layout.chat_layout,
                ChatViewHolder.class,
                firebasequry
        ) {
            @Override
            protected void populateViewHolder(final ChatViewHolder chatViewHolder, final ChatPogoClass chatPogoClass, int i) {

               final String UID = getRef(i).getKey();
                MuserDatabase.child(UID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            chatViewHolder.profilebutton.setVisibility(View.INVISIBLE);
                            chatViewHolder.usermessagetime.setVisibility(View.VISIBLE);

                            chatViewHolder.usermesssage.setVisibility(View.VISIBLE);
                            chatViewHolder.isonliendot.setVisibility(View.VISIBLE);


                            if(dataSnapshot.hasChild("username")){
                                String usernameget = dataSnapshot.child("username").getValue().toString();
                                chatViewHolder.setUsernameset(usernameget);
                            }
                            if(dataSnapshot.hasChild("imageuri")){
                                String imageuriget = dataSnapshot.child("imageuri").getValue().toString();
                                chatViewHolder.setProfieimageset(imageuriget);
                            }

                            if(dataSnapshot.hasChild("typestatas")){
                                String typestatasget = dataSnapshot.child("typestatas").getValue().toString();
                                if(typestatasget.equals("typing...")){
                                    chatViewHolder.setusermessageset("typing...");
    //                                chatViewHolder.usermesssage.setTextColor(getResources().getColor(R.color.red));
                                }
                                else if(typestatasget.equals("notyping...")){
                                    if(dataSnapshot.hasChild("usermessage")){
                                        String usermessageget = dataSnapshot.child("usermessage").getValue().toString();
                                        chatViewHolder.setusermessageset(usermessageget);
          //                             chatViewHolder.usermesssage.setTextColor(getResources().getColor(R.color.accentcolour));
                                    }
                                }
                            }

                            if(dataSnapshot.hasChild("adminstatas")){
                                String adminstatasget = dataSnapshot.child("adminstatas").getValue().toString();
                                if(adminstatasget.equals("online")){
                                    chatViewHolder.isonliendot.setImageResource(R.drawable.onlinedot);
                                }
                                else if(adminstatasget.equals("offline")) {
                                    chatViewHolder.isonliendot.setImageResource(R.drawable.offlinedot);
                                }
                            }



                            if(dataSnapshot.hasChild("usermessagetime")){
                                String usermessagetimeget = dataSnapshot.child("usermessagetime").getValue().toString();
                                chatViewHolder.setmessagetimeset(usermessagetimeget);

                            }

                            if(dataSnapshot.hasChild("myid")){
                                String id = dataSnapshot.child("myid").getValue().toString();
                                if(id.equals(CurrentUserID)){
                                    chatViewHolder.profilebutton.setVisibility(View.VISIBLE);
                                    chatViewHolder.header.setEnabled(false);
                                    chatViewHolder.usermessagetime.setVisibility(View.INVISIBLE);

                                    chatViewHolder.usermesssage.setVisibility(View.GONE);
                                    chatViewHolder.isonliendot.setVisibility(View.INVISIBLE);

                                    chatViewHolder.profilebutton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getContext(), EditProfileActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            CustomIntent.customType(getContext(), "fadein-to-fadeout");
                                        }
                                    });

                                }

                            }

                            chatViewHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("KEY", UID);
                                    startActivity(intent);
                                    CustomIntent.customType(getContext(), "fadein-to-fadeout");

                                }
                            });
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

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        private View Mview;
        private Context context;
        private CircleImageView profieimage;
        private TextView username, usermesssage, usermessagetime;
        private RelativeLayout header;
        private MaterialTextView profilebutton;
        private ImageView isonliendot;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            context = Mview.getContext();
            profieimage = Mview.findViewById(R.id.SprofileImageID);
            username = Mview.findViewById(R.id.SusernameID);
            usermesssage = Mview.findViewById(R.id.SmessageID);
            usermessagetime = Mview.findViewById(R.id.STimeID);
            header = Mview.findViewById(R.id.HeaderLayoutID);
            profilebutton = Mview.findViewById(R.id.MprofileTextID);
            isonliendot = Mview.findViewById(R.id.isonlineID);
        }

        public void setProfieimageset( String img){
            Picasso.with(context).load(img).placeholder(R.drawable.defaultimeg).into(profieimage);

            Picasso.with(context).load(img).networkPolicy(NetworkPolicy.OFFLINE).into(profieimage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
        }

        public void setUsernameset(String nam){
            username.setText(nam);
        }

        public void setusermessageset(String mess){
            usermesssage.setText(mess);
        }

        public void setmessagetimeset(String tim){
            usermessagetime.setText(tim);
        }
    }




}
