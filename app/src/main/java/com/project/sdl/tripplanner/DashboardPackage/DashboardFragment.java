package com.project.sdl.tripplanner.DashboardPackage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.sdl.tripplanner.R;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by Manish Chougule on 08-08-2019.
 */

public class DashboardFragment extends Fragment{
   FirebaseAuth mAuth;
   FirebaseUser user;
   DatabaseReference mDataBase;
   RelativeLayout switchtrip;
   RelativeLayout switchcreate;
   JSONObject tripsJson;
   RelativeLayout.LayoutParams layoutParamscard,layoutParamsimage,layoutParamsbtn;
   LinearLayout cardlayout;
   TextView textview;
   ImageView cardimage;

    private static final int MENU_1_ITEM = Menu.FIRST;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dashboard, null);
        switchcreate = (RelativeLayout) view.findViewById(R.id.createswitch);
        switchtrip = (RelativeLayout) view.findViewById(R.id.tripswitch);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        //String temp=mDataBase.child("trips/"+user.getUid()).getKey();         gives ID of user

        mDataBase = FirebaseDatabase.getInstance().getReference("trips/" + user.getUid());
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    ArrayList<String> names=new ArrayList<>();
                    switchtrip.setVisibility(View.VISIBLE);
                    switchcreate.setVisibility(View.INVISIBLE);

                    cardlayout=(LinearLayout) view.findViewById(R.id.cardcontainer);

                    Iterable<DataSnapshot> data;
                    for(DataSnapshot ds:dataSnapshot.child("tripnames").getChildren())
                    {
                        String name=ds.getValue().toString();
                        names.add(name);
                    }

                   cardlayout.removeAllViews();
                    cardlayout.invalidate();
                    int i=1;
                    for(String n: names)
                    {
                        i++;
                        createCard(n,i);
                    }
                    FloatingActionButton floatnewopen= (FloatingActionButton) view.findViewById(R.id.floatnewopen);
                    floatnewopen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getActivity(), AddTrips.class);
                            startActivity(i);
                        }
                    });
                }
                else
                {
                    switchtrip.setVisibility(View.INVISIBLE);
                    switchcreate.setVisibility(View.VISIBLE);
                    Button btnOpen = (Button) view.findViewById(R.id.btnaddtrip);
                    btnOpen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getActivity(), AddTrips.class);
                            startActivity(i);
                        }
                    });
                }
//
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        return view;
    }
/*                      Context Menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,MENU_1_ITEM, 0,"Delete");
        //menu.add(MENU2, MENU_2_ITEM, 1, getText(R.string.menu2));
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_1_ITEM) {
            Toast.makeText(getContext(), getResources() + " is selected", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }
*/
    public void createCard(final String tname, int id)
    {
        CardView card=new CardView(getContext());
        card.setId(id);

        layoutParamscard = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,900);
        layoutParamsimage= new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.MATCH_PARENT),900/2);
        layoutParamsbtn=new RelativeLayout.LayoutParams(100,100);


        card.setLayoutParams(layoutParamscard);
        card.setPadding(25, 25, 2500, 25);
        card.setContentPadding(25,25,25,25);
        card.setRadius(25f);
        card.setMaxCardElevation(100);
        card.setPreventCornerOverlap(true);
        card.setUseCompatPadding(true);
        card.setCardElevation(50f);
        card.setCardBackgroundColor(Color.rgb(166,166,166));
        card.setMaxCardElevation(6);

        RelativeLayout cardrelative = new RelativeLayout(getContext());
        cardrelative.setLayoutParams(layoutParamscard);

        cardimage=new ImageView(getContext());
        cardimage.setId(id);
        cardimage.setLayoutParams(layoutParamsimage);
        cardimage.setImageResource(R.drawable.ic_image);
        layoutParamsimage.addRule(RelativeLayout.ALIGN_TOP);
        cardrelative.addView(cardimage);
        //registerForContextMenu(card);                     Context Menu

        textview = new TextView(getContext());
        textview.setId(id);
        textview.setLayoutParams(layoutParamscard);
        textview.setText(tname);
        textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
        textview.setTextColor(Color.WHITE);
        //textview.setPadding(25,25,25,25);
        textview.setGravity(Gravity.CENTER);
        layoutParamscard.addRule(RelativeLayout.BELOW,id);
        cardrelative.addView(textview);

        final Button popbtn=new Button(getContext());
        popbtn.setId(id);
        popbtn.setLayoutParams(layoutParamsbtn);
        popbtn.setBackgroundResource(R.drawable.dots);
        layoutParamsbtn.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParamsbtn.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        cardrelative.addView(popbtn);
        popbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPopupMenu_onClick(view);
            }
        });

        card.addView(cardrelative);
        cardlayout.addView(card);
    }

    private void buttonPopupMenu_onClick(final View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(),view);
        popupMenu.getMenu().add(0, MENU_1_ITEM, 0,"Delete");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case Menu.FIRST:
                        Toast.makeText(getContext(),"YOO Bitch", Toast.LENGTH_SHORT).show();
                        int i=view.getId();
                        Log.i(null,i+"   ");



                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    /*
                        mDatabase=FirebaseDatabase.getInstance().getReference("trips/"+user.getUid());
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                tripsJson=new JSONObject(String.valueOf(dataSnapshot.getValue()));
                                System.out.println(tripsJson.getJSONArray("tripname"));
                                Array a = tripsJson.getJSONArray("tripname");

                                for(String name:tripsJson.getJSONArray("tripname")){

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                     */
}
//        if(mDataBase.child("trips/"+user.getUid()).getKey().isEmpty()) {
//            switchtrip.setVisibility(View.INVISIBLE);
//            switchcreate.setVisibility(View.VISIBLE);
//            Button btnOpen = (Button) view.findViewById(R.id.btnaddtrip);
//            btnOpen.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(getActivity(), AddTrips.class);
//                    startActivity(i);
//                }
//            });
//            return view;
//        }
//        else
//        {
//            switchtrip.setVisibility(View.VISIBLE);
//            switchcreate.setVisibility(View.INVISIBLE);
//            return view;
//        }