package com.example.kaptair.ui.main;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kaptair.R;
import com.example.kaptair.database.AppDatabase;
import com.example.kaptair.ui.main.graphiques.CardGraph;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoriqueFrag extends Fragment {

    private AppDatabase db;

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

        db=AppDatabase.getInstance(getContext());


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

        //Graph Pollution
        TextView txtTitreGraphPollution = getView().findViewById(R.id.txtGraphTitrePollution);

        LineChart graphPollution= getView().findViewById(R.id.graphPollution);

        Button btnHourPollution = getView().findViewById(R.id.btnHourPollution);
        Button btnDayPollution = getView().findViewById(R.id.btnDayPollution);
        Button btnYearPollution = getView().findViewById(R.id.btnYearPollution);

        cardPollution = new CardGraph(this,db.mesurePollutionDao(),db.moyenneDayMesuresPollutionDao(),db.moyenneYearMesuresPollutionDao(),txtTitreGraphPollution,graphPollution,btnHourPollution,btnDayPollution,btnYearPollution);

        //Graph Meteo
        TextView txtTitreGraphMeteo = getView().findViewById(R.id.txtGraphTitreMeteo);

        LineChart graphMeteo= getView().findViewById(R.id.graphMeteo);

        Button btnHourMeteo = getView().findViewById(R.id.btnHourMeteo);
        Button btnDayMeteo = getView().findViewById(R.id.btnDayMeteo);
        Button btnYearMeteo = getView().findViewById(R.id.btnYearMeteo);

        cardMeteo = new CardGraph(this,db.mesureMeteoDao(),db.moyenneDayMesuresMeteoDao(),db.moyenneYearMesuresMeteoDao(),txtTitreGraphMeteo,graphMeteo,btnHourMeteo,btnDayMeteo,btnYearMeteo);

        cards.add(cardPollution);
        cards.add(cardMeteo);

        //On initialise les graphs
        if (savedInstanceState != null){
            int saveIndex=0;
            for( CardGraph c : cards){
                ArrayList<Integer> stateBtns =  savedInstanceState.getIntegerArrayList("stateBtns "+saveIndex);

                for (int i=0;i<stateBtns.size();i++){
                    cardPollution.getBtns().get(i).setSelected(stateBtns.get(i)==1);
                }

                c.getCalendrier().setTimeInMillis(savedInstanceState.getLong("calendrier "+saveIndex));

                if(c.getBtnHour().isSelected()){
                    c.setTitreHour();
                    c.graphHour();
                }else if(c.getBtnDay().isSelected()){
                    c.setTitreDay();
                    c.graphDay();
                }else if (c.getBtnYear().isSelected()){
                    c.setTitreYear();
                    c.graphYear();
                }

                saveIndex++;

            }

        }else{
            for( CardGraph c : cards){
                c.graphHour();
                c.setTitreHour();
            }
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        int saveIndex=0;
        for( CardGraph c : cards){
            ArrayList<Integer> stateBtns = new ArrayList<>();

            for (Button b : c.getBtns()){
                stateBtns.add(b.isSelected() ? 1 : 0);
            }

            outState.putIntegerArrayList("stateBtns "+saveIndex,stateBtns);
            outState.putLong("calendrier "+saveIndex, c.getCalendrier().getTimeInMillis());
            saveIndex++;
        }


    }


}
