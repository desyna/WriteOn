package com.example.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SecurityActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LoginHistoryAdapter loginAdapter;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_security);

        recyclerView = findViewById(R.id.recycle_log);
        backBtn = findViewById(R.id.back_btn);

        setupRecycleView();
        backBtn.setOnClickListener((v)-> startActivity(new Intent(SecurityActivity.this, MainActivity.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void setupRecycleView(){
        Query query = Utility.getCollectionReferenceForLog().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<LoginHistory> options = new FirestoreRecyclerOptions.Builder<LoginHistory>().setQuery(query, LoginHistory.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loginAdapter = new LoginHistoryAdapter(options, this);
        recyclerView.setAdapter(loginAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginAdapter.startListening(); // Mulai mendengarkan perubahan data saat aktivitas dimulai
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginAdapter.stopListening(); // Hentikan mendengarkan perubahan data saat aktivitas berhenti
    }
}