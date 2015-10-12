package org.jcodec.codecs.mjpeg;

import org.jcodec.common.io.VLC;
import org.jcodec.common.io.VLCBuilder;

/**
 * This class is part of JCodec ( www.jcodec.org )
 * This software is distributed under FreeBSD License
 * 
 * @author Jay Codec
 *
 */
public class JpegConst {

    public static enum Type {
        YDC(0x00), CDC(0x01), YAC(0x10), CAC(0x11);
        private final int value;

        private Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public final static VLC YDC_DEFAULT;
    public final static VLC YAC_DEFAULT;
    public final static VLC CDC_DEFAULT;
    public final static VLC CAC_DEFAULT;
    static {
        VLCBuilder bldr1 = new VLCBuilder();
        bldr1.set(0, "00");
        bldr1.set(1, "010");
        bldr1.set(2, "011");
        bldr1.set(3, "100");
        bldr1.set(4, "101");
        bldr1.set(5, "110");
        bldr1.set(6, "1110");
        bldr1.set(7, "11110");
        bldr1.set(8, "111110");
        bldr1.set(9, "1111110");
        bldr1.set(10, "11111110");
        bldr1.set(11, "111111110");
        YDC_DEFAULT = bldr1.getVLC();

        VLCBuilder bldr2 =  new VLCBuilder();
        bldr2.set(0, "00");
        bldr2.set(1, "01");
        bldr2.set(2, "10");
        bldr2.set(3, "110");
        bldr2.set(4, "1110");
        bldr2.set(5, "11110");
        bldr2.set(6, "111110");
        bldr2.set(7, "1111110");
        bldr2.set(8, "11111110");
        bldr2.set(9, "111111110");
        bldr2.set(10, "1111111110");
        bldr2.set(11, "11111111110");
        CDC_DEFAULT = bldr2.getVLC();

        VLCBuilder bldr3 =  new VLCBuilder();
        bldr3.set(0x00, "1010");
        bldr3.set(0x01, "00");
        bldr3.set(0x02, "01");
        bldr3.set(0x03, "100");
        bldr3.set(0x04, "1011");
        bldr3.set(0x05, "11010");
        bldr3.set(0x06, "1111000");
        bldr3.set(0x07, "11111000");
        bldr3.set(0x08, "1111110110");
        bldr3.set(0x09, "1111111110000010");
        bldr3.set(0x0A, "1111111110000011");
        bldr3.set(0x11, "1100");
        bldr3.set(0x12, "11011");
        bldr3.set(0x13, "1111001");
        bldr3.set(0x14, "111110110");
        bldr3.set(0x15, "11111110110");
        bldr3.set(0x16, "1111111110000100");
        bldr3.set(0x17, "1111111110000101");
        bldr3.set(0x18, "1111111110000110");
        bldr3.set(0x19, "1111111110000111");
        bldr3.set(0x1A, "1111111110001000");

        bldr3.set(0x21, "11100");
        bldr3.set(0x22, "11111001");
        bldr3.set(0x23, "1111110111");
        bldr3.set(0x24, "111111110100");
        bldr3.set(0x25, "1111111110001001");
        bldr3.set(0x26, "1111111110001010");
        bldr3.set(0x27, "1111111110001011");
        bldr3.set(0x28, "1111111110001100");
        bldr3.set(0x29, "1111111110001101");
        bldr3.set(0x2A, "1111111110001110");

        bldr3.set(0x31, "111010");
        bldr3.set(0x32, "111110111");
        bldr3.set(0x33, "111111110101");
        bldr3.set(0x34, "1111111110001111");
        bldr3.set(0x35, "1111111110010000");
        bldr3.set(0x36, "1111111110010001");
        bldr3.set(0x37, "1111111110010010");
        bldr3.set(0x38, "1111111110010011");
        bldr3.set(0x39, "1111111110010100");
        bldr3.set(0x3A, "1111111110010101");

        bldr3.set(0x41, "111011");
        bldr3.set(0x42, "1111111000");
        bldr3.set(0x43, "1111111110010110");
        bldr3.set(0x44, "1111111110010111");
        bldr3.set(0x45, "1111111110011000");
        bldr3.set(0x46, "1111111110011001");
        bldr3.set(0x47, "1111111110011010");
        bldr3.set(0x48, "1111111110011011");
        bldr3.set(0x49, "1111111110011100");
        bldr3.set(0x4A, "1111111110011101");

        bldr3.set(0x51, "1111010");
        bldr3.set(0x52, "11111110111");
        bldr3.set(0x53, "1111111110011110");
        bldr3.set(0x54, "1111111110011111");
        bldr3.set(0x55, "1111111110100000");
        bldr3.set(0x56, "1111111110100001");
        bldr3.set(0x57, "1111111110100010");
        bldr3.set(0x58, "1111111110100011");
        bldr3.set(0x59, "1111111110100100");
        bldr3.set(0x5A, "1111111110100101");

        bldr3.set(0x61, "1111011");
        bldr3.set(0x62, "111111110110");
        bldr3.set(0x63, "1111111110100110");
        bldr3.set(0x64, "1111111110100111");
        bldr3.set(0x65, "1111111110101000");
        bldr3.set(0x66, "1111111110101001");
        bldr3.set(0x67, "1111111110101010");
        bldr3.set(0x68, "1111111110101011");
        bldr3.set(0x69, "1111111110101100");
        bldr3.set(0x6A, "1111111110101101");

        bldr3.set(0x71, "11111010");
        bldr3.set(0x72, "111111110111");
        bldr3.set(0x73, "1111111110101110");
        bldr3.set(0x74, "1111111110101111");
        bldr3.set(0x75, "1111111110110000");
        bldr3.set(0x76, "1111111110110001");
        bldr3.set(0x77, "1111111110110010");
        bldr3.set(0x78, "1111111110110011");
        bldr3.set(0x79, "1111111110110100");
        bldr3.set(0x7A, "1111111110110101");

        bldr3.set(0x81, "111111000");
        bldr3.set(0x82, "111111111000000");
        bldr3.set(0x83, "1111111110110110");
        bldr3.set(0x84, "1111111110110111");
        bldr3.set(0x85, "1111111110111000");
        bldr3.set(0x86, "1111111110111001");
        bldr3.set(0x87, "1111111110111010");
        bldr3.set(0x88, "1111111110111011");
        bldr3.set(0x89, "1111111110111100");
        bldr3.set(0x8A, "1111111110111101");

        bldr3.set(0x91, "111111001");
        bldr3.set(0x92, "1111111110111110");
        bldr3.set(0x93, "1111111110111111");
        bldr3.set(0x94, "1111111111000000");
        bldr3.set(0x95, "1111111111000001");
        bldr3.set(0x96, "1111111111000010");
        bldr3.set(0x97, "1111111111000011");
        bldr3.set(0x98, "1111111111000100");
        bldr3.set(0x99, "1111111111000101");
        bldr3.set(0x9A, "1111111111000110");

        bldr3.set(0xA1, "111111010");
        bldr3.set(0xA2, "1111111111000111");
        bldr3.set(0xA3, "1111111111001000");
        bldr3.set(0xA4, "1111111111001001");
        bldr3.set(0xA5, "1111111111001010");
        bldr3.set(0xA6, "1111111111001011");
        bldr3.set(0xA7, "1111111111001100");
        bldr3.set(0xA8, "1111111111001101");
        bldr3.set(0xA9, "1111111111001110");
        bldr3.set(0xAA, "1111111111001111");

        bldr3.set(0xB1, "1111111001");
        bldr3.set(0xB2, "1111111111010000");
        bldr3.set(0xB3, "1111111111010001");
        bldr3.set(0xB4, "1111111111010010");
        bldr3.set(0xB5, "1111111111010011");
        bldr3.set(0xB6, "1111111111010100");
        bldr3.set(0xB7, "1111111111010101");
        bldr3.set(0xB8, "1111111111010110");
        bldr3.set(0xB9, "1111111111010111");
        bldr3.set(0xBA, "1111111111011000");

        bldr3.set(0xC1, "1111111010");
        bldr3.set(0xC2, "1111111111011001");
        bldr3.set(0xC3, "1111111111011010");
        bldr3.set(0xC4, "1111111111011011");
        bldr3.set(0xC5, "1111111111011100");
        bldr3.set(0xC6, "1111111111011101");
        bldr3.set(0xC7, "1111111111011110");
        bldr3.set(0xC8, "1111111111011111");
        bldr3.set(0xC9, "1111111111100000");
        bldr3.set(0xCA, "1111111111100001");

        bldr3.set(0xD1, "11111111000");
        bldr3.set(0xD2, "1111111111100010");
        bldr3.set(0xD3, "1111111111100011");
        bldr3.set(0xD4, "1111111111100100");
        bldr3.set(0xD5, "1111111111100101");
        bldr3.set(0xD6, "1111111111100110");
        bldr3.set(0xD7, "1111111111100111");
        bldr3.set(0xD8, "1111111111101000");
        bldr3.set(0xD9, "1111111111101001");
        bldr3.set(0xDA, "1111111111101010");

        bldr3.set(0xE1, "1111111111101011");
        bldr3.set(0xE2, "1111111111101100");
        bldr3.set(0xE3, "1111111111101101");
        bldr3.set(0xE4, "1111111111101110");
        bldr3.set(0xE5, "1111111111101111");
        bldr3.set(0xE6, "1111111111110000");
        bldr3.set(0xE7, "1111111111110001");
        bldr3.set(0xE8, "1111111111110010");
        bldr3.set(0xE9, "1111111111110011");
        bldr3.set(0xEA, "1111111111110100");

        bldr3.set(0xF0, "11111111001");
        bldr3.set(0xF1, "1111111111110101");
        bldr3.set(0xF2, "1111111111110110");
        bldr3.set(0xF3, "1111111111110111");
        bldr3.set(0xF4, "1111111111111000");
        bldr3.set(0xF5, "1111111111111001");
        bldr3.set(0xF6, "1111111111111010");
        bldr3.set(0xF7, "1111111111111011");
        bldr3.set(0xF8, "1111111111111100");
        bldr3.set(0xF9, "1111111111111101");
        bldr3.set(0xFA, "1111111111111110");
        YAC_DEFAULT = bldr3.getVLC();

        VLCBuilder bldr4 =  new VLCBuilder();
        bldr4.set(0x00, "00");
        bldr4.set(0x01, "01");
        bldr4.set(0x02, "100");
        bldr4.set(0x03, "1010");
        bldr4.set(0x04, "11000");
        bldr4.set(0x05, "11001");
        bldr4.set(0x06, "111000");
        bldr4.set(0x07, "1111000");
        bldr4.set(0x08, "111110100");
        bldr4.set(0x09, "1111110110");
        bldr4.set(0x0A, "111111110100");

        bldr4.set(0x11, "1011");
        bldr4.set(0x12, "111001");
        bldr4.set(0x13, "11110110");
        bldr4.set(0x14, "111110101");
        bldr4.set(0x15, "11111110110");
        bldr4.set(0x16, "111111110101");
        bldr4.set(0x17, "1111111110001000");
        bldr4.set(0x18, "1111111110001001");
        bldr4.set(0x19, "1111111110001010");
        bldr4.set(0x1A, "1111111110001011");

        bldr4.set(0x21, "11010");
        bldr4.set(0x22, "11110111");
        bldr4.set(0x23, "1111110111");
        bldr4.set(0x24, "111111110110");
        bldr4.set(0x25, "111111111000010");
        bldr4.set(0x26, "1111111110001100");
        bldr4.set(0x27, "1111111110001101");
        bldr4.set(0x28, "1111111110001110");
        bldr4.set(0x29, "1111111110001111");
        bldr4.set(0x2A, "1111111110010000");

        bldr4.set(0x31, "11011");
        bldr4.set(0x32, "11111000");
        bldr4.set(0x33, "1111111000");
        bldr4.set(0x34, "111111110111");
        bldr4.set(0x35, "1111111110010001");
        bldr4.set(0x36, "1111111110010010");
        bldr4.set(0x37, "1111111110010011");
        bldr4.set(0x38, "1111111110010100");
        bldr4.set(0x39, "1111111110010101");
        bldr4.set(0x3A, "1111111110010110");

        bldr4.set(0x41, "111010");
        bldr4.set(0x42, "111110110");
        bldr4.set(0x43, "1111111110010111");
        bldr4.set(0x44, "1111111110011000");
        bldr4.set(0x45, "1111111110011001");
        bldr4.set(0x46, "1111111110011010");
        bldr4.set(0x47, "1111111110011011");
        bldr4.set(0x48, "1111111110011100");
        bldr4.set(0x49, "1111111110011101");
        bldr4.set(0x4A, "1111111110011110");

        bldr4.set(0x51, "111011");
        bldr4.set(0x52, "1111111001");
        bldr4.set(0x53, "1111111110011111");
        bldr4.set(0x54, "1111111110100000");
        bldr4.set(0x55, "1111111110100001");
        bldr4.set(0x56, "1111111110100010");
        bldr4.set(0x57, "1111111110100011");
        bldr4.set(0x58, "1111111110100100");
        bldr4.set(0x59, "1111111110100101");
        bldr4.set(0x5A, "1111111110100110");

        bldr4.set(0x61, "1111001");
        bldr4.set(0x62, "11111110111");
        bldr4.set(0x63, "1111111110100111");
        bldr4.set(0x64, "1111111110101000");
        bldr4.set(0x65, "1111111110101001");
        bldr4.set(0x66, "1111111110101010");
        bldr4.set(0x67, "1111111110101011");
        bldr4.set(0x68, "1111111110101100");
        bldr4.set(0x69, "1111111110101101");
        bldr4.set(0x6A, "1111111110101110");

        bldr4.set(0x71, "1111010");
        bldr4.set(0x72, "11111111000");
        bldr4.set(0x73, "1111111110101111");
        bldr4.set(0x74, "1111111110110000");
        bldr4.set(0x75, "1111111110110001");
        bldr4.set(0x76, "1111111110110010");
        bldr4.set(0x77, "1111111110110011");
        bldr4.set(0x78, "1111111110110100");
        bldr4.set(0x79, "1111111110110101");
        bldr4.set(0x7A, "1111111110110110");

        bldr4.set(0x81, "11111001");
        bldr4.set(0x82, "1111111110110111");
        bldr4.set(0x83, "1111111110111000");
        bldr4.set(0x84, "1111111110111001");
        bldr4.set(0x85, "1111111110111010");
        bldr4.set(0x86, "1111111110111011");
        bldr4.set(0x87, "1111111110111100");
        bldr4.set(0x88, "1111111110111101");
        bldr4.set(0x89, "1111111110111110");
        bldr4.set(0x8A, "1111111110111111");

        bldr4.set(0x91, "111110111");
        bldr4.set(0x92, "1111111111000000");
        bldr4.set(0x93, "1111111111000001");
        bldr4.set(0x94, "1111111111000010");
        bldr4.set(0x95, "1111111111000011");
        bldr4.set(0x96, "1111111111000100");
        bldr4.set(0x97, "1111111111000101");
        bldr4.set(0x98, "1111111111000110");
        bldr4.set(0x99, "1111111111000111");
        bldr4.set(0x9A, "1111111111001000");

        bldr4.set(0xA1, "111111000");
        bldr4.set(0xA2, "1111111111001001");
        bldr4.set(0xA3, "1111111111001010");
        bldr4.set(0xA4, "1111111111001011");
        bldr4.set(0xA5, "1111111111001100");
        bldr4.set(0xA6, "1111111111001101");
        bldr4.set(0xA7, "1111111111001110");
        bldr4.set(0xA8, "1111111111001111");
        bldr4.set(0xA9, "1111111111010000");
        bldr4.set(0xAA, "1111111111010001");

        bldr4.set(0xB1, "111111001");
        bldr4.set(0xB2, "1111111111010010");
        bldr4.set(0xB3, "1111111111010011");
        bldr4.set(0xB4, "1111111111010100");
        bldr4.set(0xB5, "1111111111010101");
        bldr4.set(0xB6, "1111111111010110");
        bldr4.set(0xB7, "1111111111010111");
        bldr4.set(0xB8, "1111111111011000");
        bldr4.set(0xB9, "1111111111011001");
        bldr4.set(0xBA, "1111111111011010");

        bldr4.set(0xC1, "111111010");
        bldr4.set(0xC2, "1111111111011011");
        bldr4.set(0xC3, "1111111111011100");
        bldr4.set(0xC4, "1111111111011101");
        bldr4.set(0xC5, "1111111111011110");
        bldr4.set(0xC6, "1111111111011111");
        bldr4.set(0xC7, "1111111111100000");
        bldr4.set(0xC8, "1111111111100001");
        bldr4.set(0xC9, "1111111111100010");
        bldr4.set(0xCA, "1111111111100011");
        bldr4.set(0xD1, "11111111001");
        bldr4.set(0xD2, "1111111111100100");
        bldr4.set(0xD3, "1111111111100101");
        bldr4.set(0xD4, "1111111111100110");
        bldr4.set(0xD5, "1111111111100111");
        bldr4.set(0xD6, "1111111111101000");
        bldr4.set(0xD7, "1111111111101001");
        bldr4.set(0xD8, "1111111111101010");
        bldr4.set(0xD9, "1111111111101011");
        bldr4.set(0xDA, "1111111111101100");
        bldr4.set(0xE1, "11111111100000");
        bldr4.set(0xE2, "1111111111101101");
        bldr4.set(0xE3, "1111111111101110");
        bldr4.set(0xE4, "1111111111101111");
        bldr4.set(0xE5, "1111111111110000");
        bldr4.set(0xE6, "1111111111110001");
        bldr4.set(0xE7, "1111111111110010");
        bldr4.set(0xE8, "1111111111110011");
        bldr4.set(0xE9, "1111111111110100");
        bldr4.set(0xEA, "1111111111110101");
        bldr4.set(0xF0, "1111111010");
        bldr4.set(0xF1, "111111111000011");
        bldr4.set(0xF2, "1111111111110110");
        bldr4.set(0xF3, "1111111111110111");
        bldr4.set(0xF4, "1111111111111000");
        bldr4.set(0xF5, "1111111111111001");
        bldr4.set(0xF6, "1111111111111010");
        bldr4.set(0xF7, "1111111111111011");
        bldr4.set(0xF8, "1111111111111100");
        bldr4.set(0xF9, "1111111111111101");
        bldr4.set(0xFA, "1111111111111110");
        CAC_DEFAULT = bldr4.getVLC();
    }
}
