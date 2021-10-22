package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.checkoutfragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.LoginFragment;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.deliverydetailsfragment.DeliveryDetailsFragment;
import com.digitalskies.virtualclothierdemo.utils.EventObserver;
import com.digitalskies.virtualclothierdemo.utils.OnEventChanged;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.digitalskies.virtualclothierdemo.models.Response;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.MainActivity;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import java.util.ArrayList;
import java.util.List;


public class CheckOutFragment extends Fragment {

    private static final int RESPONSE_SUCCESSFUL = 1;
    RecyclerView cartList;
    TextView TvTotalPrice;
    List<Product> productsInCart=new ArrayList<>();
    public final String CART_LIST="cartlist";
    private CheckOutFragmentViewModel viewModel;
    private ProgressBar progressBar;
    private CartListAdapter cartListAdapter;
    private ImageView ivProfile;
    private Button btnCheckOut;


    Observer<String> imageObserver= image -> {
        if(image!=null){



            Glide.with(requireActivity())
                    .load(image)
                    .apply(new RequestOptions().circleCrop())
                    .into(ivProfile);







        }
    };



    EventObserver<Response>  responseObserver=new EventObserver<>(new OnEventChanged<Response>() {
        @Override
        public void onUnhandledContent(Response response) {
            if(response!=null){

               ((MainActivity)requireActivity()).hideLoading();
                Toast.makeText(requireContext(),response.getResponseMessage(),Toast.LENGTH_SHORT).show();

                if(response.getResponseCode()==RESPONSE_SUCCESSFUL){
                    cartListAdapter.onCartItemsChanged(true);

                    ((MainActivity)requireActivity()).setUpCartCount(productsInCart.size());

                    if(productsInCart.size()==0){
                        showNoItemsInCartLayout();
                    }
                }
                else {

                    cartListAdapter.onCartItemsChanged(false);
                }



            }
        }
    });

    private View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_check_out,container,false);

        viewModel = new ViewModelProvider(this).get(CheckOutFragmentViewModel.class);

        productsInCart= viewModel.getProductsInCart();



        cartList= view.findViewById(R.id.cart_list);
        TvTotalPrice = view.findViewById(R.id.total);
        progressBar= view.findViewById(R.id.progressBar);

        ivProfile = view.findViewById(R.id.iv_profile);


        btnCheckOut = view.findViewById(R.id.checkout);

        viewModel.getResponse().observe(getViewLifecycleOwner(),responseObserver);


        viewModel.getUserProfileImage().observe(getViewLifecycleOwner(),imageObserver);



        if(productsInCart.size()==0){
           showNoItemsInCartLayout();
        }
        else{
            setUpRecyclerView();
        }





        setStatusBarColor();

        setUserProfile();

        setUpCheckoutButton();

        showBottomNavigation();






        return view;
    }

    private void setUpCheckoutButton() {

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavHostFragment.findNavController(CheckOutFragment.this).navigate(R.id.action_checkOutFragment_to_deliveryDetailsFragment);
            }
        });
    }

    private void showNoItemsInCartLayout() {
      view.findViewById(R.id.layout_no_items_in_Cart)
                .setVisibility(View.VISIBLE);
        btnCheckOut.setVisibility(View.GONE);

        view.findViewById(R.id.text_total).setVisibility(View.GONE);

        view.findViewById(R.id.total).setVisibility(View.GONE);

    }




    private void setStatusBarColor() {
        ((MainActivity)requireActivity()).setStatusBarColor(getActivity().getColor(R.color.app_theme_color));
    }
    private void setUserProfile() {


        Glide.with(requireActivity())
                .load(requireActivity().getDrawable(R.drawable.holder_image))
                .apply(new RequestOptions().circleCrop())
                .into(ivProfile);


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

    private void showBottomNavigation() {
        ((MainActivity)requireActivity()).showBottomNavigation();
    }


    private void setUpRecyclerView() {


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(requireContext());
        cartList.setLayoutManager(linearLayoutManager);


        cartList.addItemDecoration(new CartListItemDecorator(requireContext()));

        cartListAdapter = new CartListAdapter(requireContext(),this,productsInCart);
        cartList.setAdapter(cartListAdapter);
    }
    public void updateTotalPrice(int total){
        String totalPrice="$" + total+".00";
          TvTotalPrice.setText(totalPrice);
    }
    public void updateProductsInCart(Product product){
        ((MainActivity)requireActivity()).showLoading();

        viewModel.updateCart(product,false);



    }


}
