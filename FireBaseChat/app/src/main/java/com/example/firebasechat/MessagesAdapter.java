package com.example.firebasechat;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private List<Message> messages;

    public MessagesAdapter() {
        messages = new ArrayList<Message>();
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
        private TextView messageText;
        private TextView nameText;
        private TextView dateText;


        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.messageText);
            nameText = (TextView) itemView.findViewById(R.id.nameText);
            dateText = (TextView) itemView.findViewById(R.id.dateText);
        }

        public void bind(Message message) {
            messageText.setText(message.getMessageText());
            nameText.setText(message.getMessageUser());
            dateText.setText(DateFormat.format("HH:mm dd.MM.yyyy ", message.getMessageTime()));
        }
    }

}
