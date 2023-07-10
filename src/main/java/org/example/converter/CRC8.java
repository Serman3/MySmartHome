package org.example.converter;

public class CRC8 {
    private static byte[] crctable = new byte[0];

    public static void calculateTable_CRC8() {
     byte generator = 0x1D;
        crctable = new byte[256];
         //iterate over all byte values 0 - 255
        for (int dividend = 0; dividend < 256; dividend++)
        {
            byte currByte = (byte)dividend;
            // calculate the CRC-8 value for current byte
            for (byte bit = 0; bit < 8; bit++)
            {
                if ((currByte & 0x80) != 0)
                {
                    currByte <<= 1;
                    currByte ^= generator;
                }
                else
                {
                    currByte <<= 1;
                }
            }
            // store CRC value in lookup table
            crctable[dividend] = currByte;
        }
    }

    public static byte compute_CRC8(byte[] bytes) {
        byte crc = 0;
        for (byte b : bytes) {
            /* XOR-in next input byte */
            byte data = (byte)(b ^ crc);
            /* get current CRC value = remainder */
            crc = (byte)(crctable[data]);
        }
        return crc;
    }

    private static byte resultCRC8(byte[] bytes){
        calculateTable_CRC8();
        return compute_CRC8(bytes);
    }

}
