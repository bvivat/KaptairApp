package com.example.kaptair.bluetooth;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.kaptair.MainActivity;
import com.example.kaptair.R;
import com.example.kaptair.database.AppDatabase;
import com.example.kaptair.database.MesureMeteo;
import com.example.kaptair.database.MesurePollution;
import com.example.kaptair.database.MoyenneDayMesuresMeteo;
import com.example.kaptair.database.MoyenneDayMesuresPollution;
import com.example.kaptair.database.MoyenneYearMesuresMeteo;
import com.example.kaptair.database.MoyenneYearMesuresPollution;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//Classe s'occupant d'exploiter les retours du thread de transfert bluetooth
public class HandlerUITransfert extends Handler {

    private static final String TAG = "HandlerUI";
    WeakReference<AppCompatActivity> act;
    private AppDatabase db;
    private Executor executor = Executors.newSingleThreadExecutor();

    public static int NB_CHAMPS_PARTICULES = 5;
    public static int NB_CHAMPS_ATMOSPHERE = 3;
    public static int NB_CHAMPS_LOCALISATION = 4;

    public HandlerUITransfert(AppCompatActivity act) {
        this.act = new WeakReference<>(act);
        db = AppDatabase.getInstance(act);
    }

    public HandlerUITransfert(@Nullable Callback callback) {
        super(callback);
    }

    public HandlerUITransfert(@NonNull Looper looper) {
        super(looper);
    }

    public HandlerUITransfert(@NonNull Looper looper, @Nullable Callback callback) {
        super(looper, callback);
    }

    @Override
    public void handleMessage(Message msg) {
        // Initialisation du contexte des mesures
        Date date = new Date();
        Location location = MainActivity.getTracker().getLastLocation();

        // Si la location est null ou que la date est vieille de plus de 1 min,
        // on remplace par une nouvelle location ( latitude et longitude = 0)
        if (location == null || ((Calendar.getInstance().getTimeInMillis() - location.getTime()) > (60 * 1000))) {
            location = new Location("none");
        }

        //On recupere la trame et verifie son format
        String trame = (String) msg.obj;
        String[] valeurs = trame.split(",");

        //TODO recuperer ici date et gps quand transmis par le capteur
        if(isNumeric(valeurs[0])){
            // Il s'agit d'un message synchro
            //date= new Date(Long.valueOf(valeurs[0]));
            //location.setLatitude(Double.valueOf(valeurs[valeurs.length-2]));
            //location.setLongitude(Double.valueOf(valeurs[valeurs.length-1]));

        }

        for (int i = 1; i < valeurs.length; i++) {
            if (!isNumeric(valeurs[i])) {
                msg.what = -1;
            }
        }

        switch (msg.what) {
            case TypeMessage.PARTICULES:
                if (valeurs.length == NB_CHAMPS_PARTICULES) {
                    //On recupere les valeurs des champs
                    double pm1 = Double.valueOf(valeurs[1]);
                    double pm25 = Double.valueOf(valeurs[2]);
                    double pm10 = Double.valueOf(valeurs[3]);
                    double co2 = Double.valueOf(valeurs[4]);

                    // On affecte ces valeurs a l'interface
                    try {
                        setPM1(pm1);
                        setPM25(pm25);
                        setPM10(pm10);
                        setCO2(co2);
                    } catch (NullPointerException e) {
                        Log.e(TAG, "Impossible de modifier les compteurs");
                    }


                    // On cree une nouvelle mesure a partir de ces donnees
                    MesurePollution m = new MesurePollution(date, pm1, pm25, pm10, co2, location.getLatitude(), location.getLongitude());

                    // On l'ajoute a la base de donnees
                    insertBD(m);
                }
                break;
            case TypeMessage.ATMOSPHERE:
                if (valeurs.length == NB_CHAMPS_ATMOSPHERE) {
                    double temperature = Double.valueOf(valeurs[1]);
                    double humidity = Double.valueOf(valeurs[2]);
                    //double pression = Double.valueOf(valeurs[3]);

                    try {
                        setTemperature(temperature);
                        setHumidite(humidity);
                        //setPression(pression);
                    } catch (NullPointerException e) {
                        Log.e(TAG, "Impossible de modifier les compteurs");
                    }

                    MesureMeteo m = new MesureMeteo(date, temperature, humidity, location.getLatitude(), location.getLongitude());
                    insertBD(m);
                }
                break;
            case TypeMessage.LOCALISATION:
                if (valeurs.length == NB_CHAMPS_LOCALISATION) {
                    double latitude = Double.valueOf(valeurs[1]);
                    double longitude = Double.valueOf(valeurs[2]);
                    double altitude = Double.valueOf(valeurs[3]);


                    //setLatitude(latitude);
                    //setLongitude(longitude);
                    //setAltitude(altitude);
                }
                break;
            default:
                Log.e(TAG, "Trame invalide : ignorée");

        }

    }

