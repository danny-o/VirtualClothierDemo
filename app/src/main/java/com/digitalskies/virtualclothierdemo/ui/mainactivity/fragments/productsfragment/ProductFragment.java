package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.productsfragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.digitalskies.virtualclothierdemo.models.User;

import com.digitalskies.virtualclothierdemo.utils.EventObserver;
import com.digitalskies.virtualclothierdemo.utils.OnEventChanged;
import com.digitalskies.virtualclothierdemo.utils.OnNavigateToProductDetails;
import com.digitalskies.virtualclothierdemo.utils.ScheduleWork;
import com.digitalskies.virtualclothierdemo.models.Response;
import com.digitalskies.virtualclothierdemo.notification.NotificationWorker;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.MainActivity;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.SignInUtil;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.digitalskies.virtualclothierdemo.ui.productentryactivity.ProductEntryActivity;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.productsfragment.recycleradapter.StaggeredProductCardRecyclerViewAdapter;
import com.digitalskies.virtualclothierdemo.utils.CustomStaggeredGridLayoutManager;
import com.google.android.material.button.MaterialButton;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;

public class ProductFragment extends Fragment implements ScheduleWork, OnNavigateToProductDetails {
    private Toolbar toolbar;
    private View view;
    private SearchView searchView;

    private MaterialButton signOut;
    private TextView tvAll,tvTrending,favorites,tvMen,tvWomen,selectedCategory;
    private StaggeredProductCardRecyclerViewAdapter productsAdapter;
    private ProductsFragmentViewModel productsFragmentViewModel;
    private ProgressBar progressBar;
    private AppCompatActivity appCompatActivity;
    private SharedPreferences sharedPreferences;
    private int cartItemCount;
    private RecyclerView recyclerView;

    private CustomStaggeredGridLayoutManager staggeredLM;
    private ImageView ivProfile;
    private static final int ADD_PRODUCT = 12;
    private Boolean isAdmin=false;
    private SignInUtil signInUtil;

    private List<Product> products;
    private static final int RESPONSE_SUCCESSFUL=1;

    private static final int RESPONSE_PRODUCT_ADDED_TO_FAV=1;



   Observer<String> imageObserver= image -> {
       if(image!=null){



           Glide.with(requireActivity())
                   .load(image)
                   .apply(new RequestOptions().circleCrop())
                   .into(ivProfile);







       }
   };


