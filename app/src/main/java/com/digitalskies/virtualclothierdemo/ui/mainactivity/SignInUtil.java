package com.digitalskies.virtualclothierdemo.ui.mainactivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.RegisterFragment;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.LoginFragment;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.productsfragment.ProductFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private FirebaseFirestore firebaseFirestore;
    private static Boolean isAdmin=false;
    private static final  String TAG="Sign in";
    private MainActivity activity;
    private static SignInUtil signInUtil;
    private Boolean isANewUser=true;
    private final SharedPreferences sharedPreferences;

    private SignInUtil(MainActivity activity){

        this.activity=activity;
        //this.loginFragment=new LoginFragment();
        //this.productFragment=new ProductFragment();
        sharedPreferences = activity.getSharedPreferences("My_PREFERENCES",Activity.MODE_PRIVATE);
        setUpFireBaseFireStore();
        setUpFireBaseAuth();
        setUpAuthListener();
    }
    public static void initiateSignInUtil(MainActivity activity){
       signInUtil=new SignInUtil(activity);
    }
    public static SignInUtil getSignInUtil(){
        return  signInUtil;
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
                if(firebaseAuth.getCurrentUser()==null){
                   activity.onUserLoggedOut();



                }
                else {
                    activity.onUserLoggedIn();
                }

                   }

            };
        }

    public boolean isSignedIn(){
        if(firebaseAuth==null){
            firebaseAuth=FirebaseAuth.getInstance();

        }
        isANewUser=firebaseAuth.getCurrentUser()==null;
        return firebaseAuth.getCurrentUser() !=null;
    }
    public void signOut(){

        firebaseAuth.signOut();

    }

    public void createNewUser(Fragment fragment,final String email, String password){
       activity.showLoading();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            List<String> favProducts=new ArrayList<>();
                            List<String >productsInCart = new ArrayList<>();
                            User user = new User();
                            user.setName(email.substring(0, email.indexOf("@")));
                            user.setFavoriteProducts(favProducts);
                            user.setProductsInCart(productsInCart);
                            user.setAdmin(false);
                            user.setEmail(email);
                            firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                                    .set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                activity.hideLoading();
                                                Toast.makeText(activity, "registered successfully", Toast.LENGTH_SHORT).show();
                                                isANewUser = true;
                                                firebaseAuth.signOut();
                                            }
                                            else{
                                                Toast.makeText(activity,"something went wrong",Toast.LENGTH_SHORT).show();
                                                activity.hideLoading();


                                            }
                                        }
                                    });
                        }
                        else{
                          activity.hideLoading();
                            Toast.makeText(activity,"registration failed",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void signInWithEmailAndPassword(final String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        activity.hideLoading();
                        if(task.isSuccessful()){
                            isANewUser=true;
                            checkIfAdmin();

                            sharedPreferences.edit().putString("USER_EMAIL",email).apply();

                            sharedPreferences.edit().putString("USER_NAME",firebaseAuth.getCurrentUser().getDisplayName()).apply();

                        }
                        else{
                            Toast.makeText(activity,"sign in failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public  void signInWithGoogle() {
        GoogleSignInOptions gSignInOptions=new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient signInClient= GoogleSignIn.getClient(activity, gSignInOptions);

        Intent signInIntent=signInClient.getSignInIntent();
        //loginFragment.startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    public  void firebaseAuthWithGoogle(GoogleSignInAccount account){
        activity.showLoading();
        final AuthCredential credential=GoogleAuthProvider.getCredential(account.getIdToken(),null) ;
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                List<String >favProductList = new ArrayList<>();
                                List<String >productsInCart = new ArrayList<>();
                                User user = new User();
                                user.setName(account.getDisplayName());

                                user.setEmail(account.getEmail());

                                if(account.getPhotoUrl()!=null){
                                    user.setImage(account.getPhotoUrl().toString());



                                }






                                user.setAdmin(true);
                                user.setFavoriteProducts(favProductList);
                                user.setProductsInCart(productsInCart);
                                firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                                        .set(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(activity, "sign in with google failed", Toast.LENGTH_SHORT).show();
                                                    firebaseAuth.signOut();
                                                }
                                                else{
                                                    checkIfAdmin();
                                                }
                                            }
                                        });

                            }
                            else{
                                isANewUser=true;
                                checkIfAdmin();
                            }

                        }
                        else{
                            Toast.makeText(activity,"sign In with google failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void checkIfAdmin() {
        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {

                            User user=task.getResult().toObject(User.class);
                            isAdmin=user.getAdmin();
                            setProductsInCart(user);

                            activity.hideLoading();

                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putBoolean("isAdmin",isAdmin);


                            editor.apply();

                            activity.onUserLoggedIn();

                        }
                        else {
                            Log.d(TAG, "Error getting users: ", task.getException());
                            Toast.makeText(activity, "error retrieving users", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                        }
                    }
                });


    }

    private void setProductsInCart(User user) {
       SharedPreferences sharedPreferences=activity.getSharedPreferences("MY_PREFERENCES",Activity.MODE_PRIVATE);
       SharedPreferences.Editor editor=sharedPreferences.edit();
       editor.putInt("PRODUCTS_IN_CART",user.getProductsInCart().size());
       editor.apply();
       activity.setUpCartCount(user.getProductsInCart().size());

    }

    public boolean getIfAdmin(){
       return sharedPreferences.getBoolean("isAdmin",false);
    }
    public String getUserName(){
        return firebaseAuth.getCurrentUser().getDisplayName();
    }
    public boolean getIfIsANewUser(){
        return  isANewUser;
    }

    public FirebaseUser getUser(){
        return  firebaseAuth.getCurrentUser();
    }
    public void setIfIsANewUser(boolean isANewUser){
        this.isANewUser=isANewUser;
    }
    public void detachListener(){
        firebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
    public void attachListener(){
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

}
