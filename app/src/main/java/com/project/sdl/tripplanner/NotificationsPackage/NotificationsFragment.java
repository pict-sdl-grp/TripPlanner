package com.project.sdl.tripplanner.NotificationsPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.project.sdl.tripplanner.R;

/**
 * Created by Manish Chougule on 08-08-2019.
 */

public class NotificationsFragment extends Fragment {

    Button mapButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, null);
        mapButton = root.findViewById(R.id.button);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),BasicMapsActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }



}
