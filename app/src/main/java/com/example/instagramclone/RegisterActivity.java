package com.example.instagramclone;

import java.util.HashMap;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

// Firebase imports.
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// File import.
import com.example.instagramclone.Model.User;

public class RegisterActivity extends AppCompatActivity
{
    private EditText username;
    private EditText fullname;
    private EditText email;
    private EditText password;
    private Button register;
    private TextView txtLogin;

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private ProgressDialog pd;

    private static int usernameFlag = 0;

    // When activity is created.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txtLogin = findViewById(R.id.txt_login);

        auth = FirebaseAuth.getInstance();

        // Change to login page.
        txtLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
            }
        });

        // When user tries to register.
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password))
                {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Empty credential!", Toast.LENGTH_SHORT).show();
                }
                else if (str_password.length() < 6)
                {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    register(str_username , str_fullname , str_email , str_password);
                }
            }
        });
    }

    // Check is username is unique.
    private void checkUsernameAvailability()
    {
        final String txt_username = username.getText().toString();

        DatabaseReference mUsersRed = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("Users");
        mUsersRed.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);
                    if (txt_username.equals(user.getUsername()))
                    {
                        usernameFlag = 1;
                        Toast.makeText(RegisterActivity.this, "Username already taken! Try something else.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                usernameFlag = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });

    }

    // Register user if everything is set correctly and notify the user for any problems.
    private void register(final String username , final String fullname , String email , String password)
    {
        checkUsernameAvailability();
        if (usernameFlag == 0)
        {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful() && usernameFlag == 0)
                    {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userid = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance("https://instagramclone-4ade2-default-rtdb.firebaseio.com/").getReference().child("Users").child(userid);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", username.toLowerCase());
                        hashMap.put("fullname", fullname);
                        hashMap.put("bio", "");
                        hashMap.put("imageurl", "default");

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    pd.dismiss();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        });

                    }
                    else if (!task.isSuccessful())
                    {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, "You can't register with this email and password!", Toast.LENGTH_SHORT).show();
                    }
                    else if (usernameFlag != 0)
                    {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, "Please change the username!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
