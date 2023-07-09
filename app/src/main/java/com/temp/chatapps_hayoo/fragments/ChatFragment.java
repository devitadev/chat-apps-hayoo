package com.temp.chatapps_hayoo.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.activities.ChatActivity;
import com.temp.chatapps_hayoo.adapters.RecentConversationAdapter;
import com.temp.chatapps_hayoo.listeners.ConversationListener;
import com.temp.chatapps_hayoo.models.ChatMessage;
import com.temp.chatapps_hayoo.models.User;
import com.temp.chatapps_hayoo.utilities.Constants;
import com.temp.chatapps_hayoo.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;

public class ChatFragment extends Fragment implements ConversationListener {

    private PreferenceManager preferenceManager;
    private View view;

    private RecyclerView recyclerView;
    private ArrayList<ChatMessage> conversations;
    private RecentConversationAdapter recentConversationAdapter;
    private FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.rv_recent_chat);
        preferenceManager = new PreferenceManager(getContext());
        init();
        listenConversations();

        return view;
    }

    private void init () {
        conversations = new ArrayList<>();
        recentConversationAdapter = new RecentConversationAdapter(getContext(), conversations, ChatFragment.this);
        recyclerView.setAdapter(recentConversationAdapter);
        db = FirebaseFirestore.getInstance();
    }

    private void listenConversations () {
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderID = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverID = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderID(senderID);
                    chatMessage.setReceiverID(receiverID);
                    if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderID)) {
                        chatMessage.setConversationImage(documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE));
                        chatMessage.setConversationName(documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME));
                        chatMessage.setConversationID(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                        chatMessage.setConversationEmail(documentChange.getDocument().getString(Constants.KEY_RECEIVER_EMAIL));
                        chatMessage.setConversationLatitude(documentChange.getDocument().getDouble(Constants.KEY_RECEIVER_LATITUDE));
                        chatMessage.setConversationLongitude(documentChange.getDocument().getDouble(Constants.KEY_RECEIVER_LONGITUDE));
                    } else {
                        chatMessage.setConversationImage(documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE));
                        chatMessage.setConversationName(documentChange.getDocument().getString(Constants.KEY_SENDER_NAME));
                        chatMessage.setConversationID(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                        chatMessage.setConversationEmail(documentChange.getDocument().getString(Constants.KEY_SENDER_EMAIL));
                        chatMessage.setConversationLatitude(documentChange.getDocument().getDouble(Constants.KEY_SENDER_LATITUDE));
                        chatMessage.setConversationLongitude(documentChange.getDocument().getDouble(Constants.KEY_SENDER_LONGITUDE));
                    }
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                    chatMessage.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    conversations.add(chatMessage);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for(int i=0; i< conversations.size(); i++) {
                        String senderID = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverID = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (conversations.get(i).getSenderID().equals(senderID) && conversations.get(i).getReceiverID().equals(receiverID)) {
                            conversations.get(i).setMessage(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                            conversations.get(i).setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations, (obj1, obj2) -> obj2.getDateObject().compareTo(obj1.getDateObject()));
            recentConversationAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }
    };


    @Override
    public void onConversationClicked(User user) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}