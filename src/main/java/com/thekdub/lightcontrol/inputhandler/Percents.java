package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.DMXCommunicator;
import com.thekdub.lightcontrol.LightControl;

public class Percents extends InputHandler {
  @Override
  public boolean handle(String input) {
    if (input.equalsIgnoreCase("p") || input.equalsIgnoreCase("percents")) {
      System.out.println("\nCurrent Output Percentages:");
      for (int universe = 0; universe < LightControl.getPorts().size(); universe++) {
        final DMXCommunicator port = LightControl.getPorts().get(universe);
        System.out.printf("Universe %d", universe + 1);
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
          System.out.printf("%" + ((LightControl.getPorts().size() * 512) + "").length() + "d %3.0f",
                address + 1 + universe * 512, (port.getByte(address) & 255) / 255.0 * 100);
        }
        System.out.println();
      }
      return true;
    }
    return false;
  }
}
