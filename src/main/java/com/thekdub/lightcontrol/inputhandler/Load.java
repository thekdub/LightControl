package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.DMXCommunicator;
import com.thekdub.lightcontrol.LightControl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Load extends InputHandler {
  @Override
  public boolean handle(String input) {
    if (input.startsWith("l") && input.toLowerCase().matches("l(oad)? [a-z0-9-_.]+")) {
      final String filename = input.substring(input.indexOf(" ") + 1);
      final List<DMXCommunicator> ports = LightControl.getPorts();
      try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        System.out.println("\nLoading output from file " + filename);
        final char[] data = reader.readLine().toCharArray();
        for (int address = 0; address < data.length && address < ports.size() * 512; address++) {
          ports.get(address / 512).setByte(address % 512, (byte) data[address]);
        }
        System.out.println("File read successfully!\n");
      } catch (IOException e) {
        e.printStackTrace();
      }
      return true;
    }
    return false;
  }
}
