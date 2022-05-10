package com.example.instagramclone.Adapter;

import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

// For image loading and caching.
import com.bumptech.glide.Glide;

// File imports.
import com.example.instagramclone.Fragments.PostDetailFragment;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.R;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>
{

    private Context context;
    private List<Post> mPosts;

    // Create Photo Adapter.
    public PhotoAdapter(Context context, List<Post> mPosts)
    {
        this.context = context;
        this.mPosts = mPosts;
    }

    // Create View Holder.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.photos_item, parent , false);
        return new PhotoAdapter.ViewHolder(view);
    }

    // Handles the images.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        final Post post = mPosts.get(position);
        Glide.with(context).load(post.getPostimage()).into(holder.postImage);

        holder.postImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit();
                editor.putString("postid" , post.getPostid());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

    }

    // Get item count.
    @Override
    public int getItemCount()
    {
        return mPosts.size();
    }

    // Set View Holder.
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView postImage;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image);
        }
    }

}


