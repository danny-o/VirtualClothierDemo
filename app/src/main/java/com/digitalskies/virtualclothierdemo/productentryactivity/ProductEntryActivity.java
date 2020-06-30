package com.digitalskies.virtualclothierdemo.productentryactivity;

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

import com.digitalskies.virtualclothierdemo.Repository;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

public class ProductEntryActivity extends AppCompatActivity implements ToastMessage {

    private static final int UPLOAD_REQUEST =2 ;
    private static final int REQUEST_PERMISSIONS =1 ;
    private EditText productName,productCategory,productPrice,productDescription;
    private ImageView productImage;
    private Button saveButton;
    private Repository repository;
    private Uri imageUri;
    private ProgressBar uploadProgress;
    private ProductEntryViewModel viewModel;

    Observer<Integer> observer=new Observer<Integer>() {

        @Override
        public void onChanged(Integer uploadStatus) {
            setResult(uploadStatus);
            showToast(uploadStatus);
            hideProgressBar();
            finish();
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
        productCategory=findViewById(R.id.et_product_category);
        productDescription=findViewById(R.id.et_product_description);
        saveButton=findViewById(R.id.save_button);
        productImage=findViewById(R.id.product_image);
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
                        .setType("image/jpeg")
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
            productImage.setImageURI(imageUri);
        }

    }

   public void createProduct(View view){

        if(imageUri!=null){
            showProgressBar();
            viewModel.createProduct(
                    productName.getText().toString(),
                    productCategory.getText().toString(),
                    Integer.parseInt(productPrice.getText().toString()),
                    productDescription.getText().toString(),
                    imageUri);

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
