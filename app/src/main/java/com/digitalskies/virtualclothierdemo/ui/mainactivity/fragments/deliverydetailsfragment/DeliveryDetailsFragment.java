package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.deliverydetailsfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.digitalskies.virtualclothierdemo.models.User;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.MainActivity;
import com.digitalskies.virtualclothierdemo.utils.EventObserver;
import com.digitalskies.virtualclothierdemo.utils.OnEventChanged;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;
import com.yesterselga.countrypicker.CountryPicker;
import com.yesterselga.countrypicker.CountryPickerListener;
import com.yesterselga.countrypicker.Theme;

public class DeliveryDetailsFragment extends Fragment {

    View view;

    private EditText etEmail,etAddress,etCountry;

    private ImageView ivProfile;

    private DeliveryDetailsViewModel viewModel;



    EventObserver<User> userObserver=new EventObserver<>((OnEventChanged<User>) user -> {
        if(user!=null){
            ((MainActivity)requireActivity()).hideLoading();


            if(user.getImage()!=null){
                Glide.with(requireActivity())
                        .load(user.getImage())
                        .apply(new RequestOptions().circleCrop())
                        .into(ivProfile);


            }




            if(user.getDeliveryAddress()!=null){
                etAddress.setText(user.getDeliveryAddress());

                etAddress.setTextColor(getResources().getColor(R.color.dark_gray));
            }



            etEmail.setText(user.getEmail());





        }

    });
    private Button btnContinue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delivery_details,container,false);


        ((MainActivity)requireActivity()).showLoading();


        viewModel = new ViewModelProvider(this).get(DeliveryDetailsViewModel.class);

        etEmail=view.findViewById(R.id.et_email);

        ivProfile=view.findViewById(R.id.iv_profile);


        etAddress=view.findViewById(R.id.et_address);

        etCountry=view.findViewById(R.id.et_country);


        btnContinue = view.findViewById(R.id.btnContinue);


        setUpBtnContinue();


        setUserProfile();

        setUpCountryPicker();






        viewModel.getUser();







        viewModel.getUserLiveData().observe(getViewLifecycleOwner(),userObserver);











        return view;
    }

    private void setUpBtnContinue() {

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    private void setUpCountryPicker() {

        etCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                  showCountryPickerDialog();
                }
            }
        });

       etCountry.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showCountryPickerDialog();
           }
       });


    }

    private void showCountryPickerDialog(){
        CountryPicker picker = CountryPicker.newInstance("Select Country", Theme.DARK);  // dialog title and theme
        picker.setListener((name, code, dialCode, flagDrawableResID) -> {


            etCountry.setText(name);


            picker.dismiss();
        });

        picker.show(requireActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
    }

    private void setUserProfile() {

        Glide.with(requireActivity())
                .load(requireActivity().getDrawable(R.drawable.holder_image))
                .apply(new RequestOptions().circleCrop())
                .into(ivProfile);
    }
}
