package com.example.kaptair.ui.main;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaptair.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TempsReelFrag extends Fragment {


    public TempsReelFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_temps_reel, container, false);
    }


    public static TempsReelFrag newInstance() {
        TempsReelFrag fragment = new TempsReelFrag();
        return fragment;
    }


}
