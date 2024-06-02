package com.example.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfileActivity extends AppCompatActivity {

    EditText editText, password;
    TextView title;
    Button save;
    ImageButton backBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private static final String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        editText = findViewById(R.id.edit_form);
        password = findViewById(R.id.conf_pass);
        title = findViewById(R.id.edit_title);
        save = findViewById(R.id.update);
        backBtn = findViewById(R.id.back_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        // Mendapatkan data dari Intent
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");

        // Log untuk memeriksa data yang diterima
        Log.d(TAG, "Received name: " + name);
        Log.d(TAG, "Received email: " + email);

        if (name != null) {
            editText.setText(name);
            title.setText("Name");
        } else if (email != null) {
            editText.setText(email);
            title.setText("Email");
            password.setVisibility(View.VISIBLE);
        }

        backBtn.setOnClickListener((v)-> startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        save.setOnClickListener(v -> {
            String newValue = editText.getText().toString();
            if (name != null) {
                // Update nama pengguna
                updateUserProfile(newValue);
            } else if (email != null) {
                // Update email pengguna
                updateEmail(newValue);
            }
        });
    }

    private void updateUserProfile(String newName) {
        // Update nama pengguna
        user.updateProfile(new UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User name updated.");
                        Toast.makeText(EditProfileActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.e(TAG, "Failed to update name", task.getException());
                        Toast.makeText(EditProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEmail(String newEmail) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password.getText().toString());

        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updateEmail(newEmail)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "User email address updated.");
                                        Toast.makeText(EditProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Log.e(TAG, "Failed to update email", task1.getException());
                                        Toast.makeText(EditProfileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.e(TAG, "Reauthentication failed", task.getException());
                        Toast.makeText(EditProfileActivity.this, "Reauthentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}