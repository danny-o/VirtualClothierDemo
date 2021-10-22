package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.productdetailsfragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.digitalskies.virtualclothierdemo.utils.OnExtractDominantColorListener;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageFragment extends Fragment {

    private ConstraintLayout constraintLayout;
    private OnExtractDominantColorListener onExtractDominantColorListener;

    private int backGroundColor, darkBackgroundColor;

    public static final String IMAGE="product_image";

    ImageFragment(OnExtractDominantColorListener onExtractDominantColorListener){

        this.onExtractDominantColorListener = onExtractDominantColorListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_images,container,false);

        ImageView imageView=view.findViewById(R.id.product_image);


        constraintLayout = view.findViewById(R.id.constraint_layout);

        backGroundColor=getResources().getColor(R.color.app_theme_color);

        darkBackgroundColor=getResources().getColor(R.color.app_theme_color);


        if(getArguments()!=null){

            String image=getArguments().getString(IMAGE);



            Glide.with(this)
                    .load(image)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            if(resource instanceof BitmapDrawable){
                                getColor(((BitmapDrawable) resource).getBitmap());

                            }
                            else{
                                Log.d(ImageFragment.class.getSimpleName(),"not instance");
                            }
                            return false;
                        }
                    })

                    .into(imageView);





        }





        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        if(backGroundColor!=getResources().getColor(R.color.app_theme_color)||darkBackgroundColor!=getResources().getColor(R.color.app_theme_color)){

            constraintLayout.setBackgroundColor(backGroundColor);

            onExtractDominantColorListener.onExtractDominantColor(backGroundColor, darkBackgroundColor);
        }



    }

    public int getDominantColor(Bitmap bitmap) {
        List<Palette.Swatch> swatchesTemp = Palette.from(bitmap).generate().getSwatches();
        List<Palette.Swatch> swatches = new ArrayList<Palette.Swatch>(swatchesTemp);
        Collections.sort(swatches, new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch swatch1, Palette.Swatch swatch2) {



                return swatch2.getPopulation() - swatch1.getPopulation();
            }
        });
        return swatches.size() > 0 ? swatches.get(0).getRgb() : getResources().getColor(R.color.app_theme_color);
    }

    public void getColor(Bitmap bitmap) {
        // Generate the palette and get the vibrant swatch
        // See the createPaletteSync() method
        // from the code snippet above
        createPaletteSync(bitmap);

    }

    public void createPaletteSync(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {



                try {
                    int lightMutedColor= p.getLightMutedColor(getResources().getColor(R.color.app_theme_color));
                    int darkMutedColor=p.getDarkMutedColor(getResources().getColor(R.color.app_theme_color));

                    if(lightMutedColor!=getResources().getColor(R.color.app_theme_color)||darkMutedColor!=getResources().getColor(R.color.app_theme_color)){


                        backGroundColor=lightMutedColor;
                        darkBackgroundColor =darkMutedColor;
                        constraintLayout.setBackgroundColor(backGroundColor);

                        onExtractDominantColorListener.onExtractDominantColor(backGroundColor, darkBackgroundColor);
                    }



                }
                catch (Exception e){
                    e.printStackTrace();
                }






            }
        });
    }


}
