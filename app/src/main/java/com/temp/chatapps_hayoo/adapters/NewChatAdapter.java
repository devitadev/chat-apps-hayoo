package com.temp.chatapps_hayoo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.activities.ProfileActivity;
import com.temp.chatapps_hayoo.listeners.UserListener;
import com.temp.chatapps_hayoo.models.User;
import com.temp.chatapps_hayoo.utilities.Constants;

import java.util.ArrayList;

public class NewChatAdapter extends RecyclerView.Adapter<NewChatAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> users;
    private final UserListener userListener;

    public NewChatAdapter(Context context, ArrayList<User> users, UserListener userListener) {
        this.context = context;
        this.users = users;
        this.userListener = userListener;
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_chat , parent, false);
        return new NewChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(users.get(position).getName());
        holder.imageProfile.setImageBitmap(getUserImage(users.get(position).getImageProfile()));

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { userListener.onDetailUserClicked(users.get(position)); }
        });

        holder.btnAddNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListener.onUserClicked(users.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        RoundedImageView imageProfile;
        CardView cvUser;
        ImageView btnAddNewChat;
        LinearLayout btnDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            imageProfile = itemView.findViewById(R.id.image_profile);
            cvUser = itemView.findViewById(R.id.cv_user);
            btnAddNewChat = itemView.findViewById(R.id.btn_add_new_chat);
            btnDetails = itemView.findViewById(R.id.btn_details);
        }
    }

}
