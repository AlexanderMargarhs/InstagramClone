package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.instagramclone.Adapter.DmSearchAdapter;
import com.example.instagramclone.Adapter.UserAdapter;
import com.example.instagramclone.Model.ChatMessage;
import com.example.instagramclone.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/*
Activity to list user DMs
 */
public class DirectMessagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DmSearchAdapter userAdapter;
    private ArrayList<User> mUsers;
    private TextView dmUsername;
    private SocialAutoCompleteTextView searchBar;
    private String profileid;
    private  User user;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Log.i("s","BACK PRESSED");
            finish();
            overridePendingTransition(R.anim.nothing, R.anim.slide_out_left);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_messages);

        recyclerView = findViewById(R.id.recycler_view_users);
        dmUsername = findViewById(R.id.dmUsername);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchBar = findViewById(R.id.search_bar);

        mUsers = new ArrayList<>();
        userAdapter = new DmSearchAdapter(this , mUsers );
        recyclerView.setAdapter(userAdapter);
        SharedPreferences prefs = this.getSharedPreferences("PREFS" , Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid" , "none");

        userInfo();
        readUsers();
        searchBar.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                //
            }
        });
    }



    private void userInfo()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {


                user = dataSnapshot.getValue(User.class);
                dmUsername.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }

    private void searchUser (String s)
    {
        recyclerView.setVisibility(View.VISIBLE);

        Query query = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("Users").orderByChild("username").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }
    private void readUsers ()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (TextUtils.isEmpty(searchBar.getText().toString()))
                {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        //Log.i("msg",snapshot.toString());
                        for(DataSnapshot a : snapshot.getChildren()){
                            ChatMessage message = a.getValue(ChatMessage.class);
                            DatabaseReference uref = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference("Users").child(snapshot.getKey());
                            uref.addValueEventListener(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dts)
                                {
                                    user = dts.getValue(User.class);

                                    if(!mUsers.contains(user) && !user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    {
                                        mUsers.add(user);
                                        userAdapter.notifyDataSetChanged();
                                    }

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                { }
                            });


                        Log.i("hey","FOUND");
                        }

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }
}