    private void setPM1(Double val) {
        TextView txt = act.get().findViewById(R.id.txtPm1Val);
        txt.setText(String.valueOf(val));

        if (val >= TypeDangerDonnees.PM1_DANGER) {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorDanger));
        } else if (val >= TypeDangerDonnees.PM1_WARNING) {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorWarning));
        } else {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorSafe));
        }
    }

    private void setPM25(Double val) {
        TextView txt = act.get().findViewById(R.id.txtPm2_5Val);
        txt.setText(String.valueOf(val));

        if (val >= TypeDangerDonnees.PM25_DANGER) {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorDanger));
        } else if (val >= TypeDangerDonnees.PM25_WARNING) {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorWarning));
        } else {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorSafe));
        }
    }

    private void setPM10(Double val) {
        TextView txt = act.get().findViewById(R.id.txtPm10Val);
        txt.setText(String.valueOf(val));

        if (val >= TypeDangerDonnees.PM10_DANGER) {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorDanger));
        } else if (val >= TypeDangerDonnees.PM10_WARNING) {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorWarning));
        } else {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorSafe));
        }
    }

    private void setCO2(Double val) {
        TextView txt = act.get().findViewById(R.id.txtCo2Val);
        txt.setText(String.valueOf(val));

        if (val >= TypeDangerDonnees.CO2_DANGER) {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorDanger));
        } else if (val >= TypeDangerDonnees.CO2_WARNING) {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorWarning));
        } else {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorSafe));
        }
    }

    private void setTemperature(Double val) {
        TextView txt = act.get().findViewById(R.id.txtTemperatureVal);
        txt.setText(String.valueOf(val));

        if (val >= TypeDangerDonnees.TEMP_DANGER) {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorDanger));
        } else if (val >= TypeDangerDonnees.TEMP_WARNING) {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorWarning));
        } else {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorSafe));
        }
    }

    private void setHumidite(Double val) {
        TextView txt = act.get().findViewById(R.id.txtHumiditeVal);
        txt.setText(String.valueOf(val));

        if (val >= TypeDangerDonnees.HUMIDITY_DANGER) {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorDanger));
        } else if (val >= TypeDangerDonnees.HUMIDITY_WARNING) {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorWarning));
        } else {
            txt.setTextColor(ContextCompat.getColor(act.get(), R.color.colorSafe));
        }
    }

    private void insertBD(final MesurePollution mesure) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // On ajoute la mesure exacte
                db.mesurePollutionDao().insertAll(mesure);


                // On met a jour la moyenne 5 minutes
                MoyenneDayMesuresPollution mDay = new MoyenneDayMesuresPollution(mesure);
                MoyenneDayMesuresPollution moyenneJour = db.moyenneDayMesuresPollutionDao().getByDate(mDay.date);

                if (moyenneJour == null) {
                    //Si aucune mesures pour ces 5min, on insère
                    db.moyenneDayMesuresPollutionDao().insertAll(mDay);
                } else {
                    //Sinon, on met à jour avec la moyenne anciennes + nouvelles données
                    MoyenneDayMesuresPollution newMDate = new MoyenneDayMesuresPollution(moyenneJour, mDay);
                    db.moyenneDayMesuresPollutionDao().update(newMDate);
                }


                //On met a jour la moyenne jour
                MoyenneYearMesuresPollution mYear = new MoyenneYearMesuresPollution(mesure);
                MoyenneYearMesuresPollution moyenneAnnee = db.moyenneYearMesuresPollutionDao().getByDate(mYear.date);

                if (moyenneAnnee == null) {
                    //Si aucune mesures pour cette année, on insère
                    db.moyenneYearMesuresPollutionDao().insertAll(mYear);
                } else {
                    //Sinon, on met à jour avec la moyenne anciennes + nouvelles données
                    MoyenneYearMesuresPollution newMDate = new MoyenneYearMesuresPollution(moyenneAnnee, mYear);
                    db.moyenneYearMesuresPollutionDao().update(newMDate);
                }

            }
        });
    }

    private void insertBD(final MesureMeteo mesure) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.mesureMeteoDao().insertAll(mesure);
                MoyenneDayMesuresMeteo mDay = new MoyenneDayMesuresMeteo(mesure);
                MoyenneDayMesuresMeteo moyenneJour = db.moyenneDayMesuresMeteoDao().getByDate(mDay.date);

                if (moyenneJour == null) {
                    //Si aucune mesures pour ces 5min, on insère
                    db.moyenneDayMesuresMeteoDao().insertAll(mDay);
                } else {
                    //Sinon, on met à jour avec la moyenne anciennes + nouvelles données
                    MoyenneDayMesuresMeteo newMDate = new MoyenneDayMesuresMeteo(moyenneJour, mDay);
                    db.moyenneDayMesuresMeteoDao().update(newMDate);
                }

                MoyenneYearMesuresMeteo mYear = new MoyenneYearMesuresMeteo(mesure);
                MoyenneYearMesuresMeteo moyenneAnnee = db.moyenneYearMesuresMeteoDao().getByDate(mYear.date);

                if (moyenneAnnee == null) {
                    //Si aucune mesures pour cette année, on insère
                    db.moyenneYearMesuresMeteoDao().insertAll(mYear);
                } else {
                    //Sinon, on met à jour avec la moyenne anciennes + nouvelles données
                    MoyenneYearMesuresMeteo newMDate = new MoyenneYearMesuresMeteo(moyenneAnnee, mYear);
                    db.moyenneYearMesuresMeteoDao().update(newMDate);
                }
            }
        });
    }

    public void setAct(WeakReference<AppCompatActivity> act) {
        this.act = act;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
