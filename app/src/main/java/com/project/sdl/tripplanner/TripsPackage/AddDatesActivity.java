package com.project.sdl.tripplanner.TripsPackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.sdl.tripplanner.R;
import com.wisnu.datetimerangepickerandroid.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.wisnu.datetimerangepickerandroid.CalendarPickerView.SelectionMode.RANGE;

public class AddDatesActivity extends AppCompatActivity {

    ArrayList<String> dateRange;
    TextView doneText;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dates);
        getSupportActionBar().hide();

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        doneText = findViewById(R.id.doneText);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        final CalendarPickerView calendar =  findViewById(R.id.calendar_view);
        Date today = new Date();
        calendar.init(today, nextYear.getTime())
                .inMode(RANGE);

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Log.i("onDateSelected: ", String.valueOf(calendar.getSelectedDates()));
                dateRange = new ArrayList<>();
                for(int i=0 ;i<calendar.getSelectedDates().size();i++) {
                    dateRange.add(calendar.getSelectedDates().get(i).toString());
                }
            }

            @Override
            public void onDateUnselected(Date date) {
            }
        });

        doneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putStringArrayListExtra("dateRange",dateRange);
                setResult(Activity.RESULT_OK, resultIntent);

                Snackbar.make(findViewById(android.R.id.content), "Dates are selected.Now you can easily organize your trip", 5000)
                        .setAction("Close", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();

            }
        });




    }
}
