package com.example.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatListActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView messageList;
    private EditText myMessageText;
    private ImageButton sendMessage;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private MessagesAdapter adapter;

    private boolean myMessage = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        getSupportActionBar().hide();

        initialiseDatabase();
        databaseUpdateListener();
        initialiseViews();

        sendMessage.setOnClickListener(this);

    }

    private void initialiseDatabase() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    private void initialiseViews() {
        myMessageText = (EditText) findViewById(R.id.messageText);
        sendMessage = (ImageButton) findViewById(R.id.sendMessage);
        loadMessageList();
    }

    private void loadMessageList() {
        messageList = (RecyclerView) findViewById(R.id.messageList);
        messageList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessagesAdapter();
        messageList.setAdapter(adapter);
    }

    private void databaseUpdateListener() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.setMessages(loadData(snapshot));
                if(myMessage) {
                    messageList.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
                    myMessage = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<Message> loadData(DataSnapshot snapshot) {
        List<Message> messages = new ArrayList<Message>();
        for(DataSnapshot ds: snapshot.getChildren()) {
            messages.add(ds.getValue(Message.class));
        }
        return messages;
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
            myMessage = true;
            AuxiliaryActions.hideKeyboard(this);
            myRef.push().setValue(new Message(message, user));
            myMessageText.setText("");
            messageList.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
        }
    }
}