package com.example.dailyjournal;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private JournalDbHelper dbHelper;
    private SimpleCursorAdapter adapter;
    private ListView listView;
    private EditText searchEditText;
    private Button dateFilterButton;
    private String selectedDate = "";

    private Button clearDateButton,clearTextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        searchEditText = findViewById(R.id.searchEditText);
        dateFilterButton = findViewById(R.id.dateFilterButton);
        listView = findViewById(R.id.searchResultsListView);
        clearDateButton = findViewById(R.id.clearDateButton);
        clearTextButton = findViewById(R.id.clearTextButton);

        // Initialize database helper
        dbHelper = new JournalDbHelper(this);

        // Setup adapter
        adapter = new SimpleCursorAdapter(
                this,
                R.layout.journal_list_item,
                null,
                new String[]{JournalDbHelper.COLUMN_DATE, JournalDbHelper.COLUMN_DESCRIPTION},
                new int[]{R.id.tvDate, R.id.tvDescription},
                0
        );
        listView.setAdapter(adapter);

        // Setup search listeners
        setupSearchListeners();
        clearDateButton.setOnClickListener(v -> {
            selectedDate = "";
            dateFilterButton.setText("Filter by Date");
        });
        clearTextButton.setOnClickListener(v -> {
            searchEditText.setText("");
        });
    }

    private void setupSearchListeners() {
        // Text search
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        // Date filter
        dateFilterButton.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    try {
                        // Create date in yyyy-MM-dd format first
                        String dbFormatDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);

                        // Convert to database storage format (MMM dd,yyyy)
                        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        Date date = dbFormat.parse(dbFormatDate);
                        selectedDate = displayFormat.format(date);

                        // Update button text
                        dateFilterButton.setText(selectedDate);

                        // Perform search
                        performSearch();

                        // Debug log
                        Log.d(TAG, "Searching for date: " + selectedDate);
                    } catch (ParseException e) {
                        Log.e(TAG, "Date parsing error", e);
                        Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void performSearch() {
        String searchText = searchEditText.getText().toString().trim();
        Log.d(TAG, "Performing search - Text: '" + searchText + "', Date: '" + selectedDate + "'");

        Cursor cursor = dbHelper.searchEntries(searchText, selectedDate);
        Log.d(TAG, "Found " + cursor.getCount() + " results");

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No entries found", Toast.LENGTH_SHORT).show();
        }

        adapter.changeCursor(cursor);
    }
}