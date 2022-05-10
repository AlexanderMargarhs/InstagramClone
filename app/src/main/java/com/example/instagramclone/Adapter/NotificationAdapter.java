package com.example.instagramclone.Adapter;

// Profile image.
import com.squareup.picasso.Picasso;

import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

// File imports.
import com.example.instagramclone.Fragments.PostDetailFragment;
import com.example.instagramclone.Fragments.ProfileFragment;
import com.example.instagramclone.Model.Notification;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>
{

    private Context mContext;
    private List<Notification> mNotifications;

    // Constructor for the notification adapter.
    public NotificationAdapter(Context mContext, List<Notification> mNotifications)
    {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    // Set ViewHolder.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item , parent , false);
        return new NotificationAdapter.ViewHolder(view);
    }

    // Handles the notifications with all the necessary user info.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        final Notification notification = mNotifications.get(position);
        holder.text.setText(notification.getText());

        getUserInfo(holder.imageProfile, holder.username , notification.getUserid());

        if (notification.isIspost()) // Shows image if it's a post.
        {
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostid());
        }
        else
        {
            holder.postImage.setVisibility(View.GONE);
        }

        // Set items that we are showing.
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit();
                if (notification.isIspost())  // Shows image if it's a post.
                {
                    editor.putString("postid" , notification.getPostid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new PostDetailFragment()).commit();
                }
                else
                {
                    editor.putString("profileid" , notification.getUserid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new ProfileFragment()).commit();
                }
            }
        });

    }

    // Get item count.
    @Override
    public int getItemCount()
    {
        return mNotifications.size();
    }

    // Set view holder.
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView imageProfile;
        public ImageView postImage;
        public TextView username;
        public TextView text;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
        }
    }

    // Get user info.
    private void getUserInfo(final ImageView imageView , final TextView username , String publisherid)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImageurl()).placeholder(R.drawable.default_avatar).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }

    // Get the Post's image url and show it.
    private void getPostImage (final ImageView imageView , String postid)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Post post = dataSnapshot.getValue(Post.class);
                assert post != null;
                Picasso.get().load(post.getPostimage()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }
}