    OnBackPressedCallback onBackPressedCallback=new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {


                productsAdapter.setProductList(products);
                productsAdapter.notifyDataSetChanged();
                setEnabled(false);

                selectedCategory.setHovered(false);

                selectedCategory=tvAll;
                tvAll.setHovered(true);

        }
    };

    View.OnClickListener textOnclickListner=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getTag().toString()){

                case "All": {

                    productsAdapter.setProductList(products);

                    productsAdapter.notifyDataSetChanged();

                    selectedCategory.setHovered(false);

                    v.setHovered(true);

                    selectedCategory= (TextView) v;

                    onBackPressedCallback.setEnabled(false);
                    break;

                }

                case "Trending":{


                    selectedCategory.setHovered(false);

                    v.setHovered(true);

                    selectedCategory=(TextView) v;;

                    break;


                }
                case "Favorites": {
                    productsAdapter.setProductList(productsFragmentViewModel.getFavoriteProducts());
                    productsAdapter.notifyDataSetChanged();

                    v.setHovered(true);
                    selectedCategory.setHovered(false);
                    selectedCategory= (TextView) v;

                    onBackPressedCallback.setEnabled(true);

                    break;
                }

                case "Men":{


                    selectedCategory.setHovered(false);

                    v.setHovered(true);

                    selectedCategory=(TextView) v;

                   break;


                }
                case "Women":{


                    selectedCategory.setHovered(false);

                    v.setHovered(true);

                    selectedCategory=(TextView) v;;


                }

            }
        }
    };

    EventObserver<Response> responseObserver=new EventObserver<>(new OnEventChanged() {
        @Override
        public void onUnhandledContent(Object data) {
            if(data!=null){



                Response response=(Response)data;
                toast(response.getResponseMessage());

                hideLoading();
                if(response.getResponseCode()==RESPONSE_SUCCESSFUL){

                    if(response.getResponseFor()==RESPONSE_PRODUCT_ADDED_TO_FAV){
                        enableRecyclerView();
                        productsAdapter.onFavoritedProductUpdatedToDatabase();
                    }

                }

            }

        }
    });




    private Observer<List<Product>> observer=new Observer<List<Product>>() {
        @Override
        public void onChanged(List<Product> productList) {
            updateProducts(productList);

        }
    };



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        signInUtil=SignInUtil.getSignInUtil();
        if(savedInstanceState!=null){
            isAdmin=savedInstanceState.getBoolean("isAdmin");
        }
        else{

            isAdmin=signInUtil.getIfAdmin();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sharedPreferences = getActivity().getApplication().getSharedPreferences("MY_PREFERENCES", Activity.MODE_PRIVATE);
        isAdmin= sharedPreferences.getBoolean("isAdmin",false);



        productsFragmentViewModel = new ViewModelProvider(getActivity()).get(ProductsFragmentViewModel.class);




    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.product_fragment,container,false);


        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        productsFragmentViewModel.getUser();






        progressBar = view.findViewById(R.id.progress_bar_main);

        favorites=view.findViewById(R.id.tv_favorites);
        tvAll=view.findViewById(R.id.tv_all);
        tvTrending=view.findViewById(R.id.tv_trending);

        tvMen=view.findViewById(R.id.tv_men);

        tvWomen=view.findViewById(R.id.tv_women);
        searchView=view.findViewById(R.id.product_search_view);





        ivProfile = view.findViewById(R.id.iv_profile);

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        tvAll.setHovered(true);

        tvTrending.setHovered(false);

        favorites.setHovered(false);

        tvMen.setHovered(false);

        tvWomen.setHovered(false);

        selectedCategory=tvAll;
        productsFragmentViewModel.getUserProfileImage().observe(getViewLifecycleOwner(),imageObserver);

        View viewScrollToEnd=view.findViewById(R.id.tv_scroll_to_end);

        HorizontalScrollView horizontalScrollView=view.findViewById(R.id.category_horizontal_scroll_view);




        viewScrollToEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });









        setUpRecyclerView();
        addProductCategories();
        setButtonOnClickListeners();
        setUpSearch();

        setStatusBarColor();

        showBottomNavigation();
        setUserProfile();





        return  view;

    }

    private void addProductCategories() {

       /* String[] productCategories=getResources().getStringArray(R.array.product_categories);

        int dimensionsSide=(int) getResources().getDimension(R.dimen.text_padding_sides);

        int dimensionsTopAndBottom=(int)getResources().getDimension(R.dimen.text_padding_up_and_bottom);

       LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

       lparams.weight=1f;



        for(String productCategory:productCategories){

            TextView textView=new TextView(requireActivity());

            textView.setLayoutParams(lparams);

            textView.setText(productCategory);





            textView.setBackground(ContextCompat.getDrawable(requireActivity(),R.drawable.bar_black));

            textView.getBackground().setTintList(requireActivity().getColorStateList(R.color.products_category_selector));



            textView.setPadding(dimensionsSide,dimensionsTopAndBottom,dimensionsSide,dimensionsTopAndBottom);

            textView.setTag(productCategory);

            textView.setOnClickListener(textOnclickListner);

            if(productCategory.equals("All")){
                textView.setHovered(true);
            }

            linearLayout.addView(textView);


        }
*/
    }

    @Override
    public void onResume() {
        super.onResume();


        productsFragmentViewModel.getProducts();
        productsFragmentViewModel.getFavAndCartProducts();

        productsFragmentViewModel.products().observe(this,observer);

        productsFragmentViewModel.getResponse().observe(getViewLifecycleOwner(),responseObserver);
        productsAdapter.notifyDataSetChanged();
        scheduleCartReminder();

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
            productsFragmentViewModel.getProducts();
            productsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        productsFragmentViewModel.products().removeObserver(observer);
        Bundle bundle=new Bundle();
        bundle.putBoolean("isAdmin",isAdmin);
        onSaveInstanceState(bundle);
        super.onDestroyView();
    }




    private void setUpRecyclerView() {
        recyclerView = view.findViewById(R.id.product_list);


        recyclerView.setHasFixedSize(true);

        staggeredLM = new CustomStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredLM.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setLayoutManager(staggeredLM);
        productsAdapter = new StaggeredProductCardRecyclerViewAdapter(this,this);
        recyclerView.setAdapter(productsAdapter);
//
        /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            view.findViewById(R.id.product_grid).setBackgroundResource(R.drawable.product_grid_background_shape);*/
    }

    private void setButtonOnClickListeners() {

        favorites.setOnClickListener(textOnclickListner);

        tvAll.setOnClickListener(textOnclickListner);

        tvTrending.setOnClickListener(textOnclickListner);


        tvMen.setOnClickListener(textOnclickListner);

        tvWomen.setOnClickListener(textOnclickListner);
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
    private void setStatusBarColor() {
        ((MainActivity)requireActivity()).setStatusBarColor(getActivity().getColor(R.color.app_theme_color));
    }
    private void showBottomNavigation() {
        ((MainActivity)requireActivity()).showBottomNavigation();
    }
    private void setUserProfile() {


        if(((MainActivity)requireActivity()).getUserImage()!=null){

            String image=((MainActivity)requireActivity()).getUserImage();

            RequestOptions requestOptions=new RequestOptions()
                    .circleCrop()
                    .placeholder(requireActivity()
                            .getDrawable(R.drawable.holder_image));
            Glide.with(requireActivity())
                    .load(image)
                    .apply(requestOptions)
                    .into(ivProfile);
        }
        else{
            Glide.with(requireActivity())
                    .load(requireActivity().getDrawable(R.drawable.holder_image))
                    .apply(new RequestOptions().circleCrop())
                    .into(ivProfile);
        }



    }

    private void setUpSearch() {

        SearchManager searchManager=(SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);


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

        this.products =productList;
        productsAdapter.setProductList(productList);
        productsAdapter.notifyDataSetChanged();

        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void scheduleCartReminder() {
        if(cartItemCount!=0){
            if(signInUtil.getIfIsANewUser()){
                Constraints constraints=new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();
                Data data=new Data.Builder()
                        .putString(NotificationWorker.USERNAME,SignInUtil.getSignInUtil().getUserName())
                        .build();

                WorkRequest notificationRequest=new OneTimeWorkRequest.Builder(NotificationWorker.class)
                        .setInitialDelay(1, TimeUnit.MINUTES)
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build();
                WorkManager.getInstance(requireActivity()).enqueue(notificationRequest);
                signInUtil.setIfIsANewUser(false);

            }

        }

    }
    private void toast(String message){
        Toast.makeText(requireActivity(),message,Toast.LENGTH_SHORT).show();
    }

    private void showLoading() {

        ((MainActivity)requireActivity()).showLoading();
    }

    private void hideLoading() {

        ((MainActivity)requireActivity()).hideLoading();
    }

    private void disableRecyclerView(){
        staggeredLM.setScrollEnabled(false);

        recyclerView.setEnabled(false);
    }

    private void enableRecyclerView(){
        staggeredLM.setScrollEnabled(true);

        recyclerView.setEnabled(true);
    }

    public void updateFavoriteProduct(Product favoriteChangedProduct){
        showLoading();


        disableRecyclerView();



        productsFragmentViewModel.updateFavorites(favoriteChangedProduct);
    }


    @Override
    public void onNavigateToProductDetails(Product product) {
      ProductFragmentDirections.ActionProductFragmentToProductDetailsFragment  actionProductFragmentToProductDetailsFragment
                =ProductFragmentDirections.actionProductFragmentToProductDetailsFragment(product);


      NavHostFragment.findNavController(this).navigate(actionProductFragmentToProductDetailsFragment);


    }
}
