package com.mirea.productapp.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mirea.productapp.R;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email, pass;
    private TextView singin;
    private Button btnReg;


    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);

        email = findViewById(R.id.txtEmail);
        pass = findViewById(R.id.txtPwd);

        btnReg = findViewById(R.id.btnLogin);
        singin = findViewById(R.id.lnkLogin);


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEmail = email.getText().toString().trim();
                String mPass = email.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)) {
                    email.setError("Fill the field");
                    return;
                }

                if (TextUtils.isEmpty(mPass)) {
                    pass.setError("Fill the field");
                    return;
                }

                mDialog.setMessage("Processing...");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, mEmail);
                            Log.i(TAG, mPass);
                            mDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));


                        } else {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }

                    }
                });
            }
        });

        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
}