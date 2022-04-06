package com.thekdub.lightcontrol.inputhandler;

import com.thekdub.lightcontrol.LightControl;

public class Help extends InputHandler {

  @Override
  public boolean handle(String input) {
    if (input.equalsIgnoreCase("h") || input.equalsIgnoreCase("help")) {
      System.out.printf("""
            LightControl Command Help:
            \tTo set the value for an address, enter:
            \t\t<Address> <Value>
            \tValid addresses: 1-%d. Valid values: 0-255 OR 0-100%%
                              
            \tTo blackout all channels, enter 'b'.
                              
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
}
