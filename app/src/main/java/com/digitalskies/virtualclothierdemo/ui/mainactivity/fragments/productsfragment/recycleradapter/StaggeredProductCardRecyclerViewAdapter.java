package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.productsfragment.recycleradapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.MainActivity;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.productsfragment.ProductFragment;
import com.digitalskies.virtualclothierdemo.utils.FavoritedProductListener;
import com.digitalskies.virtualclothierdemo.utils.OnNavigateToProductDetails;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter used to show an asymmetric grid of products, with 2 items in the first column, and 1
 * item in the second column, and so on.
 */
public class StaggeredProductCardRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredProductCardViewHolder>
implements Filterable, FavoritedProductListener {

    private StaggeredProductCardViewHolder favoritedViewHolder;
    private List<Product> productList;
    private List<Product> filteredProducts;


    private OnNavigateToProductDetails onNavigateToProductDetails;
    private ProductFragment productFragment;

    public StaggeredProductCardRecyclerViewAdapter(OnNavigateToProductDetails onNavigateToProductDetails,ProductFragment productFragment) {
        this.onNavigateToProductDetails = onNavigateToProductDetails;
        this.productFragment = productFragment;
        productList=new ArrayList<>();
        filteredProducts =new ArrayList<>();
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
        int layoutId = R.layout.item_product;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new StaggeredProductCardViewHolder(layoutView,this,onNavigateToProductDetails);
    }


    @Override
    public void onBindViewHolder(@NonNull StaggeredProductCardViewHolder holder, int position) {
        if (filteredProducts != null && position < filteredProducts.size()) {
            Product product = filteredProducts.get(position);
            String price="$"+product.getPrice();
            holder.productName.setText(product.getName());
            holder.productPrice.setText(price);
            holder.product= filteredProducts.get(position);

            if(product.isFavorite()){
                holder.imgFavorite.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.icon_favorite_filled));
            }
            RequestOptions requestOptions=new RequestOptions()
                    .optionalCenterCrop()

                    .placeholder(ContextCompat.getDrawable(holder.itemView.getContext(),android.R.drawable.ic_menu_gallery));


            if(product.getImage()!=null){
                Glide.with(holder.productImage)
                        .load(product.getImage())
                        .apply(requestOptions)
                        .into(holder.productImage);
            }
            if(product.getImages()!=null){
                if(product.getImages().size()!=0){
                    Glide.with(holder.productImage)
                            .load(product.getImages().get(0))
                            .apply(requestOptions)
                            .into(holder.productImage);
                }
            }


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

    @Override
    public void onProductFavorited(RecyclerView.ViewHolder holder) {
        favoritedViewHolder=(StaggeredProductCardViewHolder) holder;



        Product product=productList.get(holder.getAdapterPosition());

        product.setFavorite(!product.isFavorite());



        productFragment.updateFavoriteProduct(product);


    }

    @Override
    public void onFavoritedProductUpdatedToDatabase() {

        if(favoritedViewHolder!=null){
            Product product=productList.get(favoritedViewHolder.getAdapterPosition());

            if(product.isFavorite()){
                favoritedViewHolder.imgFavorite.setImageDrawable(ContextCompat.getDrawable(favoritedViewHolder.itemView.getContext(),R.drawable.icon_favorite_filled));
            }
            else{
                favoritedViewHolder.imgFavorite.setImageDrawable(ContextCompat.getDrawable(favoritedViewHolder.itemView.getContext(),R.drawable.icon_favorite_border));
            }
        }

    }
}
