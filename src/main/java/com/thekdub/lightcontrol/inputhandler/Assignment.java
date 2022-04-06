package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.LightControl;

public class Assignment extends InputHandler {
  @Override
  public boolean handle(String input) {
    if (input.matches("[0-9]+ ([0-9]+\\.?[0-9]*)%?")) {
      try {
        final int universe = (Integer.parseInt(input.split(" ")[0]) - 1) / 512;
        if (universe >= LightControl.getPorts().size() || universe < 0) {
          throw new Exception("Port out of range.");
        }
        final int address = (Integer.parseInt(input.split(" ")[0]) - 1) - universe * 512;
        if (address >= 512 || address < 0) {
          throw new Exception("Port out of range.");
        }
        final int value = (int) (Math.round(Double.parseDouble(input.split(" ")[1]
              .replace("%", "")) * (input.endsWith("%") ? 2.55 : 1)));
        if (value >= 256 || value < 0) {
          throw new Exception("Value out of range.");
        }
        LightControl.getPorts().get(universe).setByte(address, (byte) (value & 255));
        System.out.printf("Set channel %d to %d / %.0f%%\n", address + 1 + universe * 512, value & 255,
              (value & 255) / 2.55);
      } catch (Exception e) {
        System.out.printf("\nError: %s\nInput: %s\n", e.getMessage(), input);
        System.out.println("Enter 'h' to view command help.\n");
      }
      return true;
    }
    return false;
  }
}
