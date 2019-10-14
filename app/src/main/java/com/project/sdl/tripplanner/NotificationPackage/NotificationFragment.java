package com.project.sdl.tripplanner.NotificationPackage;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.sdl.tripplanner.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class NotificationFragment extends Fragment {

    TextView toolbarTitle;
    ArrayList<NotificationListItem> listItems;
    ArrayList<String> swipePosition;
    int color;
    TextView clearText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_notifications, null);
        toolbarTitle = root.findViewById(R.id.toolbar_title);
        clearText = root.findViewById(R.id.clearText);

        listItems = new ArrayList<>();
        swipePosition = new ArrayList<>();


        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("onClick: ","dd");
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference ref = database.getReference("notifications/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
                ref.setValue(null);

                listItems = new ArrayList<>();

                RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
                MyListAdapter adapter = new MyListAdapter(listItems,getContext());
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }
        });


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("notifications/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> notificationHash = (HashMap<String,Object>) dataSnapshot.getValue();

                if(notificationHash != null) {

                    ArrayList<String> keys = new ArrayList<>(notificationHash.keySet());
                    Collections.sort(keys, Collections.reverseOrder());


                    JSONObject notificationJson = new JSONObject(notificationHash);
                    try {
                        for (String key:keys ) {
                            String notificationKey = key;
                            Log.i("onDataChange: ",notificationKey);


                            if(!notificationJson.getJSONObject(notificationKey).getBoolean("seen")){
                                color = Color.parseColor("#B3E5FC");
                                ref.child(notificationKey).child("seen").setValue(true);
                            }else{
                                color = Color.parseColor("#E0F7FA");
                            }
                            String photoUrl = "none";
                            try{

                               photoUrl = notificationJson.getJSONObject(notificationKey).getString("senderPhotoUrl");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            listItems.add(new NotificationListItem(
                                    notificationJson.getJSONObject(notificationKey).getString("senderName").substring(0,1).toUpperCase()+notificationJson.getJSONObject(notificationKey).getString("senderName").substring(1),
                                    "on "+notificationJson.getJSONObject(notificationKey).getString("timestamp").substring(0,11)+" at "+notificationJson.getJSONObject(notificationKey).getString("timestamp").substring(11),
                                    Html.fromHtml(notificationJson.getJSONObject(notificationKey).getString("message")),
                                    photoUrl,color,notificationJson.getJSONObject(notificationKey).getString("message"),notificationJson.getJSONObject(notificationKey).getString("timestamp")));

                            swipePosition.add(key);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
                    MyListAdapter adapter = new MyListAdapter(listItems,getContext());
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                    ItemTouchHelper itemTouchHelper = new
                            ItemTouchHelper(new SwipeToDeleteCallback(adapter,swipePosition,recyclerView));
                    itemTouchHelper.attachToRecyclerView(recyclerView);


                    ref.removeEventListener(this);
                }else{

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







        return root;
    }




}


/*
    Notifications Logic


    notifications:{

        "<receiver_user_uid>":{

            "<notification_id>":{

                "id":"<notification_id>",
                "type":"<notification_type> ex.share,updateTrip",
                "message":"<notification_message>",
                "timestamp":"<notification_timestamp>",
                "By":"<sender_user_uid>",


            },
            .
            .
            .
            .
            .

        }



    }



 */