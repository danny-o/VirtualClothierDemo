package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments;

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

import com.digitalskies.virtualclothierdemo.ui.mainactivity.SignInUtil;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.NavigationHost;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import static com.digitalskies.virtualclothierdemo.ui.mainactivity.SignInUtil.RC_SIGN_IN;

/**
 * Fragment representing the login screen for Shrine.
 */
public class LoginFragment extends Fragment {

    private SignInUtil signInUtil;
    private MaterialButton signInButton;
    private TextInputEditText passwordEditText;
    private TextInputLayout emailTextInput,passwordTextInput;
    private TextView signInWithGoogleBtn,register;
    private TextInputEditText emailEditText;
    private ProgressBar signInProgress;
    public static final String USER_IS_ADMIN = "com.digitalskies.virtualclothierdemo.LoginFragment";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        signInUtil = SignInUtil.getSignInUtil();

        emailEditText = view.findViewById(R.id.email_edit_text);
        emailTextInput=view.findViewById(R.id.email_til);
        passwordTextInput = view.findViewById(R.id.password_til);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        signInWithGoogleBtn = view.findViewById(R.id.sign_in_with_google);
        register=view.findViewById(R.id.register_here);
        signInButton = view.findViewById(R.id.button_sign_in);
        signInProgress=view.findViewById(R.id.sign_in_progress);

        setUpSignInButton();
        setUpSignInWithGoogleBtn();
        setUpRegisterTextView();
        setUpProgressBarVisibility(ProgressBar.INVISIBLE);

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== RC_SIGN_IN){
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

                if (emailEditText.getText().toString().isEmpty()){
                    emailTextInput.setError("Input the email");
                }
                if(passwordEditText.getText().toString().isEmpty()){
                    passwordTextInput.setError("Input the password");
                }
                else {
                    emailTextInput.setError(null);
                    passwordTextInput.setError(null);
                    setUpProgressBarVisibility(ProgressBar.VISIBLE);
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
                ((NavigationHost)getActivity()).navigateTo(new RegisterFragment(),true);
            }
        });
    }
    public void setUpProgressBarVisibility(int visibility) {
        signInProgress.setVisibility(visibility);
    }
}
