package com.example.firebasesocialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText edtEmail, edtUsername,edtPassword;
    private Button btnSignUp,btnSignIn;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtEmail=findViewById(R.id.edtEmail);
        edtPassword=findViewById(R.id.edtPassword);
        edtUsername=findViewById(R.id.edtUsername);
        btnSignIn=findViewById(R.id.btnSignIn);
        btnSignUp=findViewById(R.id.btnSignUp);
        mAuth=FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser!=null){
            // Transitions to next activity
            transitionToSocialMediaActivity();

        }
    }
    private void signUp(){

        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Sign Up Succesful",Toast.LENGTH_SHORT).show();

                    FirebaseDatabase.getInstance().getReference()
                            .child("my_users").child(task.getResult()
                            .getUser().getUid()).child("username")
                            .setValue(edtUsername.getText().toString());
                    UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder()
                            .setDisplayName(edtUsername.getText().toString())
                            .build();
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this,"Display name Updated",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                    transitionToSocialMediaActivity();
                }else {
                    Log.w("TAG","createUserWithEmail:Failure",task.getException());
                    Toast.makeText(MainActivity.this,"Authentication failed.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void signIn(){
        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Signed in succesfully",Toast.LENGTH_SHORT).show();
                    transitionToSocialMediaActivity();
                }else {
                    Toast.makeText(MainActivity.this,"Auth Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private  void transitionToSocialMediaActivity(){
        Intent intent=new Intent(this,SocialMediaActivity.class);
        startActivity(intent);

    }
}
