package com.example.kaptair.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import com.example.kaptair.MainActivity;
import com.google.android.gms.common.util.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

interface TypeMessage {
    int PARTICULES = 0;
    int ATMOSPHERE = 1;
    int LOCALISATION = 2;
    HashMap<String, Integer> type = new HashMap<String, Integer>() {{
        put("PM", PARTICULES);
        put("AT", ATMOSPHERE);
        put("GP", LOCALISATION);

    }};

}

public class TransfertThread extends Thread {

    private static final String SYNC_START_ID = "SYN";

    private OnConnectionChangeListener listener;

    Integer getOrDef(HashMap<String, Integer> map, String key) {
        if (map.get(key) != null) {
            return map.get(key);
        }
        return -1;
    }

    private static final String TAG = "TransfertThread";

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream
    String id = "";

    public TransfertThread(BluetoothSocket socket) {
        mmSocket = socket;

        //On initialise les streams
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);

                String msg = new String(mmBuffer, 0, numBytes);
                Log.i(TAG, "Message recu : " + msg);

                // On regarde le type
                String type = msg.substring(0, 3);

                if (type.equals(SYNC_START_ID)) {
                    // Si il s'agit d'une synchro,
                    Log.i(TAG, "Type Synchro ");

                    // On regarde l'ID de la trame
                    id = msg.substring(3, 5);

                    // On recupere le nombre de trames attendues
                    int nbTramesMax = ByteBuffer.wrap(mmBuffer, 5, 4).getInt();
                    Log.i(TAG, "nbTramesMax :" + nbTramesMax);

                    // Buffer directement lie au tableau de bytes
                    ByteBuffer buffer = ByteBuffer.wrap(mmBuffer);
                    buffer.limit(numBytes);
                    buffer.position(9);

                    Decoder decoder = new Decoder();

                    int sizeTrame = 16;


                    if (id.equals("PM")) {
                        Log.i(TAG, "Type PM ");
                        // TODO Changer size trame
                    } else if (id.equals("AT")) {
                        Log.i(TAG, "Type AT ");
                    }

                    // La trame individuelle a envoyer
                    byte[] trame = new byte[sizeTrame];
                    ByteBuffer trameBuffer = ByteBuffer.wrap(trame);
                    trameBuffer.limit(sizeTrame);
                    trameBuffer.position(0);


                    // On initialise le nombre de trames lues
                    int nbTramesLues = 0;

                    while (nbTramesLues < nbTramesMax) {

                        // Tant qu'on a pas atteint le bout du message recu
                        while (buffer.position()!= buffer.limit()) {

                            // Tant qu'on a pas rempli la trame ou atteint le bout du message recu
                            while ( trameBuffer.position() != trameBuffer.limit() && buffer.position()!= buffer.limit() ){
                                trameBuffer.put(buffer.get());
                            }
                            if (trameBuffer.position() == trameBuffer.limit()) {
                                String decodedMsg = decoder.decode(trame);
                                send(decodedMsg);
                                nbTramesLues++;
                                trameBuffer.position(0);
                            }

                        }
                        numBytes = mmInStream.read(mmBuffer);
                        buffer.position(0);
                        buffer.limit(numBytes);

                    }

                } else {
                    Log.i(TAG, "Type Instantane ");

                    if (numBytes >= 2) {
                        id = msg.substring(0, 2);
                        Log.i(TAG, "id recu : " + id);
                    }

                    send(msg);
                }

            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                if (listener != null) {
                    listener.onConnectionResult();
                }
                break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);

        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);

        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

    public void setListener(OnConnectionChangeListener listener) {
        this.listener = listener;
    }

    private void updateLinkedList(LinkedList<Byte> list, byte[] tab, int offset) {
        for (int i=offset;i<tab.length && tab[i]!= '\0';i++) {
            list.add(tab[i]);
        }
    }

    private void send(String msg) {
        // Send the obtained bytes to the UI activity.
        Message readMsg = MainActivity.handlerUI.obtainMessage(
                getOrDef(TypeMessage.type, id), -1, -1,
                msg);
        readMsg.sendToTarget();
    }
}
