package com.thekdub.lightcontrol.inputhandler;

public class Wait extends InputHandler {

  @Override
  public boolean accepts(String input) {
    return input.toLowerCase().startsWith("w") && input.toLowerCase().matches("w(ait)? ([0-9]+\\.?[0-9]*)");
  }

  @Override
  boolean execute(String input) {
    if (accepts(input)) {
      final double delay = Double.parseDouble(input.substring(input.indexOf(" ") + 1));
      try {
        System.out.printf("Waiting for %.2fs\n", delay);
        Thread.sleep((long) (delay * 1000));
        System.out.println("Finished waiting!\n");
      } catch (InterruptedException e) {
        e.printStackTrace();
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
