package com.project.sdl.tripplanner.TripsPackage;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jmedeisis.draglinearlayout.DragLinearLayout;
import com.project.sdl.tripplanner.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrganizeTripActivity extends AppCompatActivity {

    JSONObject places;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organize_trip);
        getSupportActionBar().hide();

        Snackbar.make(findViewById(android.R.id.content), "Drag Your places to organize by Dates", Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();

        final DragLinearLayout dragDropAndroidLinearLayout = (DragLinearLayout) findViewById(R.id.drag_drop_layout);

        places = new JSONObject();

        ArrayList<View> arrayList = new ArrayList<>();
        for(int i=4;i<dragDropAndroidLinearLayout.getChildCount();i++){
            arrayList.add(dragDropAndroidLinearLayout.getChildAt(i));
        }

        ScrollView scrollView = findViewById(R.id.drag_drop_layout_scroll);
        dragDropAndroidLinearLayout.setContainerScrollView(scrollView);

        for (int i = 1; i < dragDropAndroidLinearLayout.getChildCount(); i++) {

                View child = dragDropAndroidLinearLayout.getChildAt(i);

                if(i > 3){
                    dragDropAndroidLinearLayout.setViewDraggable(child, arrayList.get(i-4));
                }else {
                    dragDropAndroidLinearLayout.setViewDraggable(child, arrayList.get(i));
                }

        }




        dragDropAndroidLinearLayout.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
            @Override
            public void onSwap(View firstView, int firstPosition, View secondView, int secondPosition) {
                Log.i("onSwap: ", String.valueOf(firstView.getTag()));
                Log.i("onSwap: ", String.valueOf(firstPosition));
                Log.i("onSwap: ", String.valueOf(secondView.getTag()));
                Log.i("onSwap: ", String.valueOf(secondPosition));

                TextView textView1 = (TextView) firstView;
                TextView textView2 = (TextView) secondView;

                if(String.valueOf(secondView.getTag()).contains("header")) {
                    if (secondPosition < firstPosition) {

                        try {
                            places.put(String.valueOf(firstView.getTag()), String.valueOf(secondView.getTag()));

                            Snackbar.make(findViewById(android.R.id.content),textView1.getText() + " on " + String.valueOf(textView2.getText()).split("-")[0]+(Integer.valueOf(String.valueOf(textView2.getText()).split("-")[1])-1), Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                    .show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (secondPosition > firstPosition) {
                        try {
                            places.put(String.valueOf(firstView.getTag()),
                                    String.valueOf(secondView.getTag()).split("-")[0] + (Integer.valueOf(String.valueOf(secondView.getTag()).split("-")[1]) + 1));
                            Snackbar.make(findViewById(android.R.id.content),textView1.getText() + " on " + textView2.getText(), Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                    .show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                Log.i("onSwap: ",places.toString());
            }
        });





//        for(int i=0;i<dragDropAndroidLinearLayout1.getChildCount();i++){
//            View child = dragDropAndroidLinearLayout1.getChildAt(i);
//            dragDropAndroidLinearLayout1.setViewDraggable(child, child);
//        }
//
//        for(int i=0;i<dragDropAndroidLinearLayout2.getChildCount();i++){
//            View child = dragDropAndroidLinearLayout2.getChildAt(i);
//            dragDropAndroidLinearLayout2.setViewDraggable(child, child);
//        }



    }

}
