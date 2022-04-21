package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.DMXCommunicator;
import com.thekdub.lightcontrol.LightControl;

public class Fade extends InputHandler {

  @Override
  public boolean accepts(String input) {
    return input.toLowerCase().startsWith("f") && input.toLowerCase().matches(
          "f(ade)? [0-9]+ ([0-9]+\\.?[0-9]*)%? ([0-9]+\\.?[0-9]*)");
  }

  @Override
  boolean execute(String input) {
    if (accepts(input)) {
      input = input.substring(input.indexOf(" ") + 1);
      try {
        final int universe = (Integer.parseInt(input.split(" ")[0]) - 1) / 512;
        if (universe >= LightControl.getPorts().size() || universe < 0) {
          throw new Exception("Port out of range.");
        }
        final int address = (Integer.parseInt(input.split(" ")[0]) - 1) - universe * 512;
        if (address >= 512 || address < 0) {
          throw new Exception("Port out of range.");
        }
        final int target = (int) (Math.round(Double.parseDouble(input.split(" ")[1]
              .replace("%", "")) * (input.split(" ")[1].endsWith("%") ? 2.55 : 1)));
        if (target >= 256 || target < 0) {
          throw new Exception("Value out of range.");
        }
        final DMXCommunicator port = LightControl.getPorts().get(universe);
        final int start = port.getByte(address) & 255;
        final double period = Double.parseDouble(input.split(" ")[2]);
        final double stepPeriod = Math.abs(period / (target - start));
        final int step = Math.min(Math.max(target - start, -1), 1);
        if (step == 0) {
          return true;
        }
        System.out.printf("Fading channel %d from %d / %.0f%% to %d / %.0f%% over %.2f seconds.\n",
              address + 1 + universe * 512, port.getByte(address) & 255, (port.getByte(address) & 255) / 2.55,
              target & 255, (target & 255) / 2.55, period);
        for (int value = start + step; step >= 0 ? value <= target : value >= target; value += step) {
          port.setByte(address, (byte) (value & 255));
          try {
            Thread.sleep((long) (stepPeriod * 1000));
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        System.out.println("Finished fading!\n");
      } catch (Exception e) {
        System.out.printf("\nError: %s\nInput: %s\n", e.getMessage(), input);
        System.out.println("Enter 'h' to view command help.\n");
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean async() {
    return true;
  }
}
