package com.example.kaptair.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import com.example.kaptair.MainActivity;
import com.google.android.gms.common.util.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Benjamin Vivat on 06/23/2020.
 */
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

/**
 * Created by Benjamin Vivat on 06/23/2020.
 */
public class TransfertThread extends Thread {

    private static final String SYNC_START_ID = "SYN";
    private static final int HEADER_SIZE = 21;

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

        // Buffer pour conserver le message entier si le début n'est pas passe en 1 morceau
        byte[] savedMsg = new byte[1024];
        ByteBuffer bufferSaved = ByteBuffer.wrap(savedMsg);
        bufferSaved.position(0);

        int numBytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
                bufferSaved.put(mmBuffer, 0, numBytes);

                String msg = new String(savedMsg, 0, bufferSaved.position());
                Log.i(TAG, "Message : " + msg);

                // On regarde le type
                String type = "";
                if (bufferSaved.position() >= 3) {
                    type = msg.substring(0, 3);
                } else {
                    continue;
                }

                if (type.equals(SYNC_START_ID)) {

                    Log.i(TAG, "Type Synchro ");

                    // Si le header de la trame n'est pas complet
                    if (bufferSaved.position() < HEADER_SIZE) {
                        continue;
                    }

                    // On regarde l'ID de la trame
                    id = msg.substring(3, 5);

                    // On recupere le nombre de trames attendues
                    int nbTramesMax = ByteBuffer.wrap(savedMsg, 5, 4).getInt();
                    Log.i(TAG, "nbTramesMax :" + nbTramesMax);

                    long date = ByteBuffer.wrap(savedMsg, 9, 8).getLong();
                    Log.i(TAG, "date :" + date);

                    // Frequence des mesures en seconde
                    int frequence = ByteBuffer.wrap(savedMsg, 17, 4).getInt();
                    Log.i(TAG, "frequence :" + frequence);

                    // On prepare le buffer pour lire a partir de la fin du header
                    bufferSaved.limit(bufferSaved.position());
                    bufferSaved.position(HEADER_SIZE);

                    Decoder decoder = new Decoder();

                    int sizeTrame = 0;

                    if (id.equals("PM")) {
                        Log.i(TAG, "Type PM ");
                        sizeTrame = 16;
                    } else if (id.equals("AT")) {
                        Log.i(TAG, "Type AT ");
                        sizeTrame = 8;
                    }

                    // La trame individuelle a envoyer
                    byte[] trame = new byte[sizeTrame];
                    ByteBuffer trameBuffer = ByteBuffer.wrap(trame);
                    trameBuffer.limit(sizeTrame);
                    trameBuffer.position(0);


                    // On initialise le nombre de trames lues
                    int nbTramesLues = 0;

                    while (nbTramesLues < nbTramesMax) {

                        // Tant qu'on a pas atteint le bout du message recu et qu'on souhaite lire plus de trames
                        while (bufferSaved.position() != bufferSaved.limit() && nbTramesLues < nbTramesMax) {

                            // Tant qu'on a pas rempli la trame ou atteint le bout du message recu
                            while (trameBuffer.position() != trameBuffer.limit() && bufferSaved.position() != bufferSaved.limit()) {
                                trameBuffer.put(bufferSaved.get());
                            }
                            if (trameBuffer.position() == trameBuffer.limit()) {
                                // On cree une trame et l'envoie au handler
                                String decodedMsg = ""+ date +",";
                                decodedMsg += decoder.decode(trame);
                                send(decodedMsg);

                                date+=frequence*1000;
                                nbTramesLues++;
                                trameBuffer.position(0);
                            }

                        }

                        // Si il reste des trames a lire
                        if(nbTramesLues<nbTramesMax){
                            Arrays.fill(savedMsg, (byte) 0);

                            // On lit la suite du message
                            numBytes = mmInStream.read(savedMsg);
                            bufferSaved.position(0);
                            bufferSaved.limit(numBytes);
                        }


                    }

                } else {
                    Log.i(TAG, "Type Instantane ");

                    id = msg.substring(0, 2);
                    Log.i(TAG, "id recu : " + id);
                    send(msg);
                }


                // On reinitialise le buffer
                Arrays.fill(savedMsg, (byte) 0);
                bufferSaved.clear();

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

    private void send(String msg) {
        // Send the obtained bytes to the UI activity.
        Message readMsg = MainActivity.handlerUI.obtainMessage(
                getOrDef(TypeMessage.type, id), -1, -1,
                msg);
        Log.d(TAG,msg);
        readMsg.sendToTarget();
    }
}
