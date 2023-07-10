package org.example.converter;

import java.util.ArrayList;
import java.util.List;

public class ULEB128 {
    /*public static void main(String[] args) {
        int number = 624485;
        byte[] uleb128 = encodeULEB128(number);
        System.out.println("ULEB128: " + Arrays.toString(uleb128));
    }
*/
    private static byte[] encodeULEB128(int number) {
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
}
