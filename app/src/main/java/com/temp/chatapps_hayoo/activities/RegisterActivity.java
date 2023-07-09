package com.temp.chatapps_hayoo.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.utilities.Constants;
import com.temp.chatapps_hayoo.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etName, etPhoneNumber, etLatitude, etLongitude, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView btnLogin, textAddImage;
    private RoundedImageView imageProfile;
    private FrameLayout layoutImage;

    private PreferenceManager preferenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.et_email);
        etName = findViewById(R.id.et_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etLatitude = findViewById(R.id.et_latitude);
        etLongitude = findViewById(R.id.et_longitude);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);
        textAddImage = findViewById(R.id.text_add_image);
        imageProfile = findViewById(R.id.image_profile);
        layoutImage = findViewById(R.id.layout_image);

        preferenceManager = new PreferenceManager(getApplicationContext());

        layoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateRegister()) {
                    register();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void register(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // cek email belum ada sebelumnya
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, etEmail.getText().toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                    showToast("This email have been used before");
                }
                else {
                    HashMap<String, Object> user = new HashMap<>();
                    user.put(Constants.KEY_EMAIL, etEmail.getText().toString());
                    user.put(Constants.KEY_NAME, etName.getText().toString());
                    user.put(Constants.KEY_PHONE_NUMBER, etPhoneNumber.getText().toString());
                    user.put(Constants.KEY_LATITUDE, Float.valueOf(etLatitude.getText().toString()));
                    user.put(Constants.KEY_LONGITUDE, Float.valueOf(etLongitude.getText().toString()));
                    user.put(Constants.KEY_PASSWORD, etPassword.getText().toString());
                    user.put(Constants.KEY_IMAGE, encodedImage);

                    db.collection(Constants.KEY_COLLECTION_USERS).add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            preferenceManager.putBoolean(Constants.KEY_IS_LOGIN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                            preferenceManager.putString(Constants.KEY_NAME, etName.getText().toString());
                            preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                            preferenceManager.putString(Constants.KEY_EMAIL, etEmail.getText().toString());
                            preferenceManager.putString(Constants.KEY_LATITUDE, etLatitude.getText().toString());
                            preferenceManager.putString(Constants.KEY_LONGITUDE, etLongitude.getText().toString());
                            Intent intent = new Intent(getApplicationContext(),  HayooActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(e.getMessage());
                        }
                    });
                }
            }
        });

    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imageProfile.setImageBitmap(bitmap);
                            textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private Boolean validateRegister(){
        if (encodedImage == null){
            showToast("Select profile picture");
            return false;
        }
        else if(etEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()){
            showToast("Enter valid email address");
            return false;
        }
        else if(etName.getText().toString().trim().isEmpty()) {
            showToast("Enter name");
            return false;
        }
        else if(etPhoneNumber.getText().toString().trim().isEmpty()) {
            showToast("Enter phone number");
            return false;
        }
        else if(etLatitude.getText().toString().trim().isEmpty()) {
            showToast("Enter latitude");
            return false;
        }
        else if(etLongitude.getText().toString().trim().isEmpty()) {
            showToast("Enter longitude");
            return false;
        }
        else if(etPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        }
        else if(etConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter confirm password");
            return false;
        }
        else if(!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
            showToast("C onfirm password should matches password");
            return false;
        }
        else return true;
    }

}