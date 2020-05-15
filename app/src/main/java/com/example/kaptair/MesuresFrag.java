package com.example.kaptair;


import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaptair.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class MesuresFrag extends Fragment {


    public MesuresFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mesures, container, false);

        // On change le titre de la toolbar
        TextView txtTitre = getActivity().findViewById(R.id.txtTitre);
        txtTitre.setText(R.string.mesures);

        // On initialise le ViewPager
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getContext(), getChildFragmentManager());

        ViewPager viewPager = v.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        // On passe le ViewPager dans la toolbar si on est en mode paysage
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            TabLayout tabs = getActivity().findViewById(R.id.tabs);
            tabs.setVisibility(View.VISIBLE);
            tabs.setupWithViewPager(viewPager);
        } else {
            TabLayout tabs = v.findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
        }

        // Necessaire pour pouvoir modifier le menu de la toolbar
        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // On affiche l'item Synchroniser dans la toolbar
        menu.findItem(R.id.action_synchro).setVisible(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().findViewById(R.id.tabs).setVisibility(View.GONE); //Si on change de fragment alors qu'on est en paysage, on supprime le TabLayout de la toolbar
        }
    }
}
