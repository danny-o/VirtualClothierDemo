package com.digitalskies.virtualclothierdemo;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.digitalskies.virtualclothierdemo.models.Product;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.List;

public class Repository {
    private static final String IMAGE_UPLOAD = "ImageUpload";
    private FirebaseFirestore firebaseFirestore;
    private Product product;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private CollectionReference collectionReference;
    public MutableLiveData<List<Product>> productsLiveData=new MutableLiveData<>();
    public  MutableLiveData<Integer> uploadComplete=new MutableLiveData<>();
    private static Integer uploadStatus;
    private List<Product> productList=new ArrayList<>();
    private boolean productsQueryComplete;
    private boolean favProductsQueryComplete;
    private List<String> favNameList=new ArrayList<>();
    private ArrayList<Product> favProducts=new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    private static Repository repository;
    private MutableLiveData<String> favoritesUpdated=new MutableLiveData<>();

   private Repository(){
        initiateDatabase();
        initiateFireBaseAuth();
    }
    public static Repository getRepository(){
        if(repository==null){
            repository=new Repository();
        }
        return repository;
    }

    public void initiateDatabase(){
        if(firebaseFirestore==null){
            productsQueryComplete=false;
            favProductsQueryComplete=false;
            firebaseFirestore=FirebaseFirestore.getInstance();
            collectionReference = firebaseFirestore.collection("products");
        }
    }
    public void initiateFireBaseAuth(){
        if(firebaseAuth==null){
            firebaseAuth=FirebaseAuth.getInstance();
        }
    }

    public  void createProduct(String productName, String productCategory,int price,Uri imageUri){
        product=new Product(productName,productCategory,price);
        initiateStorage(productCategory,productName);
        uploadImage(imageUri, productName);

    }
    public void initiateStorage(String productCategory,String productName){
        if(storage==null){
            storage=FirebaseStorage.getInstance();
            storageReference = storage.getReference("products_images").child(productCategory).child(productName);
        }

    }
    public void uploadImage(Uri imageUri, final String productName){
        if(imageUri!=null) {
            storageReference.putFile(imageUri)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(IMAGE_UPLOAD, "failed to upload");
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i(IMAGE_UPLOAD, "success!");
                        }
                    })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                Log.e(IMAGE_UPLOAD,"could not get dload url");
                                throw task.getException();

                            }
                            return storageReference.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String downloadUrl = task.getResult().toString();
                            product.setImage(downloadUrl);
                            uploadProduct(productName);

                        }
                    });
        }
        else{
            product.setImage(null);
            uploadProduct(productName);
        }
    }
    private  void uploadProduct(String productName){
                collectionReference
                .document(productName)
                .set(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            uploadStatus=-1;
                            uploadComplete.setValue(uploadStatus);

                        }
                        else{
                            uploadStatus=1;
                            uploadComplete.setValue(uploadStatus);
                        }
                    }
                });

    }
    public void getProducts(){

       collectionReference.get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.isSuccessful()){
                           productList.clear();
                           for(DocumentSnapshot documentSnapshot:task.getResult()){
                               Product product=documentSnapshot.toObject(Product.class);
                               productList.add(product);


                           }
                           productsQueryComplete=true;
                               setIfFavorite();

                       }
                   }
               });
    }
    public  void getFavoriteProductsNames(){
        firebaseAuth = FirebaseAuth.getInstance();

        final DocumentReference documentReference=firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    favNameList.clear();
                    favNameList = (List<String>) task.getResult().get("favoriteProducts");


                    favProductsQueryComplete=true;

                    setIfFavorite();


                }
                else{
                    Log.e("Fav products Query",task.getException().getMessage());
                }
            }
        });
    }

    private void setIfFavorite() {
        if(productsQueryComplete&&favProductsQueryComplete) {
            String productName;
            favProducts.clear();
            for (int i = 0; i < productList.size(); i++) {
                productName = productList.get(i).getName();
                for (int j = 0; j < favNameList.size(); j++) {
                    if (productName.equals(favNameList.get(j))) {
                        productList.get(i).setFavorite(true);
                        favProducts.add(productList.get(i));
                        break;
                    }
                }
            }
            favProductsQueryComplete = false;
            productsQueryComplete = false;
            productsLiveData.setValue(productList);
        }
    }
    public void updateFavorites(String productName, boolean isfavorite){

        if(isfavorite){
            favNameList.add(productName);
        }
        else{
            favNameList.remove(productName);
        }
        firebaseFirestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .update("favoriteProducts", favNameList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                          favoritesUpdated.setValue("Added to favorites successfully");
                        }
                        else{

                            favoritesUpdated.setValue("Added to favorites failed");
                        }
                    }
                });

    }
    public LiveData<String> getIfFavsUpdated(){
        return favoritesUpdated;
    }

    public LiveData<List<Product>> productList(){
        return productsLiveData;
    }
    public ArrayList<Product> getFavoriteProducts(){
       return favProducts;
    }

    public LiveData<Integer> getProductUploadStatus() {
        return uploadComplete;
    }
}
