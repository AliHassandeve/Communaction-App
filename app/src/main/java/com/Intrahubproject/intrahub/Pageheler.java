package com.Intrahubproject.intrahub;

import android.app.NotificationManager;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class Pageheler extends FragmentPagerAdapter {
    public Pageheler(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0:
            NotifactionFeagement notifactionFeagement = new NotifactionFeagement();
            return notifactionFeagement;

            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Notification";
            case 1:
                return "Chat";
        }
        return super.getPageTitle(position);
    }
}

