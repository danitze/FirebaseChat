package com.example.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatListActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView messageList;
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
        initialiseAdapter();
        sendMessage.setOnClickListener(this);

    }

    private void initialiseDatabase() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    private void initialiseViews() {
        messageList = (ListView) findViewById(R.id.messageList);
        myMessageText = (EditText) findViewById(R.id.messageText);
        sendMessage = (ImageButton) findViewById(R.id.sendMessage);
    }

    private FirebaseListOptions<Message> setAdapterOptions() {
        return new FirebaseListOptions.Builder<Message>()
                .setQuery(myRef, Message.class)
                .setLayout(R.layout.message_item)
                .build();
    }

    private void initialiseAdapter() {
        adapter = new FirebaseListAdapter<Message>(setAdapterOptions()) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Message model, int position) {
                TextView messageText = (TextView) v.findViewById(R.id.messageText);
                TextView nameText = (TextView) v.findViewById(R.id.nameText);
                TextView dateText = (TextView) v.findViewById(R.id.dateText);

                messageText.setText(model.getMessageText());
                nameText.setText(model.getMessageUser());
                dateText.setText(DateFormat.format("yyyy.MM.dd HH:mm:ss", model.getMessageTime()));
            }
        };
        messageList.setAdapter(adapter);
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