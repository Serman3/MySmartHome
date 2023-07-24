package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class Main{
    private static String SERVER_URL = "";
    private static int ADDRESS_HUB = 0;

    public static void main(String args[]){
        SERVER_URL = "http://localhost:9998";//args[0];
        ADDRESS_HUB = Integer.parseInt("2418"/*args[1]*/);
        Device device1 = new Device("SmartHub", null);
        Payload payload1 = new Payload(ADDRESS_HUB,16383,1, (byte) 1, (byte) 1, encodeObjectToByte(device1));
        byte[] payloadBytes1 = encodeObjectToByte(payload1);
        byte sumBytes1 = 0;
        for (byte b : payloadBytes1){
            sumBytes1 += b;
        }
        Packet packet1 = new Packet(sumBytes1, encodeObjectToByte(payload1), computeCRC8Simple(payloadBytes1));
        String binary1 = byteArrayToHexString(encodeObjectToByte(packet1));
        sendDataToNetwork(binary1);

        byte[] base64decode = getDataFromNetwork();

        if(base64decode == null){
            sendDataToNetwork(binary1);
        }else{
            while(true){
                String binary = new String(base64decode, StandardCharsets.UTF_8);
                byte[] byteObject = hexStringToByteArray(binary);
                Object object = decodeByteToObject(byteObject);
                if(object == null){
                    break;
                }
                if(object instanceof Packet){
                    object = (Packet) object;
                }
                if(object instanceof Payload){
                    object = (Payload) object;
                }
                if(object instanceof Device){
                    object = (Device) object;
                }
                if(object instanceof EnvSensorProps){
                    object = (EnvSensorProps) object;
                }
                if(object instanceof EnvSensorStatusCmdBody){
                    object = (EnvSensorStatusCmdBody) object;
                }
                if(object instanceof TimerCmdBody){
                    object = (TimerCmdBody) object;
                }
                if(object instanceof Triggers){
                    object = (Triggers) object;
                }
            }
        }


    }


    private static byte[] getDataFromNetwork() {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return Base64.getDecoder().decode(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void sendDataToNetwork(String data) {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String base64encode = Base64.getEncoder().encodeToString(data.getBytes());
            connection.getOutputStream().write(base64encode.getBytes());
            int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                connection.disconnect();
            }
            if(responseCode == 204){
                connection.disconnect();
            }
            System.out.println("Response code: " + responseCode);

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b)).append(" ");
        }
        return sb.toString();
    }

    private static byte[] hexStringToByteArray(String hexString) {
        hexString = hexString.replaceAll("\\s+", "");
        int length = hexString.length();
        byte[] byteArray = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        return byteArray;
    }

    public static byte computeCRC8Simple(byte[] bytes) {
        byte generator = 0x1D;
        byte crc = 0;
        for (byte currByte : bytes){
            crc ^= currByte;
            for (int i = 0; i < 8; i++)
            {
                if ((crc & 0x80) != 0)
                {
                    crc = (byte)((crc << 1) ^ generator);
                }
                else
                {
                    crc <<= 1;
                }
            }
        }
        return crc;
    }

    private static byte[] encodeULEB128(int number){
        List<Byte> bytes = new ArrayList<>();
        do {
            byte b = (byte) (number & 0x7F);
            number >>= 7;
            if (number != 0) {
                b |= 0x80;
            }
            bytes.add(b);
        } while (number != 0);

        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }

    public static byte[] encodeObjectToByte(Object object){
        if(object instanceof Packet){
            object = (Packet) object;
        }
        if(object instanceof Payload){
            object = (Payload) object;
        }
        if(object instanceof Device){
            object = (Device) object;
        }
        if(object instanceof EnvSensorProps){
            object = (EnvSensorProps) object;
        }
        if(object instanceof EnvSensorStatusCmdBody){
            object = (EnvSensorStatusCmdBody) object;
        }
        if(object instanceof TimerCmdBody){
            object = (TimerCmdBody) object;
        }
        if(object instanceof Triggers){
            object = (Triggers) object;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    public static Object decodeByteToObject(byte[] data){
        Object deserializedObject = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            deserializedObject = ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return deserializedObject;
    }

}

class Packet implements Serializable {
    private byte length;
    private byte[] payload;
    private byte crc8;

    public Packet(byte length, byte[] payload, byte crc8) {
        this.length = length;
        this.payload = payload;
        this.crc8 = crc8;
    }

    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public byte getCrc8() {
        return crc8;
    }

    public void setCrc8(byte crc8) {
        this.crc8 = crc8;
    }

}

class Payload implements Serializable {
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

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public int getDst() {
        return dst;
    }

    public void setDst(int dst) {
        this.dst = dst;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public byte getDev_type() {
        return dev_type;
    }

    public void setDev_type(byte dev_type) {
        this.dev_type = dev_type;
    }

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte[] getCmd_body() {
        return cmd_body;
    }

    public void setCmd_body(byte[] cmd_body) {
        this.cmd_body = cmd_body;
    }

}

class Device implements Serializable {
    private String dev_name;

    private byte[] dev_props;

    public Device(String dev_name, byte[] dev_props) {
        this.dev_name = dev_name;
        this.dev_props = dev_props;
    }

    public String getDev_name() {
        return dev_name;
    }

    public void setDev_name(String dev_name) {
        this.dev_name = dev_name;
    }

    public byte[] getDev_props() {
        return dev_props;
    }

    public void setDev_props(byte[] dev_props){
        this.dev_props = dev_props;
    }

}

class EnvSensorProps implements Serializable {

    private byte sensors;

    private Triggers[] triggers;

    public EnvSensorProps(byte sensors, Triggers[] triggers){
        this.sensors = sensors;
        this.triggers = triggers;
    }

    public byte getSensors() {
        return sensors;
    }

    public void setSensors(byte sensors) {
        this.sensors = sensors;
    }

    public Triggers[] getTriggers(){
        return triggers;
    }

    public  void setTriggers(Triggers[] triggers){
        this.triggers = triggers;
    }

}

class EnvSensorStatusCmdBody implements Serializable {

    private int[] values;

    public EnvSensorStatusCmdBody(int[] values){
        this.values = values;
    }

    public int[] getValues(){
        return values;
    }

    public void setValues(int[] values){
        this.values = values;
    }

}

class TimerCmdBody implements Serializable {

    int timestamp;

    public TimerCmdBody(int timestamp){
        this.timestamp = timestamp;
    }

    public int getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(int timestamp){
        this.timestamp = timestamp;
    }

}

class Triggers implements Serializable{

    private byte op;

    private int value;

    private String name;

    public Triggers(byte op, int value, String name){
        this.op = op;
        this.value = value;
        this.name = name;
    }

    public byte getOp(){
        return op;
    }

    public void setOp(byte op){
        this.op = op;
    }

    public int getValue(){
        return value;
    }

    public void setValue(int value){
        this.value = value;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

}