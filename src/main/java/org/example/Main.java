package org.example;

import org.example.converter.CRC8;
import org.example.struct.Device;
import org.example.struct.Packet;
import org.example.struct.Payload;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class Main {
    private static final String SERVER_URL = "http://localhost:9998";

    public static void main(String[] args) {

 /*       // Пример отправки данных в сеть
        int addressHub = 0x1234; // 14-битный адрес
        Payload payload = new Payload(850,16383,1, (byte) 1, (byte) 1, new Device("SmartHub", null));
        byte[] payloadBytes = Payload.encodePayloadToByte(payload);
        byte sumBytes = 0;
        for (byte b : payloadBytes){
            sumBytes += b;
        }
        Packet packet = new Packet(sumBytes, payload, CRC8.computeCRC8Simple(payloadBytes));
        String binary = byteArrayToHexString(Packet.encodePacketToByte(packet));
        //sendDataToNetwork(packet.toString());
       //sendDataToNetwork(Packet.encodePacketToByte(packet));
        sendDataToNetwork(binary);
       // sendDataToNetwork("DbMG_38BBgaI0Kv6kzGK");

*/

        org.example.model.Device device1 = new org.example.model.Device("SmartHub", null);
        org.example.model.Payload payload1 = new org.example.model.Payload(1518,16383,1, (byte) 1, (byte) 1, org.example.model.Device.encodeDeviceToByte(device1));
        byte[] payloadBytes1 = org.example.model.Payload.encodePayloadToByte(payload1);
        byte sumBytes1 = 0;
        for (byte b : payloadBytes1){
            sumBytes1 += b;
        }
        org.example.model.Packet packet1 = new org.example.model.Packet(sumBytes1, org.example.model.Payload.encodePayloadToByte(payload1), CRC8.computeCRC8Simple(payloadBytes1));
        String binary1 = byteArrayToHexString(org.example.model.Packet.encodePacketToByte(packet1));
        sendDataToNetwork(binary1);
        //sendDataToNetwork("DbMG_38BBgaI0Kv6kzGK");
    }

    private static String getDataFromNetwork() {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            byte[] response = new byte[0];
            String line;
            while ((line = reader.readLine()) != null) {
                response = Base64.getDecoder().decode(line);
            }
            reader.close();

            return response.toString();
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
            System.out.println("Response code: " + responseCode);

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String arrayBytesToBinaryCode(byte[] bytes){
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            String binStr = Integer.toBinaryString(b & 0xFF);
            stringBuilder.append(("00000000" + binStr + " ").substring(binStr.length())).append(" ");
        }
        return stringBuilder.toString();
    }

    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b)).append(" ");
        }
        return sb.toString();
    }

}