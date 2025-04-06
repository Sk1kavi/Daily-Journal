package com.example.dailyjournal;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewEntryActivity extends AppCompatActivity {
    private EditText etDescription;
    private Spinner spinnerMood;
    private EditText etDate;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("New Journal Entry");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etDescription = findViewById(R.id.etDescription);
        spinnerMood = findViewById(R.id.spinnerMood);
        etDate = findViewById(R.id.etDate);
        calendar = Calendar.getInstance();

        // Setup mood spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mood_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMood.setAdapter(adapter);

        // Date picker
        etDate.setOnClickListener(v -> showDatePicker());
        updateDateLabel();
    }

    private void showDatePicker() {
        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateLabel() {
        String dateFormat = "MMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        etDate.setText(sdf.format(calendar.getTime()));
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }
    };

    public void onSaveClick(View view) {
        String date = etDate.getText().toString();
        String description = etDescription.getText().toString();
        String mood = spinnerMood.getSelectedItem().toString();

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter your journal content", Toast.LENGTH_SHORT).show();
            return;
        }

        JournalDbHelper dbHelper = new JournalDbHelper(this);
        long id = dbHelper.addEntry(date, description, mood, "", ""); // Empty strings for photos/videos

        if (id != -1) {
            Toast.makeText(this, "Entry saved", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Indicate success to calling activity
            finish(); // Close this activity and return
        } else {
            Toast.makeText(this, "Error saving entry", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}