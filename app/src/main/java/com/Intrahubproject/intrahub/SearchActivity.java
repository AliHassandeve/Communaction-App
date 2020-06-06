package com.Intrahubproject.intrahub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
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

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference MuserDatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(SearchActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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





        searchView = findViewById(R.id.SearchViewID);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

               startsearching(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                startsearching(newText);

                return false;
            }
        });


        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.SearchToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backicon);
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MuserDatabase.keepSynced(true);
        recyclerView = findViewById(R.id.SearchRecylearviewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            CustomIntent.customType(SearchActivity.this, "up-to-bottom");
        }



        return super.onOptionsItemSelected(item);
    }



    private void startsearching(String value){

        String query = value.toLowerCase();
        final Query firebaseQry = MuserDatabase.orderByChild("search").startAt(query).endAt(query+"\uf8ff");



        FirebaseRecyclerAdapter<ChatPogoClass, SearchHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatPogoClass, SearchHolder>(
                ChatPogoClass.class,
                R.layout.chat_layout,
                SearchHolder.class,
                firebaseQry
        ) {
            @Override
            protected void populateViewHolder(final SearchHolder searchHolder, ChatPogoClass chatPogoClass, int i) {

                final String UID = getRef(i).getKey();
                MuserDatabase.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    searchHolder.isonlinedot.setVisibility(View.VISIBLE);
                                    searchHolder.messagetext.setVisibility(View.VISIBLE);
                                    searchHolder.isprofilebutton.setVisibility(View.GONE);
                                    searchHolder.timer.setVisibility(View.VISIBLE);

                                    if(dataSnapshot.hasChild("username")){
                                        String usernameget = dataSnapshot.child("username").getValue().toString();
                                        searchHolder.setUsernameset(usernameget);
                                    }

                                    if(dataSnapshot.hasChild("imageuri")){
                                        String imageuriget = dataSnapshot.child("imageuri").getValue().toString();
                                        searchHolder.setProfileimageset(imageuriget);
                                    }

                                    if(dataSnapshot.hasChild("myid")){
                                        String myidget = dataSnapshot.child("myid").getValue().toString();
                                        if(myidget.equals(CurrentUserID)){
                                            searchHolder.isonlinedot.setVisibility(View.INVISIBLE);
                                            searchHolder.messagetext.setVisibility(View.GONE);
                                            searchHolder.isprofilebutton.setVisibility(View.VISIBLE);
                                            searchHolder.rootlayout.setEnabled(false);
                                            searchHolder.timer.setVisibility(View.INVISIBLE);
                                        }
                                        else {
                                            searchHolder.timer.setVisibility(View.VISIBLE);
                                            searchHolder.isonlinedot.setVisibility(View.VISIBLE);
                                            searchHolder.messagetext.setVisibility(View.VISIBLE);
                                            searchHolder.isprofilebutton.setVisibility(View.GONE);
                                            searchHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.putExtra("KEY", UID);
                                                    startActivity(intent);
                                                    CustomIntent.customType(SearchActivity.this, "fadein-to-fadeout");

                                                }
                                            });
                                        }
                                    }

                                    if(dataSnapshot.hasChild("typestatas")){
                                        String typestatasget = dataSnapshot.child("typestatas").getValue().toString();
                                        if(typestatasget.equals("typing...")){
                                            searchHolder.setusermessageset("typing...");
  //                                          searchHolder.istyping.setTextColor(getResources().getColor(R.color.red));
                                        }
                                        else if(typestatasget.equals("notyping...")){
                                            if(dataSnapshot.hasChild("usermessage")){
                                                String usermessageget = dataSnapshot.child("usermessage").getValue().toString();
                                                searchHolder.setusermessageset(usermessageget);
             //                                                 searchHolder.istyping.setTextColor(getResources().getColor(R.color.accentcolour));
                                            }
                                        }
                                    }

                                    if(dataSnapshot.hasChild("adminstatas")){
                                        String adminstatasget = dataSnapshot.child("adminstatas").getValue().toString();
                                        if(adminstatasget.equals("online")){
                                            searchHolder.isonlinedot.setImageResource(R.drawable.onlinedot);
                                        }
                                        else if(adminstatasget.equals("offline")) {
                                            searchHolder.isonlinedot.setImageResource(R.drawable.offlinedot);
                                        }
                                    }

                                    if(dataSnapshot.hasChild("usermessagetime")){
                                        String usermessagetimeget = dataSnapshot.child("usermessagetime").getValue().toString();
                                        searchHolder.setmessagetimeset(usermessagetimeget);

                                    }


                                }
                                else {

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);



    }

    @Override
    protected void onStart() {

        MuserDatabase.child(CurrentUserID).child("adminstatas").setValue("online");

        FirebaseRecyclerAdapter<ChatPogoClass, SearchHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatPogoClass, SearchHolder>(
                ChatPogoClass.class,
                R.layout.chat_layout,
                SearchHolder.class,
                MuserDatabase
        ) {
            @Override
            protected void populateViewHolder(final SearchHolder searchHolder, ChatPogoClass chatPogoClass, int i) {

                final String UID = getRef(i).getKey();
                MuserDatabase.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    searchHolder.isonlinedot.setVisibility(View.VISIBLE);
                                    searchHolder.messagetext.setVisibility(View.VISIBLE);
                                    searchHolder.isprofilebutton.setVisibility(View.GONE);
                                    searchHolder.timer.setVisibility(View.VISIBLE);

                                    if(dataSnapshot.hasChild("username")){
                                        String usernameget = dataSnapshot.child("username").getValue().toString();
                                        searchHolder.setUsernameset(usernameget);
                                    }

                                    if(dataSnapshot.hasChild("imageuri")){
                                        String imageuriget = dataSnapshot.child("imageuri").getValue().toString();
                                        searchHolder.setProfileimageset(imageuriget);
                                    }

                                    if(dataSnapshot.hasChild("myid")){
                                        String myidget = dataSnapshot.child("myid").getValue().toString();
                                        if(myidget.equals(CurrentUserID)){
                                            searchHolder.isonlinedot.setVisibility(View.INVISIBLE);
                                            searchHolder.messagetext.setVisibility(View.GONE);
                                            searchHolder.isprofilebutton.setVisibility(View.VISIBLE);
                                            searchHolder.rootlayout.setEnabled(false);
                                            searchHolder.timer.setVisibility(View.INVISIBLE);

                                            searchHolder.profilebutton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);

                                                }
                                            });
                                        }
                                        else {
                                            searchHolder.timer.setVisibility(View.VISIBLE);
                                            searchHolder.isonlinedot.setVisibility(View.VISIBLE);
                                            searchHolder.messagetext.setVisibility(View.VISIBLE);
                                            searchHolder.isprofilebutton.setVisibility(View.GONE);
                                            searchHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.putExtra("KEY", UID);
                                                    startActivity(intent);
                                                    CustomIntent.customType(SearchActivity.this, "fadein-to-fadeout");

                                                }
                                            });
                                        }
                                    }

                                    if(dataSnapshot.hasChild("typestatas")){
                                        String typestatasget = dataSnapshot.child("typestatas").getValue().toString();
                                        if(typestatasget.equals("typing...")){
                                            searchHolder.setusermessageset("typing...");
             //                               searchHolder.istyping.setTextColor(getResources().getColor(R.color.red));
                                        }
                                        else if(typestatasget.equals("notyping...")){
                                            if(dataSnapshot.hasChild("usermessage")){
                                                String usermessageget = dataSnapshot.child("usermessage").getValue().toString();
                                                searchHolder.setusermessageset(usermessageget);
                        //               searchHolder.istyping.setTextColor(getResources().getColor(R.color.accentcolour));
                                            }
                                        }
                                    }

                                    if(dataSnapshot.hasChild("adminstatas")){
                                        String adminstatasget = dataSnapshot.child("adminstatas").getValue().toString();
                                        if(adminstatasget.equals("online")){
                                            searchHolder.isonlinedot.setImageResource(R.drawable.onlinedot);
                                        }
                                        else if(adminstatasget.equals("offline")) {
                                            searchHolder.isonlinedot.setImageResource(R.drawable.offlinedot);
                                        }
                                    }

                                    if(dataSnapshot.hasChild("usermessagetime")){
                                        String usermessagetimeget = dataSnapshot.child("usermessagetime").getValue().toString();
                                        searchHolder.setmessagetimeset(usermessagetimeget);

                                    }


                                }
                                else {

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

    public static class SearchHolder extends RecyclerView.ViewHolder{

        private View Mview;
        private Context context;
        private CircleImageView profileimage;
        private MaterialTextView username;
        private ImageView isonlinedot;
        private MaterialTextView messagetext;
        private MaterialTextView isprofilebutton;

        private MaterialTextView istyping;
        private MaterialTextView timer;
        private RelativeLayout rootlayout;

        private MaterialTextView profilebutton;

        public SearchHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            profilebutton = Mview.findViewById(R.id.MprofileTextID);
            context = Mview.getContext();
            profileimage = Mview.findViewById(R.id.SprofileImageID);
            username = Mview.findViewById(R.id.SusernameID);
            isonlinedot = Mview.findViewById(R.id.isonlineID);
            messagetext = Mview.findViewById(R.id.SmessageID);
            isprofilebutton = Mview.findViewById(R.id.MprofileTextID);
            istyping = Mview.findViewById(R.id.SmessageID);
            timer = Mview.findViewById(R.id.STimeID);
            rootlayout = Mview.findViewById(R.id.HeaderLayoutID);
        }

        public void setProfileimageset(String img){
            Picasso.with(context).load(img).placeholder(R.drawable.defaultimeg).into(profileimage);

            Picasso.with(context).load(img).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.defaultimeg).into(profileimage, new Callback() {
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
            istyping.setText(mess);
        }

        public void setmessagetimeset(String tim){
            timer.setText(tim);
        }
    }


    @Override
    protected void onStop() {
        MuserDatabase.child(CurrentUserID).child("adminstatas").setValue("offline");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        MuserDatabase.child(CurrentUserID).child("adminstatas").setValue("offline");
        super.onDestroy();
    }




}




