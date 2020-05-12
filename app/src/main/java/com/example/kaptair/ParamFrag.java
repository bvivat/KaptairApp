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
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
public class ParamFrag extends PreferenceFragmentCompat {

    private AppDatabase db;
    private Executor executor = Executors.newSingleThreadExecutor();

    public ParamFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_param, rootKey);



        db=AppDatabase.getInstance(getContext());

        Preference export = findPreference("export");
        Preference delete = findPreference("delete");

        final Intent exportIntent = new Intent();
        exportIntent.setAction(Intent.ACTION_SEND);



        Preference.OnPreferenceClickListener listenerExport = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream output = null;
                        String exportName = "mesures.csv";
                        String exportMsg = "SEP=,\n";

                        List<MesurePollution> mesuresPollution = db.mesurePollutionDao().getAll();
                        exportMsg+="\n";
                        exportMsg+="Date,";
                        exportMsg+="PM1,";
                        exportMsg+="PM2.5,";
                        exportMsg+="PM10,";
                        exportMsg+="CO2\n";

                        for (MesurePollution m : mesuresPollution ){
                            exportMsg+=m.date+",";
                            exportMsg+=m.pm1+",";
                            exportMsg+=m.pm25+",";
                            exportMsg+=m.pm10+",";
                            exportMsg+=m.co2+"\n";
                        }

                        List<MesureMeteo> mesuresMeteo = db.mesureMeteoDao().getAll();
                        exportMsg+="\n";
                        exportMsg+="Date,";
                        exportMsg+=getString(R.string.temperature)+",";
                        exportMsg+=getString(R.string.humidite);
                        exportMsg+="\n";

                        for (MesureMeteo m : mesuresMeteo ){
                            exportMsg+=m.date+",";
                            exportMsg+=m.temperature+",";
                            exportMsg+=m.humidity+"\n";
                        }
                        try {
                            output = getContext().openFileOutput(exportName, Context.MODE_PRIVATE);
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

                        Uri uri = FileProvider.getUriForFile(getContext(),"com.example.kaptair",new File( getContext().getFilesDir(), exportName));
                        exportIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        exportIntent.setType("application/csv");
                        startActivity(Intent.createChooser(exportIntent,"Sauvegarder"));
                    }
                });

                return true;
            }
        };
        Preference.OnPreferenceClickListener listenerDelete = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                executor.execute(new Runnable() {
                    @Override
                    public void run() {

                        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        db.close();
                                        getContext().deleteDatabase(AppDatabase.DB_NAME);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(),R.style.ConfirmDialog));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                builder.setMessage(R.string.preferencesDeleteDialogBody)
                                        .setPositiveButton(android.R.string.yes, dialogClickListener)
                                        .setNegativeButton(android.R.string.cancel, dialogClickListener)
                                        .setTitle(R.string.preferencesDeleteDialogTitle)
                                        .setIcon(R.drawable.ic_delete)
                                        .show();
                            }
                        });

                    }
                });

                return true;
            }
        };
        export.setOnPreferenceClickListener(listenerExport);
        delete.setOnPreferenceClickListener(listenerDelete);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView txtTitre = getActivity().findViewById(R.id.txtTitre);
        txtTitre.setText(R.string.param);
    }
}
