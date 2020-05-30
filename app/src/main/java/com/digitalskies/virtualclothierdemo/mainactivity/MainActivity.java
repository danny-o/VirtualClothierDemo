package com.digitalskies.virtualclothierdemo.mainactivity;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.digitalskies.virtualclothierdemo.mainactivity.fragments.LoginFragment;
import com.digitalskies.virtualclothierdemo.mainactivity.fragments.ProductFragment;
import com.digitalskies.virtualclothierdemo.SignInUtil;
import com.digitalskies.virtualclothierdemo.interfaces.NavigationHost;
import com.digitalskies.virtualclothierdemo.staggeredgridlayout.StaggeredProductCardRecyclerViewAdapter;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

public class MainActivity extends AppCompatActivity implements NavigationHost {
    private static final int ADD_PRODUCT = 1;
    MainActivityViewModel mainActivityViewModel;
    private Boolean isAdmin;
    private Toolbar toolbar;
    private StaggeredProductCardRecyclerViewAdapter productsAdapter;
    private ProgressBar progressBar;
    private SignInUtil signInUtil=new SignInUtil();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        if(!signInUtil.isSignedIn()){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container,new LoginFragment())
                    .commit();
        }
        else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container,new ProductFragment())
                    .commit();
        }

    }

    @Override
    public void navigateTo(Fragment fragment, Boolean addToBackStack) {
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
