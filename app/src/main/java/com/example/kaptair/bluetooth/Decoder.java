package com.example.kaptair.bluetooth;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

public class Decoder {

    public Decoder() {

    }

    public String decode(byte[] trame) {
        String decodedMsg = "";

        for(int i=0;i<trame.length;i+=4){
            // On recupere le float associe a chaque tranche de 4 octets
            decodedMsg += ByteBuffer.wrap(trame, i, 4).getFloat();
            decodedMsg += ",";
        }

        // On enleve la derniere virgule
        decodedMsg = decodedMsg.substring(0,decodedMsg.length()-1);



        return decodedMsg;


    }

}
