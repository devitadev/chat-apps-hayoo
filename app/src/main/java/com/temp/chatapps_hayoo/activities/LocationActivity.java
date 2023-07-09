package com.temp.chatapps_hayoo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.fragments.MapsFragment;
import com.temp.chatapps_hayoo.models.User;
import com.temp.chatapps_hayoo.utilities.Constants;
import com.temp.chatapps_hayoo.utilities.PreferenceManager;

import java.util.HashMap;

public class LocationActivity extends BaseActivity {

    private ImageView btnBack;
    private Button btnHome, btnLogout;
    private User user;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        btnBack = findViewById(R.id.btn_back);
        btnHome = findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);

        user = (User) getIntent().getSerializableExtra(Constants.KEY_USER);

        setFragment();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationActivity.this, HayooActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void setFragment(){
        MapsFragment mapsFragment = new MapsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_USER, user);
        mapsFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, mapsFragment);
        fragmentTransaction.commit();
    }

    private void logout() {
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

    private void showToast(String message) {
        Toast.makeText(LocationActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}