package com.example.mycanvas;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView registerUser;
    private EditText editTextEmail, editTextAge;
    private TextInputEditText editTextPassword;
    private RadioButton male, female;
    private FirebaseServices firebaseServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseServices = new FirebaseServices();

        setContentView(R.layout.activity_register_user);

        registerUser = (Button) findViewById(R.id.Reg_User);
        registerUser.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.Reg_email);
        editTextPassword = (TextInputEditText) findViewById(R.id.Password_Layout);
        editTextAge = (EditText) findViewById(R.id.Reg_Age);

        male = findViewById(R.id.Reg_Male);
        female = findViewById(R.id.Reg_Female);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Reg_User:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String age = editTextAge.getText().toString();
        String m1 = male.getText().toString().trim();
        String m2 = female.getText().toString().trim();
        String userGender  = "";

        //validate
        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("Password should be more than 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        if(age.isEmpty()){
            editTextAge.setError("Age is missing");
            editTextAge.requestFocus();
        }


        if(male.isChecked()){
            userGender = m1;
        }

        if(female.isChecked()){
            userGender = m2;
        }

        // Create User Map
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("gender", userGender);
        user.put("age", age);

        // Put the user data in the database.
        firebaseServices.registerUser(user, password,new FirebaseServices.FirebaseServicesListener() {
            @Override
            public void onError(String msg) {
                Toast.makeText(RegisterUser.this, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(Object response) {
                Log.d(TAG, (String) response);
                Toast.makeText(RegisterUser.this, "Successfully register, please login!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(RegisterUser.this, MainActivity.class));
            }
        });
    }
}