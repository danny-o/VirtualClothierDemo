package com.digitalskies.virtualclothierdemo.interfaces;

import androidx.fragment.app.Fragment;

public interface NavigationHost {
    void navigateTo(Fragment fragment,Boolean addToBackStack);
}
