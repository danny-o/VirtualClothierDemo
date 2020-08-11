package com.digitalskies.virtualclothierdemo.ui.mainactivity.recycleradapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter used to show an asymmetric grid of products, with 2 items in the first column, and 1
 * item in the second column, and so on.
 */
public class StaggeredProductCardRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredProductCardViewHolder>
implements Filterable {
    private List<Product> productList;
    private List<Product> filteredProducts;

    private Context context;

    public StaggeredProductCardRecyclerViewAdapter(Context context) {
        productList=new ArrayList<>();
        filteredProducts =new ArrayList<>();
        this.context=context;
    }
    public void setProductList(List<Product> filteredProducts){
            productList.clear();
            productList.addAll(filteredProducts);
            this.filteredProducts = filteredProducts;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 3;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public StaggeredProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.product_card;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new StaggeredProductCardViewHolder(layoutView,context);
    }


    @Override
    public void onBindViewHolder(@NonNull StaggeredProductCardViewHolder holder, int position) {
        if (filteredProducts != null && position < filteredProducts.size()) {
            Product product = filteredProducts.get(position);
            String price="$"+product.getPrice();
            holder.productName.setText(product.getName());
            holder.productPrice.setText(price);
            holder.product= filteredProducts.get(position);
            RequestOptions requestOptions=new RequestOptions().placeholder(context.getDrawable(android.R.drawable.ic_menu_gallery));
            Glide.with(holder.productImage)
                    .load(product.getImage())
                    .apply(requestOptions)
                    .into(holder.productImage);
        }
    }

    @Override
    public int getItemCount() {
        return filteredProducts.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString=charSequence.toString();
                if(charString.isEmpty()){
                  filteredProducts=productList;
                }
                else{
                    ArrayList<Product> filteredList = new ArrayList<>();
                    for(int i=0;i<productList.size();i++){
                        if(productList.get(i).getName().toLowerCase().contains(charString)){
                            filteredList.add(productList.get(i));
                        }
                        else if(productList.get(i).getProductCategory().toLowerCase().contains(charSequence) ){
                            filteredList.add(productList.get(i));
                        }
                    }
                    filteredProducts=filteredList;
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=filteredProducts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredProducts=(ArrayList<Product>)results.values;
                notifyDataSetChanged();
            }
        };
    }
}
