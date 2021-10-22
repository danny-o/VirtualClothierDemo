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
import androidx.fragment.app.FragmentActivity;
import androidx.palette.graphics.Palette;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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

public class ImageAdapter extends FragmentStateAdapter {

    public static final String IMAGE="product_image";
    private List<String> images;
    private int itemCount;
    private OnExtractDominantColorListener onExtractDominantColorListener;

    public ImageAdapter(@NonNull FragmentActivity fragmentActivity, List<String> images, int itemCount, OnExtractDominantColorListener onExtractDominantColorListener) {
        super(fragmentActivity);
        this.images = images;
        this.itemCount = itemCount;
        this.onExtractDominantColorListener = onExtractDominantColorListener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {


        ImageFragment imageFragment= new ImageFragment(onExtractDominantColorListener);
        if(images.size()>position){
            Bundle bundle=new Bundle();
            bundle.putString(IMAGE,images.get(position));

            imageFragment.setArguments(bundle);
        }


        return imageFragment;
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }



}
