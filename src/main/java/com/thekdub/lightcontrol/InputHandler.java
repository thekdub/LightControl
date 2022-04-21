package com.thekdub.lightcontrol;

import com.thekdub.lightcontrol.component.Address;
import com.thekdub.lightcontrol.component.Channel;
import com.thekdub.lightcontrol.component.SubMaster;
import com.thekdub.lightcontrol.exception.OutOfBoundsException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputHandler {

  private enum InputPattern {
    CHANNEL_ASSIGNMENT(Pattern.compile("(?i)^(chan )?([0-9]+ (?>(?>thru|\\+|-) [0-9]+ )*)+@ ([0-9]+(\\.[0-9]+)?)+")),
    ADDRESS_ASSIGNMENT(Pattern.compile("(?i)^(addr )([0-9]+ (?>(?>thru|\\+|-) [0-9]+ )*)+@ ([0-9]+(\\.[0-9]+)?)+")),
    GROUP_ASSIGNMENT(Pattern.compile("(?i)^(group )([0-9]+ (?>(?>thru|\\+|-) [0-9]+ )*)+@ ([0-9]+(\\.[0-9]+)?)+")),
    SUB_MASTER_ASSIGNMENT(Pattern.compile("(?i)^(sub )([0-9]+ (?>(?>thru|\\+|-) [0-9]+ )*)+@ ([0-9]+(\\.[0-9]+)?)+"));
    /*
    Groups:
    1 - Target
    2 - Selection
    3 - Value


     */

    private final Pattern PATTERN;

    InputPattern(Pattern pattern) {
      this.PATTERN = pattern;
    }

    public Pattern getPattern() {
      return PATTERN;
    }

    public Matcher getMatcher(String string) {
      return getPattern().matcher(string);
    }
  }

  private final LinkedList<String> HISTORY = new LinkedList<>();
  final Scanner scanner = new Scanner(System.in);

  public void read() {
    System.out.print("> ");
    HISTORY.push(scanner.nextLine());
  }

  public void execute() throws OutOfBoundsException {
    String input = HISTORY.peek();
    if (input == null) {
      return;
    }
    Matcher matcher;
    // Channel Assignment
    if ((matcher = InputPattern.CHANNEL_ASSIGNMENT.getMatcher(input)).matches()) {
      HashSet<Integer> channels = selectionParser(matcher.group(2));
      double value = Double.parseDouble(matcher.group(3));
      if (value < 0 || value > 100) {
        throw new OutOfBoundsException("Channel values must be within 0 and 100.");
      }
      for (int channel : channels) {
        System.out.printf("Set Channel %d to %.2f%%\n", channel, value);
        Channel temp = LightControl.CHANNELS.get(channel);
        if (temp == null) {
          throw new OutOfBoundsException("Channel " + channel + " does not exist!");
        }
        else {
          temp.setValue(value);
        }
      }
      return;
    }
    // Address Assignment
    if ((matcher = InputPattern.ADDRESS_ASSIGNMENT.getMatcher(input)).matches()) {
      HashSet<Integer> addresses = selectionParser(matcher.group(2));
      double value = Double.parseDouble(matcher.group(3));
      if (value < 0 || value > 255) {
        throw new OutOfBoundsException("Address values must be within 0 and 255.");
      }
      for (int address : addresses) {
        System.out.printf("Set Address %d to %.0f\n", address, value);
        Address temp = LightControl.ADDRESSES.get(address);
        if (temp == null) {
          throw new OutOfBoundsException("Address " + address + " does not exist!");
        }
        else {
          temp.setValue((int) value);
        }
      }
      return;
    }
    // Group Assignment
    if ((matcher = InputPattern.GROUP_ASSIGNMENT.getMatcher(input)).matches()) {
      HashSet<Integer> groups = selectionParser(matcher.group(2));
      double value = Double.parseDouble(matcher.group(3));
      if (value < 0 || value > 100) {
        throw new OutOfBoundsException("Group values must be within 0 and 100.");
      }
      for (int group : groups) {
        System.out.printf("Set Group %d to %.2f%%\n", group, value);
        //LightControl.GROUPS.get(group).setValue(value);
      }
      return;
    }
    // SubMaster Assignment
    if ((matcher = InputPattern.SUB_MASTER_ASSIGNMENT.getMatcher(input)).matches()) {
      HashSet<Integer> subMasters = selectionParser(matcher.group(2));
      double value = Double.parseDouble(matcher.group(3));
      if (value < 0 || value > 100) {
        throw new OutOfBoundsException("SubMaster values must be within 0 and 100.");
      }
      for (int subMaster : subMasters) {
        System.out.printf("Set SubMaster %d to %.2f%%\n", subMaster, value);
        SubMaster temp = LightControl.SUB_MASTERS.get(subMaster);
        if (temp == null) {
          throw new OutOfBoundsException("SubMaster " + subMaster + " does not exist!");
        }
        else {
          temp.setValue(value);
        }
      }
      return;
    }
    // Quit
    if (input.equalsIgnoreCase("q")) {
      System.exit(1);
      return;
    }
    System.out.printf("\nError: Input not recognized.\nInput: %s\n", input);
    System.out.println("Enter 'h' to view command help.");
  }

  private HashSet<Integer> selectionParser(String input) {
    HashSet<Integer> selection = new HashSet<>();
    String[] split = input.contains(" ") ? input.strip().replaceAll("  +", " ").split(" ") :
          new String[]{input};
    int target = Integer.parseInt(split[0]);
    selection.add(target);
    for (int i = 2; i < split.length; i += 2) {
      String modifier = split[i - 1].toLowerCase();
      int secondary = Integer.parseInt(split[i]);
      switch (modifier) {
        case "thru":
          for (int j = target; j <= secondary; j++) {
            selection.add(j);
          }
          break;
        case "+":
          selection.add(secondary);
          break;
        case "-":
          selection.remove(secondary);
          break;
      }
    }
    return selection;
  }

}
