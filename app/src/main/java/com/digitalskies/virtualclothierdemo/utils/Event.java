package com.digitalskies.virtualclothierdemo.utils;

public class Event<T>{
    private boolean hasBeenHandled=false;
    private T content;
    public Event(T content){
        this.content=content;
    }
    public  T getContentIfNotHandled(){
        if(!hasBeenHandled){
            hasBeenHandled=true;
            return content;

        }
        else{
            return null;
        }



    }
    public  boolean isHandled(){
        return hasBeenHandled;
    }
}
