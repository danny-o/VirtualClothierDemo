package com.digitalskies.virtualclothierdemo.mainactivity.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.digitalskies.virtualclothierdemo.NavigationIconClickListener;
import com.digitalskies.virtualclothierdemo.mainactivity.SignInUtil;
import com.digitalskies.virtualclothierdemo.mainactivity.MainActivityViewModel;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.digitalskies.virtualclothierdemo.productentryactivity.ProductEntryActivity;
import com.digitalskies.virtualclothierdemo.recycleradapter.StaggeredProductCardRecyclerViewAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProductFragment extends Fragment {
    private Toolbar toolbar;
    private View view;
    private SearchView searchView;
    private FrameLayout cartContainer;
    private MaterialButton signOut,favorites;
    private TextView cartBadge;
    private StaggeredProductCardRecyclerViewAdapter productsAdapter;
    private MainActivityViewModel mainActivityViewModel;
    private ProgressBar progressBar;
    private static final int ADD_PRODUCT = 12;
    private Boolean isAdmin=false;
    private Observer<List<Product>> observer=new Observer<List<Product>>() {
        @Override
        public void onChanged(List<Product> productList) {
            updateProducts(productList);

        }
    };
    private AppCompatActivity appCompatActivity;
    private SharedPreferences sharedPreferences;
    private int cartItemCount;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            isAdmin=savedInstanceState.getBoolean("isAdmin");
        }
        else{
            isAdmin=SignInUtil.getSignInUtil().getIfAdmin();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sharedPreferences = getActivity().getApplication().getSharedPreferences("MY_PREFERENCES", Activity.MODE_PRIVATE);
        isAdmin= sharedPreferences.getBoolean("isAdmin",false);



        mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.products().observe(this,observer);



    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.product_fragment,container,false);



        toolbar = view.findViewById(R.id.app_bar);
        progressBar = view.findViewById(R.id.progress_bar_main);
        signOut = view.findViewById(R.id.sign_out);
        favorites=view.findViewById(R.id.favorites);
        searchView=view.findViewById(R.id.search);
        cartContainer =view.findViewById(R.id.cart_container);
        cartBadge =view.findViewById(R.id.cart_badge);


        progressBar.setVisibility(ProgressBar.INVISIBLE);
        Log.d("MainActivity",getActivity().getLifecycle().getCurrentState().toString());

        setUpToolbar();
        setUpRecyclerView();
        setButtonOnClickListeners();
        setUpSearch();
        setCartItemCount();

        return  view;

    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityViewModel.getProducts();
        mainActivityViewModel.getFavAndCartProducts();
        productsAdapter.notifyDataSetChanged();
        setCartItemCount();
        
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isAdmin",isAdmin);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ADD_PRODUCT &&resultCode==RESULT_OK){
            progressBar.setVisibility(ProgressBar.VISIBLE);
            mainActivityViewModel.getProducts();
            productsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        mainActivityViewModel.products().removeObserver(observer);
        Bundle bundle=new Bundle();
        bundle.putBoolean("isAdmin",isAdmin);
        onSaveInstanceState(bundle);
        super.onDestroyView();
    }


    private void setUpToolbar() {
        appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.setSupportActionBar(toolbar);
        }
        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.product_grid),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.icon_menu), // Menu open icon
                getContext().getResources().getDrawable(R.drawable.shr_close_menu))); // Menu close icon
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.product_list);
        recyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager staggeredLM=new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredLM.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setLayoutManager(staggeredLM);
        productsAdapter = new StaggeredProductCardRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(productsAdapter);
//
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            view.findViewById(R.id.product_grid).setBackgroundResource(R.drawable.product_grid_background_shape);
    }

    private void setButtonOnClickListeners() {
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SignInUtil.getSignInUtil().signOut();
            }
        });
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productsAdapter.setProductList(mainActivityViewModel.getFavoriteProducts());
                productsAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem=menu.findItem(R.id.add_products);

        menuItem.setVisible(isAdmin);


    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.product_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId=item.getItemId();
        if (itemId == R.id.add_products) {
            startActivityForResult(new Intent(requireActivity(), ProductEntryActivity.class), ADD_PRODUCT);
        }
        return true;


    }
    private void setCartItemCount() {

        cartItemCount = sharedPreferences.getInt("PRODUCTS_IN_CART",0);
        if(cartItemCount ==0){
            if(cartBadge.getVisibility()!=TextView.GONE)
                cartBadge.setVisibility(TextView.GONE);
        }
        else{
            cartBadge.setText(String.valueOf(Math.min(cartItemCount,99)));
            if(cartBadge.getVisibility()!=TextView.VISIBLE){
                cartBadge.setVisibility(TextView.VISIBLE);
            }

        }

    }
    private void setUpSearch() {

        SearchManager searchManager=(SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartContainer.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
               cartContainer.setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                productsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productsAdapter.getFilter().filter(newText);
                return  false;
            }


        });
    }

    public void updateProducts(List<Product> productList){
        productsAdapter.setProductList(productList);
        productsAdapter.notifyDataSetChanged();
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

}
