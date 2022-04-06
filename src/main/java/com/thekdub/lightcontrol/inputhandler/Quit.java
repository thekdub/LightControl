package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.DMXCommunicator;
import com.thekdub.lightcontrol.LightControl;

public class Quit extends InputHandler {

  @Override
  public boolean handle(String input) {
    if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
      System.out.println("\nTerminating application...");
      for (final DMXCommunicator port : LightControl.getPorts()) {
        for (int address = 0; address < 512; address++) {
          port.setByte(address, (byte) 0);
        }
      }
      System.exit(1);
      return true;
    }
    return false;
  }
}
