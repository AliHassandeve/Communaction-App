package com.Intrahubproject.intrahub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class bottomsheed_helper extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        RelativeLayout submitbutton = view.findViewById(R.id.SubmitbuttonIDa);

        final EditText email = view.findViewById(R.id.UserEmailID);
        final EditText message = view.findViewById(R.id.UserMessagelID);

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailtext = email.getText().toString();
                String messagetext = message.getText().toString();

                if(emailtext.isEmpty()){
                    email.setError("Email require");
                }
                else if(messagetext.isEmpty()){
                    message.setError("Message require");
                }
                else {
                    String Subject = "Users Request";
                    String[] recipicent = {"unimiftah734@gmail.com"};
                    String message  = messagetext+"\n\n"+"Users Email is: "+emailtext;
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, recipicent);
                    intent.putExtra(Intent.EXTRA_SUBJECT, Subject);
                    intent.putExtra(Intent.EXTRA_TEXT, message);

                    intent.setType("message/rfc822");
                    startActivity(Intent.createChooser(intent, "Choose Email"));
                }
            }
        });

        return view;
    }
}
