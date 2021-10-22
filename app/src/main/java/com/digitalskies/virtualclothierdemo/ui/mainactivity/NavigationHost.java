package com.digitalskies.virtualclothierdemo.ui.mainactivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public interface NavigationHost {
    void navigateTo(Fragment fragment, Boolean addToBackStack, Bundle bundle);
}
