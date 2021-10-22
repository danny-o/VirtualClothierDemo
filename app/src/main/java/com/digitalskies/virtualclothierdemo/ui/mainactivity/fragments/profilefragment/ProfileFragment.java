package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.profilefragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.digitalskies.virtualclothierdemo.models.Response;
import com.digitalskies.virtualclothierdemo.models.User;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.MainActivity;
import com.digitalskies.virtualclothierdemo.ui.mainactivity.SignInUtil;
import com.digitalskies.virtualclothierdemo.ui.productentryactivity.ProductEntryActivity;
import com.digitalskies.virtualclothierdemo.utils.EventObserver;
import com.digitalskies.virtualclothierdemo.utils.OnEventChanged;
import com.google.android.material.button.MaterialButton;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import com.theartofdev.edmodo.cropper.CropImage;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;


import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private View view;
    private Button btnSignOut;
    private ImageButton editEmail;
    private ImageButton editDeliveryAddress;
    private EditText etEmail;
    private EditText etDeliveryAddress;
    private MaterialButton btnSave;
    private SharedPreferences sharedPreferences;
    private EditText etName;
    private ImageView profileImage;
    private ProfileFragmentViewModel viewModel;
    private Button btnAddProduct;


    EventObserver<User> userObserver=new EventObserver<>((OnEventChanged<User>) user -> {
        if(user!=null){
            hideLoading();

            if(user.getImage()!=null){
                Glide.with(requireActivity())
                        .load(user.getImage())
                        .apply(new RequestOptions().circleCrop())
                        .into(profileImage);


            }
            if(user.getDeliveryAddress()!=null){
                etDeliveryAddress.setText(user.getDeliveryAddress());
            }

            if(user.getAdmin()){
                btnAddProduct.setVisibility(View.VISIBLE);

                btnAddProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), ProductEntryActivity.class));
                    }
                });
            }

            etName.setText(user.getName());

            etEmail.setText(user.getEmail());





        }

    });

    EventObserver<Response> responseObserver=new EventObserver<>(new OnEventChanged() {
        @Override
        public void onUnhandledContent(Object data) {
            if(data!=null){

                Response response=(Response)data;
                toast(response.getResponseMessage());

                hideLoading();


            }

        }
    });
    private ImageButton editImage;
    private ImageButton editName;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile,container,false);

        viewModel = new ViewModelProvider(this).get(ProfileFragmentViewModel.class);







        btnSignOut = view.findViewById(R.id.btn_signout);

        editEmail = view.findViewById(R.id.iv_edit_email);

        editDeliveryAddress = view.findViewById(R.id.iv_edit_delivery_address);

        editImage = view.findViewById(R.id.iv_edit_profile_image);

        editName = view.findViewById(R.id.iv_edit_name);

        etEmail = view.findViewById(R.id.et_email);

        etDeliveryAddress = view.findViewById(R.id.et_delivery_address);

        btnSave = view.findViewById(R.id.btn_save);

        etName = view.findViewById(R.id.tv_name);

        profileImage = view.findViewById(R.id.iv_profile);

        btnAddProduct = view.findViewById(R.id.btnAddProduct);







        setUpSignOutButton();


        setUpEditButtons();

        setStatusBarColor();

        showBottomNavigation();



        setUserProfile();

        viewModel.getResponse().observe(getViewLifecycleOwner(),responseObserver);


        viewModel.getUserLiveData().observe(getViewLifecycleOwner(),userObserver);




        return view;

    }



    private void setUpEditButtons() {





        editEmail.setOnClickListener(v -> {


            etDeliveryAddress.setEnabled(false);

            etEmail.setEnabled(!etEmail.isEnabled());



            etEmail.requestFocus();
            btnSave.setEnabled(etEmail.isEnabled());

        });

        editDeliveryAddress.setOnClickListener(v -> {
            etEmail.setEnabled(false);
            etDeliveryAddress.setEnabled(!etDeliveryAddress.isEnabled());

            etDeliveryAddress.requestFocus();
            btnSave.setEnabled(etDeliveryAddress.isEnabled());
        });

        editName.setOnClickListener(v -> {
            etName.setEnabled(true);


            etName.requestFocus();
            btnSave.setEnabled(true);
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PickImageDialog.build(new PickSetup())
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult pickResult) {
                                if (pickResult.getError() == null) {
                                    setImageForCrop(pickResult);
                                }
                            }
                        }).setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {

                    }
                }).show(requireActivity().getSupportFragmentManager());
            }
        });

        btnSave.setOnClickListener(v -> {


            String email=etEmail.getText().toString();

            String deliveryAddress=etDeliveryAddress.getText().toString();

            String name=etName.getText().toString();

            HashMap<String,Object> data=new HashMap<>();

            if(!email.isEmpty()){
                data.put("email",email);
            }

            if(!deliveryAddress.isEmpty()){
                data.put("deliveryAddress",deliveryAddress);

            }
            if(name.isEmpty()){
                etName.setError("Name cannot be blank");
                return;
            }
            data.put("name",name);


            showLoading();

            viewModel.updateUserData(data);



        });






    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                profileImage.setImageURI(resultUri);

                RequestOptions requestOptions=new RequestOptions()
                        .placeholder(requireActivity().getDrawable(R.drawable.holder_image))
                        .circleCrop();

                Glide.with(requireActivity())

                        .load(resultUri)
                        .apply(requestOptions)
                        .into(profileImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                toast(error.getMessage());
            }
        }
    }
    private void setUserProfile() {


            Glide.with(requireActivity())
                    .load(requireActivity().getDrawable(R.drawable.holder_image))
                    .apply(new RequestOptions().circleCrop())
                    .into(profileImage);




    }
    private void setImageForCrop(PickResult r) {
//        Intent intent = CropImage.activity(r.getUri()).getIntent(getmActivity());
//        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        CropImage.activity(r.getUri()).start(requireActivity(), this);

    }

    private void setStatusBarColor() {
        ((MainActivity)requireActivity()).setStatusBarColor(getActivity().getColor(R.color.app_theme_color_dark));
    }

    private void showBottomNavigation() {
        ((MainActivity)requireActivity()).showBottomNavigation();
    }

    private void setUpSignOutButton() {

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInUtil.getSignInUtil().signOut();
            }
        });
    }

    private void showLoading() {

        ((MainActivity)requireActivity()).showLoading();
    }

    private void hideLoading() {

        ((MainActivity)requireActivity()).hideLoading();
    }
    private void toast(String message){
        Toast.makeText(requireActivity(),message,Toast.LENGTH_SHORT).show();
    }

}
