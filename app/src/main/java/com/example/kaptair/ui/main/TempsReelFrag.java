package com.example.kaptair.ui.main;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
       /* Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);*/
        return fragment;
    }
}
