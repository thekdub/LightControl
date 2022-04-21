package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.LightControl;

public class Blackout extends InputHandler {

  @Override
  public boolean accepts(String input) {
    return input.equalsIgnoreCase("b") || input.equalsIgnoreCase("blackout");
  }

  @Override
  boolean execute(String input) {
    if (accepts(input)) {
      LightControl.getPorts().parallelStream().forEach(port -> {
        for (int address = 0; address < 512; address++) {
          port.setByte(address, (byte) 0);
        }
      });
      System.out.println("Blacked out all channels.\n");
      return true;
    }
    return false;
  }

  @Override
  public boolean async() {
    return false;
  }
}
