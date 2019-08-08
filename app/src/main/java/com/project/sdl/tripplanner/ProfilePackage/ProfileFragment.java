package com.project.sdl.tripplanner.ProfilePackage;

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

public class ProfileFragment extends Fragment {

    Button editProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //removes Notifiaction bar
//        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View root = inflater.inflate(R.layout.fragment_profile, null);
        editProfile = root.findViewById(R.id.editProf);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfile_intent = new Intent(getContext(),EditProfile_Activity.class);
                startActivity(editProfile_intent);
            }
        });


        return root;
    }

}
