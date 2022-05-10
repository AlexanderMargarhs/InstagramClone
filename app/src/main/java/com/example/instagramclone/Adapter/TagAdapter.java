package com.example.instagramclone.Adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

// Firebase imports.
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

// File imports.
import com.example.instagramclone.Fragments.TagListFragment;
import com.example.instagramclone.Model.HashTag;
import com.example.instagramclone.R;


public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>
{

    private Context mContext;
    private List<String> mTags;
    private List<String> mTagsCount;

    // Set tag adapter.
    public TagAdapter(Context mContext, List<String> mTags, List<String> mTagsCount)
    {
        this.mContext = mContext;
        this.mTags = mTags;
        this.mTagsCount = mTagsCount;
    }

    // Create view holder.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item , parent , false);

        return new TagAdapter.ViewHolder(view);
    }

    // Set the view holder.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
    {

        final List<HashTag> mTagsPost = new ArrayList<>();

        DatabaseReference mTagsRef = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("HashTags").child(mTags.get(position));
        mTagsRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mTagsPost.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    HashTag ht = snapshot.getValue(HashTag.class);
                    mTagsPost.add(ht);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });

        holder.tag.setText("#" + mTags.get(position));
        holder.noOfPosts.setText(mTagsCount.get(position) + " posts");

        // Set item view.
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Gson gson = new Gson();
                String json = gson.toJson(mTagsPost);

                SharedPreferences.Editor editor = mContext.getSharedPreferences("TagPrefs" , Context.MODE_PRIVATE).edit();
                editor.putString("tagList" , json);
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new TagListFragment()).commit();
            }
        });
    }

    // Get item count.
    @Override
    public int getItemCount()
    {
        return mTags.size();
    }

    // Create view holder.
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView tag;
        public TextView noOfPosts;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            tag = itemView.findViewById(R.id.hash_tag);
            noOfPosts = itemView.findViewById(R.id.no_of_posts);
        }
    }

    // Set filter list.
    public void filterList(List<String> filteredTags , List<String> filteredTagsCount)
    {
        this.mTags = filteredTags;
        this.mTagsCount = filteredTagsCount;
        notifyDataSetChanged();
    }
}