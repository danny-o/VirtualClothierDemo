package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digitalskies.virtualclothierdemo.ui.mainactivity.SignInUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import static com.digitalskies.virtualclothierdemo.ui.mainactivity.SignInUtil.RC_SIGN_IN;

public class RegisterFragment extends Fragment {

    private EditText userEmail,password,confirmPassword;
    private TextInputLayout tilEmail,tilPassword,tilConfirmPassword;
    private TextView signInWithGoogle;
    private ImageView googleIcon;
    private Button registerButton;
    private SignInUtil signInUtil;
    private ProgressBar progressBar;
    private RegisterFragment registerFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.register_fragment,container,false);

        registerFragment = this;

        progressBar = view.findViewById(R.id.register_progress);
        userEmail=view.findViewById(R.id.et_email);
        password=view.findViewById(R.id.et_user_password);
        confirmPassword=view.findViewById(R.id.et_confirm_password);
        tilEmail=view.findViewById(R.id.til_user_email);
        tilPassword=view.findViewById(R.id.til_user_password);
        tilConfirmPassword=view.findViewById(R.id.til_confirm_password);
        registerButton=view.findViewById(R.id.register_button);
        googleIcon=view.findViewById(R.id.image_google_icon);
        signInWithGoogle=view.findViewById(R.id.tv_sign_in_with_google);

        setUpRegisterButton();

        setUpProgressBar(ProgressBar.INVISIBLE);

        setUPSignInWithGoogle();

        signInUtil=SignInUtil.getSignInUtil();

        return view;

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== RC_SIGN_IN){
            setUpProgressBar(ProgressBar.VISIBLE);
            try {
                Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account=task.getResult(ApiException.class);
                signInUtil.firebaseAuthWithGoogle(account.getIdToken(),account.getDisplayName());
                Toast.makeText(getActivity(),"sign in with google succeeded",Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                e.printStackTrace();
                Log.e(getClass().getSimpleName(),"GoogleSignIn failed");
            }

        }
    }

    public void setUpRegisterButton(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                else if(!userEmail.getText().toString().contains("@")){
                    tilEmail.setError("please enter a valid email");
                }
                else{
                    tilEmail.setError(null);
                    tilPassword.setError(null);
                    tilConfirmPassword.setError(null);
                    signInUtil.createNewUser(registerFragment,userEmail.getText().toString(),password.getText().toString());
                }

            }
        });
    }
    private boolean isPasswordValid(@Nullable Editable text) {
        return text != null && text.length() >= 8;

    }
    private void setUPSignInWithGoogle() {
        googleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUtil.signInWithGoogle();
            }
        });
        signInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUtil.signInWithGoogle();
            }
        });
    }

    public void setUpProgressBar(int visibility){
        progressBar.setVisibility(visibility);
    }
}
