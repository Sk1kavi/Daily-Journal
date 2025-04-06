package com.example.dailyjournal;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class JournalEntryActivity extends AppCompatActivity {
    private EditText etDescription;
    private long entryId;
    private JournalDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entry);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Journal Entry");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etDescription = findViewById(R.id.etDescription);
        dbHelper = new JournalDbHelper(this);
        entryId = getIntent().getLongExtra("ENTRY_ID", -1);

        if (entryId != -1) {
            loadEntry();
        }
    }

    private void loadEntry() {
        Cursor cursor = dbHelper.getEntry(entryId);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(JournalDbHelper.COLUMN_DESCRIPTION));
            etDescription.setText(description);
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_journal_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveEntry();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveEntry() {
        String description = etDescription.getText().toString();
        if (description.isEmpty()) {
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        int updated = dbHelper.updateEntry(entryId, description, "", "", "");
        if (updated > 0) {
            Toast.makeText(this, "Entry updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating entry", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEntry() {
        dbHelper.deleteEntry(entryId);
        Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this journal entry?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    deleteEntry(); // Existing delete function
                    Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
