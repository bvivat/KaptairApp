package com.example.kaptair.bluetooth;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

public class Decoder {

    public Decoder() {

    }

    public String decode(byte[] trame) {
        String decodedMsg = "";

        decodedMsg += ByteBuffer.wrap(trame, 0, 4).getFloat();
        decodedMsg += ByteBuffer.wrap(trame, 4, 4).getFloat();
        decodedMsg += ByteBuffer.wrap(trame, 8, 4).getFloat();
        decodedMsg += ByteBuffer.wrap(trame, 12, 4).getFloat();



        return decodedMsg;


    }

}
