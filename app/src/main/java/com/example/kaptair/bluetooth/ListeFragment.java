package com.example.kaptair.bluetooth;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

    public static final String ARG_DEVICES = "devices";

    ArrayList<Device> devices;
    ListeAdapter adapter;

    private DialogInterface.OnDismissListener onDismissListener;

    public ListeFragment() {
        // Required empty public constructor
    }


    public static ListeFragment newInstance(ArrayList<Device> liste) {

        //On cree une liste et on lui passe en argument les devices a afficher
        ListeFragment fragment = new ListeFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_DEVICES, liste);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);

    }

    @Override
    public void onStart() {
        super.onStart();
        //this.getDialog().getWindow().setLayout(1000,2000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_liste, container, false);

        //On recupere les devices a afficher
        devices = getArguments().getParcelableArrayList(ARG_DEVICES);

        //On genere la recyclerView qui va lister les devices
        RecyclerView recyclerView = v.findViewById(R.id.rcyclRes);

        LinearLayoutManager lytManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(lytManager);

        adapter = new ListeAdapter(this.getContext(), devices);
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

    public void addItem(Device device) {
        devices.add(device);
        adapter.notifyItemInserted(devices.size() - 1);

    }

    @Override
    public void onItemClick(View view, int position) {
        MainActivity act = (MainActivity) getActivity();

        BluetoothApp b = act.getBluetooth();
        b.connecter(adapter.getItem(position).getAdrMac()); // On se connecte a l'addresse clilquee
        b.unregisterReceiver(true); // On arrete de discover des nouveaux devices

        this.dismiss();
    }


    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        MainActivity act = (MainActivity) getActivity();

        BluetoothApp b = act.getBluetooth();
        b.unregisterReceiver(true); // On arrete de discover des nouveaux devices
    }



    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }


}
