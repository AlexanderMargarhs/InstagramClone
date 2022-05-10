package com.example.instagramclone.Fragments;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

// Firebase imports.
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// Social view imports.
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

// File imports.
import com.example.instagramclone.Adapter.TagAdapter;
import com.example.instagramclone.Adapter.UserAdapter;
import com.example.instagramclone.Model.HashTag;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;

public class SearchFragment extends Fragment
{
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewTags;
    private TagAdapter tagAdapter;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    private List<HashTag> mTagsPost;

    private SocialAutoCompleteTextView searchBar;

    private List<String> mAvailablehashtags;
    private List<String> mAvailablehashtagsCount;

    // Create view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewTags = view.findViewById(R.id.recycler_view_tags);
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));

        searchBar = view.findViewById(R.id.search_bar);
        mTagsPost = new ArrayList<>();
        mAvailablehashtags = new ArrayList<>();
        mAvailablehashtagsCount = new ArrayList<>();

        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext() , mUsers , true);
        recyclerView.setAdapter(userAdapter);

        tagAdapter = new TagAdapter(getContext() , mAvailablehashtags , mAvailablehashtagsCount);
        recyclerViewTags.setAdapter(tagAdapter);

        readUsers();
        readTags();

        // Search bar.
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
                filter(s.toString());
            }
        });

        return view;
    }

    // Search based on filter.
    private void filter(String text)
    {
        List<String> mSearchtags = new ArrayList<>();
        List<String> mSearchTagsCount = new ArrayList<>();

        for (String s : mAvailablehashtags)
        {
            if (s.toLowerCase().contains(text.toLowerCase()))
            {
                mSearchtags.add(s);
                mSearchTagsCount.add(mAvailablehashtagsCount.get(mAvailablehashtags.indexOf(s)));
            }
        }
        tagAdapter.filterList(mSearchtags , mSearchTagsCount);
    }

    // Read tags.
    private void readTags()
    {
        FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mAvailablehashtagsCount.clear();
                mAvailablehashtags.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    mAvailablehashtags.add(snapshot.getKey());
                    mAvailablehashtagsCount.add(snapshot.getChildrenCount() + " ");
                }

                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }

    // Search for user.
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

    // Read users.
    private void readUsers ()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("Users");
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
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }

    // When the search fragment starts we set the firebase and recycle view with some users and tags.
    @Override
    public void onStart()
    {
        super.onStart();

        final ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<>(getContext());

        FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mAvailablehashtagsCount.clear();
                mAvailablehashtags.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    hashtagAdapter.add(new Hashtag(snapshot.getKey() , (int)snapshot.getChildrenCount()));
                    mAvailablehashtags.add(snapshot.getKey());
                    mAvailablehashtagsCount.add(snapshot.getChildrenCount() + " ");
                    Log.d("HashTag" , snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {}
        });

        searchBar.setHashtagAdapter(hashtagAdapter);
    }
}
