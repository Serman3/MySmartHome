package org.example.struct;


import java.io.*;

public class Payload implements Serializable {

    private int src;   // это 14-битный “адрес” устройства-отправителя;

    private int dst;  // 14-битный “адрес” устройства-получателя, причем адреса 0x0000 и 0x3FFF (16383) зарезервированы. Адрес 0x3FFF означает “широковещательную” рассылку, то есть данные адресованы всем устройствам одновременно;

    private int serial; // это порядковый номер пакета, отправленного устройством, от момента его включения. serial нумеруется с 1;

    private byte dev_type; // это тип устройства, отправившего пакет

    private byte cmd; // это команда протокола;

    private Device cmd_body; // формат которого зависит от команды протокола.

    // Тип устройства dev_type и команда cmd в совокупности определяют данные, которые передаются в cmd_body, как будет описано далее.

    public Payload(int src, int dst, int serial, byte dev_type, byte cmd, Device cmd_body) {
        this.src = src;
        this.dst = dst;
        this.serial = serial;
        this.dev_type = dev_type;
        this.cmd = cmd;
        this.cmd_body = cmd_body;
    }

    public static byte[] encodePacketToByte(Payload payload){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(payload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    public Payload decodeByteToPacket(byte[] data){
        Payload deserializedPayload = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            deserializedPayload = (Payload) ois.readObject();
            System.out.println(deserializedPayload);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return deserializedPayload;
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

    public Device getCmd_body() {
        return cmd_body;
    }

    public void setCmd_body(Device cmd_body) {
        this.cmd_body = cmd_body;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "src=" + src +
                ", dst=" + dst +
                ", serial=" + serial +
                ", devType=" + dev_type +
                ", cmd=" + cmd +
                ", cmdBody=" + cmd_body +
                '}';
    }
}
