package com.project.sdl.tripplanner.NotificationPackage;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.project.sdl.tripplanner.R;

import java.util.ArrayList;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{

    private ArrayList<NotificationListItem> listdata;
    private Context context;
    private NotificationListItem mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
//    public static Boolean undoFlag = false;

    // RecyclerView recyclerView;
    public MyListAdapter(ArrayList<NotificationListItem> listdata,Context context) {
        this.listdata = new ArrayList<>(listdata);
        this.context = context;
//        undoFlag = false;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.notification_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    public void deleteItem(int position, RecyclerView rv) {
        mRecentlyDeletedItem = listdata.get(position);
        mRecentlyDeletedItemPosition = position;
        listdata.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar(rv);
    }

    private void showUndoSnackbar(RecyclerView rv) {
//        undoFlag = false;
//
        Snackbar snackbar = Snackbar.make(rv, "deleted",
                Snackbar.LENGTH_LONG);
        snackbar.setAction("undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyListAdapter.this.undoDelete();

            }
        });

        snackbar.show();
    }

    private void undoDelete() {

        listdata.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        String id  = mRecentlyDeletedItem.getExactTime().substring(11);

        SwipeToDeleteCallback.swipePosition.add(mRecentlyDeletedItemPosition,id);

        Notification notification = new Notification(id,"share",
                mRecentlyDeletedItem.getString(),
                mRecentlyDeletedItem.getExactTime(),mRecentlyDeletedItem.getSender(),mRecentlyDeletedItem.getSenderPhotoUrl(), ServerValue.TIMESTAMP,true);

        mDatabase.child("notifications/").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id).setValue(notification);

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NotificationListItem myListData = listdata.get(position);
        holder.notificationSender.setText(listdata.get(position).getSender());
        holder.notificationDate.setText(listdata.get(position).getDate());
        holder.notificationMessage.setText(listdata.get(position).getMessage());
        holder.notificationBlock.setBackgroundColor(listdata.get(position).getColor());
        Glide.with(context)
                .load(listdata.get(position).getSenderPhotoUrl())
                .thumbnail(Glide.with(context).load(R.drawable.profile1))
                .into(holder.circularImageView);
//        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(),"click on item: ",Toast.LENGTH_LONG).show();
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView notificationSender;
        public TextView notificationDate;
        public TextView notificationMessage;
        public CircularImageView circularImageView;
        public LinearLayout notificationBlock;


        public ViewHolder(View itemView) {
            super(itemView);
            this.notificationSender = itemView.findViewById(R.id.notification_sender);
            this.notificationDate = itemView.findViewById(R.id.notification_date);
            this.notificationMessage = itemView.findViewById(R.id.notification_message);
            this.circularImageView = itemView.findViewById(R.id.notification_image);
            this.notificationBlock = itemView.findViewById(R.id.notification_block);
        }
    }
}