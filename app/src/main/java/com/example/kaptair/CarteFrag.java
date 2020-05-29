package com.example.kaptair;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.kaptair.database.AppDatabase;
import com.example.kaptair.database.InterfacesMesures.Mesure;
import com.example.kaptair.database.InterfacesMesures.MesureDao;
import com.example.kaptair.ui.main.YearPickerDialog;
import com.example.kaptair.ui.main.graphiques.CardGraph;
import com.example.kaptair.ui.main.graphiques.Graph;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

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
public class CarteFrag extends Fragment {

    private static final String SAVE_ZOOM = "zoom";
    private static final String SAVE_LAT = "latitude";
    private static final String SAVE_LONG = "longitude";
    private MapView map;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MyLocationNewOverlay mLocationOverlay;

    // Non map related attributs
    private AppDatabase db;
    private Executor executor = Executors.newSingleThreadExecutor();
    Calendar calendrier = Calendar.getInstance();

    MesureDao hourDao;
    MesureDao dayDao;
    MesureDao yearDao;

    ArrayList<Button> btns = new ArrayList<>();
    TextView titre;
    Button btnHour;
    Button btnDay;
    Button btnYear;

    public CarteFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_carte, container, false);

        Context ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        TextView txtTitre = getActivity().findViewById(R.id.txtTitre);
        txtTitre.setText(R.string.carte);

        map = (MapView) v.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setMultiTouchControls(true);

        // Icone position
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()), map);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.setPersonIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_person));
        map.getOverlays().add(mLocationOverlay);

        IMapController mapController = map.getController();
        GeoPoint startPoint;

        if (savedInstanceState == null) {
            // On initialise arbitrairement la position et le niveau de zoom
            mapController.setZoom(10.0);
            startPoint = new GeoPoint(48.8534, 2.3488); //TODO Utiliser lastLocation

        } else {
            // On restore la position et le niveau de zoom sauvegarde
            mapController.setZoom(savedInstanceState.getDouble(SAVE_ZOOM));
            startPoint = new GeoPoint(savedInstanceState.getDouble(SAVE_LAT), savedInstanceState.getDouble(SAVE_LONG));
        }
        mapController.setCenter(startPoint);

        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        // Marqueurs
        ArrayList<Marker> marqueurs = new ArrayList<Marker>();

        Marker m0 = new Marker(map);
        m0.setPosition(new GeoPoint(48.599944d, 2.178222d));
        m0.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        m0.setTitle("20 mai 2020");
        m0.setSnippet("PM1 : 10 | PM2.5 : 20 | PM10 : 30 | CO2 : 40");
        m0.setIcon(getResources().getDrawable(R.drawable.ic_marker));
        m0.setInfoWindow(new MarkerInfoWindow(R.layout.marker_info, map));
        marqueurs.add(m0);


        // On affiche les marqueurs
        for (Marker m : marqueurs) {
            map.getOverlays().add(m);
        }


        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ConstraintLayout toolbar = getActivity().findViewById(R.id.appBarLayout);
        toolbar.setVisibility(View.GONE);

        db = AppDatabase.getInstance(getContext());

        calendrier.set(Calendar.MINUTE, 0);
        calendrier.set(Calendar.SECOND, 0);
        calendrier.set(Calendar.MILLISECOND, 0);

        hourDao =  db.mesurePollutionDao();
        dayDao =  db.moyenneDayMesuresPollutionDao();
        yearDao = db.moyenneYearMesuresPollutionDao();

        titre = getView().findViewById(R.id.txtTitreMap);

        btnHour = getView().findViewById(R.id.btnHourMap);
        btnDay = getView().findViewById(R.id.btnDayMap);
        btnYear = getView().findViewById(R.id.btnYearMap);

        btns.add(btnHour);
        btns.add(btnDay);
        btns.add(btnYear);

        // Date Pickers Listeners \\
        DatePickerDialog.OnDateSetListener dateHeure = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                //On definit le calendrier aux valeurs choisies par l'utilisateur
                calendrier.set(Calendar.YEAR, year);
                calendrier.set(Calendar.MONTH, monthOfYear);
                calendrier.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.MyDatePicker,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendrier.set(Calendar.HOUR_OF_DAY, hourOfDay);

                                // On met a jour le graph et son titre
                                setTitreHour();
                                reperesHour();

                            }
                        }, calendrier.get(Calendar.HOUR_OF_DAY), 0, true);
                timePickerDialog.show();
            }

        };

        DatePickerDialog.OnDateSetListener dateJour = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                //On definit le calendrier aux valeurs choisies par l'utilisateur
                calendrier.set(Calendar.YEAR, year);
                calendrier.set(Calendar.MONTH, monthOfYear);
                calendrier.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendrier.set(Calendar.HOUR_OF_DAY, 0);

                // On met a jour le graph et son titre
                setTitreDay();
                reperesDay();
            }
        };

        final DatePickerDialog.OnDateSetListener dateAnnee = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                //On definit le calendrier aux valeurs choisies par l'utilisateur
                calendrier.set(Calendar.YEAR, year);
                calendrier.set(Calendar.MONTH, 0);
                calendrier.set(Calendar.DAY_OF_MONTH, 1);
                calendrier.set(Calendar.HOUR_OF_DAY, 0);

                // On met a jour le graph et son titre
                setTitreYear();
                reperesYear();
            }
        };

        final DatePickerDialog pickerHeure = new DatePickerDialog(getContext(), R.style.MyDatePicker, dateHeure, calendrier.get(Calendar.YEAR), calendrier.get(Calendar.MONTH), calendrier.get(Calendar.DAY_OF_MONTH));
        final DatePickerDialog pickerJour = new DatePickerDialog(getContext(), R.style.MyDatePicker, dateJour, calendrier.get(Calendar.YEAR), calendrier.get(Calendar.MONTH), calendrier.get(Calendar.DAY_OF_MONTH));


        // Boutons Listener \\
        View.OnClickListener listenerChoixGraphPollution = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button btnTmp = (Button) v;
                if (!btnTmp.isSelected()) {
                    btnTmp.setSelected(true); // On le selectionne
                }
                for (Button b : btns) {
                    if (b.getId() != btnTmp.getId() && b.isSelected()) {
                        b.setSelected(false); // On deselectionne les autres boutons
                    }
                }

                //On affiche le date picker associe
                if (v.getId() == btnHour.getId()) {
                    pickerHeure.show();
                } else if (v.getId() == btnDay.getId()) {
                    pickerJour.show();
                } else if (v.getId() == btnYear.getId()) {
                    YearPickerDialog y = new YearPickerDialog();
                    y.setListener(dateAnnee);
                    y.show(getFragmentManager(), "picker");
                }
            }
        };

        // On affecte le listener aux boutons
        for (Button b : btns) {
            b.setOnClickListener(listenerChoixGraphPollution);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // On sauvegarde la position et le niveau de zoom
        outState.putDouble(SAVE_ZOOM, map.getZoomLevelDouble());
        outState.putDouble(SAVE_LAT, map.getMapCenter().getLatitude());
        outState.putDouble(SAVE_LONG, map.getMapCenter().getLongitude());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        final ConstraintLayout toolbar = getActivity().findViewById(R.id.appBarLayout);
        toolbar.setVisibility(View.VISIBLE);

    }

    public void setTitreHour() {
        SimpleDateFormat sdfHours = new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm");
        titre.setText(sdfHours.format(calendrier.getTime()));
    }

    public void setTitreDay() {
        SimpleDateFormat sdfHours = new SimpleDateFormat("EEEE dd MMMM yyyy");
        titre.setText(sdfHours.format(calendrier.getTime()));
    }

    public void setTitreYear() {
        SimpleDateFormat sdfHours = new SimpleDateFormat("yyyy");
        titre.setText(sdfHours.format(calendrier.getTime()));
    }

    //TODO changer et renommer ces fonctions
    public void reperesHour() {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // On recupere les donnees associees a l'heure choisie dans la BD
                Date d1 = calendrier.getTime();
                Date d2 = new Date(d1.getTime() + Graph.ONE_HOUR);
                List<? extends Mesure> mesures = hourDao.getAllByDate(d1, d2);

                // On construit les reperes

            }
        });
    }

    public void reperesDay() {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // On recupere les donnees associees a l'heure choisie dans la BD
                Date d1 = calendrier.getTime();
                Date d2 = new Date(d1.getTime() + Graph.ONE_DAY);
                List<? extends Mesure> mesures = dayDao.getAllByDate(d1, d2);

                // On construit les reperes


            }
        });

    }

    public void reperesYear() {

        final boolean bissextile;
        if (calendrier.getActualMaximum(Calendar.DAY_OF_YEAR) > 365) {
            bissextile = true;
        } else {
            bissextile = false;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // On recupere les donnees associees a l'heure choisie dans la BD
                Date d1 = calendrier.getTime();
                Date d2 = new Date(d1.getTime() + Graph.ONE_YEAR + (bissextile ? Graph.ONE_DAY : 0));
                List<? extends Mesure> mesures = yearDao.getAllByDate(d1, d2);

                // On construit les reperes

            }
        });

    }
}
