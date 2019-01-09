package tools;

import jssc.SerialPort;
import jssc.SerialPortException;

public class SerialConnection {
    private SerialPort serialPort;

    public void connect() {
        if (serialPort == null) {
            this.serialPort = new SerialPort("COM3");
            try {
                System.out.println("Port opened: " + serialPort.openPort());
                System.out.println("Params set: " + serialPort.setParams(9600, 8, 1, 0));
            }
            catch (SerialPortException e){
                System.out.println(e.getMessage());
            }

            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                System.out.println("Interrupted Exception: " + e.getMessage());
            }
        } else {
            System.out.println("Already connected");
        }

    }

    public void writeMessage(String message)
    {
        try {
            serialPort.writeString("<" + message + ">");
            System.out.println("Message: - " + message + " -  sent");
        } catch (SerialPortException e) {
            System.err.println("Error at sending message: " + e);
        }
    }
}
