package com.temp.chatapps_hayoo.activities;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.fragments.ChatFragment;
import com.temp.chatapps_hayoo.fragments.NewChatFragment;
import com.temp.chatapps_hayoo.utilities.Constants;
import com.temp.chatapps_hayoo.utilities.PreferenceManager;

import java.util.HashMap;

public class HayooActivity extends BaseActivity {

    private PreferenceManager preferenceManager;
    private Button btnSwitchFragment, btnLogout;
    private int fragment = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hayoo);

        btnSwitchFragment = findViewById(R.id.btn_home_new_chat);
        btnLogout = findViewById(R.id.btn_logout);

        preferenceManager = new PreferenceManager(getApplicationContext());
        getToken();
        initFragment();

        btnSwitchFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void initFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new ChatFragment());
        fragmentTransaction.commit();
        btnSwitchFragment.setText("New Chat");
        fragment = 1;
    }

    private void switchFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragment == 1){
            fragmentTransaction.replace(R.id.frame_layout, new NewChatFragment());
            fragmentTransaction.commit();
            btnSwitchFragment.setText("Home");
            fragment = 2;
        }
        else if (fragment == 2){
            fragmentTransaction.replace(R.id.frame_layout, new ChatFragment());
            fragmentTransaction.commit();
            btnSwitchFragment.setText("New Chat");
            fragment = 1;
        }
    }

    private void showToast(String message) {
        Toast.makeText(HayooActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_USERS)
                .document( preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("unable to update token"));
    }

    private void logout(){
        showToast("Logout");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {
            preferenceManager.clear();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }).addOnFailureListener(e -> showToast("unable to logout"));
    }
}