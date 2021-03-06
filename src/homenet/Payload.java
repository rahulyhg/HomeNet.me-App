/*
 * Copyright (c) 2011 Matthew Doll <mdoll at homenet.me>.
 *
 * This file is part of HomeNet.
 *
 * HomeNet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HomeNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HomeNet.  If not, see <http://www.gnu.org/licenses/>.
 */
package homenet;

/**
 *
 * @author mdoll
 */
public class Payload {

    int length = 0;
    byte[] data = new byte[56];

    public Payload(final byte value) {
        length = 1;
        data[0] = value;
    }

    public Payload(final byte[] value, final int size) {
        System.arraycopy(value, 0, data, 0, size);
        length = size;
    }

    public Payload(final byte[] value) {
        this(value, value.length);
    }

    public Payload(final int value) {
        length = 2;
        data[1] = (byte) ((value >> 8) & 0xFF);
        data[0] = (byte) (value & 0xFF);
    }

    public Payload(final int[] value) {
        length = value.length * 2;
        for (int i = 0; i < value.length; i += 2) {
            data[i * 2] = (byte) ((value[i] >> 8) & 0xFF);
            data[(i * 2) + 1] = (byte) (value[i] & 0xFF);
        }
    }

    public Payload(final String string) {
        length = string.length();
        for (int i = 0; i < length; i++) {
            data[i] = (byte) (string.charAt(i));
        }
    }
    /*
    Payload(final char value[]) {
    int i = 0;
    
    while (value[i] != null) {
    data[i] = value[i];
    i++;
    }
    length = i;
    }
     */

    public Payload(final float value) {
        length = 0;
        /*
        union {
        float in;
        long out;
        } number;
        
        number.in = value;
        
        Payload payload;
        length = 4;
        
        data[0] = (number.out >> 24) &0xFF;
        data[1] = (number.out >> 16) &0xFF;
        data[2] = (number.out >> 8) &0xFF;
        data[3] = number.out & 0xFF;
         */
    }

    public Payload(final float[] value) {
        length = 0;
    }

    public Payload(final long value) {
        length = 4;
        data[0] = (byte) ((value >> 24) & 0xFF);
        data[1] = (byte) ((value >> 16) & 0xFF);
        data[2] = (byte) ((value >> 8) & 0xFF);
        data[3] = (byte) (value & 0xFF);
    }

    public Payload() {
        length = 0;
    }

    public int getByte() {
        return (getByte(0));
    }

    public int getByte(int place) {
        return (data[place]) & 0xFF;
    }

    public int getInt() {
        return (int) (data[1] << 8) | (int) (data[0]);
    }

    public int getLength() {
        return length;
    }

    public float getFloat() {

        if (length != 4) {
            return 0.0F;
        }

        // Same as DataInputStream's 'readInt' method
        int i = (((data[0] & 0xff) << 24) | ((data[1] & 0xff) << 16)
                | ((data[2] & 0xff) << 8) | (data[3] & 0xff));


        return Float.intBitsToFloat(i);
    }
}
