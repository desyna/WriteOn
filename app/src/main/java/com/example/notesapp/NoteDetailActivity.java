package com.example.notesapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveBtn, backBtn, favoriteBtn;
    TextView pageTitleTextView, deleteNoteTextViewBtn;
    String title, content, docId;
    boolean isEditMode = false;
    boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_detail);

        titleEditText = findViewById(R.id.note_title);
        contentEditText = findViewById(R.id.note_content);
        saveBtn = findViewById(R.id.save_btn);
        backBtn = findViewById(R.id.back_btn);
//        pageTitleTextView = findViewById(R.id.note_title);
        favoriteBtn = findViewById(R.id.fav_btn);
        deleteNoteTextViewBtn = findViewById(R.id.delete_note);

        //receive data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");
        isFavorite = getIntent().getBooleanExtra("favorite", false);

        if (docId!=null && !docId.isEmpty()){
            isEditMode= true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        updateFavoriteIcon();

        if (isEditMode){
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        saveBtn.setOnClickListener((v)-> saveNote());
        backBtn.setOnClickListener((v)-> finish());
        deleteNoteTextViewBtn.setOnClickListener((v) -> deleteNoteFromFirebase());
        favoriteBtn.setOnClickListener((v) -> toggleFavorite());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if (noteTitle==null || noteTitle.isEmpty()){
            titleEditText.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());
        note.setFavorite(isFavorite);

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if (isEditMode){
//          // update note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else {
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Utility.showToast(NoteDetailActivity.this, "Note add succesfully");
                    finish();
                }else {
                    Utility.showToast(NoteDetailActivity.this, "Failed while adding note");
                }
            }
        });
    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // note is deleted
                    Utility.showToast(NoteDetailActivity.this, "Note deleted succesfully");
                    finish();
                }else {
                    Utility.showToast(NoteDetailActivity.this, "Failed while deleting note");
                }
            }
        });
    }

    void toggleFavorite() {
        isFavorite = !isFavorite;
        updateFavoriteIcon();
        saveNote();
    }

    void updateFavoriteIcon() {
        if (isFavorite) {
            favoriteBtn.setImageResource(R.drawable.baseline_bookmark_24);
        } else {
            favoriteBtn.setImageResource(R.drawable.baseline_bookmark_border_24);
        }
    }
}