package com.thekdub.lightcontrol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LightControl {
  
  public static void main(String[] args) {

    if (args.length == 0) {
      System.out.println("Usage: java -jar lightcontrol.jar <port1> <port2> ...\n");
      System.out.println("\tEach port provided will be assigned to each universe in order.");
      System.out.println("Valid Ports:");
      if (DMXCommunicator.getValidSerialPorts().size() > 0) {
        for (String port : DMXCommunicator.getValidSerialPorts()) {
          System.out.printf("\t> %s\n", port);
        }
      }
      else {
        System.out.println("\tNo valid serial ports detected.");
      }
      return;
    }

    final List<DMXCommunicator> PORTS = new ArrayList<>();

    for (int i = 0; i < args.length; i++) {
      final String PORT = args[i];
      try {
        PORTS.add(new DMXCommunicator(PORT));
        System.out.printf("\tUsing port %s for universe %d\n", PORT, i+1);
      }
      catch (Exception e) {
        System.out.printf("Unable to connect to port %s! Double check your spelling and make sure the " +
              "adapter is connected!\n", PORT);
        return;
      }
    }

    System.out.println("Beginning output!");
    for (DMXCommunicator port : PORTS) {
      port.start();
    }

    System.out.println("System ready! Enter <address: 1-" + PORTS.size() * 512 + "> <value: 0-255 / 0-100%> to set a value.");
    System.out.println("Type 'q' to exit, 'v' for output values, or 'p' for output percents.");
    final Scanner SCANNER = new Scanner(System.in);
    while (true) {
      System.out.print("> ");
      final String INPUT = SCANNER.nextLine();
      if (INPUT.matches("[0-9]+ ([0-9]+\\.?[0-9]*)%?")) {
        try {
          final int UNIVERSE = (Integer.parseInt(INPUT.split(" ")[0])-1) / 512;
          if (UNIVERSE >= PORTS.size() || UNIVERSE < 0) {
            throw new Exception("Port out of range.");
          }
          final int ADDRESS = (Integer.parseInt(INPUT.split(" ")[0])-1) - UNIVERSE * 512;
          if (ADDRESS >= 512 || ADDRESS < 0) {
            throw new Exception("Port out of range.");
          }
          final int VALUE = (int)(Math.round(Double.parseDouble(INPUT.split(" ")[1]
                .replace("%", "")) * (INPUT.endsWith("%") ? 2.55 : 1)));
          if (VALUE >= 256 || VALUE < 0) {
            throw new Exception("Value out of range.");
          }
          PORTS.get(UNIVERSE).setByte(ADDRESS, (byte) (VALUE&255));
        }
        catch (Exception e) {
          System.out.println("\tError: " + e.getMessage());
          System.out.println("\tTo set a value, use <address: 1-" + PORTS.size() * 512 + "> <value: 0-255 / 0-100%>, " +
                "e.g. 12 127, or 53 83%");
          System.out.println("\tType 'q' to exit, 'v' for output values, or 'p' for output percents.");
        }
      }
      else {
        if (INPUT.equalsIgnoreCase("q") || INPUT.equalsIgnoreCase("quit")) {
          System.out.println("Terminating application...");
          break;
        }
        else if (INPUT.equalsIgnoreCase("v") || INPUT.equalsIgnoreCase("values")) {
          System.out.println("Current Output Values:");
          for (int universe = 0; universe < PORTS.size(); universe++) {
            final DMXCommunicator port = PORTS.get(universe);
            System.out.printf("\tUNIVERSE %d", universe+1);
            System.out.print("\n\t\t");
            for (int i = 0; i < 16; i++) {
              if (i != 0) {
                System.out.print(" | ");
              }
              System.out.print("ADR VAL");
            }
            for (int address = 0; address < 512; address++) {
              if (address % 16 == 0) {
                System.out.print("\n\t\t");
              }
              else {
                System.out.print(" | ");
              }
              System.out.printf("%" + ((PORTS.size()*512)+"").length() + "d %3d",
                    address+1+universe*512, port.getByte(address)&255);
            }
            System.out.println();
          }
        }
        else if (INPUT.equalsIgnoreCase("p") || INPUT.equalsIgnoreCase("percents")) {
          System.out.println("Current Output Percentages:");
          for (int universe = 0; universe < PORTS.size(); universe++) {
            final DMXCommunicator port = PORTS.get(universe);
            System.out.printf("\tUNIVERSE %d", universe+1);
            System.out.print("\n\t\t");
            for (int i = 0; i < 16; i++) {
              if (i != 0) {
                System.out.print(" | ");
              }
              System.out.print("ADR   %");
            }
            for (int address = 0; address < 512; address++) {
              if (address % 16 == 0) {
                System.out.print("\n\t\t");
              }
              else {
                System.out.print(" | ");
              }
              System.out.printf("%" + ((PORTS.size()*512)+"").length() + "d %3.0f",
                    address+1+universe*512, (port.getByte(address)&255)/255.0*100);
            }
            System.out.println();
          }
        }
        else {
          System.out.println("\tInput not recognized.");
          System.out.println("\tTo set a value, use <address: 1-" + PORTS.size() * 512 + "> <value: 0-255 / 0-100%>, " +
                "e.g. 12 127, or 53 83%");
          System.out.println("\tType 'q' to exit, 'v' for output values, or 'p' for output percents.");
        }
      }
    }
    try {
      while (!(System.in.available() == 0)) {
        Thread.sleep(100);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Stopping output!");
    for (DMXCommunicator port : PORTS) {
      port.stop();
    }
  }
  
}
