package com.chancemagno.parley.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chancemagno.parley.R;
import com.chancemagno.parley.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>{
    private ArrayList<User> mUsers = new ArrayList<>();
    private Context mContext;

    public ItemListAdapter(Context context, ArrayList<User> users) {
        mContext = context;
        mUsers = users;
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




        public ItemViewHolder (View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();

        }



        public void bindItem(User user) {
        mNameTextView.setText(user.getFullName());
            Picasso.with(mProfileImageView.getContext()).load(user.getPhotoURL()).fit().centerCrop().into(mProfileImageView);
        }

        @Override
        public void onClick(View v){
            int itemPosition = getLayoutPosition();

        }
    }
}
