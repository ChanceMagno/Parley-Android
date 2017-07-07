package com.chancemagno.parley.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chancemagno.parley.R;
import com.chancemagno.parley.models.User;
import com.chancemagno.parley.ui.SearchForFriendsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.chancemagno.parley.ui.SearchForFriendsActivity.*;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>{
    private ArrayList<User> mUsers = new ArrayList<>();
    private ArrayList<String> mKeys = new ArrayList<>();
    private Context mContext;
    String loggedInUser;

    public ItemListAdapter(Context context, ArrayList<User> users, ArrayList<String> keys) {
        mContext = context;
        mUsers = users;
        mKeys = keys;

       loggedInUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }



    @Override
    public ItemListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_friends_profiles, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItemListAdapter.ItemViewHolder holder, int position) {
        holder.bindItem(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.nameTextView) TextView mNameTextView;
        @Bind(R.id.friendInviteTextView) TextView mFriendInviteTextView;
        @Bind(R.id.eventInviteTextView) TextView mEventInviteTextView;
        @Bind(R.id.profileImageView) ImageView mProfileImageView;
        @Bind(R.id.addFriendFloatingActionButton) FloatingActionButton mAddFriendButton;




        public ItemViewHolder (View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();

        }



        public void bindItem(User user) {
        mNameTextView.setText(user.getFullName());
            Picasso.with(mProfileImageView.getContext()).load(user.getPhotoURL()).fit().centerCrop().into(mProfileImageView);
            mAddFriendButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            int itemPosition = getLayoutPosition();
            if(v == mAddFriendButton){
                sendFriendRequest(itemPosition);
            }

        }

        public void sendFriendRequest(final int itemPosition){
         DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(mKeys.get(itemPosition)).child("friendRequests");
            ref.push().setValue(loggedInUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    updateUsersFriendRequestList(itemPosition);
                }
            });
        }

        public void updateUsersFriendRequestList(final int itemPosition){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(loggedInUser).child("sentFriendRequests");
            ref.push().setValue(mKeys.get(itemPosition)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(mContext, String.format("Friend Request sent to %s", mUsers.get(itemPosition).getFullName()), Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}
