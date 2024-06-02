package com.example.notesapp;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView profileNameTextView, profileEmailTextView, profileLocationTextView;
    Button resetPassword, editName;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    ImageButton backBtn;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profile_image);
        profileNameTextView = findViewById(R.id.profile_name);
        profileEmailTextView = findViewById(R.id.profile_email);
        profileLocationTextView = findViewById(R.id.profile_location);
        editName = findViewById(R.id.edit_name);
        resetPassword = findViewById(R.id.reset_password);
        backBtn = findViewById(R.id.back_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user != null) {
            profileNameTextView.setText(user.getDisplayName());
            profileEmailTextView.setText(user.getEmail());

            if (user.getPhotoUrl() != null) {
                Picasso.get().load(user.getPhotoUrl()).into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.profile_pict1);
            }
            fetchAndDisplayLocation();
        }

        backBtn.setOnClickListener((v)-> startActivity(new Intent(ProfileActivity.this, MainActivity.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editName.setOnClickListener(v -> {
            // Menambahkan data nama ke Intent
            String name = user.getDisplayName();
            Log.d(TAG, "Sending name: " + name);
            // Membuat Intent untuk pindah ke EditProfileActivity
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("name", name);

            // Menjalankan EditProfileActivity
            startActivity(intent);
        });

        resetPassword.setOnClickListener(v -> {
            // Dapatkan email pengguna saat ini
            String email = user.getEmail();
            if (email != null && !email.isEmpty()) {
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email reset password telah dikirim.");
                                Toast.makeText(ProfileActivity.this, "Email reset password telah dikirim.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "Gagal mengirim email reset password.", task.getException());
                                Toast.makeText(ProfileActivity.this, "Gagal mengirim email reset password.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Log.e(TAG, "Email pengguna tidak ditemukan.");
                Toast.makeText(ProfileActivity.this, "Email pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAndDisplayLocation() {
        // Query the latest login history for the current user
        if (user != null) {
            String userUid = user.getUid();
            db.collection("users")
                    .document(userUid)
                    .collection("loginHistory")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            Double latitude = documentSnapshot.getDouble("latitude");
                            Double longitude = documentSnapshot.getDouble("longitude");

                            if (latitude != null && longitude != null) {
                                String address = getAddressFromLocation(this, latitude, longitude);
                                profileLocationTextView.setText(address);
                            } else {
                                Log.e(TAG, "Latitude or longitude is null");
                                profileLocationTextView.setText("Location not available");
                            }
                        } else {
                            Log.e(TAG, "No login history found.");
                            profileLocationTextView.setText("Location not available");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to fetch login history: ", e);
                        profileLocationTextView.setText("Failed to fetch location");
                    });
        }
    }

    public String getAddressFromLocation(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressLine = address.getLocality(); // Alamat kota
                String countryName = address.getCountryName(); // Nama negara
                return addressLine + ", " + countryName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address not found";
    }
}