package com.digitalskies.virtualclothierdemo.ui.productentryactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.digitalskies.virtualclothierdemo.data.Repository;
import com.digitalskies.virtualclothierdemo.models.ImageUploadResponse;
import com.digitalskies.virtualclothierdemo.models.Product;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ProductEntryActivity extends AppCompatActivity implements ToastMessage {

    private static final int UPLOAD_REQUEST =2 ;
    private static final int REQUEST_PERMISSIONS =1 ;
    private EditText productName,productCategory,productPrice,productDescription,productTags;
    private ImageView productImage;
    private Button saveButton;
    private Repository repository;
    private Uri imageUri;
    private ProgressBar uploadProgress;
    private ProductEntryViewModel viewModel;
    private List<Uri> imageUris=new ArrayList<>();
    private List<String> downLoadUrls=new ArrayList<>();

    private int uploadedImages=0;

    Observer<Integer> observer=new Observer<Integer>() {

        @Override
        public void onChanged(Integer uploadStatus) {

            showToast(uploadStatus);
            hideProgressBar();

        }
    };
    Observer<ImageUploadResponse> imageUploadObserver= imageUploadResponse -> {

        if(imageUploadResponse.isImageUploaded()){
            uploadedImages++;
            downLoadUrls.add(imageUploadResponse.getDownloadUrl());
            if(uploadedImages<imageUris.size()){


                viewModel.uploadImage(imageUris.get(uploadedImages),productCategory.getText().toString(),productName.getText().toString(),uploadedImages);
            }
            if(uploadedImages==imageUris.size()){

                List<String> tags=Arrays.asList(productTags.getText().toString().replace(" ","").split(","));

               tags.forEach(s -> s.replace(",",""));


                viewModel.createProduct(productName.getText().toString(),
                        productCategory.getText().toString(),Integer.parseInt(productPrice.getText().toString()),
                        productDescription.getText().toString(),downLoadUrls,tags);
            }
        }
        else{
            showToast(1);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_entry2);

        viewModel = new ViewModelProvider(this).get(ProductEntryViewModel.class);
        viewModel.getUploadStatus().observe(this,observer);

        productName=findViewById(R.id.et_product_name);
        productPrice=findViewById(R.id.et_product_price);
        productTags=findViewById(R.id.et_product_tags);
        productCategory=findViewById(R.id.et_product_category);
        productDescription=findViewById(R.id.et_product_description);
        saveButton=findViewById(R.id.save_button);
        productImage=findViewById(R.id.item_product_image);
        uploadProgress=findViewById(R.id.uploadProgress);
        hideProgressBar();
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission
                        (ProductEntryActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ProductEntryActivity.this,
                            new String[]{Manifest.permission
                                    .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                            REQUEST_PERMISSIONS);
                }
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT)
                        .setType("image/*")
                        .putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                ProductEntryActivity.this.startActivityForResult(Intent.createChooser(intent,"Select picture"),UPLOAD_REQUEST);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==UPLOAD_REQUEST&&resultCode==RESULT_OK){

            imageUri = data.getData();
            imageUris.add(data.getData());

            if(imageUris.size()==1){
                productImage.setImageURI(imageUri);
            }

        }

    }

   public void createProduct(View view){

        if(imageUris.size()!=0){
            showProgressBar();

            viewModel.getImageUploadResponse().observe(this,imageUploadObserver);

            viewModel.uploadImage(imageUris.get(0),productCategory.getText().toString(),productName.getText().toString(),0);


        }
        else{
            Toast.makeText(this,"please set the Image",Toast.LENGTH_SHORT).show();
        }


    }
    public void showProgressBar(){
        uploadProgress.setVisibility(ProgressBar.VISIBLE);
    }
    public void hideProgressBar() {
            uploadProgress.setVisibility(ProgressBar.INVISIBLE);

    }

    @Override
    public void showToast(Integer uploadStatus) {
        if(uploadStatus==-1){
            Toast.makeText(this,getString(R.string.product_added),Toast.LENGTH_LONG).show() ;
        }
        else{
            Toast.makeText(this,getString(R.string.product_upload_failed),Toast.LENGTH_LONG).show();
        }

    }
}
