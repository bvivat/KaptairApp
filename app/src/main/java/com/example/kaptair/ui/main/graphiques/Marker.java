package com.example.kaptair.ui.main.graphiques;

import android.content.Context;

import com.github.mikephil.charting.components.MarkerImage;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class Marker extends MarkerView {

    //TODO Utiliser ou supprimer classe + xml associé + drawable associé

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public Marker(Context context, int layoutResource) {
        super(context, layoutResource);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        super.refreshContent(e, highlight);
    }
}
