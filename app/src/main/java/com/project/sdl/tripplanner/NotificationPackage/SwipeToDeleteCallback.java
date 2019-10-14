package com.project.sdl.tripplanner.NotificationPackage;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private MyListAdapter mAdapter;
    public static ArrayList<String> swipePosition;
    private RecyclerView rv;

    public SwipeToDeleteCallback(MyListAdapter adapter, ArrayList<String> positionArray,RecyclerView recyclerView) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        swipePosition = new ArrayList<>(positionArray);
        mAdapter = adapter;
        rv = recyclerView;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();
        Log.i("onSwiped: ",swipePosition.get(position));
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("notifications/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.child(swipePosition.get(position)).setValue(null);

        mAdapter.deleteItem(position,rv);
        swipePosition.remove(position);
    }
}
