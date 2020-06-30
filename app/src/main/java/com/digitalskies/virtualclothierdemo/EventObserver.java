package com.digitalskies.virtualclothierdemo;


import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;


public class EventObserver<T> implements Observer<Event<T>> {

    private OnEventChanged onEventChanged;

    public EventObserver(OnEventChanged onEventChanged){
        this.onEventChanged=onEventChanged;
    }

    @Override
    public void onChanged(@Nullable Event<T> tEvent) {
        if(tEvent!=null&& !tEvent.isHandled()&& onEventChanged!=null)
            onEventChanged.onUnhandledContent(tEvent.getContentIfNotHandled());
    }
}
