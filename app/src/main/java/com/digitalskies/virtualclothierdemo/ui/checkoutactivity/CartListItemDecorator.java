package com.digitalskies.virtualclothierdemo.ui.checkoutactivity;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.codelabs.mdc.java.virtualclothierdemo.R;

public class CartListItemDecorator extends RecyclerView.ItemDecoration {

    private final Drawable divider;

    public  CartListItemDecorator(Context context) {
        this.divider = context.getResources().getDrawable(R.drawable.divider);
    }
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left=parent.getPaddingLeft();
        int right=parent.getWidth()-parent.getPaddingRight();

        int childCount=parent.getChildCount();
        for(int i=0;i<(childCount-1);i++){

            View child=parent.getChildAt(i);
            RecyclerView.LayoutParams params=(RecyclerView.LayoutParams)child.getLayoutParams();

            int top=child.getBottom() +params.bottomMargin;


            int bottom=(int)(top+ divider.getIntrinsicHeight());
            divider.setBounds(left,top,right,bottom);
            divider.draw(c);


        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position=parent.getChildAdapterPosition(view);
        if(position==parent.getChildCount()-1){
            outRect.setEmpty();
        }
        else{
            outRect.set(0,0,0, divider.getIntrinsicHeight());
        }


    }
}
