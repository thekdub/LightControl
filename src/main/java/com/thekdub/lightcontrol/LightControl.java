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
        for (final String PORT : DMXCommunicator.getValidSerialPorts()) {
          System.out.printf("\t> %s\n", PORT);
        }
      }
      else {
        System.out.println("\tNo valid serial ports detected.");
      }
      return;
    }

    System.out.println("\nStarting LightControl...\n");

    final List<DMXCommunicator> PORTS = new ArrayList<>();

    for (int i = 0; i < args.length; i++) {
      final String PORT = args[i];
      try {
        PORTS.add(new DMXCommunicator(PORT));
        System.out.printf("Using port %s for universe %d\n", PORT, i + 1);
      } catch (Exception e) {
        System.out.printf("Unable to connect to port %s! Double check your spelling and make sure the " +
              "adapter is connected!\n", PORT);
        System.out.println("Terminating application!");
        return;
      }
    }

    for (DMXCommunicator port : PORTS) {
      port.start();
    }

    System.out.println("\nSystem ready! Enter 'h' to view command help.");
    final Scanner SCANNER = new Scanner(System.in);
    EXIT:
    while (true) {
      System.out.print("> ");
      final String INPUT = SCANNER.nextLine();
      final String[] COMMANDS = INPUT.contains(",") ? INPUT.split(",") : new String[]{INPUT};
      for (String command : COMMANDS) {
        command = command.strip();
        if (command.matches("[0-9]+ ([0-9]+\\.?[0-9]*)%?")) {
          try {
            final int UNIVERSE = (Integer.parseInt(command.split(" ")[0]) - 1) / 512;
            if (UNIVERSE >= PORTS.size() || UNIVERSE < 0) {
              throw new Exception("Port out of range.");
            }
            final int ADDRESS = (Integer.parseInt(command.split(" ")[0]) - 1) - UNIVERSE * 512;
            if (ADDRESS >= 512 || ADDRESS < 0) {
              throw new Exception("Port out of range.");
            }
            final int VALUE = (int) (Math.round(Double.parseDouble(command.split(" ")[1]
                  .replace("%", "")) * (command.endsWith("%") ? 2.55 : 1)));
            if (VALUE >= 256 || VALUE < 0) {
              throw new Exception("Value out of range.");
            }
            PORTS.get(UNIVERSE).setByte(ADDRESS, (byte) (VALUE & 255));
            System.out.printf("Set channel %d to %d / %.0f%%\n", ADDRESS + 1 + UNIVERSE * 512, VALUE & 255, (VALUE & 255) / 2.55);
          } catch (Exception e) {
            System.out.printf("\nError: %s\nInput: %s\n", e.getMessage(), command);
            System.out.println("Enter 'h' to view command help.");
          }
        }
        else {
          if (command.equalsIgnoreCase("q") || command.equalsIgnoreCase("quit")) {
            System.out.println("\nTerminating application...");
            for (final DMXCommunicator PORT : PORTS) {
              for (int address = 0; address < 512; address++) {
                PORT.setByte(address, (byte) 0);
              }
            }
            break EXIT;
          }
          else if (command.equalsIgnoreCase("v") || command.equalsIgnoreCase("values")) {
            System.out.println("\nCurrent Output Values:");
            for (int universe = 0; universe < PORTS.size(); universe++) {
              final DMXCommunicator port = PORTS.get(universe);
              System.out.printf("UNIVERSE %d", universe + 1);
              System.out.print("\n\t");
              for (int i = 0; i < 16; i++) {
                if (i != 0) {
                  System.out.print(" | ");
                }
                System.out.print("ADR VAL");
              }
              for (int address = 0; address < 512; address++) {
                if (address % 16 == 0) {
                  System.out.print("\n\t");
                }
                else {
                  System.out.print(" | ");
                }
                System.out.printf("%" + ((PORTS.size() * 512) + "").length() + "d %3d",
                      address + 1 + universe * 512, port.getByte(address) & 255);
              }
              System.out.println();
            }
          }
          else if (command.equalsIgnoreCase("p") || command.equalsIgnoreCase("percents")) {
            System.out.println("\nCurrent Output Percentages:");
            for (int universe = 0; universe < PORTS.size(); universe++) {
              final DMXCommunicator port = PORTS.get(universe);
              System.out.printf("UNIVERSE %d", universe + 1);
              System.out.print("\n\t");
              for (int i = 0; i < 16; i++) {
                if (i != 0) {
                  System.out.print(" | ");
                }
                System.out.print("ADR   %");
              }
              for (int address = 0; address < 512; address++) {
                if (address % 16 == 0) {
                  System.out.print("\n\t");
                }
                else {
                  System.out.print(" | ");
                }
                System.out.printf("%" + ((PORTS.size() * 512) + "").length() + "d %3.0f",
                      address + 1 + universe * 512, (port.getByte(address) & 255) / 255.0 * 100);
              }
              System.out.println();
            }
          }
          else if (command.equalsIgnoreCase("h") || command.equalsIgnoreCase("help")) {
            System.out.printf("""
                                    
                  LightControl Command Help:
                  \tTo set the value for an address, enter:
                  \t\t<Address> <Value>
                  \tValid addresses: 1-%d. Valid values: 0-255 OR 0-100%%
                                    
                  \tTo view output values, enter 'v' for 0-255 values, or 'p' for 1-100%% values.
                                    
                  \tTo view Command Help, enter 'h'.
                                    
                  \tTo stop output and exit, enter 'q'.
                                    
                  \tMultiple commands can be chained together by separating them with commas.
                  \t\tExample: 1 100%%, 5 23, 512 193, p
                  """, PORTS.size() * 512);
          }
          else {
            System.out.printf("\nError: Input not recognized.\nInput: %s\n", command);
            System.out.println("Enter 'h' to view command help.");
          }
        }
      }
    }
    for (DMXCommunicator port : PORTS) {
      port.stop();
    }
    System.out.println("\nGoodbye!\n");
  }
  
}
