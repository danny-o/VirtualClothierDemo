package com.digitalskies.virtualclothierdemo;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.digitalskies.virtualclothierdemo.interfaces.NavigationHost;
import com.digitalskies.virtualclothierdemo.interfaces.SetUpOptionsMenu;
import com.digitalskies.virtualclothierdemo.mainactivity.MainActivity;
import com.digitalskies.virtualclothierdemo.mainactivity.fragments.LoginFragment;
import com.digitalskies.virtualclothierdemo.mainactivity.fragments.ProductFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.digitalskies.virtualclothierdemo.models.User;

import java.util.ArrayList;
import java.util.List;

public class SignInUtil{

    public static final int RC_SIGN_IN = 123;
    private static FirebaseAuth.AuthStateListener mAuthStateListener;
    private static FirebaseAuth firebaseAuth;
    private ProductFragment productFragment;
    private static  FirebaseFirestore firebaseFirestore;
    private static Boolean isAdmin=false;
    private static final  String TAG="Sign in";
    private Activity activity;
    private LoginFragment loginFragment;
    private GoogleSignInClient signInClient;
    private SignInUtil signInUtil;

    public SignInUtil(){
        setUpFireBaseAuth();
        setUpAuthListener();


    }
    public  SignInUtil(Activity activity){
        productFragment=null;
        this.activity=activity;
        setUpFireBaseAuth();
        setUpFireBaseFireStore();

    }

    public SignInUtil(Activity activity,LoginFragment loginFragment,ProductFragment productFragment){

        this.activity=activity;
        this.loginFragment=loginFragment;
        this.productFragment=productFragment;

        setUpFireBaseFireStore();
    }

    private void setUpFireBaseAuth() {
        if(firebaseAuth==null){
            firebaseAuth= FirebaseAuth.getInstance();
        }
    }
    private void setUpFireBaseFireStore() {
        if(firebaseFirestore==null){
            firebaseFirestore=FirebaseFirestore.getInstance();
        }
    }
    private void setUpAuthListener() {
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    if(productFragment!=null){
                        checkIfAdmin();
                    }


                }



            }

        };
    }
    public boolean isSignedIn(){
        if(firebaseAuth==null){
            firebaseAuth=FirebaseAuth.getInstance();
        }
        return firebaseAuth.getCurrentUser() != null;
    }
    public void signOut(Activity activity){
        if(firebaseAuth==null){
            firebaseAuth=FirebaseAuth.getInstance();
        }
        firebaseAuth.signOut();
        ((MainActivity)activity).navigateTo(new LoginFragment(),false);
    }

    public void createNewUser(final String email, String password){
        ((RegisterActivity)activity).setUpProgressBar(ProgressBar.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            List<String> favProducts=new ArrayList<>();
                            favProducts.add("");
                            User user = new User();
                            user.setName(email.substring(0, email.indexOf("@")));

                            user.setFavoriteProducts(favProducts);
                            user.setAdmin(false);
                            firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                                    .set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                ((RegisterActivity) activity).setUpProgressBar(ProgressBar.INVISIBLE);
                                                Toast.makeText(activity, "registered successfully", Toast.LENGTH_SHORT).show();
                                                activity.finish();
                                                firebaseAuth.signOut();
                                            }
                                            else{
                                                Toast.makeText(activity,"something went wrong",Toast.LENGTH_SHORT).show();
                                                ((RegisterActivity) activity).setUpProgressBar(ProgressBar.INVISIBLE);
                                                Intent intent=new Intent(activity,MainActivity.class);
                                                activity.startActivity(intent);
                                                activity.finish();

                                            }
                                        }
                                    });
                        }
                        else{
                            ((RegisterActivity)activity).setUpProgressBar(ProgressBar.INVISIBLE);
                            Toast.makeText(activity,"registration failed",Toast.LENGTH_SHORT).show();
                            activity.finish();
                        }

                    }
                });
    }

    public void signInWithEmailAndPassword(final String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            ((NavigationHost)activity).navigateTo(productFragment,false);

                        }
                        else{
                            Toast.makeText(activity,"sign in failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public  void signInWithGoogle(){

        Intent signInIntent=getSignInClient().getSignInIntent();
        loginFragment.startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    public  void firebaseAuthWithGoogle(String idToken,final String name){
        AuthCredential credential=GoogleAuthProvider.getCredential(idToken,null) ;
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                List<String >favProductList = new ArrayList<>();
                                favProductList.add("");
                                User user = new User();
                                user.setName(name);
                                user.setAdmin(true);
                                user.setFavoriteProducts(favProductList);
                                firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                                        .set(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(activity, "sign in with google in failed", Toast.LENGTH_SHORT).show();
                                                    firebaseAuth.signOut();
                                                    loginFragment.setUpProgressBarVisibility(ProgressBar.INVISIBLE);
                                                }
                                            }
                                        });

                            }

                            ((NavigationHost) activity).navigateTo(productFragment, false);

                        }
                        else{
                            Toast.makeText(activity,"sign In with google failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public GoogleSignInClient  getSignInClient(){
        GoogleSignInOptions gSignInOptions=new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(activity, gSignInOptions);
    }
    private void checkIfAdmin() {
        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            //activity.Toast.makeText(activity, "success", Toast.LENGTH_SHORT).show();
                            ((SetUpOptionsMenu)productFragment).setAdmin(isAdmin);

                        }
                        else {
                            Log.d(TAG, "Error getting users: ", task.getException());
                            Toast.makeText(activity, "error retrieving users", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
    public void detachListener(){
        firebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
    public void attachListener(){
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

}
