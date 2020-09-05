package com.example.firebasechat;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private List<Message> messages;
    private FirebaseUser firebaseUser;
    private Context context;

    public MessagesAdapter(FirebaseUser firebaseUser, Context context) {
        messages = new ArrayList<Message>();
        this.firebaseUser = firebaseUser;
        this.context = context;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessagesViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout messageLayout;

        private TextView messageText;
        private TextView nameText;
        private TextView dateText;


        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            messageLayout = (RelativeLayout) itemView.findViewById(R.id.messageLayout);

            messageText = (TextView) itemView.findViewById(R.id.messageText);
            nameText = (TextView) itemView.findViewById(R.id.nameText);
            dateText = (TextView) itemView.findViewById(R.id.dateText);
        }

        public void bind(Message message) {
            if(message.getMessageUser().getEmail().equals(firebaseUser.getEmail())) {
                messageLayout.setBackground(context.getDrawable(R.drawable.background_my_message));
                nameText.setTextColor(context.getColor(R.color.myMessageTextColor));
                dateText.setTextColor(context.getColor(R.color.myMessageTextColor));
                messageText.setTextColor(context.getColor(R.color.colorBlack));
            }

            messageText.setText(message.getMessageText());
            nameText.setText(message.getMessageUser().getName());
            dateText.setText(DateFormat.format("HH:mm dd.MM.yyyy ", message.getMessageTime()));
        }
    }

}
