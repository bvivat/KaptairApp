package com.example.kaptair.ui.main.sousFragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kaptair.R;

/**
 * Created by Benjamin Vivat on 06/23/2020.
 */
public class HistoriqueMeteo extends Fragment {


    public HistoriqueMeteo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historique_meteo, container, false);
    }

}