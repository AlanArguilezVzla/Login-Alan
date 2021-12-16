package com.ut.login.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ut.login.R;

import java.util.HashMap;
import java.util.Map;

public class Resgistro extends AppCompatActivity {

    EditText txtEmail, txtPassword, txtFirstName, txtLastName, txtPhone;
    TextView goToLogin;
    Button registrar;
    RadioGroup radioGroupGender;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    int gender;

    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgistro);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(Resgistro.this);

        txtEmail = findViewById(R.id.editEmail);
        txtPassword = findViewById(R.id.editPassword);
        registrar = findViewById(R.id.btnRegistrar);
        goToLogin = findViewById(R.id.txtGoLogin);

        txtFirstName = findViewById(R.id.txtSignUpFirstName);
        txtLastName = findViewById(R.id.txtSignUpLastName);
        txtPhone = findViewById(R.id.txtSignUpPhone);
        radioGroupGender = findViewById(R.id.radioGroupGender);

        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.radioButtonMale){
                    gender = 0;
                } else {
                    gender = 1;
                }
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginMethod();
            }
        });
        
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setCancelable(false);
                dialog.setTitle("Registro");
                dialog.setMessage("Validando usuario...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                verifyUser();
            }
        });
    }

    private void verifyUser(){
        db.collection("Users").document(txtEmail.getText().toString().trim())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        dialog.dismiss();
                        Toast.makeText(Resgistro.this,
                                "Este usuario ya ha sido registrado", Toast.LENGTH_SHORT).show();
                        goToLoginMethod();
                    } else {
                        addUserToDatabase();
                    }
                } else {
                    dialog.dismiss();
                }
            }
        });
    }

    private void addUserToDatabase(){
        dialog.setMessage("Agrengando a base de datos...");
        Map<String, Object> user = new HashMap<>();
        user.put("firstname", txtFirstName.getText().toString());
        user.put("lastname", txtLastName.getText().toString());
        user.put("phone", txtPhone.getText().toString());
        user.put("gender", gender);
        user.put("photo", "");

        db.collection("Users").document(txtEmail.getText().toString().trim())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        signUpUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(Resgistro.this,
                                "Ocurrio un error al agregar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signUpUser() {
        dialog.setMessage("Registrando usuario...");
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            addUserToDatabase();
                            updateUI(null);
                        }
                    }
                });

    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            Toast.makeText(Resgistro.this, "Registro Exitoso!!", Toast.LENGTH_LONG).show();
            goToLoginMethod();
        }else{
            dialog.dismiss();
            Toast.makeText(Resgistro.this, "Ocurrio un error", Toast.LENGTH_LONG).show();
        }
    }

    private void goToLoginMethod(){
        startActivity(new Intent(Resgistro.this, MainActivity.class));
    }

}
