package com.example.kaptair.ui.main;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.kaptair.R;
import com.example.kaptair.database.AppDatabase;
import com.example.kaptair.database.MesureMeteo;
import com.example.kaptair.database.MesurePollution;
import com.example.kaptair.database.MoyenneDayMesuresMeteo;
import com.example.kaptair.database.MoyenneDayMesuresPollution;
import com.example.kaptair.database.MoyenneYearMesuresMeteo;
import com.example.kaptair.database.MoyenneYearMesuresPollution;
import com.example.kaptair.ui.main.graphiques.MeteoGraph;
import com.example.kaptair.ui.main.graphiques.PollutionGraph;
import com.github.mikephil.charting.charts.LineChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoriqueFrag extends Fragment {

    private AppDatabase db;
    private Executor executor = Executors.newSingleThreadExecutor();

    LineChart graphPollution;
    LineChart graphMeteo;

    public Calendar getC() {
        return c;
    }

    final Calendar c = Calendar.getInstance();


    public HistoriqueFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_historique, container, false);

        db=AppDatabase.getInstance(getContext());

        //Graphs
        final TextView txtTitreGraph = v.findViewById(R.id.txtGraphTitrePollution);

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        final int mHour= c.get(Calendar.HOUR_OF_DAY);
        final int mMinute=0;

        DatePickerDialog.OnDateSetListener dateHeure = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                TimePickerDialog timePickerDialog = new TimePickerDialog(HistoriqueFrag.this.getContext(),R.style.MyDatePicker,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                c.set(Calendar.MINUTE,mMinute);
                                SimpleDateFormat sdfHours = new SimpleDateFormat("EEEE dd MMMM YYYY HH:mm");
                                txtTitreGraph.setText(sdfHours.format(c.getTime()));

                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }

        };

        DatePickerDialog.OnDateSetListener dateJour = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdfHours = new SimpleDateFormat("EEEE dd MMMM YYYY");
                txtTitreGraph.setText(sdfHours.format(c.getTime()));
            }
        };

        final DatePickerDialog.OnDateSetListener dateAnnee = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                c.set(Calendar.YEAR, year);
                SimpleDateFormat sdfHours = new SimpleDateFormat("YYYY");
                txtTitreGraph.setText(sdfHours.format(c.getTime()));
            }
        };

        final DatePickerDialog pickerHeure = new DatePickerDialog(this.getContext(),R.style.MyDatePicker,dateHeure,mYear,mMonth,mDay);
        final DatePickerDialog pickerJour = new DatePickerDialog(this.getContext(),R.style.MyDatePicker,dateJour,mYear,mMonth,mDay);
        final DatePickerDialog pickerAnnee = new DatePickerDialog(this.getContext(),R.style.MyDatePicker,dateAnnee,mYear,mMonth,mDay);


        //Graphs Pollution
        graphPollution= v.findViewById(R.id.graphPollution);
        
        Button btnHourPollution = v.findViewById(R.id.btnHourPollution);
        Button btnDayPollution = v.findViewById(R.id.btnDayPollution);
        Button btnYearPollution = v.findViewById(R.id.btnYearPollution);

        btnHourPollution.setSelected(true);

        final ArrayList<Button> btnsChoixGraphPollution = new ArrayList<Button>();
        btnsChoixGraphPollution.add(btnHourPollution);
        btnsChoixGraphPollution.add(btnDayPollution);
        btnsChoixGraphPollution.add(btnYearPollution);


        View.OnClickListener listenerChoixGraphPollution = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnTmp = (Button) v;
                if(!btnTmp.isSelected()){
                    btnTmp.setSelected(true);
                }
                for (Button b : btnsChoixGraphPollution){
                    if(b.getId()!= btnTmp.getId() && b.isSelected()){
                        b.setSelected(false);
                    }
                }
                switch (v.getId()){ //TODO Remplacer exemples par mesures de la BD (dans des fonctions séparées)
                    case R.id.btnHourPollution:
                        pickerHeure.show();
                        graphPollutionHour();
                        break;
                    case R.id.btnDayPollution:
                        pickerJour.show();
                        graphPollutionDay();
                        break;
                    case R.id.btnYearPollution:
                        YearPickerDialog y = new YearPickerDialog();
                        y.show(getFragmentManager(),"picker");
                        y.setListener(dateAnnee);
                        //pickerAnnee.show();
                        graphPollutionYear();
                        break;
                    default:
                        break;
                }
            }
        };

        for(Button b : btnsChoixGraphPollution){
            b.setOnClickListener(listenerChoixGraphPollution);
        }

        //Graphs Meteo
        graphMeteo= v.findViewById(R.id.graphMeteo);

        Button btnHourMeteo = v.findViewById(R.id.btnHourMeteo);
        Button btnDayMeteo = v.findViewById(R.id.btnDayMeteo);
        Button btnYearMeteo = v.findViewById(R.id.btnYearMeteo);

        btnHourMeteo.setSelected(true);

        final ArrayList<Button> btnsChoixGraphMeteo = new ArrayList<Button>();
        btnsChoixGraphMeteo.add(btnHourMeteo);
        btnsChoixGraphMeteo.add(btnDayMeteo);
        btnsChoixGraphMeteo.add(btnYearMeteo);

        View.OnClickListener listenerChoixGraphMeteo = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnTmp = (Button) v;
                if(!btnTmp.isSelected()){
                    btnTmp.setSelected(true);
                }
                for (Button b : btnsChoixGraphMeteo){
                    if(b.getId()!= btnTmp.getId() && b.isSelected()){
                        b.setSelected(false);
                    }
                }
                switch (v.getId()){ //TODO Remplacer exemples par mesures de la BD
                    case R.id.btnHourMeteo:
                        graphMeteoHour();
                        break;
                    case R.id.btnDayMeteo:
                        graphMeteoDay();
                        break;
                    case R.id.btnYearMeteo:
                        graphMeteoYear();
                        break;
                    default:
                        break;
                }
            }
        };

        for(Button b : btnsChoixGraphMeteo){
            b.setOnClickListener(listenerChoixGraphMeteo);
        }


        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //On initialise les graphs
        graphPollutionHour();
        graphMeteoHour();
    }

    public static HistoriqueFrag newInstance() {
        HistoriqueFrag fragment = new HistoriqueFrag();
        return fragment;
    }

    public void graphPollutionHour(){
        /*
        PollutionMesure m = new MesurePollution(new Date(1586998800000L),50,1,5,400);
        PollutionMesure m1 = new MesurePollution(new Date(1586999703000L),10,1,100,600);
        PollutionMesure m2 = new MesurePollution(new Date(1587000723785L),30,2,150,500);
        PollutionMesure m3 = new MesurePollution(new Date(1587001599999L),20,3,20,800);

        ArrayList<PollutionMesure> a = new ArrayList<>();

        a.add(m);
        a.add(m1);
        a.add(m2);
        a.add(m3);

        PollutionGraph phg = new PollutionGraph(HistoriqueFrag.this,a,PollutionGraph.HOUR);
        phg.draw();

         */
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Date d1 =new Date(120,3,28,11,0);
                //Date d2= new Date(120,3,28,12,0);
                Date d1 = c.getTime();
                Date d2= new Date(d1.getTime()+3600000);
                List<MesurePollution> mesures= db.mesurePollutionDao().getAllByDate(d1,d2);
                final PollutionGraph graph = new PollutionGraph(HistoriqueFrag.this,mesures,PollutionGraph.HOUR);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        graph.draw();
                    }
                });

            }
        });
    }
    public void graphPollutionDay(){
        /*
        PollutionMesure md = new MoyenneDayMesuresPollution(new Date(1586988000000L),50,1,5,400);
        PollutionMesure md1 = new MoyenneDayMesuresPollution(new Date(1587025949000L),10,1,100,600);
        PollutionMesure md2 = new MoyenneDayMesuresPollution(new Date(1587042509000L),30,2,150,500);
        PollutionMesure md3 = new MoyenneDayMesuresPollution(new Date(1587074369000L),20,3,20,800);

        ArrayList<PollutionMesure> ad = new ArrayList<>();

        ad.add(md);
        ad.add(md1);
        ad.add(md2);
        ad.add(md3);
        PollutionGraph pdg = new PollutionGraph(HistoriqueFrag.this,ad,PollutionGraph.DAY);
        pdg.draw();

         */
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Date d1 =new Date(120,3,28,0,0);
                Date d2= new Date(120,3,29,0,0);
                List<MoyenneDayMesuresPollution> mesures= db.moyenneDayMesuresPollutionDao().getAllByDate(d1,d2);
                final PollutionGraph graph = new PollutionGraph(HistoriqueFrag.this,mesures,PollutionGraph.DAY);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        graph.draw();
                    }
                });

            }
        });

    }
    public void graphPollutionYear(){
        /*
        PollutionMesure my = new MoyenneYearMesuresPollution(new Date(1577833200000L),50,1,5,400);
        PollutionMesure my1 = new MoyenneYearMesuresPollution(new Date(1581721200000L),10,1,100,600);
        PollutionMesure my2 = new MoyenneYearMesuresPollution(new Date(1592431200000L),30,2,150,500);
        PollutionMesure my3 = new MoyenneYearMesuresPollution(new Date(1609369200000L),20,3,20,800);

        ArrayList<PollutionMesure> ay = new ArrayList<>();

        ay.add(my);
        ay.add(my1);
        ay.add(my2);
        ay.add(my3);

        PollutionGraph pyg = new PollutionGraph(HistoriqueFrag.this,ay,PollutionGraph.YEAR,true);
        pyg.draw();

         */
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Date d1 =new Date(120,0,1,0,0);
                Date d2= new Date(121,0,1,0,0);
                List<MoyenneYearMesuresPollution> mesures= db.moyenneYearMesuresPollutionDao().getAllByDate(d1,d2);
                final PollutionGraph graph = new PollutionGraph(HistoriqueFrag.this,mesures,PollutionGraph.YEAR,true);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        graph.draw();
                    }
                });

            }
        });

    }
    public void graphMeteoHour(){
        /*
        MeteoMesure m = new MesureMeteo(new Date(1586998800000L),20.2,35);
        MeteoMesure m1 = new MesureMeteo(new Date(1586999703000L),21.9,32.3);
        MeteoMesure m2 = new MesureMeteo(new Date(1587000723785L),31.8,8.2);
        MeteoMesure m3 = new MesureMeteo(new Date(1587001599999L),17.4,76.1);

        ArrayList<MeteoMesure> a = new ArrayList<>();

        a.add(m);
        a.add(m1);
        a.add(m2);
        a.add(m3);

        MeteoGraph phg = new MeteoGraph(HistoriqueFrag.this,a,MeteoGraph.HOUR);
        phg.draw();

         */
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Date d1 =new Date(120,3,28,11,0);
                Date d2= new Date(120,3,28,12,0);
                List<MesureMeteo> mesures= db.mesureMeteoDao().getAllByDate(d1,d2);
                final MeteoGraph graph = new MeteoGraph(HistoriqueFrag.this,mesures,MeteoGraph.HOUR);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        graph.draw();
                    }
                });

            }
        });
    }
    public void graphMeteoDay(){
        /*
        MeteoMesure md = new MoyenneDayMesuresMeteo(new Date(1586988000000L),20.2,35);
        MeteoMesure md1 = new MoyenneDayMesuresMeteo(new Date(1587025949000L),21.9,32.3);
        MeteoMesure md2 = new MoyenneDayMesuresMeteo(new Date(1587042509000L),31.8,8.2);
        MeteoMesure md3 = new MoyenneDayMesuresMeteo(new Date(1587074369000L),17.4,76.1);

        ArrayList<MeteoMesure> ad = new ArrayList<>();

        ad.add(md);
        ad.add(md1);
        ad.add(md2);
        ad.add(md3);
        MeteoGraph pdg = new MeteoGraph(HistoriqueFrag.this,ad,MeteoGraph.DAY);
        pdg.draw();

         */
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Date d1 =new Date(120,3,28,11,0);
                Date d2= new Date(120,3,28,12,0);
                List<MoyenneDayMesuresMeteo> mesures= db.moyenneDayMesuresMeteoDao().getAllByDate(d1,d2);
                final MeteoGraph graph = new MeteoGraph(HistoriqueFrag.this,mesures,MeteoGraph.DAY);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        graph.draw();
                    }
                });

            }
        });
    }
    public void graphMeteoYear(){
        /*
        MeteoMesure my = new MoyenneYearMesuresMeteo(new Date(1577833200000L),20.2,35);
        MeteoMesure my1 = new MoyenneYearMesuresMeteo(new Date(1581721200000L),21.9,32.3);
        MeteoMesure my2 = new MoyenneYearMesuresMeteo(new Date(1592431200000L),31.8,8.2);
        MeteoMesure my3 = new MoyenneYearMesuresMeteo(new Date(1609369200000L),17.4,76.1);

        ArrayList<MeteoMesure> ay = new ArrayList<>();

        ay.add(my);
        ay.add(my1);
        ay.add(my2);
        ay.add(my3);

        MeteoGraph pyg = new MeteoGraph(HistoriqueFrag.this,ay,MeteoGraph.YEAR,true);
        pyg.draw();

         */
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Date d1 =new Date(120,3,28,11,0);
                Date d2= new Date(120,3,28,12,0);
                List<MoyenneYearMesuresMeteo> mesures= db.moyenneYearMesuresMeteoDao().getAllByDate(d1,d2);
                final MeteoGraph graph = new MeteoGraph(HistoriqueFrag.this,mesures,MeteoGraph.YEAR,true);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        graph.draw();
                    }
                });

            }
        });
    }
}
