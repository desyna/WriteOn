package com.example.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    NoteAdapter noteAdapter;
    FloatingActionButton addNote;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    LinearLayout menuFav, menuNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        addNote = findViewById(R.id.add_note);
        recyclerView = findViewById(R.id.recycle_view);
        menuBtn = findViewById(R.id.menu_btn);
        menuFav = findViewById(R.id.favorite);
        menuNote = findViewById(R.id.notes);

        addNote.setOnClickListener((v)-> startActivity(new Intent(MainActivity.this, NoteDetailActivity.class)));
//        menuBtn.setOnClickListener((v)-> showMenu());
        menuBtn.setOnClickListener((v)-> startActivity(new Intent(MainActivity.this, MenuActivity.class)));
        setupRecycleView();
        menuFav.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, FavoriteActivity.class)));
        menuNote.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, AllNoteActivity.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

//    void showMenu(){
//        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuBtn);
//        popupMenu.getMenu().add("Logout");
//        popupMenu.show();
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if (item.getTitle() == "Logout"){
//                    FirebaseAuth.getInstance().signOut();
//                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                    return true;
//                }
//                return false;
//            }
//        });
//    }

    void setupRecycleView(){
        Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}