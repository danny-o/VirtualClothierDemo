package com.digitalskies.virtualclothierdemo.ui.checkoutactivity;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalskies.virtualclothierdemo.models.Product;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {

    private ArrayList<Product> productList;
    private Context context;
    private int total;
    private Integer[] subtotalList;


    public CartListAdapter(Context context, ArrayList<Product> productList){
        this.context=context;
        this.productList=productList;
        getTotalPrice();
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_recyclerview_item, parent, false);

        return new ViewHolder(layoutView,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String price="$"+productList.get(position).getPrice();
        holder.itemPosition=position;
        holder.unitPrice=productList.get(position).getPrice();
        holder.productName.setText(productList.get(position).getName());
        holder.price.setText(price);
        holder.tvSubtotal.setText(price);
        holder.quantity.setText("1");
        Picasso.get()
                .load(productList.get(position).getImage())
                .into(holder.productImage);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public void getTotalPrice(){
        total = 0;
        subtotalList = new Integer[productList.size()];
       for(int i=0;i<productList.size();i++) {
          subtotalList[i]=productList.get(i).getPrice();
          total = total +productList.get(i).getPrice();
       }
        ((CheckOutActivity)context).updateTotalPrice(total);

    }
    public void updateTotal(int itemPosition,int newSubtotal){
        total=total-subtotalList[itemPosition]+newSubtotal;
        subtotalList[itemPosition]=newSubtotal;
        ((CheckOutActivity)context).updateTotalPrice(total);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        ImageButton increment,decrement,removeItem;
        EditText quantity;
        TextView tvSubtotal,remove,price,productName;
        int unitPrice,itemPosition;
        private int productSubtotal;


        public ViewHolder(@NonNull final View itemView, final Context context) {
            super(itemView);
            productImage=itemView.findViewById(R.id.item_product_image);
            productName=itemView.findViewById(R.id.name_of_product);
            price=itemView.findViewById(R.id.price_of_product);
            increment=itemView.findViewById(R.id.increment);
            decrement=itemView.findViewById(R.id.decrement);
            tvSubtotal =itemView.findViewById(R.id.price_subtotal);
            quantity=itemView.findViewById(R.id.count);
            removeItem= itemView.findViewById(R.id.remove_btn);
            remove=itemView.findViewById(R.id.remove);

            increment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  int itemCount=Integer.parseInt(quantity.getText().toString());

                  itemCount++;
                    productSubtotal = unitPrice*itemCount;


                  quantity.setText(Integer.toString(itemCount));
                  tvSubtotal.setText(Integer.toString(productSubtotal));
                  updateTotal(itemPosition, productSubtotal);



                }
            });
            decrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemCount=Integer.parseInt(quantity.getText().toString());
                    itemCount--;
                    productSubtotal=unitPrice*itemCount;
                    quantity.setText(Integer.toString(itemCount));
                    tvSubtotal.setText(Integer.toString(productSubtotal));

                    updateTotal(itemPosition,productSubtotal);
                }
            });
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   itemView.setVisibility(View.GONE);
                   total=total-productSubtotal;
                   ((CheckOutActivity)context).updateTotalPrice(total);
                   productList.remove(getAdapterPosition());
                   notifyItemRemoved(getAdapterPosition());

                }
            });
            removeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.setVisibility(View.GONE);
                    total=total-productSubtotal;
                    ((CheckOutActivity)context).updateTotalPrice(total);
                    productList.remove(itemPosition);
                    ((CheckOutActivity)context).updateProductsInCart(productList);
                    notifyItemRemoved(itemPosition);
                }
            });
        }


    }
}
