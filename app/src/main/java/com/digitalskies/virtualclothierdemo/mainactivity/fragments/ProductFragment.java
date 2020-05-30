package com.digitalskies.virtualclothierdemo.mainactivity.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.SearchView;


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
import com.digitalskies.virtualclothierdemo.SignInUtil;
import com.digitalskies.virtualclothierdemo.interfaces.SetUpOptionsMenu;
import com.digitalskies.virtualclothierdemo.mainactivity.MainActivityViewModel;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.digitalskies.virtualclothierdemo.staggeredgridlayout.StaggeredProductCardRecyclerViewAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProductFragment extends Fragment implements SetUpOptionsMenu {
    private Toolbar toolbar;
    private View view;
    private SearchView searchView;
    private MaterialButton signOut,favorites;
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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.products().observe(this,observer);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.product_fragment,container,false);



        toolbar = view.findViewById(R.id.app_bar);
        progressBar = view.findViewById(R.id.progress_bar_main);
        searchView=view.findViewById(R.id.search);
        signOut = view.findViewById(R.id.sign_out);
        favorites=view.findViewById(R.id.favorites);


        progressBar.setVisibility(ProgressBar.INVISIBLE);


        setUpToolbar();
        setUpRecyclerView();
        setUpSearchView();
        setButtonOnClickListeners();




        return  view;

    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityViewModel.getProducts();
        mainActivityViewModel.getFavoriteProductsNames();
        productsAdapter.notifyDataSetChanged();
        
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
                getContext().getResources().getDrawable(R.drawable.shr_branded_menu), // Menu open icon
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
            view.findViewById(R.id.product_grid).setBackgroundResource(R.drawable.shr_product_grid_background_shape);
    }
    private void setUpSearchView() {
        searchView=view.findViewById(R.id.search);

        SearchManager searchManager=(SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.getNavigationIcon().setVisible(false,false);
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
    private void setButtonOnClickListeners() {
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInUtil signInUtil=new SignInUtil();
                signInUtil.signOut(getActivity());
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.product_fragment_menu, menu);
    }


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem=menu.findItem(R.id.add_products);

            menuItem.setVisible(isAdmin);


    }
    public void updateProducts(List<Product> productList){
        productsAdapter.setProductList(productList);
        productsAdapter.notifyDataSetChanged();
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void setAdmin(Boolean isAdmin) {
        this.isAdmin=isAdmin;
        if(getActivity()!=null){
            getActivity().invalidateOptionsMenu();
        }

    }
}
