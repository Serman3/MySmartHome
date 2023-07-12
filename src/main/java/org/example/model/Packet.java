package org.example.model;

import java.io.*;

public class Packet implements Serializable {
    private byte length;
    private byte[] payload;
    private byte crc8;

    public Packet(byte length, byte[] payload, byte crc8) {
        this.length = length;
        this.payload = payload;
        this.crc8 = crc8;
    }

    public static byte[] encodePacketToByte(org.example.model.Packet packet){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    public org.example.struct.Packet decodeByteToPacket(byte[] data){
        org.example.struct.Packet deserializedPacket = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            deserializedPacket = (org.example.struct.Packet) ois.readObject();
            System.out.println(deserializedPacket);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return deserializedPacket;
    }

}
