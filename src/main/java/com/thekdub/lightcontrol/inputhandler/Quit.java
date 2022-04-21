package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.DMXCommunicator;
import com.thekdub.lightcontrol.LightControl;

public class Quit extends InputHandler {

  @Override
  public boolean accepts(String input) {
    return input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit");
  }

  @Override
  boolean execute(String input) {
    if (accepts(input)) {
      System.out.println("\nTerminating application...");
      System.exit(1);
      return true;
    }
    return false;
  }

  @Override
  public boolean async() {
    return false;
  }
}
