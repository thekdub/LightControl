package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.DMXCommunicator;
import com.thekdub.lightcontrol.LightControl;

public class Values extends InputHandler {

  @Override
  public boolean accepts(String input) {
    return input.equalsIgnoreCase("v") || input.equalsIgnoreCase("values");
  }

  @Override
  boolean execute(String input) {
    if (accepts(input)) {
      System.out.println("\nCurrent Output Values:");
      for (int universe = 0; universe < LightControl.getPorts().size(); universe++) {
        final DMXCommunicator port = LightControl.getPorts().get(universe);
        System.out.printf("Universe %d", universe + 1);
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
          System.out.printf("%" + ((LightControl.getPorts().size() * 512) + "").length() + "d %3d",
                address + 1 + universe * 512, port.getByte(address) & 255);
        }
        System.out.println();
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean async() {
    return false;
  }
}
