

package com.mirea.productapp.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mirea.productapp.R;

import org.jetbrains.annotations.NotNull;

public class InvalidFragment extends Fragment {
    @NotNull
    @Override
    public View onCreateView(LayoutInflater inflater, @NotNull ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invalid, container, false);
    }
}
