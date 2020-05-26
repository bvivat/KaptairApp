package com.example.kaptair.ui.main;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaptair.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class TempsReelFrag extends Fragment {

    // On enregistre toutes les TextViews des valeurs dès le lancement, une fois pour toute
    final static ArrayList<Integer> values = new ArrayList<Integer>() {{
        add(R.id.txtPm1Val);
        add(R.id.txtPm2_5Val);
        add(R.id.txtPm10Val);
        add(R.id.txtCo2Val);
        add(R.id.txtTemperatureVal);
        add(R.id.txtHumiditeVal);
    }};

    public TempsReelFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_temps_reel, container, false);

        if (savedInstanceState != null) {
            // On restaure les couleurs de chaque valeurs
            for (Integer i : values) {
                int color = savedInstanceState.getInt(Integer.toString(i));
                ((TextView) v.findViewById(i)).setTextColor(color);
            }
        }

        return v;
    }


    public static TempsReelFrag newInstance() {
        TempsReelFrag fragment = new TempsReelFrag();
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // On sauvegarde les couleurs de chaque valeurs (le texte est enregistre via l'attribut xml "freezesText"
        for (Integer i : values) {
            int color = ((TextView) getView().findViewById(i)).getCurrentTextColor();
            outState.putInt(Integer.toString(i), color);
        }

    }
}
