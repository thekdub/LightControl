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

    System.out.println("System ready! Enter <address: 1-" + PORTS.size() * 512 + "> <value: 0-255> to set a value.");
    System.out.println("Type 'q' to exit.");
    final Scanner SCANNER = new Scanner(System.in);
    while (true) {
      System.out.print("> ");
      final String INPUT = SCANNER.nextLine();
      if (INPUT.matches("[0-9]+ [0-9]+")) {
        try {
          int address = Math.min(PORTS.size() * 512-1, Math.max(0, Integer.parseInt(INPUT.split(" ")[0])-1));
          final byte VALUE = (byte) Math.min(255, Math.max(0, Integer.parseInt(INPUT.split(" ")[1])));
          final int UNIVERSE = address / 512;
          address = address - UNIVERSE * 512;
          PORTS.get(UNIVERSE).setByte(address, (byte) VALUE);
        }
        catch (Exception e) {
          System.out.println("Invalid input! Please use format <address: 1-" + PORTS.size() * 512 + "> <value: 0-255>");
          System.out.println("Type 'q' to exit.");
        }
      }
      else {
        if (INPUT.equalsIgnoreCase("q") || INPUT.equalsIgnoreCase("quit")) {
          break;
        }
        else {
          System.out.println("Invalid input! Please use format <address: 1-" + PORTS.size() * 512 + "> <value: 0-255>");
          System.out.println("Type 'q' to exit.");
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
