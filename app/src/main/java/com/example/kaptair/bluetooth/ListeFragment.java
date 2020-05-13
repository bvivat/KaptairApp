package com.example.kaptair.bluetooth;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaptair.MainActivity;
import com.example.kaptair.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListeFragment extends DialogFragment implements ListeAdapter.ItemClickListener {


    ArrayList<Device> devices;
    ListeAdapter adapter;
    public ListeFragment() {
        // Required empty public constructor
    }


    public static ListeFragment newInstance(ArrayList<Device> liste) {
        ListeFragment fragment = new ListeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("LISTE", liste);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);

    }

    @Override
    public void onStart(){
        super.onStart();
        //this.getDialog().getWindow().setLayout(1000,2000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_liste, container, false);


        devices=getArguments().getParcelableArrayList("LISTE");

        RecyclerView recyclerView = v.findViewById(R.id.rcyclRes);
        LinearLayoutManager lytManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(lytManager);
        adapter = new ListeAdapter(this.getContext(),devices);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                lytManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        this.getDialog().setTitle(R.string.btListeTitre);
        TextView t = this.getDialog().findViewById(android.R.id.title);
        t.setGravity(Gravity.CENTER);

        return v;
    }

    public void addItem(Device device){
        devices.add(device);
        adapter.notifyItemInserted(devices.size() - 1);

    }

    @Override
    public void onItemClick(View view, int position) {
        MainActivity act =  (MainActivity)getActivity();
        BluetoothApp b =  act.getBluetooth();
        b.connecter(adapter.getItem(position).getAdrMac());
        b.unregisterReceiver(true);
        this.dismiss();
    }


    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        MainActivity act =  (MainActivity)getActivity();
        BluetoothApp b =  act.getBluetooth();
        b.unregisterReceiver(true);
    }
}
