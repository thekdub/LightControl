package com.thekdub.lightcontrol;

import gnu.io.NRSerialPort;
import gnu.io.RXTXPort;

import java.io.DataOutputStream;

public class LightControl {
  
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Usage: java -jar LightControl.jar <port>");
      System.out.println("Port name not provided. Available ports:");
      for (String s : NRSerialPort.getAvailableSerialPorts()) {
        System.out.println("\t> " + s);
      }
      if (NRSerialPort.getAvailableSerialPorts().size() == 0) {
        System.out.println("\tNo serial interfaces detected!");
      }
      return;
    }
    final int BAUD = 250000;
    final String PORT = args[0];
  
    System.out.println("Connecting on " + PORT + " @ " + BAUD);
    
    final int[] CHANNELS = new int[16];
    for (int i = 0; i < CHANNELS.length; i++) {
      CHANNELS[i] = 0;
    }
    CHANNELS[0] = 0xff;
    CHANNELS[2] = 0xff;
  
    CHANNELS[4] = 0xff;
    CHANNELS[7] = 0xff;
    
    NRSerialPort serial = new NRSerialPort(PORT, BAUD);
    serial.connect();
    RXTXPort s = serial.getSerialPortInstance();
    s.enableRs485(false, 0, 0);
    s.setRTS(false);
    DataOutputStream out = new DataOutputStream(s.getOutputStream());
    try {
      while (!Thread.interrupted()) {
        // BREAK 0 x 24
        out.write(0);
        out.write(0);
        out.write(0);
        // MARK AFTER BREAK (MAB) 1 x 3
        out.write(0b11);
        // START CODE
        out.write(0x00);
        // CHANNEL DATA
        for (int i = 0; i < CHANNELS.length; i++) {
          // MARK TIME BETWEEN FRAMES (MTBF)
          out.write(0xf);
          // START CODE
          out.write(0b0);
          // DATA
          out.write(CHANNELS[i] & 0xff);
          // STOP CODE
          out.write(0b11);
          System.out.println("Sent " + CHANNELS[i] + " to channel " + (i+1));
        }
        // MARK TIME BETWEEN PACKETS (MTBP)
        out.write(0xffffffff);
        out.write(0xffffffff);
        Thread.sleep(500);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    serial.disconnect();
    
  }
  
}
