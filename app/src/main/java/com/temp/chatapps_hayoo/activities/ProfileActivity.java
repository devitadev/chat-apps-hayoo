package com.temp.chatapps_hayoo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.impl.model.Preference;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.fragments.ChatFragment;
import com.temp.chatapps_hayoo.fragments.MapsFragment;
import com.temp.chatapps_hayoo.models.User;
import com.temp.chatapps_hayoo.utilities.Constants;
import com.temp.chatapps_hayoo.utilities.PreferenceManager;

import java.util.HashMap;

public class ProfileActivity extends BaseActivity {

    private ImageView btnBack;
    private Button btnHome, btnLogout;
    private User user;

    private RoundedImageView imageProfile;
    private TextView tvName, tvPhoneNumber, tvEmail;
    private ImageView btnAddNewChat;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnBack = findViewById(R.id.btn_back);
        btnHome = findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);
        imageProfile = findViewById(R.id.image_profile);
        tvName = findViewById(R.id.tv_name);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        tvEmail = findViewById(R.id.tv_email);
        btnAddNewChat = findViewById(R.id.btn_add_new_chat);

        user = (User) getIntent().getSerializableExtra(Constants.KEY_USER);

        imageProfile.setImageBitmap(getUserImage(user.getImageProfile()));
        tvName.setText(user.getName());
        tvPhoneNumber.setText(user.getPhoneNumber());
        tvEmail.setText(user.getEmail());

        setFragment();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAddNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(Constants.KEY_USER, user);
                startActivity(intent);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HayooActivity.class);
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

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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
        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}