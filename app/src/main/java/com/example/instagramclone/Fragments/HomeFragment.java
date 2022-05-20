package com.example.instagramclone.Fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

// Firebase imports.
import com.example.instagramclone.DirectMessagesActivity;
import com.example.instagramclone.MainActivity;
import com.example.instagramclone.PostActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

// File imports.
import com.example.instagramclone.Adapter.PostAdapter;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.R;


public class HomeFragment extends Fragment
{
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    private List<String> followingList;

    private ProgressBar progressBar;
    private ImageView dmsBtn;
    // Create the home view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext() , postList);
        recyclerView.setAdapter(postAdapter);
        dmsBtn = view.findViewById(R.id.dmBtn);

        //Will add slide in animation to transit to the DMs
        dmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) getContext();
                activity.startActivity(new Intent(getActivity(), DirectMessagesActivity.class));
                activity.overridePendingTransition(R.anim.slide_in_right,R.anim.nothing);
            }
        });

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL , false);

        progressBar = view.findViewById(R.id.progress_circular);

        checkFollowing();

        return view;
    }

    // Check the users our user is following to show the new posts.
    private void checkFollowing()
    {
        followingList = new ArrayList<>();

        DatabaseReference reference = getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    followingList.add(snapshot.getKey());
                }
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }

    // Read posts.
    private void readPosts ()
    {
        DatabaseReference reference = getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference("Posts");

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                postList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {

                    Post post = snapshot.getValue(Post.class);

                    for (String id : followingList)
                    {
                        if (post.getPublisher().equals(id))
                        {
                            postList.add(post);
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }
}
