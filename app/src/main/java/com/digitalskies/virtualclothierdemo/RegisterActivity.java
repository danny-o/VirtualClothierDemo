package com.digitalskies.virtualclothierdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

public class RegisterActivity extends AppCompatActivity {
    EditText userEmail,password,confirmPassword;
    TextInputLayout tilEmail,tilPassword,tilConfirmPassword;
    private SignInUtil signInUtil;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressBar = findViewById(R.id.register_progress);
        userEmail=findViewById(R.id.et_email);
        password=findViewById(R.id.et_user_password);
        confirmPassword=findViewById(R.id.et_confirm_password);
        tilEmail=findViewById(R.id.til_user_email);
        tilPassword=findViewById(R.id.til_user_password);
        tilConfirmPassword=findViewById(R.id.til_confirm_password);

        setUpProgressBar(ProgressBar.INVISIBLE);

        signInUtil=new SignInUtil(this);


    }
    public void register(View view){
        if(userEmail.getText().toString().isEmpty()){
            tilEmail.setError("field cannot be empty");
        }
        else if(password.getText().toString().isEmpty()){
            tilPassword.setError("field cannot be empty");
        }
        else if(confirmPassword.getText().toString().isEmpty()){
            tilConfirmPassword.setError("field cannot be empty");
        }
        else if(!isPasswordValid(password.getText())){
            tilPassword.setError(getString(R.string.shr_error_password));
        }
        else if(!password.getText().toString().equals(confirmPassword.getText().toString())){
            tilConfirmPassword.setError("passwords must match");
        }
        else{
            tilEmail.setError(null);
            tilPassword.setError(null);
            tilConfirmPassword.setError(null);
            signInUtil.createNewUser(userEmail.getText().toString(),password.getText().toString());
        }


    }
    private boolean isPasswordValid(@Nullable Editable text) {
        return text != null && text.length() >= 8;

    }
    public void setUpProgressBar(int visibility){
        progressBar.setVisibility(visibility);
    }
}
