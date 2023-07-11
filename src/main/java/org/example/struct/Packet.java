package org.example.struct;

import java.io.*;

public class Packet implements Serializable {

    private byte length;  // это размер поля payload в октетах (байтах);

    private Payload payload;  //  данные, передаваемые в пакете, конкретный формат данных для каждого типа пакета

    private byte crc8; // контрольная сумма поля payload, вычисленная по алгоритму cyclic redundancy check 8

    public Packet(byte length, Payload payload, byte crc8) {
        this.length = length;
        this.payload = payload;
        this.crc8 = crc8;
    }

    public static byte[] encodePacketToByte(Packet packet){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    public Packet decodeByteToPacket(byte[] data){
        Packet deserializedPacket = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            deserializedPacket = (Packet) ois.readObject();
            System.out.println(deserializedPacket);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return deserializedPacket;
    }


    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public byte getCrc8() {
        return crc8;
    }

    public void setCrc8(byte crc8) {
        this.crc8 = crc8;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "length=" + length +
                ", payload=" + payload +
                ", crc8=" + crc8 +
                '}';
    }
}
