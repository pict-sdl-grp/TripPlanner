package com.project.sdl.tripplanner.NotificationsPackage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.sdl.tripplanner.R;

/**
 * Created by Manish Chougule on 08-08-2019.
 */

public class NotificationsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_notifications, null);
    }
}