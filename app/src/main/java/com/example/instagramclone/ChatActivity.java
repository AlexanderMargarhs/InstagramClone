package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.instagramclone.Adapter.ChatMessageAdapter;
import com.example.instagramclone.Adapter.MessageAdapterTest;
import com.example.instagramclone.Model.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


//TODO: Load previous messages before listening for new messages
public class ChatActivity extends AppCompatActivity {
    FloatingActionButton fab;
    Bundle extras;
    String receiverId;
    ArrayList<ChatMessage> messages;

    FirebaseRecyclerOptions<ChatMessage> options;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;

    private RecyclerView recyclerView;
    private MessageAdapterTest adapter; // to change back, change it to ChatMessageAdapter
    //private ChatMessageAdapter adapter;
    private EditText input;
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

            finish();
            overridePendingTransition(R.anim.nothing, R.anim.slide_out_left);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        input = (EditText)findViewById(R.id.input);

        messages = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            receiverId = extras.getString("ReceipentId");
            //The key argument here must match that used in the other activity
            Log.i("RECEIVED",receiverId);
        }
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(receiverId)
                .orderByKey();


        options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, new SnapshotParser<ChatMessage>() {
                    @NonNull
                    @Override
                    public ChatMessage parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return snapshot.getValue(ChatMessage.class);
                    }
                }).build();

        adapter = new MessageAdapterTest(options);//TO WORK ON LEGACY, CHANGE TO ChatMessageAdapter
        recyclerView = findViewById(R.id.list_of_messages);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.getAdapter();
                String messageText = input.getText().toString();

                if(!messageText.equals("")){
                    DatabaseReference reference1 = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("Messages")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(receiverId);
                    DatabaseReference reference2 = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("Messages")
                            .child(receiverId)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ChatMessage msg = new ChatMessage(messageText,FirebaseAuth.getInstance().getCurrentUser().getUid(),receiverId);

                    reference1.push().setValue(msg);
                    reference2.push().setValue(msg);

                }
                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                Log.i("SENDING","SENDING TO " + receiverId);
                Log.i("ID","OUR ID IS " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                // Clear the input
                input.setText("");
            }
        });
        input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (input.getRight() - input.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        //TO OPEN CAMERA, UNCOMMENT THE FOLLOWING
                        // your action here
                        //ITS BUGGED
                        Log.i("CAMERA","OPEN CAM");
                        //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        dispatchTakePictureIntent();
                        //try {
                        //    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        //} catch (ActivityNotFoundException e) {
                            // display error state to the user
                        //}

                        return true;
                    }
                }
                return false;
            }
        });

        Log.i("CURRENT USER",FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference reference1 = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("Messages")
                .child(receiverId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference reference2 = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(receiverId);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user: snapshot.getChildren()) {

                    //Log.i("SNAPSHOT",user.toString());
                    //this is all you need to get a specific user by Uid
                    ChatMessage msg = user.getValue(ChatMessage.class);
                    if(msg.getRecipient().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        msg.setRead(true);
                        String msgid = user.getKey();
                        Map<String,Object> itmToChange= new HashMap<>();
                        itmToChange.put(msgid,msg);
                        reference1.updateChildren(itmToChange);
                    }
                    //**********************************************
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user: snapshot.getChildren()) {

                    //Log.i("SNAPSHOT",user.toString());
                    //this is all you need to get a specific user by Uid
                    ChatMessage msg = user.getValue(ChatMessage.class);
                    if(msg.getRecipient().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        msg.setRead(true);
                        String msgid = user.getKey();
                        Map<String,Object> itmToChange= new HashMap<>();
                        itmToChange.put(msgid,msg);
                        reference2.updateChildren(itmToChange);
                    }
                    //**********************************************
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.i("EXCEPTION",ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("REQUEST,RESULT", String.valueOf(requestCode) + " " + String.valueOf(resultCode));
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");

            String date = dateFormat.format(new Date().getTime());
            StorageReference storageReference = FirebaseStorage.getInstance("gs://instagramclone-4ade2.appspot.com").getReference("media_chat")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"_"+receiverId+"_"+ date);

            //Bitmap imageBitmap = (Bitmap) extras.get("data");

            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            //byte[] uploadData = baos.toByteArray();
            String uri = data.getStringExtra(MediaStore.EXTRA_OUTPUT);
            File f = new File(currentPhotoPath);

            Uri contentUri = Uri.fromFile(f);

            UploadTask uploadTask = storageReference.putFile(contentUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            //createNewPost(imageUrl);
                            Log.i("METADATA",imageUrl);
                            DatabaseReference reference1 = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("Messages")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(receiverId);
                            DatabaseReference reference2 = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("Messages")
                                    .child(receiverId)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            ChatMessage msg = new ChatMessage("",FirebaseAuth.getInstance().getCurrentUser().getUid(),receiverId);
                            msg.setMessageImage(imageUrl);
                            reference1.push().setValue(msg);
                            reference2.push().setValue(msg);
                        }
                    });
                }
            });
        }


    }
}