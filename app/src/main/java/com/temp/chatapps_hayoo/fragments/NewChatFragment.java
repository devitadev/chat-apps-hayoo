package com.temp.chatapps_hayoo.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.activities.ChatActivity;
import com.temp.chatapps_hayoo.activities.ProfileActivity;
import com.temp.chatapps_hayoo.adapters.NewChatAdapter;
import com.temp.chatapps_hayoo.listeners.UserListener;
import com.temp.chatapps_hayoo.models.User;
import com.temp.chatapps_hayoo.utilities.Constants;
import com.temp.chatapps_hayoo.utilities.PreferenceManager;

import java.util.ArrayList;


public class NewChatFragment extends Fragment implements UserListener {

    private PreferenceManager preferenceManager;
    private View view;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_chat, container, false);
        recyclerView = view.findViewById(R.id.rv_new_chat);
        preferenceManager = new PreferenceManager(getContext());
        getUsers();
        return view;
    }

    private void getUsers(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String currentUserID = preferenceManager.getString(Constants.KEY_USER_ID);
                if (task.isSuccessful() && task.getResult() != null) {
                    ArrayList<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        if (currentUserID.equals(queryDocumentSnapshot.getId())) {
                            continue;
                        }
                        User user = new User();
                        user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                        user.setName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                        user.setPhoneNumber(queryDocumentSnapshot.getString(Constants.KEY_PHONE_NUMBER));
                        user.setImageProfile(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                        user.setLatitude(queryDocumentSnapshot.getDouble(Constants.KEY_LATITUDE));
                        user.setLongitude(queryDocumentSnapshot.getDouble(Constants.KEY_LONGITUDE));
                        user.setFcmToken(queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN));
                        user.setId(queryDocumentSnapshot.getId());
                        users.add(user);
                    }
                    if (users.size() > 0) {
                        NewChatAdapter newChatAdapter = new NewChatAdapter(getContext(), users,  NewChatFragment.this);
                        recyclerView.setAdapter(newChatAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

                    } else {
                        System.out.println("No Users Available !");
                    }
                } else {
                    System.out.println("No Users Available !");
                }
            }
        });
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }

    @Override
    public void onDetailUserClicked(User user) {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}