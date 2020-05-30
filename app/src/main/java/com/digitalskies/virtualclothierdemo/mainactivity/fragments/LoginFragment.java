package com.digitalskies.virtualclothierdemo.mainactivity.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digitalskies.virtualclothierdemo.RegisterActivity;
import com.digitalskies.virtualclothierdemo.SignInUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

/**
 * Fragment representing the login screen for Shrine.
 */
public class LoginFragment extends Fragment {

    private SignInUtil signInUtil;
    private MaterialButton signInButton;
    private TextInputEditText passwordEditText;
    private TextInputLayout passwordTextInput;
    private TextView signInWithGoogleBtn,register;
    private TextInputEditText emailEditText;
    private ProgressBar signInProgress;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        signInUtil = new SignInUtil(getActivity(),this,new ProductFragment());

        emailEditText = view.findViewById(R.id.email_edit_text);
        passwordTextInput = view.findViewById(R.id.password_til);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        signInWithGoogleBtn = view.findViewById(R.id.sign_in_with_google);
        register=view.findViewById(R.id.register);
        signInButton = view.findViewById(R.id.button_sign_in);
        signInProgress=view.findViewById(R.id.sign_in_progress);

        setUpSignInButton();
        setUpSignInWithGoogleBtn();
        setUpRegisterTextView();
        setUpProgressBarVisibility(ProgressBar.INVISIBLE);

        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        signInUtil.attachListener();
    }

    @Override
    public void onDestroy() {
        signInUtil.detachListener();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== SignInUtil.RC_SIGN_IN){
            setUpProgressBarVisibility(ProgressBar.VISIBLE);
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

    private void setUpSignInButton() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (emailEditText.getText().toString().isEmpty()||passwordEditText.getText().toString().isEmpty()){
                    passwordTextInput.setError("Input the empty fields");
                }
                else {
                    passwordTextInput.setError(null);// Clear the error
                    //add to back stack is false so that user doesn't go back to login screen
                    signInUtil.signInWithEmailAndPassword(emailEditText.getText().toString(),passwordEditText.getText().toString());
                }

            }
        });
    }
    private void setUpSignInWithGoogleBtn() {
        signInWithGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUtil.signInWithGoogle();
            }
        });

    }
    private void setUpRegisterTextView() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RegisterActivity.class));
            }
        });
    }
    public void setUpProgressBarVisibility(int visibility) {
        signInProgress.setVisibility(visibility);
    }
}
