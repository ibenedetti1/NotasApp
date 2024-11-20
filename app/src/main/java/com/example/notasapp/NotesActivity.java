package com.example.notasapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NotesActivity extends AppCompatActivity {

    private EditText titleField, contentField;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);


        titleField = findViewById(R.id.note_title);
        contentField = findViewById(R.id.note_content);
        Button saveButton = findViewById(R.id.save_button);
        Button backToLoginButton = findViewById(R.id.back_to_login_button);
        ListView notesList = findViewById(R.id.notes_list);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        saveButton.setOnClickListener(v -> saveNote());
        backToLoginButton.setOnClickListener(v -> goToLogin()); // Acción del botón
        loadNotes(notesList);
    }

    private void saveNote() {
        String title = titleField.getText().toString().trim();
        String content = contentField.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO Notes (user_id, title, content) VALUES (?, ?, ?)", new Object[]{userId, title, content});
        Toast.makeText(this, "Nota guardada con éxito", Toast.LENGTH_SHORT).show();

        // Limpiar el formulario después de guardar la nota
        titleField.setText("");
        contentField.setText("");

        // Recargar la lista de notas
        loadNotes(findViewById(R.id.notes_list));
        db.close();
    }

    private void loadNotes(ListView notesList) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, title, content FROM Notes WHERE user_id=?", new String[]{String.valueOf(userId)});

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[]{"title", "content"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0
        );

        notesList.setAdapter(adapter);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
