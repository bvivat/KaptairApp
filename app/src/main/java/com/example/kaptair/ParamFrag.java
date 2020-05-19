package com.example.kaptair;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaptair.bluetooth.OnConnectionResultListener;
import com.example.kaptair.database.AppDatabase;
import com.example.kaptair.database.MesureMeteo;
import com.example.kaptair.database.MesurePollution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParamFrag extends PreferenceFragmentCompat implements SimpleDialogCallback {

    private AppDatabase db;
    private Executor executor = Executors.newSingleThreadExecutor();

    Preference export;
    Preference delete;
    Preference modifNomCapteur;
    Preference changerCapteur;
    private final static String TAG = "Param";

    public ParamFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_param, rootKey);

        db = AppDatabase.getInstance(getContext());

        export = findPreference("export");
        delete = findPreference("delete");
        modifNomCapteur = findPreference("modifNom"); //TODO Faire la modification du nom
        changerCapteur = findPreference("changerCapteur");

        // On initialise les noms de preferences dynamiques
        initDynamicParams();

        final Intent exportIntent = new Intent();
        exportIntent.setAction(Intent.ACTION_SEND);

        // Listener du parametre Export
        Preference.OnPreferenceClickListener listenerExport = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Export de la BD sous forme de CSV \\
                        FileOutputStream output;
                        String exportName = "mesures.csv";
                        String exportMsg = "SEP=,\n";

                        List<MesurePollution> mesuresPollution = db.mesurePollutionDao().getAll();
                        exportMsg += "\n";
                        exportMsg += "Date,";
                        exportMsg += "PM1,";
                        exportMsg += "PM2.5,";
                        exportMsg += "PM10,";
                        exportMsg += "CO2\n";

                        for (MesurePollution m : mesuresPollution) {
                            exportMsg += m.date + ",";
                            exportMsg += m.pm1 + ",";
                            exportMsg += m.pm25 + ",";
                            exportMsg += m.pm10 + ",";
                            exportMsg += m.co2 + "\n";
                        }

                        List<MesureMeteo> mesuresMeteo = db.mesureMeteoDao().getAll();
                        exportMsg += "\n";
                        exportMsg += "Date,";
                        exportMsg += getString(R.string.temperature) + ",";
                        exportMsg += getString(R.string.humidite);
                        exportMsg += "\n";

                        for (MesureMeteo m : mesuresMeteo) {
                            exportMsg += m.date + ",";
                            exportMsg += m.temperature + ",";
                            exportMsg += m.humidity + "\n";
                        }
                        try {
                            output = getContext().openFileOutput(exportName, Context.MODE_PRIVATE);
                            // Ces 3 premiers octets permettent d'indiquer qu'il s'agit d'UTF-8
                            output.write(239);
                            output.write(187);
                            output.write(191);

                            output.write(exportMsg.getBytes());
                            output.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Uri uri = FileProvider.getUriForFile(getContext(), "com.example.kaptair", new File(getContext().getFilesDir(), exportName));
                        exportIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        exportIntent.setType("application/csv");

                        // On affiche le dialog d'exportation
                        startActivity(Intent.createChooser(exportIntent, "Sauvegarder"));
                    }
                });

                return true;
            }
        };

        // Listener du parametre Supprimer
        Preference.OnPreferenceClickListener listenerDelete = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Dialog de confirmation
                DialogFragment dialog = new SimpleDialog();

                Bundle args = new Bundle();
                args.putString(SimpleDialog.ARG_TITLE, getString(R.string.preferencesDeleteDialogTitle));
                args.putString(SimpleDialog.ARG_MESSAGE, getString(R.string.preferencesDeleteDialogBody));
                args.putInt(SimpleDialog.ARG_ICON, R.drawable.ic_delete);
                args.putInt(SimpleDialog.ARG_TYPE, SimpleDialog.TYPE_YES_NO);
                dialog.setArguments(args);

                dialog.show(getChildFragmentManager(), "Delete Dialog");

                return true;
            }
        };
        export.setOnPreferenceClickListener(listenerExport);
        delete.setOnPreferenceClickListener(listenerDelete);


        // Listener du thread Connect
        final OnConnectionResultListener connectionResultListener = new OnConnectionResultListener() {
            @Override
            public void onConnectionResult() {
                initDynamicParams();
            }
        };

        // Listener du parametre ChangerCapteur
        Preference.OnPreferenceClickListener listenerChangerCapteur = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                //On affiche les devices bluetooth
                ((MainActivity) getActivity()).checkLocationPermission();

                //On rafraichit le fragment param
                ((MainActivity) getActivity()).getBluetooth().setListener(connectionResultListener);
                return true;
            }
        };

        changerCapteur.setOnPreferenceClickListener(listenerChangerCapteur);

        try {
            ((MainActivity) getActivity()).getBluetooth().setListener(connectionResultListener);
            ((MainActivity) getActivity()).getBluetooth().getConnect().setListener(connectionResultListener);
        } catch (NullPointerException e) {
            Log.d(TAG, "Connect thread or bluetooth null");
        }

    }

    private void initDynamicParams() {
        try {
            // Si l'appareil est connecte a un capteur
            if (((MainActivity) getActivity()).getBluetooth().getConnect().isConnected()) {

                modifNomCapteur.setEnabled(true);
                modifNomCapteur.setSummary(getString(R.string.preferencesModifNomCapteurSum, ((MainActivity) getActivity()).getBluetooth().getConnect().getDeviceName()));

                changerCapteur.setSummary(getString(R.string.preferencesChangerCapteurSum, ((MainActivity) getActivity()).getBluetooth().getConnect().getDeviceName()));

            } else {
                modifNomCapteur.setSummary(R.string.preferencesNotConnected);
                modifNomCapteur.setEnabled(false);

                changerCapteur.setSummary(R.string.preferencesNotConnected);
            }
        } catch (NullPointerException e) {
            // La connection bluetooth n'a pas ete initialisee
            modifNomCapteur.setSummary(R.string.preferencesNotConnected);
            modifNomCapteur.setEnabled(false);

            changerCapteur.setSummary(R.string.preferencesNotConnected);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView txtTitre = getActivity().findViewById(R.id.txtTitre);
        txtTitre.setText(R.string.param);
    }

    @Override
    public void positiveBtnClicked() {
        db.close();
        // On supprime la DB
        getContext().deleteDatabase(AppDatabase.DB_NAME);
    }

    @Override
    public void negativeBtnClicked() {


    }
}
