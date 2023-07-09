package com.temp.chatapps_hayoo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.models.ChatMessage;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ChatMessage> chatMessages;
    private String senderID;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(Context context, ArrayList<ChatMessage> chatMessages, String senderID) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.senderID = senderID;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_received_message, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        holder.tvTextMessage.setText(chatMessages.get(position).getMessage()); 
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public int getItemViewType (int position){
        if (chatMessages.get(position).getSenderID().equals(senderID)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTextMessage;
        ConstraintLayout cvMessage, cvReceivedMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTextMessage = itemView.findViewById(R.id.tv_text_message);
            cvMessage = itemView.findViewById(R.id.cv_message);
            cvReceivedMessage = itemView.findViewById(R.id.cv_received_message);
        }
    }
}
