package com.digitalskies.virtualclothierdemo.ui.mainactivity.recycleradapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalskies.virtualclothierdemo.ui.productdetailsactivity.ProductDetailsActivity;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.google.android.material.card.MaterialCardView;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StaggeredProductCardViewHolder extends RecyclerView.ViewHolder {
    public MaterialCardView materialCardView;
    public ImageView productImage;
    public TextView productName;
    public TextView productPrice;
    public Product product;

    public static String productDetails="com.digitalskies.virtualclothierdemo.staggeredgridlayout.StaggeredProductCardViewHolder.product";

    StaggeredProductCardViewHolder(@NonNull View itemView, final Context context) {
        super(itemView);
        productImage = itemView.findViewById(R.id.item_product_image);
        productName = itemView.findViewById(R.id.product_title);
        productPrice = itemView.findViewById(R.id.tv_product_price);
        product=new Product();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ProductDetailsActivity.class);
                intent.putExtra(productDetails,product);
                context.startActivity(intent);
            }
        });
    }
}
