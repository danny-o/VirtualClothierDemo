package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.productdetailsfragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.digitalskies.virtualclothierdemo.models.Response;
import com.digitalskies.virtualclothierdemo.models.User;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.MainActivity;
import com.digitalskies.virtualclothierdemo.utils.EventObserver;
import com.digitalskies.virtualclothierdemo.utils.OnEventChanged;
import com.digitalskies.virtualclothierdemo.utils.OnExtractDominantColorListener;
import com.digitalskies.virtualclothierdemo.utils.ScheduleWork;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.digitalskies.virtualclothierdemo.notification.NotificationWorker;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.SignInUtil;
import com.digitalskies.virtualclothierdemo.utils.ByteUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;
import com.google.common.collect.Collections2;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.productsfragment.recycleradapter.StaggeredProductCardViewHolder.productDetails;

public class ProductDetailsFragment extends Fragment implements ScheduleWork, OnExtractDominantColorListener {
    ImageView productImage,ivExpand;
    TextView productName,productPrice,tvDescription,tvDescriptionDetails;

    Button btnAddTOCart;

    //NestedScrollView descriptionDetailsScrollView;
    String price;
    Boolean expanded=false;
    BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private int cartItemsCount;
    private TextView selectedClothSizeTextView;
    private View view;
    private ImageView ivProfile;
    private String selectClothSize;
    private boolean cartReminderShecduled;
    private Product product;

    private static final int RESPONSE_SUCCESSFUL = 1;
    private ProductDetailsViewModel viewModel;


    EventObserver<Response> responseObserver=new EventObserver<>(new OnEventChanged<Response>() {
        @Override
        public void onUnhandledContent(Response response) {
        if(response!=null){
            (( MainActivity)requireActivity()).hideLoading();
            Toast.makeText(requireContext(),response.getResponseMessage(),Toast.LENGTH_SHORT).show();
            if(response.getResponseCode()==RESPONSE_SUCCESSFUL){
                scheduleCartReminder();
                cartItemsCount++;
                SharedPreferences sharedPreferences=requireActivity().getSharedPreferences("MY_PREFERENCES", MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("PRODUCTS_IN_CART",cartItemsCount);
                editor.apply();

                product.setInCart(true);




               (( MainActivity)requireActivity()).setUpCartCount(cartItemsCount);
            }


        }

        }
    });
    private ConstraintLayout constraintLayout;
    private TabLayout tabLayout;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_product_details,container,false);

        viewModel=new ViewModelProvider(this).get(ProductDetailsViewModel.class);



        viewModel.getResponse().observe(getViewLifecycleOwner(),responseObserver);

        //productImage=view.findViewById(R.id.iv_product_image);
        constraintLayout = view.findViewById(R.id.constraint_layout);

        tabLayout = view.findViewById(R.id.tab_layout);
        productName=view.findViewById(R.id.product_name);
        productPrice=view.findViewById(R.id.tv_product_price);
        tvDescription=view.findViewById(R.id.text_description);
       // descriptionDetailsScrollView =view.findViewById(R.id.scroll_view_product_description);

        btnAddTOCart=view.findViewById(R.id.add_to_cart);

        tvDescriptionDetails=view.findViewById(R.id.description_details);
        ivExpand=view.findViewById(R.id.iv_expand);

        ivProfile = view.findViewById(R.id.iv_profile);


      /*  Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)requireActivity()).setSupportActionBar(toolbar);*/




        bindDataToViews();

        //descriptionDetailsScrollView.setVisibility(View.GONE);






        populateClothSizeTextViews();



        cartItemsCount=getCartItemsCount();



        setCartReminderStatus();

        setStatusBarColor();

        showBottomNavigation();

       setUserProfile();






        btnAddTOCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        tvDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandProductDescription();
            }
        });

        ivExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandProductDescription();
            }
        });






        return view;
    }

    private void populateClothSizeTextViews() {

        ConstraintLayout constraintLayout=view.findViewById(R.id.clothsizes_cl);

        String[] clothSizes={"S","M","L","XL","XXL"};

        List<TextView> textViews=new ArrayList<>();

        for(String clothSize:clothSizes){
            TextView textView=new TextView(requireActivity());

            textView.setText(clothSize);

            textView.setGravity(Gravity.CENTER);

            textView.setWidth((int)requireActivity().getResources().getDimension(R.dimen.clothe_size_textview_size));

            textView.setHeight((int)requireActivity().getResources().getDimension(R.dimen.clothe_size_textview_size));

            textView.setBackground(requireActivity().getDrawable(R.drawable.drawable_circle));

            textView.getBackground().setTint(requireActivity().getColor(android.R.color.white));


            textView.setId(View.generateViewId());


            switch (clothSize){
                case "S": textView.setTag("Small");
                case "M":textView.setTag("Medium");
                case "L": textView.setTag("Large");

                case "XL":textView.setTag("XLarge");

                case "XXL": textView.setTag("XXLarge");
            }

            if(clothSize.equals("S")){


                selectClothSize=(String) textView.getTag();



                selectedClothSizeTextView =textView;

                textView.setHovered(true);

                ;
            }
            else {
                textView.setHovered(false);
            }



            textView.setTextColor(requireActivity().getColorStateList(R.color.clothesize_textview_selector));



            textView.setOnClickListener(v -> {

                if(v.equals(selectedClothSizeTextView)){
                    return;
                }


                selectedClothSizeTextView.setHovered(false);



                v.setHovered(true);

                selectClothSize = (String) v.getTag();

                selectedClothSizeTextView = (TextView) v;
            });

            textViews.add(textView);




            constraintLayout.addView(textView);
        }

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);



        View previousItem = null;
        for(TextView tv : textViews) {
            boolean lastItem =textViews.indexOf(tv) ==textViews.size() - 1;
            if(previousItem == null) {
                constraintSet.connect(tv.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
            } else {
                constraintSet.connect(tv.getId(), ConstraintSet.LEFT, previousItem.getId(), ConstraintSet.RIGHT);
                if(lastItem) {
                    constraintSet.connect(tv.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
                }
            }
            previousItem = tv;
        }
        int[] viewIds =  ByteUtils.toIntArray(new ArrayList<>(Collections2.transform(textViews, View::getId)));
        constraintSet.createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, (int[]) viewIds, null, ConstraintSet.CHAIN_SPREAD);
        constraintSet.applyTo(constraintLayout);

    }


    private void setStatusBarColor() {
        ((MainActivity)requireActivity()).setStatusBarColor(getActivity().getColor(R.color.app_theme_color));
    }

    private void showBottomNavigation() {
        ((MainActivity)requireActivity()).showBottomNavigation();
    }





    private void bindDataToViews() {

        product=ProductDetailsFragmentArgs.fromBundle(getArguments()).getProduct();

     /*   CollapsingToolbarLayout coll_toolbar = view.findViewById(R.id.collapsing_toolbar_layout);
        coll_toolbar.setTitle(product.getName());

        coll_toolbar.setExpandedTitleColor(ContextCompat.getColor(requireActivity(),android.R.color.transparent));
       ;
        coll_toolbar.setContentScrimColor(getResources().getColor(R.color.app_theme_color));*/




            RequestOptions requestOptions=new RequestOptions()
                    .placeholder(requireActivity().getDrawable(android.R.drawable.ic_menu_gallery));
           /* Glide.with(this)
                    .load(product.getImage())
                    .apply(requestOptions)
                    .into(productImage);*/


            ViewPager2 viewPager2=view.findViewById(R.id.view_pager);

            viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        if(product.getImages()!=null) {

            if (product.getImages().size() != 0) {
                ImageAdapter imageAdapter = new ImageAdapter(requireActivity(), product.getImages(), product.getImages().size(), this);

                viewPager2.setAdapter(imageAdapter);

                TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {

                });

                tabLayoutMediator.attach();
            }
        }
            else{

                List<String> imageList=new ArrayList<>();
                imageList.add(product.getImage());

                ImageAdapter imageAdapter=new ImageAdapter(requireActivity(),imageList,imageList.size(),this);

                viewPager2.setAdapter(imageAdapter);
            }










            price =getString(R.string.price_prefix)+ product.getPrice();
            productName.setText(product.getName());
            productPrice.setText(price);
            tvDescriptionDetails.setText(product.getProductDescription());




    }
    public void expandProductDescription(){
        if(!expanded){
            ivExpand.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_add_24));

            //descriptionDetailsScrollView.setVisibility(TextView.GONE);
            expanded=true;
        }
        else{
            ivExpand.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_minimize_24));

            //descriptionDetailsScrollView.setVisibility(TextView.VISIBLE);
            expanded=false;
        }

    }
    private void setUserProfile() {


        /*if(getArguments().getString(MainActivity.USER_IMAGE)!=null){

            String image=getArguments().getString(MainActivity.USER_IMAGE);

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
        }*/



    }


    public void favAndCartUpdated(String message){
        Toast.makeText(requireActivity(),message,Toast.LENGTH_SHORT).show();
    }
    private void setCartReminderStatus() {
        cartReminderShecduled=viewModel.getCartReminderStatus();
    }
    public int getCartItemsCount(){

        SharedPreferences sharedPreferences=requireActivity().getSharedPreferences("MY_PREFERENCES",MODE_PRIVATE);
        return sharedPreferences.getInt("PRODUCTS_IN_CART",0);

    }
    public  void addToCart(){
        if(product.isInCart()){
           Toast.makeText(requireActivity(),"product already in cart",Toast.LENGTH_SHORT).show();
        }
        else{

            (( MainActivity)requireActivity()).showLoading();


            viewModel.addProductToCart(product);
        }

    }

    @Override
    public void scheduleCartReminder() {
        if(!cartReminderShecduled){
            Constraints constraints=new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            Data data=new Data.Builder()
                    .putString(NotificationWorker.USERNAME, SignInUtil.getSignInUtil().getUserName())
                    .build();

            WorkRequest notificationRequest=new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInitialDelay(1, TimeUnit.MINUTES)
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build();
            WorkManager.getInstance(requireActivity()).enqueue(notificationRequest);
            viewModel.setCartReminderScheduled(true);
        }

    }

    @Override
    public void onExtractDominantColor(int lightMutedColor,int darkMutedColor) {

        if(isAdded()){
            constraintLayout.setBackgroundColor(lightMutedColor);
            ((MainActivity)requireActivity()).setStatusBarColor(lightMutedColor);

            ((MainActivity)requireActivity()).setBottomNavigationColor(lightMutedColor,darkMutedColor);

            btnAddTOCart.setBackgroundColor(darkMutedColor);

            btnAddTOCart.setTextColor(getResources().getColor(android.R.color.white));

            tabLayout.setBackgroundColor(lightMutedColor);
        }





    }
}
