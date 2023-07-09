package com.temp.chatapps_hayoo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.utilities.Constants;
import com.temp.chatapps_hayoo.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView btnRegister;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_LOGIN)) {
            Intent intent = new Intent(getApplicationContext(), HayooActivity.class);
            startActivity(intent);
            finish();
        }

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLogin()) {
                    login();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void login(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, etEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, etPassword.getText().toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    preferenceManager.putBoolean(Constants.KEY_IS_LOGIN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                    preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                    preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                    preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                    preferenceManager.putString(Constants.KEY_LATITUDE, documentSnapshot.getDouble(Constants.KEY_LATITUDE).toString());
                    preferenceManager.putString(Constants.KEY_LONGITUDE, documentSnapshot.getDouble(Constants.KEY_LONGITUDE).toString());
                    Intent intent = new Intent(getApplicationContext(), HayooActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    showToast("Unable to login");
                }
            }
        });
    }

    private Boolean validateLogin(){
        if(etEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()){
            showToast("Enter valid email address");
            return false;
        }
        else if(etPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        }
        else return true;
    }

}