package com.example.kaptair.bluetooth;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

public class Decoder {

    byte[] tab = new byte[16];

    public Decoder() {

    }

    public String decode(ArrayList<Byte> msg) {
        String decodedMsg = "";
        updateByteTab(msg,tab,0);
        decodedMsg += ByteBuffer.wrap(tab, 0, 4).getFloat();
        decodedMsg += ByteBuffer.wrap(tab, 4, 4).getFloat();
        decodedMsg += ByteBuffer.wrap(tab, 8, 4).getFloat();
        decodedMsg += ByteBuffer.wrap(tab, 12, 4).getFloat();



        return decodedMsg;


    }

    private void updateByteTab(ArrayList<Byte> list, byte[] tab, int offset) {
        for (int i=offset;i<list.size();i++) {
            tab[i]=list.get(i);
        }
    }
}
