package com.example.myfutsal.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myfutsal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity<progressBar> extends AppCompatActivity {

    EditText txtEmail, txtPassword, txtConfirmPassword;
    Button btn_register;
    ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__from);

        txtEmail = (EditText)findViewById(R.id.txtemail);
        txtPassword = (EditText)findViewById(R.id.txtpsw);
        txtConfirmPassword = (EditText)findViewById(R.id.txtpsw);
        btn_register = (Button) findViewById(R.id.btn_signup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });



        firebaseAuth = FirebaseAuth.getInstance();


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                String confirPassword = txtConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){

                    Toast.makeText(RegisterActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){

                    Toast.makeText(RegisterActivity.this, "Please Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirPassword)){

                    Toast.makeText(RegisterActivity.this, "Please Enter confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length()<6){
                    Toast.makeText(RegisterActivity.this, "password too short", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.VISIBLE);



                if(password.equals(confirPassword)){

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    progressBar.setVisibility(View.GONE);


                                    if (task.isSuccessful()) {

                                        Intent setupIntent = new Intent(RegisterActivity.this,SetupActivity.class);
                                        startActivity(setupIntent);
                                        finish();

                                        startActivity(new Intent(RegisterActivity.this, SetupActivity.class));
                                        Toast.makeText(RegisterActivity.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                                    } else {

                                        Toast.makeText(RegisterActivity.this, "Authentication Failed" +task.getException().toString(), Toast.LENGTH_SHORT).show();

                                    }


                                    // ...
                                }
                            });

                }
            }
        });


    }
}
