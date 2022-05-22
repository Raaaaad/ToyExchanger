package com.rp.toyexchanger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.rp.toyexchanger.data.User;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        editTextFullName = findViewById(R.id.full_name_edit_text);
        editTextAge = findViewById(R.id.age_edit_text);
        editTextEmail = findViewById(R.id.email_edit_text);
        editTextPassword = findViewById(R.id.password_edit_text);

        Button registerUserButton = findViewById(R.id.register_user_button);
        registerUserButton.setOnClickListener(v -> {
            registerUser();
        });
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();

        boolean error = false;
        if (fullName.isEmpty()) {
            editTextFullName.setError("Full name is required");
            editTextFullName.requestFocus();
            error = true;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            error = true;
        }

        if (age.isEmpty()) {
            editTextAge.setError("Age is required");
            editTextAge.requestFocus();
            error = true;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            error = true;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password is too short, please provide at least 6 characters.");
            editTextPassword.requestFocus();
            error = true;
        }

        if (error)
            return;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(result -> {
                    if (result.isSuccessful()) {
                        User user = new User(fullName, age, email);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .setValue(user).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                                    }
                                });

                    } else {
                        Toast.makeText(RegisterActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                    }
                });

    }
}