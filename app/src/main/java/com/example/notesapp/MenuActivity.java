package com.example.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {
    LinearLayout security, faq, theme;
    TextView signout;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        security = findViewById(R.id.security_menu);
        faq = findViewById(R.id.faq_menu);
        theme = findViewById(R.id.theme_menu);
        signout = findViewById(R.id.signout);
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener((v) -> startActivity(new Intent(MenuActivity.this, MainActivity.class)));
        security.setOnClickListener((v) -> startActivity(new Intent(MenuActivity.this, SecurityActivity.class)));
        signout.setOnClickListener((v) -> signOut());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    void signOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MenuActivity.this, LoginActivity.class));
    }
}
