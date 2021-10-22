package com.digitalskies.virtualclothierdemo.ui.mainactivity;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.digitalskies.virtualclothierdemo.utils.OnAuthenticationChangedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnAuthenticationChangedListener {
    private static final int ADD_PRODUCT = 1;


    private SignInUtil signInUtil;
    //private BottomNavigationView bottomNavigationView;


    private MaterialCardView layoutLoading;

    public static final String USER_IMAGE = "user_image";

    private String userImage;
    private BottomNavigationView bottomNavigationView;
    private ColorStateList bottomNavigationItemColorStateList;
    private int[][] states;
    private int[] colors;
    private int index;
    private NavHostFragment navHostFragment;
    private AppBarConfiguration appBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);


        index = 0;





        layoutLoading = findViewById(R.id.progress_layout);





        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);





        SignInUtil.initiateSignInUtil(this);
        signInUtil = SignInUtil.getSignInUtil();
        signInUtil.attachListener();


        createBottomNavigationItemColorStateList();


        navHostFragment = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.container);


       if (signInUtil.isSignedIn()) {


            int cartItemCount = getSharedPreferences("MY_PREFERENCES", Activity.MODE_PRIVATE).getInt("PRODUCTS_IN_CART", 0);


            setUpCartCount(cartItemCount);





            navHostFragment.getNavController().getGraph().setStartDestination(R.id.productFragment);





        }


        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());



       navHostFragment.getNavController().addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
           @Override
           public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
               if(destination.getLabel().toString().equals("LoginFragment")||destination.getLabel().toString().equals("RegisterFragment")){
                   bottomNavigationView.setVisibility(View.GONE);
               }
               else{
                   bottomNavigationView.setVisibility(View.VISIBLE);
               }
               if(!destination.getLabel().toString().equals("productDetailsFragment")){

                   TypedValue a = new TypedValue();
                   getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
                   if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                       // windowBackground is a color
                       int color = a.data;

                       bottomNavigationView.setBackgroundColor(color);
                   } else {
                       // windowBackground is not a color, probably a drawable


                       bottomNavigationView.setBottom(a.resourceId);


                   }






                   colors[0]=getColor(R.color.pink_reddish);


                   bottomNavigationItemColorStateList = new ColorStateList(states, colors);






                   bottomNavigationView.setItemTextColor(bottomNavigationItemColorStateList);

                   bottomNavigationView.setItemIconTintList(bottomNavigationItemColorStateList);
               }
           }
       });











    }



    public void setUpCartCount(int cartItemCount) {


        if (cartItemCount != 0) {


            bottomNavigationView.getOrCreateBadge(R.id.checkOutFragment).setNumber(cartItemCount);

        } else {
            if (bottomNavigationView.getBadge(R.id.checkOutFragment) != null) {
                bottomNavigationView.getBadge(R.id.checkOutFragment).setVisible(false, false);
            }

        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        signInUtil.detachListener();
    }

    public void setStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.setStatusBarColor(color);
    }

    public void setBottomNavigationColor(int bottomNavColor,int itemColor){
        bottomNavigationView.setBackgroundColor(bottomNavColor);

       colors[0]=itemColor;

       bottomNavigationItemColorStateList=new ColorStateList(states,colors);

       bottomNavigationView.setItemTextColor(bottomNavigationItemColorStateList);

       bottomNavigationView.setItemIconTintList(bottomNavigationItemColorStateList);


    }

    public void showBottomNavigation() {

        //bottomNavigationView.setVisibility(View.VISIBLE);


    }

    public void hideBottomNavigation() {

        //bottomNavigationView.setVisibility(View.GONE);


    }




    public void showLoading() {
        //bottomNavigationView.setClickable(false);
        layoutLoading.setVisibility(View.VISIBLE);

    }

    public void hideLoading() {
        //bottomNavigationView.setClickable(true);
        layoutLoading.setVisibility(View.GONE);
    }


    public String getUserImage(){
        return userImage;
    }


    @Override
    public void onUserLoggedIn() {
        navHostFragment.getNavController().getGraph().setStartDestination(R.id.productFragment);
        Navigation.findNavController(this,R.id.container).navigate(R.id.action_global_productFragment);
    }

    @Override
    public void onUserLoggedOut() {
        Navigation.findNavController(this,R.id.container).navigate(R.id.action_global_loginFragment);
    }

    @Override
    public boolean onNavigateUp() {
        return Navigation.findNavController(this,R.id.container).navigateUp()||super.onNavigateUp();
    }

    private void createBottomNavigationItemColorStateList() {

        states = new int[][] {
                new int[] { android.R.attr.state_selected}, // enabled
                new int[] {-android.R.attr.state_selected}, // disabled
               // pressed
        };


        colors = new int[] {
                getColor(R.color.pink_reddish),
                getColor(R.color.faded_pink),

        };





        bottomNavigationItemColorStateList = new ColorStateList(states, colors);
    }


}
