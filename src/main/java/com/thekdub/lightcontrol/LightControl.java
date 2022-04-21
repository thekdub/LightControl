package com.thekdub.lightcontrol;

import com.thekdub.lightcontrol.component.Address;
import com.thekdub.lightcontrol.component.Channel;
import com.thekdub.lightcontrol.component.SubMaster;
import com.thekdub.lightcontrol.inputhandler.*;

import java.util.*;

public class LightControl {

  private static final List<InputHandler> INPUT_HANDLERS = new ArrayList<>();
  private static final List<DMXCommunicator> PORTS = new ArrayList<>();

  public static final HashMap<Integer, Address> ADDRESSES = new HashMap<>();
  public static final HashMap<Integer, Channel> CHANNELS = new HashMap<>();
  public static final HashMap<Integer, SubMaster> SUB_MASTERS = new HashMap<>();

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
    INPUT_HANDLERS.add(new com.thekdub.lightcontrol.inputhandler.Channel());
    INPUT_HANDLERS.add(new Fade());
    INPUT_HANDLERS.add(new Help());
    INPUT_HANDLERS.add(new Load());
    INPUT_HANDLERS.add(new Percents());
    INPUT_HANDLERS.add(new Quit());
    INPUT_HANDLERS.add(new Save());
    INPUT_HANDLERS.add(new Values());
    INPUT_HANDLERS.add(new Wait());

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

    for (int universe = 0; universe < PORTS.size(); universe++) {
      for (int address = 0; address < 512; address++) {
        ADDRESSES.put(universe * 512 + address, new Address(universe, address));
      }
    }
    for (int channel = 0; channel < 64; channel++) {
      CHANNELS.put(channel, new Channel(channel));
      CHANNELS.get(channel).addAddress(ADDRESSES.get(channel));
    }
    for (int subMaster = 0; subMaster < 8; subMaster++) {
      SUB_MASTERS.put(subMaster, new SubMaster(subMaster));
      SUB_MASTERS.get(subMaster).addChannel(CHANNELS.get(subMaster));
    }

    for (DMXCommunicator port : PORTS) {
      port.start();
    }

    LightEngine lightEngine = new LightEngine();
    lightEngine.start();

    System.out.println("\nSystem ready! Enter 'h' to view command help.");
    final Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.print("> ");
      final String input = scanner.nextLine();
      final String[] commands = input.contains(",") ?
            input.replaceAll(" *, *", ",").split(",") : new String[]{input};
      Arrays.stream(commands).map(String::strip).forEachOrdered(command -> {
        if (INPUT_HANDLERS.stream().filter(handler -> handler.handle(command)).findAny().isEmpty()) {
          System.out.printf("\nError: Input not recognized.\nInput: %s\n", command);
          System.out.println("Enter 'h' to view command help.");
        }
      });
      while (InputHandler.threadsRunning()) {
        try {
          Thread.sleep(5);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static List<DMXCommunicator> getPorts() {
    return PORTS.stream().toList();
  }

  private static class ShutdownHook extends Thread {

    @Override
    public void run() {
      for (final DMXCommunicator port : PORTS) {
        for (int address = 0; address < 512; address++) {
          port.setByte(address, (byte) 0);
        }
      }
      try {
        Thread.sleep(10L * PORTS.size());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      for (final DMXCommunicator port : PORTS) {
        port.stop();
      }
      System.out.println("\nGoodbye!\n");
    }

  }

  private static class LightEngine extends Thread {

    public void run() {
      System.out.println("Starting LightEngine!");
      while (!interrupted() && this.isAlive()) {
        for (int universe = 0; universe < PORTS.size(); universe++) {
          for (int address = 0; address < 512; address++) {
            Address addr = ADDRESSES.get(universe * 512 + address);
            int value = addr.getValue();
            List<Channel> channels = LightControl.CHANNELS.values().parallelStream()
                  .filter(channel -> channel.getAddresses().contains(addr)).toList();
            for (Channel channel : channels) {
              value = (int) (channel.getValue() / 100.0 * 255);
            }
            List<SubMaster> subMasters = LightControl.SUB_MASTERS.values().parallelStream()
                  .filter(subMaster -> subMaster.getChannels().stream().anyMatch(channels::contains)).toList();
            for (SubMaster subMaster : subMasters) {
              value = (int) (value * subMaster.getValue() / 100.0);
            }
            value = Math.min(255, Math.max(0, value));
            PORTS.get(universe).setByte(address, (byte) (value & 255));
          }
        }
      }
      System.out.println("Stopping LightEngine!");
    }

  }

}
