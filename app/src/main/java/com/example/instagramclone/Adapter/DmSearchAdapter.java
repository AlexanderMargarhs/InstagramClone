package com.example.instagramclone.Adapter;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

// Profile image.
import com.example.instagramclone.ChatActivity;
import com.example.instagramclone.DirectMessagesActivity;
import com.squareup.picasso.Picasso;

// Circle image view.
import de.hdodenhof.circleimageview.CircleImageView;

// Firebase imports.
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// File imports.
import com.example.instagramclone.Fragments.ProfileFragment;
import com.example.instagramclone.MainActivity;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;


public class DmSearchAdapter extends RecyclerView.Adapter<DmSearchAdapter.ViewHolder>
{
    private Context mContext;
    private List<User> mUsers;


    // Set user adapter.
    public DmSearchAdapter(Context mContext, List<User> mUsers)
    {
        this.mContext = mContext;
        this.mUsers = mUsers;

    }

    // Create view holder.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item , parent , false);
        return new DmSearchAdapter.ViewHolder(view);
    }

    // Set the view holder.
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position)
    {


        final User user = mUsers.get(position);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());
        Picasso.get().load(user.getImageurl()).placeholder(R.drawable.default_avatar).into(holder.imageProfile);

        // Set items.
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            //TODO: GOTO CHAT WITH USER
            @Override
            public void onClick(View v)
            {
                //Intent intent = new Intent(mContext , MainActivity.class);
                //intent.putExtra("publisherid" , user.getId());
                Activity activity = (Activity) mContext;
                Intent i  = new Intent(mContext,ChatActivity.class);
                i.putExtra("ReceipentId",user.getId());
                activity.startActivity(i);
                activity.overridePendingTransition(R.anim.slide_in_right,R.anim.nothing);

                //Intent i  = new Intent(mContext,ChatActivity.class);
                //.putExtra("ReceipentId",user.getId());
                //mContext.startActivity(i);

                //mContext.startActivity(intent);
            }
        });



    }


    // Get item count.
    @Override
    public int getItemCount()
    {
        return mUsers.size();
    }

    // Create the view holder.
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username;
        public TextView fullname;
        public CircleImageView imageProfile;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            imageProfile = itemView.findViewById(R.id.image_profile);
        }
    }

}
