package com.example.kaptair;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaptair.database.AppDatabase;
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

        final Intent exportIntent = new Intent();
        exportIntent.setAction(Intent.ACTION_SEND);



        Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {



                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream output = null;
                        String exportName = "csvTest.csv";
                        String exportMsg = "SEP=,\n";

                        List<MesurePollution> mesuresPollution = db.mesurePollutionDao().getAll();
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
                        try {
                            output = getContext().openFileOutput(exportName, Context.MODE_PRIVATE);
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
        export.setOnPreferenceClickListener(listener);
    }


}
