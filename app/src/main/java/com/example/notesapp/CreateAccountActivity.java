package com.example.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText, nameEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        nameEditText = findViewById(R.id.et_name);
        progressBar = findViewById(R.id.progress_bar);
        createAccountBtn = findViewById(R.id.create_account_btn);
        loginBtnTextView = findViewById(R.id.login_text_btn);

        createAccountBtn.setOnClickListener(v-> createAccount());
        loginBtnTextView.setOnClickListener(v-> startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void createAccount(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String name = nameEditText.getText().toString();

        boolean isValidated = validateData(email,password,name);
        if(!isValidated){
            return;
        }

        createAccounInFirebase(name,email,password);
    }

    void createAccounInFirebase(String name, String email, String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()){
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> profileTask) {
                                if (profileTask.isSuccessful()) {
                                    Utility.showToast(CreateAccountActivity.this, "Successfully created account, check email to verify");
                                    user.sendEmailVerification();
                                    firebaseAuth.signOut();
                                    finish();
                                } else {
                                    Utility.showToast(CreateAccountActivity.this, "Failed to update profile: " + profileTask.getException().getLocalizedMessage());
                                }
                            }
                        });
                    }
                } else {
                    Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());
                }
            }
        });
    }


    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(TextView.VISIBLE);
            createAccountBtn.setVisibility(TextView.GONE);
        }else{
            progressBar.setVisibility(TextView.GONE);
            createAccountBtn.setVisibility(TextView.VISIBLE);
        }
    }

    boolean validateData(String name, String password, String email){
        // if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        if(email.isEmpty()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordEditText.setError("password length is invalid");
            return false;
        }
        if(name.isEmpty()){
            nameEditText.setError("name is empty");
            return false;
        }
        return true;
    }
}