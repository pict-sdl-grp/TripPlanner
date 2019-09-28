package com.project.sdl.tripplanner.DashboardPackage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AddTrips extends AppCompatActivity {

    EditText tripname;
    ArrayList<String> trip_name;
    TextView name;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;
    JSONObject tripsJson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trips);
        tripname=(EditText)findViewById(R.id.txttripname);
        name=(TextView)findViewById(R.id.textView4);
        mAuth=FirebaseAuth.getInstance();
        trip_name=new ArrayList<>();
    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.createtrip, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btncreatetrip:
                // User chose the "Settings" item, show the app settings UI...
                String tripName=tripname.getText().toString();
                if(tripName.isEmpty())
                {
                    Toast.makeText(AddTrips.this,"Please Enter trip name to create!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    name.setText(tripName);
                    user=mAuth.getCurrentUser();
                    mDatabase=FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("trips/"+user.getUid()).child("tripnames").push().setValue(tripName);
                    finish();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
