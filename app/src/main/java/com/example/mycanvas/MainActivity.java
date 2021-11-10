package com.example.mycanvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextemail;
    private TextInputEditText editTextPassword;
    private Button Login;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //initialise
        Login = (Button) findViewById(R.id.login);
        Login.setOnClickListener((View.OnClickListener) this);

        editTextemail = (EditText) findViewById(R.id.Email);
        editTextPassword = (TextInputEditText) findViewById(R.id.Password);
        mAuth = FirebaseAuth.getInstance();

        TextView textView = findViewById(R.id.SignUp);

        //make "Sign Up clickable"

        String text = "Sign Up now!";

        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                switch (view.getId()){
                    case R.id.SignUp:
                        startActivity(new Intent(MainActivity.this, RegisterUser.class));
                        break;

                }
            }
        };

        ss.setSpan(clickableSpan1,0,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());



    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.login:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = editTextemail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //validation
        if(email.isEmpty()){
            editTextemail.setError("Email is required");
            editTextemail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextemail.setError("Please provide valid email!");
            editTextemail.requestFocus();
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

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //redirect to homepage
                    startActivity(new Intent(MainActivity.this, HomePage.class));

                }
                else{
                    Toast.makeText(MainActivity.this, "Failed to Login! Please check your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}