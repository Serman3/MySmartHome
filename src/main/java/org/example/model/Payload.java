package org.example.model;

import java.io.*;

public class Payload implements Serializable {
    private int src;

    private int dst;

    private int serial;

    private byte dev_type;

    private byte cmd;

    private byte[] cmd_body;

    public Payload(int src, int dst, int serial, byte dev_type, byte cmd, byte[] cmd_body) {
        this.src = src;
        this.dst = dst;
        this.serial = serial;
        this.dev_type = dev_type;
        this.cmd = cmd;
        this.cmd_body = cmd_body;
    }

    public static byte[] encodePayloadToByte(org.example.model.Payload payload){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(payload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    public org.example.struct.Payload decodeByteToPayload(byte[] data){
        org.example.struct.Payload deserializedPayload = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            deserializedPayload = (org.example.struct.Payload) ois.readObject();
            System.out.println(deserializedPayload);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return deserializedPayload;
    }

}
