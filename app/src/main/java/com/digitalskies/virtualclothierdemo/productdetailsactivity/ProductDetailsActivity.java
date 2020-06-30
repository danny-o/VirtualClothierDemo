package com.digitalskies.virtualclothierdemo.productdetailsactivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.digitalskies.virtualclothierdemo.EventObserver;
import com.digitalskies.virtualclothierdemo.OnEventChanged;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import static com.digitalskies.virtualclothierdemo.recycleradapter.StaggeredProductCardViewHolder.productDetails;

public class ProductDetailsActivity extends AppCompatActivity {
    ImageView productImage,expandIcon;
    TextView productName,productPrice,tvDescription,descriptionDetails;
    String price;
    Boolean expanded=false;
    private Toolbar toolbar;
    private int cartItemsCount;
    private ImageButton favoriteBtn;
    private Button checkOutBtn;
    private boolean isFavorite;
    private Product product;
    private ProductDetailsViewModel viewModel;


    EventObserver<String> eventObserver=new EventObserver<>(new OnEventChanged() {
        @Override
        public void onUnhandledContent(Object data) {
        if(data!=null){
                favAndCartUpdated((String)data);
        }

        }
    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ViewModelProvider viewModelProvider=new ViewModelProvider(this);
        viewModel = viewModelProvider.get(ProductDetailsViewModel.class);
        viewModel.favoritesUpdated().observe(this,eventObserver);


        productImage=findViewById(R.id.product_image);
        productName=findViewById(R.id.product_name);
        productPrice=findViewById(R.id.tv_product_price);
        tvDescription=findViewById(R.id.et_product_description);
        descriptionDetails=findViewById(R.id.description_details);
        expandIcon=findViewById(R.id.expand_ic);
        favoriteBtn = findViewById(R.id.favorite);
        toolbar=findViewById(R.id.product_details_toolbar);
        checkOutBtn=findViewById(R.id.add_to_cart);

        setSupportActionBar(toolbar);

        descriptionDetails.setVisibility(View.GONE);


        bindDataToViews();

        setUpFavButton();

        cartItemsCount=getCartItemsCount();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.product_details_activity_menu,menu);
        setUpCartBadge(menu);

        return true;
    }
    private void setUpCartBadge(Menu menu){
        MenuItem menuItem=menu.findItem(R.id.product_details_cart);
        View view=menuItem.getActionView();
        TextView cartBadge=view.findViewById(R.id.cart_badge);
        if(getCartItemsCount()==0){
            cartBadge.setVisibility(View.GONE);
        }else
        {
            cartBadge.setText(String.valueOf(cartItemsCount));
            if(cartBadge.getVisibility()!=TextView.VISIBLE){
                cartBadge.setVisibility(TextView.VISIBLE);
            }
        }

    }

    private void bindDataToViews() {
        product = getIntent().getParcelableExtra(productDetails);
        RequestOptions requestOptions=new RequestOptions().placeholder(getDrawable(android.R.drawable.ic_menu_gallery));
        Glide.with(this)
                .load(product.getImage())
                .apply(requestOptions)
                .into(productImage);

        price =getString(R.string.price_prefix)+ product.getPrice();
        productName.setText(product.getName());
        productPrice.setText(price);
        descriptionDetails.setText(product.getProductDescription());


    }
    public void expandProductDescription(View view){
        if(!expanded){
            expandIcon.setImageDrawable(getDrawable(R.drawable.icon_expand_more));
            descriptionDetails.setVisibility(TextView.VISIBLE);
            expanded=true;
        }
        else{
            expandIcon.setImageDrawable(getDrawable(R.drawable.icon_expand_less));
            descriptionDetails.setVisibility(TextView.GONE);
            expanded=false;
        }

    }
    public void setIfFavorite(View view){
        if(!isFavorite){
            favoriteBtn.setImageDrawable(getDrawable(R.drawable.icon_favorite_filled));
            isFavorite=true;
            product.setFavorite(true);
            viewModel.updateFavorites(product.getName(),isFavorite);
        }
        else{
            favoriteBtn.setImageDrawable(getDrawable(R.drawable.icon_favorite_border));
            isFavorite=false;
            product.setFavorite(false);
            viewModel.updateFavorites(product.getName(),isFavorite);

        }
    }
    private void setUpFavButton() {
        isFavorite=product.isFavorite();
        if(isFavorite){

            favoriteBtn.setImageDrawable(getDrawable(R.drawable.icon_favorite_filled));
        }
        else {
            favoriteBtn.setImageDrawable(getDrawable(R.drawable.icon_favorite_border));
        }
    }
    public void favAndCartUpdated(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
    public int getCartItemsCount(){

        SharedPreferences sharedPreferences=getSharedPreferences("MY_PREFERENCES",MODE_PRIVATE);
        return sharedPreferences.getInt("PRODUCTS_IN_CART",0);

    }
    public  void addToCart(View view){
        if(product.isInCart()){
           Toast.makeText(this,"product already in cart",Toast.LENGTH_SHORT).show();
        }
        else{
            cartItemsCount++;
            SharedPreferences sharedPreferences=getSharedPreferences("MY_PREFERENCES", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt("PRODUCTS_IN_CART",cartItemsCount);
            editor.apply();
            invalidateOptionsMenu();

            viewModel.addProductToCart(product.getName());
        }

    }
}
