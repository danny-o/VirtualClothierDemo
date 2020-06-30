package com.digitalskies.virtualclothierdemo;

public interface OnEventChanged<T> {

    void onUnhandledContent(T data);
}
