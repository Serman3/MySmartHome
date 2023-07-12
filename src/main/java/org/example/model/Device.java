package org.example.model;

import java.io.*;

public class Device implements Serializable {
    private String dev_name;

    private byte[] dev_props;

    public Device(String dev_name, byte[] dev_props) {
        this.dev_name = dev_name;
        this.dev_props = dev_props;
    }

    public static byte[] encodeDeviceToByte(org.example.model.Device device){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(device);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    public org.example.struct.Device decodeByteToDevice(byte[] data){
        org.example.struct.Device deserializedDevice = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            deserializedDevice = (org.example.struct.Device) ois.readObject();
            System.out.println(deserializedDevice);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return deserializedDevice;
    }

}
