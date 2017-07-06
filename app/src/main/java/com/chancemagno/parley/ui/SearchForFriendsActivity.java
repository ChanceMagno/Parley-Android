package com.chancemagno.parley.ui;

import android.content.Context;
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
import android.widget.Toast;

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
    ArrayList<String> keys;
    @Bind(R.id.searchEditText)
    TextView mSearchEditText;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private TextWatcher textWatcher;

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
                if (user == null) {
                    Intent intent = new Intent(SearchForFriendsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        createTextViewChangeListener();
    }

    public void createTextViewChangeListener() {
        mSearchEditText.addTextChangedListener(textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                users = new ArrayList<User>();
                keys = new ArrayList<String>();
                mAdapter = new ItemListAdapter(getApplicationContext(), users, keys);
                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchForFriendsActivity.this);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchQuery = formatSearchQuery().trim();
                if (searchQuery.contains(" ")) {
                    searchParameter = "profile/fullName";
                    searchUsers();
                } else {
                    searchParameter = "profile/firstName";
                    searchUsers();
                }

            }
        });
    }

    public String formatSearchQuery() {
        searchQuery = WordUtils.capitalizeFully(mSearchEditText.getText().toString());
        return searchQuery;
    }

    public void searchUsers() {
        Query ref = FirebaseDatabase.getInstance().getReference().child("users").orderByChild(searchParameter).equalTo(searchQuery);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = new ArrayList<>();
                keys = new ArrayList<String>();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    keys.add(messageSnapshot.getKey());
                    String firstName = (String) messageSnapshot.child("profile").child("firstName").getValue();
                    String lastName = (String) messageSnapshot.child("profile").child("lastName").getValue();
                    String email = (String) messageSnapshot.child("profile").child("email").getValue();
                    String photoURL = (String) messageSnapshot.child("profile").child("photoURL").getValue();
                    User newUser = new User(firstName, lastName, email, photoURL);
                    users.add(newUser);
                }
                mAdapter = new ItemListAdapter(getApplicationContext(), users, keys);
                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchForFriendsActivity.this);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        createTextViewChangeListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (textWatcher != null) {
            mSearchEditText.removeTextChangedListener(textWatcher);
        }
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);

        }
    }

}
