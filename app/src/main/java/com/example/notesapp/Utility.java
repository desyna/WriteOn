package com.example.notesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class Utility {
    static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    static CollectionReference getCollectionReferenceForNotes(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("notes")
                .document(currentUser.getUid()).collection("my_notes");
    }

    static CollectionReference getCollectionReferenceForLog(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getUid()).collection("loginHistory");
    }

    static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("dd/MM/yy").format(timestamp.toDate());
    }
}
