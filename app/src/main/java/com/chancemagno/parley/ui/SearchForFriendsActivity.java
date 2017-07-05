package com.chancemagno.parley.ui;

import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;
import com.chancemagno.parley.R;
import com.chancemagno.parley.adapters.ItemListAdapter;
import com.chancemagno.parley.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchForFriendsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String searchQuery;
    String searchParameter;
    private ItemListAdapter mAdapter;
    ArrayList<User> users;
    @Bind(R.id.searchEditText) TextView mSearchEditText;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_friends);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(SearchForFriendsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        createTextViewChangeListener();
    }

    public void createTextViewChangeListener(){
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchQuery = formatSearchQuery().trim();
                if(searchQuery.contains(" ")){
                    searchParameter = "profile/fullName";
                    searchUsers();
                } else {
                    searchParameter = "profile/firstName";
                    searchUsers();
                }

            }
        });
    }

   public String formatSearchQuery(){
           searchQuery = WordUtils.capitalizeFully(mSearchEditText.getText().toString());
            return searchQuery;
    }

    public void searchUsers(){
         users = new ArrayList<>();
      Query ref = FirebaseDatabase.getInstance().getReference().child("users").orderByChild(searchParameter).equalTo(searchQuery);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(dataSnapshot.child("users").child(getKey().), "key");
                Log.i("user", String.valueOf(dataSnapshot.child("U5KtMIyyt6Vhurp0iP7vj9bOZjb2").child("profile").getValue()));

//                mAdapter = new ItemListAdapter(getApplicationContext(), users);
//                mRecyclerView.setAdapter(mAdapter);
//                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchForFriendsActivity.this);
//                mRecyclerView.setLayoutManager(layoutManager);
//                mRecyclerView.setHasFixedSize(true);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
