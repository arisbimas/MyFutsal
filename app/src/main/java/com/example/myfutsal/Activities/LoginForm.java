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

public class LoginForm extends AppCompatActivity {

    EditText txtEmail, txtpassword;
    Button btn_login;
    private FirebaseAuth firebaseAuth;
    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__from);

        txtEmail = (EditText)findViewById(R.id.txt_Email);
        txtpassword = (EditText)findViewById(R.id.txtpsw);
        btn_login = (Button)findViewById(R.id.buttonLogin);
        loginProgress = (ProgressBar)findViewById(R.id.login_progress);



        firebaseAuth = FirebaseAuth.getInstance();



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = txtEmail.getText().toString().trim();
                String password = txtpassword.getText().toString().trim();


                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    loginProgress.setVisibility(View.VISIBLE);

                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginForm.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        finish();

                                    } else {

                                        Toast.makeText(LoginForm.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();

                                    }
                                    loginProgress.setVisibility(View.INVISIBLE);

                                }
                            });

                }
                else if (TextUtils.isEmpty(email)){

                    Toast.makeText(LoginForm.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(password)){

                    Toast.makeText(LoginForm.this, "Please Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if (password.length()<6){
                    Toast.makeText(LoginForm.this, "password too short", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    public void btn_signupForm(View view) {

        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }
}
