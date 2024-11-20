package com.example.notasapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);

        dbHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void loginUser() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username=? AND password=?", new String[]{username, password});

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            Intent intent = new Intent(this, NotesActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Usuario no registrado", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    private void registerUser() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username=?", new String[]{username});

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            db.insert("Users", null, values);
            Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }
}
