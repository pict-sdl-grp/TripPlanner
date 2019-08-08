package com.project.sdl.tripplanner.ProfilePackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.project.sdl.tripplanner.R;

/**
 * Created by Manish Chougule on 08-08-2019.
 */

public class ProfileFragment extends Fragment {

    Button editProfile;
    ImageView setting;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_profile, null);
        editProfile = root.findViewById(R.id.editProf);
        setting =   root.findViewById(R.id.settingProf);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfile_intent = new Intent(getContext(),EditProfile_Activity.class);
                startActivity(editProfile_intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setting_intent=new Intent(getContext(),Setting_Activity.class);
                startActivity(setting_intent);
            }
        });


        return root;
    }

}
