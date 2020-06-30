package com.digitalskies.virtualclothierdemo.mainactivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.digitalskies.virtualclothierdemo.mainactivity.fragments.LoginFragment;
import com.digitalskies.virtualclothierdemo.mainactivity.fragments.ProductFragment;
import com.digitalskies.virtualclothierdemo.recycleradapter.StaggeredProductCardRecyclerViewAdapter;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

public class MainActivity extends AppCompatActivity implements NavigationHost {
    private static final int ADD_PRODUCT = 1;
    MainActivityViewModel mainActivityViewModel;
    private Boolean isAdmin;
    private Toolbar toolbar;
    private StaggeredProductCardRecyclerViewAdapter productsAdapter;
    private ProgressBar progressBar;
    private SignInUtil signInUtil;

    private LoginFragment loginFragment;
    private ProductFragment productFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        SignInUtil.initiateSignInUtil(this);
        signInUtil=SignInUtil.getSignInUtil();
        signInUtil.attachListener();

        if(savedInstanceState!=null){
            productFragment=(ProductFragment)getSupportFragmentManager().getFragment(savedInstanceState,"savedFragment");
        }
        else{
            productFragment=(ProductFragment)signInUtil.getProductFragment();
        }


        if(!signInUtil.isSignedIn()){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container,signInUtil.getLoginFragment())
                    .commit();
        }
        else if(!productFragment.isAdded()){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container,productFragment)
                    .commit();
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState,"savedFragment",signInUtil.getProductFragment());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        signInUtil.detachListener();
    }

    @Override
    public void navigateTo(Fragment fragment, Boolean addToBackStack) {

            Log.d(getClass().getSimpleName(),getLifecycle().getCurrentState().toString());
            FragmentTransaction transaction =
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, fragment);

            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();

        }





}
