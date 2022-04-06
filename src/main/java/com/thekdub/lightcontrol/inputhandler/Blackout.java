package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.LightControl;

public class Blackout extends InputHandler {
  @Override
  public boolean handle(String input) {
    if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("blackout")) {
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
}
