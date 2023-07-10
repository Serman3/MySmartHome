package org.example.struct;

public class Packet {

    private byte length;  // это размер поля payload в октетах (байтах);

    private byte[] payload;  //  данные, передаваемые в пакете, конкретный формат данных для каждого типа пакета

    private byte crc8; // контрольная сумма поля payload, вычисленная по алгоритму cyclic redundancy check 8

}
