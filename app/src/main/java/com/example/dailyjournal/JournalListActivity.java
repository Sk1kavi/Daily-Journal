package com.example.dailyjournal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class JournalListActivity extends AppCompatActivity {
    private JournalDbHelper dbHelper;
    private SimpleCursorAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Daily Journal");
        }

        // Initialize database helper
        dbHelper = new JournalDbHelper(this);

        // Set up list view
        listView = findViewById(R.id.listView);
        setupAdapter();

        // Set up Floating Action Button (replaced menu add button)
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, NewEntryActivity.class));
        });

        // Set item click listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, JournalEntryActivity.class);
            intent.putExtra("ENTRY_ID", id);
            startActivity(intent);
        });
        // Search button setup
       @SuppressLint("MissingInflatedId") FloatingActionButton searchButton = findViewById(R.id.fabsearch);
        searchButton.setOnClickListener(v -> {
            startActivity(new Intent(JournalListActivity.this, SearchActivity.class));
        });
    }

    private void setupAdapter() {
        adapter = new SimpleCursorAdapter(
                this,
                R.layout.journal_list_item,
                null,
                new String[]{JournalDbHelper.COLUMN_DATE, JournalDbHelper.COLUMN_DESCRIPTION},
                new int[]{R.id.tvDate, R.id.tvDescription},
                0
        );
        listView.setAdapter(adapter);
        refreshJournalList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshJournalList();
    }

    private void refreshJournalList() {
        Cursor newCursor = dbHelper.getAllEntries();
        adapter.changeCursor(newCursor);
    }

    // New options menu implementation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options, menu); // Changed from menu_journal_list
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            showSettings();
            return true;
        } else if (id == R.id.menu_about) {
            showAbout();
            return true;
        } else if (id == R.id.menu_exit) {
            confirmExit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettings() {
        Intent intent = new Intent(this, FragmentContainerActivity.class);
        intent.putExtra("fragment_type", "settings");
        startActivity(intent);
    }

    private void showAbout() {
        Intent intent = new Intent(this, FragmentContainerActivity.class);
        intent.putExtra("fragment_type", "about");
        startActivity(intent);
    }

    private void confirmExit() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Journal")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                .setNegativeButton("Cancel", null)
                .show();
    }
}