package com.project.sdl.tripplanner.HomePackage.PlaceInfoPackage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.Toast;

import com.project.sdl.tripplanner.R;

public class CalenderDialog extends AppCompatActivity {

    public  CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calender_dialog);
        getSupportActionBar().hide();

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.calender_dialog);


        calendarView = findViewById(R.id.calendarView);

        calendarView.setMaxDate(System.currentTimeMillis());



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView1, int i, int i1, int i2) {
                Toast.makeText(getApplicationContext(), i2+ "/"+(i1+1)+"/"+i, Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("date", i2+ "/"+(i1+1)+"/"+i);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });




    }




}