package com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.productdetailsfragment;

public class CartReminderStatus {

    private boolean cartReminderScheduled;
    private static CartReminderStatus cartReminderStatus;
    private CartReminderStatus(){
        cartReminderScheduled=false;
    }
    public static CartReminderStatus getCartReminderStatus(){
        if(cartReminderStatus==null){
            cartReminderStatus=new CartReminderStatus();
        }
        return cartReminderStatus;
    }
    boolean getCartReminderScheduled(){
        return cartReminderScheduled;
    }
    void setCartReminderScheduled(boolean status){
        cartReminderScheduled=status;
    }
}
