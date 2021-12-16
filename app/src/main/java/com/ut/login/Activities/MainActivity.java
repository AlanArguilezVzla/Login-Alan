package com.ut.login.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ut.login.R;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button login;
    EditText email,password;
    TextView goToSignUp;
    CheckBox checkRemember;

    private FirebaseAuth mAuth;

    //Declaracion de SharePreferences
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //Inicializando SharedPreferences
        sharedPreferences = getSharedPreferences("email", Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("password", Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("check", Context.MODE_PRIVATE);

        login = findViewById(R.id.btnLogin);
        email = findViewById(R.id.editCorreo);
        password = findViewById(R.id.editContra);
        goToSignUp = findViewById(R.id.txtRegistro);

        checkRemember = findViewById(R.id.checkRemember);

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentExplicito();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onCheckboxClicked();
                doLogin();
            }
        });
    }

    private void doLogin() {
        String vEmail = email.getText().toString().trim();
        String vPassword = password.getText().toString();

        mAuth.signInWithEmailAndPassword(vEmail, vPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            //Inicializacion del editor
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(checkRemember.isChecked()){
                //Agregar campos al objeto
                editor.putString("email", user.getEmail());
                editor.putString("password", password.getText().toString());
                editor.putBoolean("check", true);
                //Aplicar cambios
                editor.apply();
            }else {
                editor.putBoolean("check", false);
                editor.apply();
            }
            goToHome();
        }else{
            Toast.makeText(MainActivity.this,
                    "Ocurrio un al inicial sesion", Toast.LENGTH_LONG).show();
        }
    }

    private void goToHome() {
        startActivity(new Intent(MainActivity.this, Home.class));

        Toast.makeText(MainActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(sharedPreferences.getBoolean("check", false) == true){
            email.setText(sharedPreferences.getString("email", ""));
            password.setText(sharedPreferences.getString("password", ""));
            checkRemember.setChecked(true);
        }

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            goToHome();
        }
    }

    public void intentExplicito(){
        Intent intent = new Intent(MainActivity.this,Resgistro.class);
        startActivity(intent);
    }

}