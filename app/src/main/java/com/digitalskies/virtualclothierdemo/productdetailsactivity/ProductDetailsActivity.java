package com.digitalskies.virtualclothierdemo.productdetailsactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import static com.digitalskies.virtualclothierdemo.staggeredgridlayout.StaggeredProductCardViewHolder.*;

public class ProductDetailsActivity extends AppCompatActivity {
    ImageView productImage,expandIcon;
    TextView productName,productPrice,tvDescription,descriptionDetails;
    String price;
    Boolean expanded=false;
    private ImageButton favoriteBtn;
    private boolean isFavorite;
    private Product product;
    private ProductDetailsViewModel viewModel;

    Observer<String> observer=new Observer<String>() {
        @Override
        public void onChanged(String s) {
            favoriteProductsUpdatedToast(s);
            Log.d("ProductDetails Activity"," "+getLifecycle().getCurrentState()+s);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ViewModelProvider viewModelProvider=new ViewModelProvider(this);
        viewModel = viewModelProvider.get(ProductDetailsViewModel.class);
        viewModel.favoritesUpdated().observe(this,observer);

        productImage=findViewById(R.id.product_image);
        productName=findViewById(R.id.product_name);
        productPrice=findViewById(R.id.tv_product_price);
        tvDescription=findViewById(R.id.product_description);
        descriptionDetails=findViewById(R.id.description_details);
        expandIcon=findViewById(R.id.expand_ic);
        favoriteBtn = findViewById(R.id.favorite);

        descriptionDetails.setVisibility(View.GONE);
        bindDataToViews();

        setUpFavButton();

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
            expandIcon.setImageDrawable(getDrawable(R.drawable.ic_expand_more_black_24dp));
            descriptionDetails.setVisibility(TextView.VISIBLE);
            expanded=true;
        }
        else{
            expandIcon.setImageDrawable(getDrawable(R.drawable.ic_expand_less_black_24dp));
            descriptionDetails.setVisibility(TextView.GONE);
            expanded=false;
        }

    }
    public void setIfFavorite(View view){
        if(!isFavorite){
            favoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_favorite_filled_24dp));
            isFavorite=true;
            product.setFavorite(true);
            viewModel.updateFavorites(product.getName(),isFavorite);
        }
        else{
            favoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_24dp));
            isFavorite=false;
            product.setFavorite(false);
            viewModel.updateFavorites(product.getName(),isFavorite);

        }
    }
    private void setUpFavButton() {
        isFavorite=product.isFavorite();
        if(isFavorite){

            favoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_favorite_filled_24dp));
        }
        else {
            favoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_24dp));
        }
    }
    public void favoriteProductsUpdatedToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
