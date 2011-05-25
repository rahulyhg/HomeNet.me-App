/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package homenet;

/**
 *
 * @author mdoll
 */
import gnu.io.*;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author mdoll
 * @see proccessing serial lib
 */
public class Serial {

    public String port;
    SerialPort serialPort;
    InputStream input;
    OutputStream output;
    byte buffer[] = new byte[1024];
    int bufferIndex = 0;
    int bufferLast = 0;

    public Serial(String port) {
        super();
        this.port = port;
    }

    public boolean begin(int speed) {
         try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(this.port);
            if (portIdentifier.isCurrentlyOwned()) {
                //System.out.println("Error: Port is currently in use");
                throw new Exception("Error: Port is currently in use");
            } else {
                CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

                if (commPort instanceof SerialPort) {
                    serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(speed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                    input = serialPort.getInputStream();
                    output = serialPort.getOutputStream();
                } else {
                    throw new Exception("Only Supports Serial Ports");
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(e.getMessage());
            //exception = e;
            //e.printStackTrace();
            serialPort = null;
            input = null;
            output = null;
            return false;
        }
         return true;
    }
    
    public boolean begin() {
       return begin(115200);
    }

    public void end() {
        try {
            // do io streams need to be closed first?
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        input = null;
        output = null;

        try {
            if (serialPort != null) {
                serialPort.close();  // close the port
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        serialPort = null;

    }

    public void flush() {
        bufferIndex = 0;
        bufferLast = 0;
    }

    public int read() {
        if (bufferIndex == bufferLast) {
            return -1;
        }

        synchronized (buffer) {
            int outgoing = buffer[bufferIndex++] & 0xff;
            if (bufferIndex == bufferLast) {  // rewind
                bufferIndex = 0;
                bufferLast = 0;
            }
            return outgoing;
        }

    }

    public int available() {
        return (bufferLast - bufferIndex);
    }

    public void print() {
    }

    /**
     * This will handle both ints, bytes and chars transparently.
     */
    public void write(int what) {  // will also cover char
        try {
            output.write(what & 0xff);  // for good measure do the &
            output.flush();   // hmm, not sure if a good idea

        } catch (Exception e) { // null pointer or serial port dead
            System.err.println(e.getMessage());
        }
    }

    public void write(byte bytes[]) {
        try {
            output.write(bytes);
            output.flush();   // hmm, not sure if a good idea

        } catch (Exception e) { // null pointer or serial port dead
            //errorMessage("write", e);
            e.printStackTrace();
        }
    }

    public static ArrayList listPorts() {
        ArrayList ports = new ArrayList<String>();

        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                ports.add(portIdentifier.getName());
                //System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
            }
        }

        return ports;
    }

    public static String getPortTypeName(int portType) {
        switch (portType) {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
}