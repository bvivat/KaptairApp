package com.example.kaptair.ui.main;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.kaptair.R;
import com.example.kaptair.database.AppDatabase;
import com.example.kaptair.ui.main.graphiques.CardGraph;
import com.example.kaptair.ui.main.graphiques.Graph;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


/**
 * Created by Benjamin Vivat on 06/23/2020.
 */
public class HistoriqueFrag extends Fragment {

    private AppDatabase db;

    public static final String SAVE_STATE_BTN = "stateBtns ";
    public static final String SAVE_CALENDAR = "calendar ";

    CardGraph cardPollution;
    CardGraph cardMeteo;

    ArrayList<CardGraph> cards = new ArrayList<CardGraph>();


    public HistoriqueFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_historique, container, false);

        db = AppDatabase.getInstance(getContext());

        // Inflate the layout for this fragment
        return v;
    }


    public static HistoriqueFrag newInstance() {
        HistoriqueFrag fragment = new HistoriqueFrag();
        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TabLayout tab = getActivity().findViewById(R.id.tabs);
        final ConstraintLayout toolbar = getActivity().findViewById(R.id.appBarLayout);

        AppCompatImageButton btnFullPoll = getView().findViewById(R.id.btnFullScreenPoll);
        AppCompatImageButton btnFullMeteo = getView().findViewById(R.id.btnFullScreenMeteo);

        final View fragPoll = getView().findViewById(R.id.fragHistoriquePoll);
        final View fragMeteo = getView().findViewById(R.id.fragHistoriqueMeteo);

        // Listener des boutons plein ecran
        View.OnClickListener fullScreenListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    // Si deja en plein ecran, on reaffiche tout
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    tab.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    fragMeteo.setVisibility(View.VISIBLE);
                    fragPoll.setVisibility(View.VISIBLE);
                    v.setSelected(false);
                } else {
                    // On enleve l'autre graph
                    switch (v.getId()) {
                        case R.id.btnFullScreenPoll:
                            fragMeteo.setVisibility(View.GONE);
                            break;

                        case R.id.btnFullScreenMeteo:
                            fragPoll.setVisibility(View.GONE);
                            break;
                    }
                    // On enleve la toolbar et les onglets du ViewPager
                    tab.setVisibility(View.GONE);
                    toolbar.setVisibility(View.GONE);

                    // On passe en mode plein ecran ( suppression de la bare de statut )
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    v.setSelected(true);
                }

            }
        };
        btnFullPoll.setOnClickListener(fullScreenListener);
        btnFullMeteo.setOnClickListener(fullScreenListener);


        //Graph Pollution
        TextView txtTitreGraphPollution = getView().findViewById(R.id.txtGraphTitrePollution);

        LineChart graphPollution = getView().findViewById(R.id.graphPollution);

        Button btnHourPollution = getView().findViewById(R.id.btnHourPollution);
        Button btnDayPollution = getView().findViewById(R.id.btnDayPollution);
        Button btnYearPollution = getView().findViewById(R.id.btnYearPollution);

        cardPollution = new CardGraph(this, db.mesurePollutionDao(), db.moyenneDayMesuresPollutionDao(), db.moyenneYearMesuresPollutionDao(), txtTitreGraphPollution, graphPollution, btnHourPollution, btnDayPollution, btnYearPollution, Graph.GRAPH_POLLUTION);

        //Graph Meteo
        TextView txtTitreGraphMeteo = getView().findViewById(R.id.txtGraphTitreMeteo);

        LineChart graphMeteo = getView().findViewById(R.id.graphMeteo);

        Button btnHourMeteo = getView().findViewById(R.id.btnHourMeteo);
        Button btnDayMeteo = getView().findViewById(R.id.btnDayMeteo);
        Button btnYearMeteo = getView().findViewById(R.id.btnYearMeteo);

        cardMeteo = new CardGraph(this, db.mesureMeteoDao(), db.moyenneDayMesuresMeteoDao(), db.moyenneYearMesuresMeteoDao(), txtTitreGraphMeteo, graphMeteo, btnHourMeteo, btnDayMeteo, btnYearMeteo, Graph.GRAPH_METEO);

        cards.add(cardPollution);
        cards.add(cardMeteo);

        //On initialise les cards
        if (savedInstanceState != null) {
            int saveIndex = 0;
            for (CardGraph c : cards) {
                ArrayList<Integer> stateBtns = savedInstanceState.getIntegerArrayList(SAVE_STATE_BTN + saveIndex);

                for (int i = 0; i < stateBtns.size(); i++) {
                    // On restore l'etat des boutons
                    c.getBtns().get(i).setSelected(stateBtns.get(i) == 1);
                }

                // On restore le calendrier
                c.getCalendrier().setTimeInMillis(savedInstanceState.getLong(SAVE_CALENDAR + saveIndex));

                // On initialise le graph
                if (c.getBtnHour().isSelected()) {
                    c.setTitreHour();
                    c.graphHour();
                } else if (c.getBtnDay().isSelected()) {
                    c.setTitreDay();
                    c.graphDay();
                } else if (c.getBtnYear().isSelected()) {
                    c.setTitreYear();
                    c.graphYear();
                }

                saveIndex++;

            }

        } else {
            for (CardGraph c : cards) {
                c.getBtnHour().setSelected(true);
                c.graphHour();
                c.setTitreHour();
            }
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        int saveIndex = 0;
        for (CardGraph c : cards) { // Pour chaque card (pollution et meteo)
            ArrayList<Integer> stateBtns = new ArrayList<>();

            for (Button b : c.getBtns()) {
                stateBtns.add(b.isSelected() ? 1 : 0); // On sauvegarde l'etat de ses boutons
            }

            outState.putIntegerArrayList(SAVE_STATE_BTN + saveIndex, stateBtns);
            outState.putLong(SAVE_CALENDAR + saveIndex, c.getCalendrier().getTimeInMillis());
            saveIndex++;
        }


    }


}
