package com.temp.chatapps_hayoo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.listeners.ConversationListener;
import com.temp.chatapps_hayoo.models.ChatMessage;
import com.temp.chatapps_hayoo.models.User;

import java.util.ArrayList;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ChatMessage> chatMessages;
    private ConversationListener conversationListener;

    public RecentConversationAdapter(Context context, ArrayList<ChatMessage> chatMessages, ConversationListener conversationListener) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.conversationListener = conversationListener;
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @NonNull
    @Override
    public RecentConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_chat , parent, false);
        return new RecentConversationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentConversationAdapter.ViewHolder holder, int position) {
        holder.imageProfile.setImageBitmap(getUserImage(chatMessages.get(position).getConversationImage()));
        holder.tvName.setText(chatMessages.get(position).getConversationName());
        holder.tvRecentMessage.setText(chatMessages.get(position).getMessage());

//        if (!chatMessages.get(position).isConversationReadStatus()) {
//            holder.cvUser.setCardBackgroundColor(Color.parseColor("#5EBB86FC"));
//        }

        holder.cvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setId(chatMessages.get(position).getConversationID());
                user.setName(chatMessages.get(position).getConversationName());
                user.setImageProfile(chatMessages.get(position).getConversationImage());
                user.setEmail(chatMessages.get(position).getConversationEmail());
                user.setLatitude(chatMessages.get(position).getConversationLatitude());
                user.setLongitude(chatMessages.get(position).getConversationLongitude());
                conversationListener.onConversationClicked(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRecentMessage;
        RoundedImageView imageProfile;
        CardView cvUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvRecentMessage = itemView.findViewById(R.id.tv_recent_message);
            imageProfile = itemView.findViewById(R.id.image_profile);
            cvUser = itemView.findViewById(R.id.cv_user);
        }
    }
}
