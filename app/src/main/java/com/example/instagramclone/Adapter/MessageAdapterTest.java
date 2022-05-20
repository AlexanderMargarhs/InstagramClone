package com.example.instagramclone.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.squareup.picasso.Picasso;

public class MessageAdapterTest extends FirebaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder> {
    User user;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MEDIA_SENT = 3;
    private static final int VIEW_TYPE_MEDIA_RECEIVED = 4;
    public MessageAdapterTest(
            @NonNull FirebaseRecyclerOptions<ChatMessage> options)
    {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull ChatMessage model) {
        ChatMessage msg = getItem(position);
        int flag=0;
        if(position == getItemCount()-1){
            flag = 1;
        }
        switch(holder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((chatViewholder)holder).bind(msg,flag);
                break;
            case VIEW_TYPE_MESSAGE_SENT:
                ((chatViewholder)holder).bind(msg,flag);
                break;
            case VIEW_TYPE_MEDIA_RECEIVED:
                ((chatViewHolderImage)holder).bind(msg,flag);
                break;
            case VIEW_TYPE_MEDIA_SENT:
                ((chatViewHolderImage)holder).bind(msg,flag);
                break;
        }
    }


    @Override
    public int getItemViewType(int position) {
        ChatMessage message = (ChatMessage) getItem(position);
        //TODO: Add image support
        if (message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            // If the current user is the sender of the message
            //return VIEW_TYPE_MESSAGE_SENT;
            if(message.getMessageImage().equals(""))
               return VIEW_TYPE_MESSAGE_SENT;
            else
                return VIEW_TYPE_MEDIA_SENT;
        } else {
            //return VIEW_TYPE_MESSAGE_RECEIVED;
            // If some other user sent the message
            if(message.getMessageImage().equals(""))
                return VIEW_TYPE_MESSAGE_RECEIVED;
            else
                return VIEW_TYPE_MEDIA_RECEIVED;

        }
    }
    /*
    @Override
    protected void onBindViewHolder(@NonNull chatViewholder holder, int position, @NonNull ChatMessage model) {
        //for(ChatMessage m : getSnapshots()){
        //    Log.i("DATATEST",m.toString());
        //}
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
                MessageAdapterTest.this.user = dts.getValue(User.class);

                holder.username.setText(MessageAdapterTest.this.user.getUsername());
                holder.body.setText(model.getBody());
                holder.message_time.setText(model.getTimeSent());
                //if message is read, the message is the last one and sender is equal to the current user
                //Then we want to display "Seen" with the emoji next to it
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
    */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        //Since views change a little, actually the only thing that changes is the orientation
        //We can use the same viewholder
        //No new elements are added if we send or receive message
        //Fields are the same: username, body, time sent and read status

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_sent_layout, parent, false);
            return new chatViewholder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_received_layout, parent, false);
            return new chatViewholder(view);
        }
        else if(viewType==VIEW_TYPE_MEDIA_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_received_layout, parent, false);
            return new chatViewHolderImage(view);
        }
        else if(viewType==VIEW_TYPE_MEDIA_SENT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_sent_layout, parent, false);
            return new chatViewHolderImage(view);
        }
        return null;
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
        void bind(ChatMessage message,int flag) {
            body.setText(message.getBody());

            // Format the stored timestamp into a readable String using method.
            message_time.setText(message.getTimeSent());

            username.setText(message.getSender());

            DatabaseReference uref = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference("Users").child(message.getSender());
            uref.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dts)
                {
                    User usr = dts.getValue(User.class);
                    username.setText( usr.getUsername());
                    //if message is read, the message is the last one and sender is equal to the current user
                    //Then we want to display "Seen" with the emoji next to it
                    if(message.getRead() && flag==1 && message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        message_read.setText("Seen");
                        message_read.setCompoundDrawablesWithIntrinsicBounds(R.drawable.verified_user_1_, 0, 0, 0);
                    }
                    else
                        message_read.setText("");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                { }
            });


        }

    }

    public class chatViewHolderImage extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView messageImage;
        public TextView message_time;
        public TextView message_read;
        public chatViewHolderImage(@NonNull View itemView)
        {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            messageImage = itemView.findViewById(R.id.msgImg);
            message_time = itemView.findViewById(R.id.message_time);
            message_read = itemView.findViewById(R.id.message_read);
        }
        void bind(ChatMessage message,int flag) {
            Picasso.get().load(message.getMessageImage()).into(messageImage);

            // Format the stored timestamp into a readable String using method.
            message_time.setText(message.getTimeSent());


            DatabaseReference uref = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference("Users").child(message.getSender());
            uref.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dts)
                {
                    User usr = dts.getValue(User.class);
                    username.setText( usr.getUsername());

                    //if message is read, the message is the last one and sender is equal to the current user
                    //Then we want to display "Seen" with the emoji next to it
                    if(message.getRead() && flag==1 && message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        message_read.setText("Seen");
                        message_read.setCompoundDrawablesWithIntrinsicBounds(R.drawable.verified_user_1_, 0, 0, 0);
                    }
                    else
                        message_read.setText("");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                { }
            });

        }
    }
}
