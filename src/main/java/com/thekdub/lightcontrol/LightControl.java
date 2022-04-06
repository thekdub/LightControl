package com.thekdub.lightcontrol;

import com.thekdub.lightcontrol.inputhandler.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LightControl {

  private static final List<InputHandler> INPUT_HANDLERS = new ArrayList<>();
  private static final List<DMXCommunicator> PORTS = new ArrayList<>();

  public static void main(String[] args) {

    if (args.length == 0) {
      System.out.println("Usage: java -jar lightcontrol.jar <port1> <port2> ...\n");
      System.out.println("\tEach port provided will be assigned to each universe in order.");
      System.out.println("Valid Ports:");
      if (DMXCommunicator.getValidSerialPorts().size() > 0) {
        for (final String port : DMXCommunicator.getValidSerialPorts()) {
          System.out.printf("\t> %s\n", port);
        }
      }
      else {
        System.out.println("\tNo valid serial ports detected.");
      }
      return;
    }

    Runtime.getRuntime().addShutdownHook(new ShutdownHook());

    INPUT_HANDLERS.add(new Assignment());
    INPUT_HANDLERS.add(new Blackout());
    INPUT_HANDLERS.add(new Help());
    INPUT_HANDLERS.add(new Load());
    INPUT_HANDLERS.add(new Percents());
    INPUT_HANDLERS.add(new Quit());
    INPUT_HANDLERS.add(new Save());
    INPUT_HANDLERS.add(new Values());

    System.out.println("\nStarting LightControl...\n");

    for (int i = 0; i < args.length; i++) {
      final String port = args[i];
      try {
        PORTS.add(new DMXCommunicator(port));
        System.out.printf("Using port %s for universe %d\n", port, i + 1);
      } catch (Exception e) {
        System.out.printf("Unable to connect to port %s! Double check your spelling and make sure the " +
              "adapter is connected!\n", port);
        System.out.println("Terminating application!");
        return;
      }
    }

    for (DMXCommunicator port : PORTS) {
      port.start();
    }

    System.out.println("\nSystem ready! Enter 'h' to view command help.");
    final Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.print("> ");
      final String input = scanner.nextLine();
      final String[] commands = input.contains(",") ? input.split(",") : new String[]{input};
      Arrays.stream(commands).map(String::strip).forEachOrdered(command -> {
        if (INPUT_HANDLERS.stream().filter(handler -> handler.handle(command)).findFirst().isEmpty()) {
          System.out.printf("\nError: Input not recognized.\nInput: %s\n", command);
          System.out.println("Enter 'h' to view command help.");
        }
        ;
      });
    }
  }

  public static List<DMXCommunicator> getPorts() {
    return PORTS.stream().toList();
  }

  private static class ShutdownHook extends Thread {

    @Override
    public void run() {
      for (DMXCommunicator port : PORTS) {
        port.stop();
      }
      System.out.println("\nGoodbye!\n");
    }

  }

}
