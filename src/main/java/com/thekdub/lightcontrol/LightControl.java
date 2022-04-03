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
      for (String port : DMXCommunicator.getValidSerialPorts()) {
        System.out.printf("\t> %s\n", port);
      }
      return;
    }

    final List<DMXCommunicator> PORTS = new ArrayList<>();

    for (String port : args) {
      try {
        PORTS.add(new DMXCommunicator(port));
      }
      catch (Exception e) {
        System.out.printf("Unable to connect to port %s! Double check your spelling and make sure the " +
              "adapter is connected!\n", port);
        return;
      }
    }

    System.out.println("Beginning output!");
    for (DMXCommunicator port : PORTS) {
      port.start();
    }

    System.out.println("System ready! Enter <address: 1-" + PORTS.size() * 512 + "> <value: 0-255> to set a value.");
    System.out.println("Type 'q' to exit.");
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.print("> ");
      String input = scanner.nextLine();
      if (input.matches("[0-9]+ [0-9]+")) {
        try {
          int address = Math.min(PORTS.size() * 512-1, Math.max(0, Integer.parseInt(input.split(" ")[0])-1));
          byte value = (byte) Math.min(255, Math.max(0, Integer.parseInt(input.split(" ")[1])));
          int universe = address / 512;
          address = address - universe * 512;
          PORTS.get(universe).setByte(address, (byte) value);
        }
        catch (Exception e) {
          System.out.println("Invalid input! Please use format <address: 1-" + PORTS.size() * 512 + "> <value: 0-255>");
          System.out.println("Type 'q' to exit.");
        }
      }
      else {
        if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
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
