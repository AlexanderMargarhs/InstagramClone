package com.example.instagramclone.Adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Model.ChatMessage;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
* LEGACY ADAPTER: IF YOU WANT TO REVERT BACK, JUST CHANGE APPROPRIATE LINES IN ChatActivity
*
* */

public class ChatMessageAdapter extends FirebaseRecyclerAdapter<ChatMessage, ChatMessageAdapter.chatViewholder> {
    User user;
    String username;

    public ChatMessageAdapter(
            @NonNull FirebaseRecyclerOptions<ChatMessage> options)
    {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull chatViewholder holder, int position, @NonNull ChatMessage model) {
        for(ChatMessage m : getSnapshots()){
            //Log.i("DATATEST",m.toString());
        }
        Boolean lastItem = false;
        if(position==getItemCount()-1){
            lastItem = true;
        }
        else{
            lastItem=false;
        }

        DatabaseReference uref = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference("Users").child(model.getSender());
        //Used to get username given the user ID since everything is handled in firebase using usedID
        //Boolean finalLastItem = lastItem;//in order to display "Read" only on last item
        Boolean finalLastItem = lastItem;
        uref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dts)
            {
                ChatMessageAdapter.this.user = dts.getValue(User.class);


                if(model.getSender().equals(FirebaseAuth.getInstance().getUid())){

                    holder.body.setBackground(ContextCompat.getDrawable(holder.body.getContext(), R.drawable.circular_view));
                    holder.body.setTextColor(Color.WHITE);

                }

                holder.username.setText(ChatMessageAdapter.this.user.getUsername());
                holder.body.setText(model.getBody());
                holder.message_time.setText(model.getTimeSent());
                Log.i("SEPARATOR","---------------------------------------------");
                Log.i("GET READ", String.valueOf(model.getRead()));
                //Log.i("IS FINAL", String.valueOf(finalLastItem));
                Log.i("MODEL SENDER",model.getSender().toString());
                Log.i("USERID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                if(model.getRead() && finalLastItem && model.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                   holder.message_read.setText("Seen");
                   holder.message_read.setCompoundDrawablesWithIntrinsicBounds(R.drawable.verified_user_1_, 0, 0, 0);
                }
                else
                    holder.message_read.setText("");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }

    @NonNull
    @Override
    public chatViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);

        return new chatViewholder(view);
    }

    public class chatViewholder extends RecyclerView.ViewHolder
    {
        public TextView username;
        public TextView body;
        public TextView message_time;
        public TextView message_read;
        public chatViewholder(@NonNull View itemView)
        {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            body = itemView.findViewById(R.id.body);
            message_time = itemView.findViewById(R.id.message_time);
            message_read = itemView.findViewById(R.id.message_read);
        }


    }
}
