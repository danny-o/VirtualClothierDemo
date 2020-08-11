package com.digitalskies.virtualclothierdemo.ui.checkoutactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.widget.TextView;

import com.digitalskies.virtualclothierdemo.models.Product;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import java.util.ArrayList;

public class CheckOutActivity extends AppCompatActivity {

    RecyclerView cartList;
    TextView TvTotalPrice;
    ArrayList<Product> productsInCart=new ArrayList<>();
    public final String CART_LIST="cartlist";
    private CheckOutActivityViewModel viewModeldel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);


        viewModeldel = new ViewModelProvider(this).get(CheckOutActivityViewModel.class);
        cartList=findViewById(R.id.cart_list);
        TvTotalPrice =findViewById(R.id.total);

        setUpRecyclerView();




    }

    @Override
    protected void onPause() {
        super.onPause();
        int s=productsInCart.size();
        viewModeldel.updateCartProductNames(productsInCart);


    }

    private void setUpRecyclerView() {
        productsInCart=viewModeldel.getProductsInCart();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        cartList.setLayoutManager(linearLayoutManager);


        cartList.addItemDecoration(new CartListItemDecorator(this));

        CartListAdapter cartListAdapter=new CartListAdapter(this,productsInCart);
        cartList.setAdapter(cartListAdapter);
    }
    public void updateTotalPrice(int total){
        String totalPrice="$" + total+".00";
          TvTotalPrice.setText(totalPrice);
    }
    public void updateProductsInCart(ArrayList<Product> productsInCart){
            this.productsInCart=productsInCart;
    }
}
