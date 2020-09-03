package com.example.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChatListActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView messageList;
    private EditText myMessageText;
    private ImageButton sendMessage;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseListAdapter<Message> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        getSupportActionBar().hide();

        initialiseDatabase();
        initialiseViews();
        databaseUpdateListener();
        sendMessage.setOnClickListener(this);

    }

    private void initialiseDatabase() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    private void initialiseViews() {
        messageList = (RecyclerView) findViewById(R.id.messageList);
        myMessageText = (EditText) findViewById(R.id.messageText);
        sendMessage = (ImageButton) findViewById(R.id.sendMessage);
    }

    private void databaseUpdateListener() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showData(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showData(DataSnapshot snapshot) {
        for(DataSnapshot ds: snapshot.getChildren()) {
            Log.d("message", ds.getValue(Message.class).getMessageText());
        }
    }

    @Override
    public void onClick(View view) { //for sending message button
        String message = myMessageText.getText().toString();
        String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        if(message.isEmpty()) {
            myMessageText.setError(getString(R.string.blank_field));
            myMessageText.requestFocus();
        }

        else {
            myRef.push().setValue(new Message(message, user));
            myMessageText.setText("");
        }
    }
}