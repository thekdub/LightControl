package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.LightControl;

public class Channel extends InputHandler {

  @Override
  public boolean accepts(String input) {
    return input.matches("c[0-9]+ ([0-9]+\\.?[0-9]*)%?");
  }

  @Override
  boolean execute(String input) {
    if (accepts(input)) {
      try {
        final int channel = Integer.parseInt(input.split(" ")[0].substring(1));
        if (!LightControl.CHANNELS.containsKey(channel)) {
          throw new Exception("Channel does not exist.");
        }
        final int value = (int) (Math.round(Double.parseDouble(input.split(" ")[1]
              .replace("%", "")) * (input.endsWith("%") ? 2.55 : 1)));
        if (value >= 256 || value < 0) {
          throw new Exception("Value out of range.");
        }
        LightControl.CHANNELS.get(channel).setValue(value & 255);
        System.out.printf("Set !!channel %d to %d / %.0f%%\n\n", channel, value & 255,
              (value & 255) / 2.55);
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
    return false;
  }
}
