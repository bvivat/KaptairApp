package com.example.kaptair.bluetooth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kaptair.R;

import java.util.ArrayList;

/**
 * Created by Benjamin Vivat on 06/23/2020.
 */
public class ListeAdapter extends RecyclerView.Adapter<ListeAdapter.ViewHolder> {

    private ArrayList<Device> devices; // Les devices a afficher dans la liste
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;


    ListeAdapter(Context context, ArrayList<Device> devices) {
        this.mInflater = LayoutInflater.from(context);
        this.devices = devices;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_liste, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.txtNom.setText(device.getNom());
        holder.txtAdresse.setText(device.getAdrMac());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtNom;
        TextView txtAdresse;

        ViewHolder(View itemView) {
            super(itemView);
            txtNom = itemView.findViewById(R.id.txtNom);
            txtAdresse = itemView.findViewById(R.id.txtAdresse);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Device getItem(int id) {
        return devices.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}