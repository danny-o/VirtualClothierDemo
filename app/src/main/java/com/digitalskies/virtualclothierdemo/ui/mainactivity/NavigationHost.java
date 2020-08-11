package com.digitalskies.virtualclothierdemo.ui.mainactivity;

import androidx.fragment.app.Fragment;

public interface NavigationHost {
    void navigateTo(Fragment fragment,Boolean addToBackStack);
}
