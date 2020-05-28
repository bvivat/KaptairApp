package com.example.kaptair.bluetooth;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

public class Decoder {

    public Decoder() {

    }

    public String decode(byte[] trame) {
        String decodedMsg = "";

        // TODO automatiser en fonction de la taille de trame
        decodedMsg += ByteBuffer.wrap(trame, 0, 4).getFloat();
        decodedMsg += ",";
        decodedMsg += ByteBuffer.wrap(trame, 4, 4).getFloat();
        decodedMsg += ",";
        decodedMsg += ByteBuffer.wrap(trame, 8, 4).getFloat();
        decodedMsg += ",";
        decodedMsg += ByteBuffer.wrap(trame, 12, 4).getFloat();



        return decodedMsg;


    }

}
