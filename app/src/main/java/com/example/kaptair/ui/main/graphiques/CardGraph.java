package com.example.kaptair.ui.main.graphiques;

import android.widget.Button;
import android.widget.TextView;

import com.example.kaptair.database.MesureDao;
import com.example.kaptair.ui.main.HistoriqueFrag;
import com.example.kaptair.ui.main.YearPickerDialog;
import com.github.mikephil.charting.charts.LineChart;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.kaptair.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class CardGraph {

    private final WeakReference<HistoriqueFrag> frag;
    private Executor executor = Executors.newSingleThreadExecutor();

    Calendar calendrier = Calendar.getInstance();
    MesureDao hourDao;
    MesureDao dayDao;
    MesureDao yearDao;
    ArrayList<Button> btns = new ArrayList<>();
    TextView titre;
    LineChart chart;

    Button btnHour;
    Button btnDay;
    Button btnYear;

    public CardGraph(HistoriqueFrag _frag, MesureDao _hourDao, MesureDao _dayDao, MesureDao _yearDao, TextView _titre, LineChart _chart, Button _btnHour, Button _btnDay, Button _btnYear) {
        this.frag =  new WeakReference<HistoriqueFrag>(_frag);
        this.hourDao = _hourDao;
        this.dayDao = _dayDao;
        this.yearDao = _yearDao;
        this.titre = _titre;
        this.chart = _chart;
        this.btnHour = _btnHour;
        this.btnDay=_btnDay;
        this.btnYear=_btnYear;

        calendrier.set(Calendar.MINUTE,0);
        calendrier.set(Calendar.SECOND,0);
        calendrier.set(Calendar.MILLISECOND,0);

        btns.add(btnHour);
        btns.add(btnDay);
        btns.add(btnYear);

        DatePickerDialog.OnDateSetListener dateHeure = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendrier.set(Calendar.YEAR, year);
                calendrier.set(Calendar.MONTH, monthOfYear);
                calendrier.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                TimePickerDialog timePickerDialog = new TimePickerDialog(frag.get().getContext(),R.style.MyDatePicker,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendrier.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                setTitreHour();
                                graphHour();

                            }
                        }, calendrier.get(Calendar.HOUR_OF_DAY), 0, true);
                timePickerDialog.show();
            }

        };

        DatePickerDialog.OnDateSetListener dateJour = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendrier.set(Calendar.YEAR, year);
                calendrier.set(Calendar.MONTH, monthOfYear);
                calendrier.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendrier.set(Calendar.HOUR_OF_DAY,0);

                setTitreDay();

                graphDay();
            }
        };

        final DatePickerDialog.OnDateSetListener dateAnnee = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendrier.set(Calendar.YEAR, year);
                calendrier.set(Calendar.MONTH, 0);
                calendrier.set(Calendar.DAY_OF_MONTH, 1);
                calendrier.set(Calendar.HOUR_OF_DAY,0);

                setTitreYear();

                graphYear();
            }
        };

        final DatePickerDialog pickerHeure = new DatePickerDialog(frag.get().getContext(),R.style.MyDatePicker,dateHeure,calendrier.get(Calendar.YEAR),calendrier.get(Calendar.MONTH),calendrier.get(Calendar.DAY_OF_MONTH));
        final DatePickerDialog pickerJour = new DatePickerDialog(frag.get().getContext(),R.style.MyDatePicker,dateJour,calendrier.get(Calendar.YEAR),calendrier.get(Calendar.MONTH),calendrier.get(Calendar.DAY_OF_MONTH));


        View.OnClickListener listenerChoixGraphPollution = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button btnTmp = (Button) v;
                if(!btnTmp.isSelected()){
                    btnTmp.setSelected(true);
                }
                for (Button b : btns){
                    if(b.getId()!= btnTmp.getId() && b.isSelected()){
                        b.setSelected(false);
                    }
                }
                if (v.getId()==btnHour.getId()){
                    pickerHeure.show();
                }else if(v.getId()==btnDay.getId()){
                    pickerJour.show();
                }else if (v.getId()==btnYear.getId()){
                    YearPickerDialog y = new YearPickerDialog();
                    y.setListener(dateAnnee);
                    y.show(frag.get().getFragmentManager(),"picker");
                }
            }
        };

        for(Button b : btns){
            b.setOnClickListener(listenerChoixGraphPollution);
        }

        btnHour.setSelected(true);
        setTitreHour();
        graphHour();

    }

    public void graphHour(){

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Date d1 = calendrier.getTime();
                Date d2= new Date(d1.getTime()+ Graph.ONE_HOUR);
                List<? extends Mesure> mesures= hourDao.getAllByDate(d1,d2);
                final Graph graph = new Graph(frag.get(),CardGraph.this,mesures, Graph.HOUR, Graph.GRAPH_POLLUTION);
                frag.get().getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        graph.draw();
                    }
                });

            }
        });
    }

    public void graphDay(){

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Date d1 = calendrier.getTime();
                Date d2= new Date(d1.getTime()+ Graph.ONE_DAY);
                List<? extends Mesure> mesures= dayDao.getAllByDate(d1,d2);
                final Graph graph = new Graph(frag.get(),CardGraph.this,mesures, Graph.DAY, Graph.GRAPH_POLLUTION);
                frag.get().getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        graph.draw();
                    }
                });

            }
        });

    }

    public void graphYear(){

        final boolean bissextile;
        if (calendrier.getActualMaximum(Calendar.DAY_OF_YEAR)>365){
            bissextile=true;
        }else {
            bissextile=false;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Date d1 = calendrier.getTime();
                Date d2= new Date(d1.getTime()+ Graph.ONE_YEAR+ (bissextile ? Graph.ONE_DAY : 0));
                List<? extends Mesure> mesures=yearDao.getAllByDate(d1,d2);
                final Graph graph = new Graph(frag.get(),CardGraph.this,mesures, Graph.YEAR,bissextile, Graph.GRAPH_POLLUTION);
                frag.get().getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        graph.draw();
                    }
                });

            }
        });

    }

    public void setTitreHour(){
        SimpleDateFormat sdfHours = new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm");
        titre.setText(sdfHours.format(calendrier.getTime()));
    }

    public void setTitreDay(){
        SimpleDateFormat sdfHours = new SimpleDateFormat("EEEE dd MMMM yyyy");
        titre.setText(sdfHours.format(calendrier.getTime()));
    }

    public void setTitreYear(){
        SimpleDateFormat sdfHours = new SimpleDateFormat("yyyy");
        titre.setText(sdfHours.format(calendrier.getTime()));
    }

    public Calendar getCalendrier() {
        return calendrier;
    }

    public void setCalendrier(Calendar calendrier) {
        this.calendrier = calendrier;
    }

    public ArrayList<Button> getBtns() {
        return btns;
    }

    public void setBtns(ArrayList<Button> btns) {
        this.btns = btns;
    }

    public Button getBtnHour() {
        return btnHour;
    }

    public Button getBtnDay() {
        return btnDay;
    }

    public Button getBtnYear() {
        return btnYear;
    }

    public LineChart getChart() {
        return chart;
    }
}
