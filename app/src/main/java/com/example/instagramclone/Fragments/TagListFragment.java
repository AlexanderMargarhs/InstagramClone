package com.example.instagramclone.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// Firebase imports.
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

// File imports.
import com.example.instagramclone.Adapter.PostAdapter;
import com.example.instagramclone.Model.HashTag;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.R;

public class TagListFragment extends Fragment
{
    private String jsonString;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<HashTag> tagList;

    // Create view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("TagPrefs" , Context.MODE_PRIVATE);
        jsonString = sharedPreferences.getString("tagList" , "none");

        Gson gson = new Gson();
        Type type = new TypeToken<List<HashTag>>() {}.getType();
        tagList = gson.fromJson(jsonString , type);

        readMultiplePosts();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext() , postList);
        recyclerView.setAdapter(postAdapter);

        return view;
    }

    // Read posts.
    private void readMultiplePosts()
    {
        FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    for (HashTag tag : tagList)
                    {
                        if (snapshot.getKey().equals(tag.getPostid()))
                        {
                            Post post = snapshot.getValue(Post.class);
                            postList.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }
}