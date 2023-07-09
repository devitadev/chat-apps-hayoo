package com.temp.chatapps_hayoo.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.temp.chatapps_hayoo.Network.ApiClient;
import com.temp.chatapps_hayoo.Network.ApiService;
import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.adapters.ChatAdapter;
import com.temp.chatapps_hayoo.models.ChatMessage;
import com.temp.chatapps_hayoo.models.User;
import com.temp.chatapps_hayoo.utilities.Constants;
import com.temp.chatapps_hayoo.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {

    private TextView tvName;
    private ImageView btnBack, btnLocation;
    private RoundedImageView imageProfile;
    private EditText etMessage;
    private Button btnSend;

    private User receiverUser;
    private ArrayList<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private String conversationID = null;
    private boolean isReceiverAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvName = findViewById(R.id.tv_name);
        btnBack = findViewById(R.id.btn_back);
        btnLocation = findViewById(R.id.btn_location);
        imageProfile = findViewById(R.id.image_profile);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.rv_chat);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        loadReceiverDetails();
        init();
        listenMessages();

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, LocationActivity.class);
                intent.putExtra(Constants.KEY_USER, receiverUser);
                startActivity(intent);
            }
        });

    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(getApplicationContext(), chatMessages, preferenceManager.getString(Constants.KEY_USER_ID));
        recyclerView.setAdapter(chatAdapter);
        db = FirebaseFirestore.getInstance();
    }

    private void sendMessage () {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.getId());
        message.put(Constants.KEY_MESSAGE, etMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        db.collection(Constants.KEY_COLLECTION_CHAT).add(message);

        if (conversationID != null) {
            updateConversation(etMessage.getText().toString());
        } else {
            HashMap <String, Object> conversation = new HashMap<>();
            conversation.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversation.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversation.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversation.put(Constants.KEY_SENDER_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
            conversation.put(Constants.KEY_SENDER_LATITUDE, Double.valueOf(preferenceManager.getString(Constants.KEY_LATITUDE)));
            conversation.put(Constants.KEY_SENDER_LONGITUDE, Double.valueOf(preferenceManager.getString(Constants.KEY_LONGITUDE)));

            conversation.put(Constants.KEY_RECEIVER_ID, receiverUser.getId());
            conversation.put(Constants.KEY_RECEIVER_NAME, receiverUser.getName());
            conversation.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.getImageProfile());
            conversation.put(Constants.KEY_LAST_MESSAGE, etMessage.getText().toString());
            conversation.put(Constants.KEY_RECEIVER_EMAIL, receiverUser.getEmail());
            conversation.put(Constants.KEY_RECEIVER_LATITUDE, receiverUser.getLatitude());
            conversation.put(Constants.KEY_RECEIVER_LONGITUDE, receiverUser.getLongitude());

            conversation.put(Constants.KEY_TIMESTAMP, new Date());
            addConversation(conversation);
        }

        if (!isReceiverAvailable) {
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.getFcmToken());

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE, etMessage.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA, data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                sendNotification(body.toString());
            } catch (Exception e) {
                showToast(e.getMessage());
            }
        }

        etMessage.setText(null);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messageBody) {
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(), messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull  Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()) {
                    try {
                        if (response.body() != null) {
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if (responseJson.getInt("failure") == 1) {
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showToast("notification sent successfully");
                } else {
                    showToast("Error: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    private void listenAvailabilityOfReceiver() {
        db.collection(Constants.KEY_COLLECTION_USERS).document(
                receiverUser.getId()
        ).addSnapshotListener(ChatActivity.this, (value, error) -> {
            if(error != null) {
                return;
            }
            if(value != null) {
                if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                    int availability = Objects.requireNonNull(
                            value.getLong(Constants.KEY_AVAILABILITY)
                    ).intValue();
                    isReceiverAvailable = availability == 1;
                }
                receiverUser.setFcmToken(value.getString(Constants.KEY_FCM_TOKEN));
                if (receiverUser.getImageProfile() == null) {
                    receiverUser.setImageProfile(value.getString(Constants.KEY_IMAGE));
                    receiverUser.setEmail(value.getString(Constants.KEY_EMAIL));
                    receiverUser.setLatitude(value.getDouble(Constants.KEY_LATITUDE));
                    receiverUser.setLatitude(value.getDouble(Constants.KEY_LONGITUDE));
                    System.out.println("IMAGE=" + value.getString(Constants.KEY_IMAGE));
                    imageProfile.setImageBitmap(getUserImage(receiverUser.getImageProfile()));
                }
            }
        });
    }

    private void listenMessages () {
        db.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.getId())
                .addSnapshotListener(eventListener);
        db.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.getId())
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderID(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    chatMessage.setReceiverID(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_MESSAGE));
                    chatMessage.setConversationEmail(documentChange.getDocument().getString(Constants.KEY_EMAIL));
                    chatMessage.setConversationLatitude(documentChange.getDocument().getDouble(Constants.KEY_LATITUDE));
                    chatMessage.setConversationLongitude(documentChange.getDocument().getDouble(Constants.KEY_LONGITUDE));
                    chatMessage.setDateTime(getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)));
                    chatMessage.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.getDateObject().compareTo(obj2.getDateObject()));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            }
            else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                recyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            recyclerView.setVisibility(View.VISIBLE);
        }
        if (conversationID == null) {
            checkForConversation();
        }
    };

    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        if (receiverUser.getImageProfile() != null) {
            imageProfile.setImageBitmap(getUserImage(receiverUser.getImageProfile()));
        }
        tvName.setText(receiverUser.getName());
    }

    private Bitmap getUserImage(String encodedImage){
        if (encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        else return null;
    }

    private String getReadableDateTime (Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversation (HashMap<String, Object> conversation) {
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS).add(conversation)
                .addOnSuccessListener(documentReference -> conversationID = documentReference.getId());
    }

    private void updateConversation (String message) {
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversationID);
        documentReference.update(Constants.KEY_LAST_MESSAGE, message, Constants.KEY_TIMESTAMP, new Date());
    }

    private void checkForConversation () {
        if (chatMessages.size() != 0) {
            checkForConversationRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.getId()
            );
            checkForConversationRemotely(
                    receiverUser.getId(),
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    private void checkForConversationRemotely (String senderID, String receiverID) {
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS).whereEqualTo(Constants.KEY_SENDER_ID, senderID)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverID).get().addOnCompleteListener(conversationOnCompleteListener);

    }

    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationID = documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}