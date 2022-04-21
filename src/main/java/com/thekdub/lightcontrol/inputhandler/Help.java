package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.LightControl;

public class Help extends InputHandler {

  @Override
  public boolean accepts(String input) {
    return input.equalsIgnoreCase("h") || input.equalsIgnoreCase("help");
  }

  @Override
  boolean execute(String input) {
    if (accepts(input)) {
      System.out.printf("""
            LightControl Command Help:
            \tTo set the value for an address, enter '<address> <value>'.
            \tValid addresses: 1-%d. Valid values: 0-255 OR 0-100%%.
                        
            \tTo fade an address to a value, enter 'f <Address> <value> <seconds>'.
                        
            \tTo blackout all channels, enter 'b'.
                        
            \tTo pause input handling, enter 'w <seconds>'.
                        
            \tTo view output values, enter 'v' for 0-255 values, or 'p' for 1-100%% values.
                              
            \tTo view Command Help, enter 'h'.
                              
            \tTo save a scene, enter 's <filename>'.
            \tTo load a scene, enter 'l <filename>'.
                              
            \tTo stop output and exit, enter 'q'.
                              
            \tMultiple commands can be chained together by separating them with commas.
            \t\tExample: 1 100%%, 5 23, 512 193, p
                              
            """, LightControl.getPorts().size() * 512);
      return true;
    }
    return false;
  }

  @Override
  public boolean async() {
    return false;
  }
}
