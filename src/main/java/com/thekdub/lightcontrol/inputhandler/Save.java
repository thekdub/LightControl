package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.DMXCommunicator;
import com.thekdub.lightcontrol.LightControl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Save extends InputHandler {
  @Override
  public boolean handle(String input) {
    if (input.startsWith("s") && input.toLowerCase().matches("s(ave)? [a-z0-9-_.]+")) {
      final String filename = input.substring(input.indexOf(" ") + 1);
      final List<DMXCommunicator> ports = LightControl.getPorts();
      try (FileWriter writer = new FileWriter(filename)) {
        System.out.println("\nWriting current output to file " + filename);
        for (final DMXCommunicator port : ports) {
          for (int address = 0; address < 512; address++) {
            writer.write((char) port.getByte(address) & 255);
          }
        }
        writer.flush();
        System.out.println("File written successfully!\n");
      } catch (IOException e) {
        e.printStackTrace();
      }
      return true;
    }
    return false;
  }
}
