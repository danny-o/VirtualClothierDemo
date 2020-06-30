package com.digitalskies.virtualclothierdemo;

import android.app.Application;
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
    private Application application;
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
    private boolean favAndCartQueryComplete;
    private List<String> favNameList=new ArrayList<>();
    private List<String> cartProductsNames=new ArrayList<>();
    private ArrayList<Product> favProducts=new ArrayList<>();
    private ArrayList<Product> productsInCart=new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    private static Repository repository;
    private MutableLiveData<Event<String>> favsAndCartUpdated =new MutableLiveData<>();

   private Repository(Application application){
        this.application=application;
        initiateDatabase();
        initiateFireBaseAuth();
    }
    public static Repository getRepository(Application application){
        if(repository==null){
            repository=new Repository(application);
        }
        return repository;
    }

    public void initiateDatabase(){
        if(firebaseFirestore==null){
            productsQueryComplete=false;
            favAndCartQueryComplete =false;
            firebaseFirestore=FirebaseFirestore.getInstance();
            collectionReference = firebaseFirestore.collection("products");
        }
    }
    public void initiateFireBaseAuth(){
        if(firebaseAuth==null){
            firebaseAuth=FirebaseAuth.getInstance();
        }
    }

    public  void createProduct(String productName, String productCategory,int price,String productDescription,Uri imageUri){
        product=new Product(productName,productCategory,price,productDescription);
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
                               setIfFavOrInCart();

                       }
                   }
               });
    }
    public  void getFavAndCartProducts(){
        firebaseAuth = FirebaseAuth.getInstance();

        final DocumentReference documentReference=firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    favNameList.clear();
                    favNameList = (List<String>) task.getResult().get("favoriteProducts");

                    cartProductsNames.clear();
                    cartProductsNames =(List<String>) task.getResult().get("productsInCart");


                    favAndCartQueryComplete =true;

                    setIfFavOrInCart();



                }
                else{
                    Log.e("Fav products Query",task.getException().getMessage());
                }
            }
        });
    }

    private void setIfFavOrInCart() {
        if(productsQueryComplete&& favAndCartQueryComplete) {
            String productName;
            favProducts.clear();
            productsInCart.clear();
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
            for (int i = 0; i < productList.size(); i++) {
                productName = productList.get(i).getName();
                for (int j = 0; j < cartProductsNames.size(); j++) {
                    if (productName.equals(cartProductsNames.get(j))) {
                        productList.get(i).setInCart(true);
                        productsInCart.add(productList.get(i));
                        break;
                    }
                }


            }
            favAndCartQueryComplete = false;
            productsQueryComplete = false;
            productsLiveData.setValue(productList);
        }
    }
    public void updateFavorites(String productName, final boolean isfavorite){

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
                            if(isfavorite)
                                favsAndCartUpdated.setValue(new Event<>("Product added to favorites"));
                            else{
                                favsAndCartUpdated.setValue(new Event<>("Product removed from favorites"));
                            }
                        }
                        else{

                            favsAndCartUpdated.setValue(new Event<>("Failed to add to favorites"));
                        }
                    }
                });

    }
    public void addProductToCart(String productName){


       cartProductsNames.add(productName);

        firebaseFirestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .update("productsInCart", cartProductsNames)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            favsAndCartUpdated.setValue(new Event<>("Product added to cart"));
                        }
                        else{

                            favsAndCartUpdated.setValue(new Event<>("Failed to add to cart"));
                        }
                    }
                });

    }
    public LiveData<Event<String>> getIfFavsUpdated(){
        return favsAndCartUpdated;
    }

    public LiveData<List<Product>> productList(){
        return productsLiveData;
    }
    public ArrayList<Product> getFavoriteProducts(){
       return favProducts;
    }
    public ArrayList<Product> getProductsInCart(){
        return productsInCart;
    }

    public LiveData<Integer> getProductUploadStatus() {
        return uploadComplete;
    }
}
