package com.example.findmate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    EditText mail, password;
    TextView register, forgotPassword;
    Button login;
    FirebaseAuth auth;
    DatabaseReference ref;
    FirebaseDatabase database;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.loginButton);
        mail = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        register = findViewById(R.id.tv_register);
        forgotPassword = findViewById(R.id.forgot_password);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("users");

        register.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),Register.class);
            startActivity(intent);
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mailStr = mail.getText().toString();
                String passwordStr = password.getText().toString();

                if(!mailStr.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mailStr).matches() && mailStr.contains("@std.yildiz.edu.tr")){
                    if(!passwordStr.isEmpty()){
                        auth.signInWithEmailAndPassword(mailStr,passwordStr)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(Login.this, "Login Successfull!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this, MainScreen.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Login.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{
                        password.setError("Password can not be empty!");
                    }
                }else if(mailStr.isEmpty())
                    mail.setError("E-mail can not be empty!");
                else
                    mail.setError("Not a valid e-mail!");
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot,null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.filter_filter).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();

                        if(TextUtils.isEmpty(userEmail) | !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                            Toast.makeText(Login.this, "Enter your registered e-mail!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Check your e-mail!", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(Login.this, "Unable to send verification mail!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                });
                dialogView.findViewById(R.id.filter_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if(dialog.getWindow() != null)
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.show();
            }
        });

    }
}
