package org.example.struct;

/*Обработка команд в зависимости от типа устройства
SmartHub
WHOISHERE, IAMHERE — dev_props пуст
EnvSensor
WHOISHERE, IAMHERE — dev_props имеет следующую структуру:

type env_sensor_props struct {
  sensors byte
  triggers [] struct {
    op byte
    value varuint
    name string
  }
};
*/

import java.io.*;
import java.util.Arrays;

public class Device implements Serializable {

    private String dev_name;

    private byte[] dev_props;  // Содержимое dev_props определяется в зависимости от типа устройства.

    /*Типы устройств
    0x01 - SmartHub — это устройство, которое моделирует ваша программа, оно единственное устройство этого типа в сети;
    0x02 - EnvSensor — датчик характеристик окружающей среды (температура, влажность, освещенность, загрязнение воздуха);
    0x03 - Switch — переключатель;
    0x04 - Lamp — лампа;
    0x05 - Socket — розетка;
    0x06 - Clock — часы, которые широковещательно рассылают сообщения TICK. Часы гарантрированно присутствуют в сети и только в одном экземпляре.*/
    /*



    0x01 - WHOISHERE — отправляется устройством, желающим обнаружить своих соседей в сети. Адрес рассылки dst должен быть широковещательным 0x3FFF. Поле cmd_body описывает характеристики самого устройства в виде структуры:

    type device struct {
    dev_name string
    dev_props bytes
    };

    Содержимое dev_props определяется в зависимости от типа устройства.

    0x02 - IAMHERE — отправляется устройством, получившим команду WHOISHERE, и содержит информацию о самом устройстве в поле cmd_body. Команда IAMHERE отправляется строго в ответ на WHOISHERE. Команда отправляется на широковещательный адрес.
    0x03 - GETSTATUS — отправляется хабом какому-либо устройству для чтения состояния устройства. Если устройство не поддерживает команду GETSTATUS (например, таймер), команда игнорируется.
    0x04 - STATUS — отправляется устройством хабу и как ответ на запросы GETSTATUS, SETSTATUS, и самостоятельно при изменении состояния устройства. Например, переключатель отправляет сообщение STATUS в момент переключения. В этом случае адресом получателя устанавливается устройство, которое последнее отправило данному устройству команду GETSTATUS. Если такой команды ещё не поступало, сообщение STATUS не отправляется никому.
    0x05 - SETSTATUS — отправляется хабом какому-либо устройству, чтобы устройство изменило свое состояние, например, чтобы включилась лампа. Если устройство не поддерживает изменение состояния (например, таймер), команда игнорируется.
    0x06 - TICK — тик таймера, отправляется таймером. Периодичность отправления не гарантируется, но если на некоторый момент времени запланировано событие, то срабатывание события должно наступать, когда время, передаваемое в сообщении TICK становится больше или равно запланированному. Поле cmd_body содержит следующие данные:
    */

    public Device(){};

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

    public void setDev_props(byte[] dev_props) {
        this.dev_props = dev_props;
    }

    public static byte[] encodeDeviceToByte(Device device){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(device);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    public Device decodeByteToDevice(byte[] data){
        Device deserializedDevice = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            deserializedDevice = (Device) ois.readObject();
            System.out.println(deserializedDevice);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return deserializedDevice;
    }

    @Override
    public String toString() {
        return "Device{" +
                "dev_name='" + dev_name + '\'' +
                ", dev_props=" + Arrays.toString(dev_props) +
                '}';
    }
}
